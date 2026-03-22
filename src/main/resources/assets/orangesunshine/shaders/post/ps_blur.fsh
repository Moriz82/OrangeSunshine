#version 330

uniform sampler2D InSampler;

layout(std140) uniform PsBlurConfig {
    vec2 PixelSize;
    float HBlur;
    float VBlur;
    float Repeats;
    float _pad0;
    float _pad1;
    float _pad2;
};

in vec2 texCoord;

out vec4 fragColor;

vec3 blurPass(vec3 outcolor, int axis, float totalAlpha) {
    vec3 newColor = outcolor * 0.2;

    float xMul = (axis == 0) ? PixelSize.x * HBlur : 0.0;
    float yMul = (axis == 1) ? PixelSize.y * VBlur : 0.0;

    for (float i = -1.0; i < 2.0; i += 2.0) {
        newColor += texture(InSampler, clamp(vec2(texCoord[0] + 1.0 * i * xMul, texCoord[1] + 1.0 * i * yMul), 0.0, 1.0)).rgb * 0.15;
        newColor += texture(InSampler, clamp(vec2(texCoord[0] + 2.0 * i * xMul, texCoord[1] + 2.0 * i * yMul), 0.0, 1.0)).rgb * 0.11;
        newColor += texture(InSampler, clamp(vec2(texCoord[0] + 3.0 * i * xMul, texCoord[1] + 3.0 * i * yMul), 0.0, 1.0)).rgb * 0.09;
        newColor += texture(InSampler, clamp(vec2(texCoord[0] + 4.0 * i * xMul, texCoord[1] + 4.0 * i * yMul), 0.0, 1.0)).rgb * 0.05;
    }

    return mix(outcolor, newColor, totalAlpha);
}

void main() {
    vec4 texel = texture(InSampler, texCoord);
    vec3 outcolor = texel.rgb;

    int repeatsInt = int(Repeats);
    for (int n = 0; n < repeatsInt; n++) {
        float activeHBlur = min(1.0, HBlur - float(n));
        float activeVBlur = min(1.0, VBlur - float(n));

        if (activeHBlur > 0.0 && activeVBlur > 0.0) {
            outcolor = mix(blurPass(outcolor, 0, activeHBlur), blurPass(outcolor, 1, activeVBlur), 0.5);
        } else {
            if (activeHBlur > 0.0) {
                outcolor = blurPass(outcolor, 0, activeHBlur);
            }
            if (activeVBlur > 0.0) {
                outcolor = blurPass(outcolor, 1, activeVBlur);
            }
        }
    }

    fragColor = vec4(outcolor, texel.a);
}
