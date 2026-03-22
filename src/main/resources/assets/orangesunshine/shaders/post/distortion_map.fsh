#version 330

uniform sampler2D InSampler;
uniform sampler2D OverlaySampler1;
uniform sampler2D OverlaySampler2;

layout(std140) uniform DistortionMapConfig {
    float TotalAlpha;
    float Strength;
    float _pad0;
    float _pad1;
    vec2 TexTranslation0;
    vec2 TexTranslation1;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 noisePixel0 = texture(OverlaySampler1, texCoord + TexTranslation0);
    vec4 noisePixel1 = texture(OverlaySampler2, texCoord + TexTranslation1);
    vec2 joinedTranslation = clamp(noisePixel0.rg + noisePixel1.rg - 1.0, 0.0, 1.0);

    vec2 water1 = abs(noisePixel0.rg - 0.5) * 2.0;
    joinedTranslation *= mix(vec2(1.0), water1, noisePixel0.b);

    vec2 water2 = abs(noisePixel1.rg - 0.5) * 2.0;
    joinedTranslation *= mix(vec2(1.0), water2, noisePixel1.b);

    vec4 newColor = texture(InSampler, texCoord + joinedTranslation * Strength);

    if (TotalAlpha == 1.0) {
        fragColor = newColor;
    } else {
        fragColor = mix(texture(InSampler, texCoord.st), newColor, TotalAlpha);
    }
}
