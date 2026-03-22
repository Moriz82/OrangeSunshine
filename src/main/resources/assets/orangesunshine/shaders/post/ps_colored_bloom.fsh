#version 330

uniform sampler2D InSampler;

layout(std140) uniform PsColoredBloomConfig {
    vec2 PixelSize;
    float Vertical;
    float TotalAlpha;
    vec4 BloomColor;
};

in vec2 texCoord;

out vec4 fragColor;

float influenceFromColor(vec3 color1, vec3 color2) {
    vec3 rdistCol = (color1 - color2);
    vec3 distCol = sqrt(rdistCol * rdistCol);

    float influence = 1.0 - (distCol.r + distCol.g + distCol.b) * 2.0;

    return clamp(influence, 0.0, 1.0);
}

void main() {
    vec4 texel = texture(InSampler, texCoord);
    vec3 newColor = texel.rgb;
    float bloomInfluence = 0.0;

    vec2 dirVec = vec2(
        (Vertical == 0.0) ? PixelSize.x : 0.0,
        (Vertical == 1.0) ? PixelSize.y : 0.0
    );

    for (float i = -1.0; i < 2.0; i += 2.0) {
        vec2 activeDirVec = i * dirVec;
        vec3 color1 = texture(InSampler, clamp(texCoord + 1.0 * activeDirVec, 0.0, 1.0)).rgb;
        vec3 color2 = texture(InSampler, clamp(texCoord + 2.0 * activeDirVec, 0.0, 1.0)).rgb;
        vec3 color3 = texture(InSampler, clamp(texCoord + 3.0 * activeDirVec, 0.0, 1.0)).rgb;
        vec3 color4 = texture(InSampler, clamp(texCoord + 4.0 * activeDirVec, 0.0, 1.0)).rgb;

        bloomInfluence += influenceFromColor(color1, BloomColor.rgb) * 0.028 * 2.0;
        bloomInfluence += influenceFromColor(color2, BloomColor.rgb) * 0.020 * 2.0;
        bloomInfluence += influenceFromColor(color3, BloomColor.rgb) * 0.016 * 2.0;
        bloomInfluence += influenceFromColor(color4, BloomColor.rgb) * 0.012 * 2.0;
    }

    newColor = mix(newColor, BloomColor.rgb, clamp(bloomInfluence, 0.0, 1.0));

    fragColor = vec4(mix(texel.rgb, newColor, TotalAlpha), texel.a);
}
