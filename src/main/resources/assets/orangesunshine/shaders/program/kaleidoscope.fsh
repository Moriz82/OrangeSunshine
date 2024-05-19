#version 120

#define TAU 6.283185

uniform sampler2D DiffuseSampler;

uniform float SegmentCount;
uniform float Intensity;
uniform float Extend;

varying vec2 texCoord;

void main() {
    // https://danielilett.com/2020-02-19-tut3-8-crazy-kaleidoscopes/
    vec2 offset = vec2(0.5);
    vec2 shiftST = texCoord - offset;
    float radius = length(shiftST);
    float angle = atan(shiftST.y, shiftST.x);

    float segmentAngle = TAU/SegmentCount;

    angle -= segmentAngle*floor(angle/segmentAngle);
    angle = min(angle, segmentAngle - angle);

    vec2 st = vec2(cos(angle), sin(angle))*radius + (1.0 - offset);
    st = max(min(st, 2.0 - st), -st);

    // vignette masking (https://www.shadertoy.com/view/lsKSWR)
    // I would've done blending w/ another shader but for some reason the destination color is always black?
    vec2 uv = texCoord*(1.0 - texCoord);
    float alpha = pow(uv.x*uv.y * Intensity, Extend);
    vec3 col = mix(texture2D(DiffuseSampler, st).rgb, texture2D(DiffuseSampler, texCoord).rgb, min(alpha, 1.0));

    gl_FragColor = vec4(col, 1.0);
}