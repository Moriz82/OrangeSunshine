#version 120
uniform sampler2D tex0;
uniform float ticks;
uniform float recursionStrength;
uniform float totalAlpha;

void main() {
    vec2 uv     = gl_TexCoord[0].st;
    if (recursionStrength < 0.001) {
        gl_FragColor = texture2D(tex0, uv);
        return;
    }
    vec2 center  = vec2(0.5, 0.5);
    float scale  = 1.0 - recursionStrength * 0.35;
    vec2 zoomUV  = clamp((uv - center) * scale + center, 0.0, 1.0);
    float drift  = sin(ticks * 0.01) * 0.02 * recursionStrength;
    zoomUV       = clamp(zoomUV + drift, 0.0, 1.0);
    vec4 base    = texture2D(tex0, uv);
    vec4 zoomed  = texture2D(tex0, zoomUV);
    gl_FragColor = mix(base, zoomed, recursionStrength * 0.6 * totalAlpha);
}
