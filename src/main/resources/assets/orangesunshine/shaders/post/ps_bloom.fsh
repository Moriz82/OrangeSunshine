#version 330

uniform sampler2D InSampler;

layout(std140) uniform PsBloomConfig {
    vec2 PixelSize;
    float Vertical;
    float TotalAlpha;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 texel = texture(InSampler, texCoord);
    vec3 newColor = texel.rgb;
    vec3 bloomColor = vec3(0.0);

    vec2 dirVec = vec2((Vertical == 0.0) ? PixelSize.x : 0.0, (Vertical == 1.0) ? PixelSize.y : 0.0) * 3.0;

    float colorInfluence = 15.0 / (5.0 + newColor.r + newColor.g + newColor.b);
    colorInfluence = colorInfluence * colorInfluence;

    for (float i = -1.0; i < 2.0; i += 2.0) {
        vec2 activeDirVec = i * dirVec;
        bloomColor += texture(InSampler, clamp(texCoord + 1.0 * activeDirVec, 0.0, 1.0)).rgb * 0.028 * colorInfluence;
        bloomColor += texture(InSampler, clamp(texCoord + 2.0 * activeDirVec, 0.0, 1.0)).rgb * 0.020 * colorInfluence;
        bloomColor += texture(InSampler, clamp(texCoord + 3.0 * activeDirVec, 0.0, 1.0)).rgb * 0.016 * colorInfluence;
        bloomColor += texture(InSampler, clamp(texCoord + 4.0 * activeDirVec, 0.0, 1.0)).rgb * 0.012 * colorInfluence;
    }

    newColor += bloomColor * bloomColor * bloomColor;

    fragColor = vec4(mix(texel.rgb, newColor, TotalAlpha), texel.a);
}
