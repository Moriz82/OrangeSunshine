#version 120

const int ITERATIONS = 3;

uniform sampler2D DiffuseSampler;

uniform float Scale;
uniform float Shift;
uniform float TimePassed;
uniform float TimePassedSin;
uniform float Intensity;
uniform float Extend;

varying vec2 texCoord;

mat2 rotate(float angle) {
    float x = cos(angle);
    float y = sin(angle);
    return mat2(x, -y, y, x);
}

void main() {
    // https://www.shadertoy.com/view/tltSWs
    vec2 st = texCoord - 0.5;

    st *= TimePassedSin*0.5 + 1.5;

    for (int i = 0; i < ITERATIONS; i++) {
        st *= Scale;
        st *= rotate(TimePassed);
        st = abs(st);
        st -= Shift;
    }

    // vignette masking (https://www.shadertoy.com/view/lsKSWR)
    vec2 uv = texCoord*(1.0 - texCoord);
    float alpha = pow(uv.x*uv.y * Intensity, Extend);
    vec3 col = mix(texture2D(DiffuseSampler, fract(st)).rgb, texture2D(DiffuseSampler, texCoord).rgb, min(alpha, 1.0));

    gl_FragColor = vec4(col, 1.0);
}