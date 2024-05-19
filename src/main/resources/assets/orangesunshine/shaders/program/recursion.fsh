#version 120

uniform sampler2D DiffuseSampler;

uniform float TimePassed;
uniform float Layers;
uniform float Gap;
uniform float Speed;
uniform float Intensity;
uniform float Extend;

varying vec2 texCoord;

void main() {
    vec2 ndc = texCoord*2.0 - 1.0;
    vec4 col;

    for (float i = 0.0; i < Layers; i += Gap) {
        vec2 s = ndc*(i - fract(TimePassed*Speed)*Gap);
        vec2 t = s*0.5 + 0.5;

        if (fract(t) == t) {
            float alpha = (1.0 - s.x*s.x*s.x*s.x)*(1.0 - s.y*s.y*s.y*s.y);
            col.rgb = alpha*texture2D(DiffuseSampler, t).rgb + (1.0 - alpha)*col.rgb;
            col.a = alpha + (1.0 - col.a);
        }
    }

    // vignette masking (https://www.shadertoy.com/view/lsKSWR)
    vec2 uv = texCoord*(1.0 - texCoord);
    float alpha = pow(uv.x*uv.y * Intensity, Extend);
    vec3 final = mix(col.rgb, texture2D(DiffuseSampler, texCoord).rgb, min(alpha, 1.0));

    gl_FragColor = vec4(final, 1.0);
}
