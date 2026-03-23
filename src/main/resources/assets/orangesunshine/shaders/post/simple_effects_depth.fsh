#version 330

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform SimpleEffectsDepthConfig {
    float Ticks;
    float ColorSafeMode;
    vec4 WorldColorization;
};

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 texel = texture(InSampler, texCoord);
    vec3 outcolor = texel.rgb;

    float depth = texture(DepthSampler, texCoord).r;

    if (WorldColorization.a > 0.0) {
        float mix_val = WorldColorization.a;
        if (ColorSafeMode == 1.0) {
            mix_val *= clamp((depth - 0.5) * 5.0, 0.0, 1.0);
        }
        outcolor = mix(outcolor, WorldColorization.rgb, mix_val);
    }

    fragColor = vec4(outcolor, texel.a);
}
