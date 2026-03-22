#version 330

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;
uniform sampler2D NoiseSampler;

layout(std140) uniform HeatDistortionConfig {
    vec2 PixelSize;
    float Strength;
    float Ticks;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 texel = texture(InSampler, texCoord.st);
    vec4 depthPixel = texture(DepthSampler, texCoord.st);
    vec4 noisePixel1 = texture(NoiseSampler, texCoord.st * 4.0 + vec2(Ticks * 0.324823048, Ticks * 0.48913801));
    vec4 noisePixel2 = texture(NoiseSampler, texCoord.ts * 4.0 + vec2(Ticks * 0.52890348, Ticks * 0.6318212));

    vec4 joinedNoise = noisePixel1 + noisePixel2 - 1.0;

    float depthMul = min(0.4 / sqrt(sqrt(sqrt(1.0 - depthPixel.r))) - 0.4, 1.0);
    vec3 newColor = texture(InSampler, clamp(texCoord.st + joinedNoise.rg * Strength * depthMul, 0.0, 1.0)).rgb;

    fragColor = vec4(newColor, texel.a);
}
