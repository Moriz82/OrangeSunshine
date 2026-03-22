#version 330
#moj_import <orangesunshine:include/random_from_vec.glsl>
#moj_import <orangesunshine:include/get_desaturated_color.glsl>
#moj_import <orangesunshine:include/reduce_pallete.glsl>
#moj_import <orangesunshine:include/pixelate.glsl>
#moj_import <orangesunshine:include/linear.glsl>

uniform sampler2D InSampler;
uniform sampler2D AsciiSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform DigitalConfig {
    vec2 NewResolution;
    float TextProgress;
    float MaxColors;
    float Saturation;
    float TotalAlpha;
    float _pad0;
    float _pad1;
    vec2 DepthRange;
    float _pad2;
    float _pad3;
};

in vec2 texCoord;

out vec4 fragColor;

float getPixelDensity(vec2 newUV, vec4 newColor) {
    float textureDepth = texture(DepthSampler, newUV).r;
    return linearize(textureDepth, DepthRange.x, DepthRange.y);
}

void main() {
    vec2 newUV = (NewResolution.x > 0.0 && NewResolution.y > 0.0)
        ? pixelate(texCoord.st, NewResolution)
        : texCoord.st;
    vec4 newColor = texture(InSampler, newUV);

    if (Saturation < 1.0) {
        newColor.rgb = mix(getDesaturatedColor(newColor.rgb), newColor.rgb, Saturation);
    }

    if (MaxColors >= 0.0) {
        newColor = reducePalette(newColor, MaxColors);
    }

    if (TextProgress > 0.0) {
        float textProg = clamp(TextProgress, 0.0, 1.0);

        float invTextProg = 1.0 - clamp(0.0, textProg - 0.2, 0.8) * 1.25;
        float bgMaxAlpha = sqrt(invTextProg);
        if ((randomFromVec(newUV) * 0.999) < textProg || bgMaxAlpha < 1.0) {
            float binaryProg = TextProgress - textProg;

            float pixelDensity = getPixelDensity(newUV, newColor);

            float pixelDensityParts = 95.0;
            float pixelDensityPart = float(ceil(pixelDensity * (pixelDensityParts - 1.0) - (0.5 / pixelDensityParts))) / pixelDensityParts;
            bool isBinary = (randomFromVec(newUV) * 0.999) < binaryProg;
            if (isBinary) {
                pixelDensityPart = min((ceil(pixelDensity - 0.25)) / pixelDensityParts, 1.0 / pixelDensityParts);
            }

            vec2 innerUV = (newUV - texCoord.st) * NewResolution;
            innerUV.x = 1.0 - innerUV.x;
            vec4 textTexturePixel = texture(AsciiSampler, vec2(pixelDensityPart + innerUV.x / pixelDensityParts, innerUV.y * 0.5 + (isBinary ? 0.5 : 0.0)));

            newColor.rgb = mix(newColor.rgb, textTexturePixel.rgb * newColor.rgb, clamp(0.0, max(textProg * textProg * textProg, 1.0 - bgMaxAlpha), 1.0));
        }
    }

    if (TotalAlpha == 1.0) {
        fragColor = newColor;
    } else {
        fragColor = mix(texture(InSampler, texCoord.st), newColor, TotalAlpha);
    }
}
