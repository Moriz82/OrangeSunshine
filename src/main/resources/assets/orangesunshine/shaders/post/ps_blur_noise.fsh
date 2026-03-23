#version 330

float randomFromVec(vec2 aVec) {
    return fract(sin(dot(aVec.xy, vec2(12.9898,78.233))) * 43758.5453);
}

float randomFromSeed(float aSeed) {
    return fract(mod(aSeed * 12374.123814, 18034.805912));
}

uniform sampler2D InSampler;

layout(std140) uniform PsBlurNoiseConfig {
    vec2 PixelSize;
    float TotalAlpha;
    float Seed;
    float Strength;
    float _pad0;
    float _pad1;
    float _pad2;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 newTexCoords = floor(texCoord / PixelSize) * PixelSize;
    newTexCoords.t += (mod(randomFromSeed(Seed), 1.0) - 0.5) * Strength * 0.04;

    vec4 texel = texture(InSampler, newTexCoords);
    vec3 newColor = texel.rgb;

    float blurChance = Strength * 0.01;

    for (float f = -Strength * 40.0; f < Strength * 40.0 + 0.5; f += 1.0) {
        if (f != 0.0) {
            vec2 bTexCoords = vec2(newTexCoords.s, newTexCoords.t + f * PixelSize.y);

            if (bTexCoords.t > 0.0 && bTexCoords.t < 1.0) {
                float randomOne = randomFromVec(vec2(bTexCoords.s, bTexCoords.t + Seed));

                if (randomOne < blurChance) {
                    newColor = mix(newColor, texture(InSampler, bTexCoords).rgb, 1.0 / (f * f * 0.004 + 1.0));
                }
            }
        }
    }

    fragColor = vec4(mix(texel.rgb, newColor, TotalAlpha), texel.a);
}
