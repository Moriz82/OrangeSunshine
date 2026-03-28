#version 330

uniform sampler2D InSampler;

layout(std140) uniform KaleidoscopeRecursionConfig {
    float TotalAlpha;
    float SegmentCount;
    float Zoom;
    float Spin;
    float Recursion;
    float Ticks;
    float _pad0;
    float _pad1;
};

in vec2 texCoord;
out vec4 fragColor;

vec2 kaleido(vec2 uv, float segs, float spin) {
    float angle = atan(uv.y, uv.x) + spin;
    float radius = length(uv);
    float sector = 6.28318530718 / max(segs, 1.0);
    angle = mod(angle, sector);
    angle = abs(angle - sector * 0.5);
    return vec2(cos(angle), sin(angle)) * radius;
}

void main() {
    vec4 base = texture(InSampler, texCoord);

    vec2 centered = texCoord - 0.5;
    vec3 accum = vec3(0.0);
    float total = 0.0;

    float segs = max(4.0, SegmentCount);
    float rec = clamp(Recursion, 0.0, 1.0);
    int steps = int(2.0 + rec * 4.0);

    for (int i = 0; i < 6; i++) {
        if (i >= steps) break;
        float fi = float(i);
        float layer = fi / max(float(steps - 1), 1.0);
        float layerZoom = mix(1.0, Zoom, layer) + rec * fi * 0.08;
        float layerSpin = Spin * (1.0 + layer * 1.7) + Ticks * 0.01 * layer;

        vec2 uv = centered / max(layerZoom, 0.001);
        uv = kaleido(uv, segs + fi * 0.5, layerSpin);
        uv += 0.5;
        uv = clamp(uv, 0.0, 1.0);

        vec3 c = texture(InSampler, uv).rgb;
        float w = mix(1.0, 0.35, layer);
        accum += c * w;
        total += w;
    }

    vec3 effected = (total > 0.0) ? (accum / total) : base.rgb;
    fragColor = vec4(mix(base.rgb, effected, clamp(TotalAlpha, 0.0, 1.0)), base.a);
}
