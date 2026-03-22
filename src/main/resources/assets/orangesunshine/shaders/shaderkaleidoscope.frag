#version 120
uniform sampler2D tex0;
uniform float ticks;
uniform float kaleidoscopeStrength;
uniform float totalAlpha;

void main() {
    vec2 uv = gl_TexCoord[0].st;
    if (kaleidoscopeStrength < 0.001) {
        gl_FragColor = texture2D(tex0, uv);
        return;
    }
    vec2 center = vec2(0.5, 0.5);
    vec2 delta  = uv - center;
    float r     = length(delta);
    float angle = atan(delta.y, delta.x);
    float segments = 2.0 + floor(kaleidoscopeStrength * 6.0);
    float segAngle  = 3.14159265 * 2.0 / segments;
    angle = mod(angle, segAngle);
    if (angle > segAngle * 0.5) angle = segAngle - angle;
    angle += ticks * 0.003 * kaleidoscopeStrength;
    vec2 newUV = clamp(center + vec2(cos(angle), sin(angle)) * r, 0.0, 1.0);
    gl_FragColor = mix(texture2D(tex0, uv), texture2D(tex0, newUV), kaleidoscopeStrength * totalAlpha);
}
