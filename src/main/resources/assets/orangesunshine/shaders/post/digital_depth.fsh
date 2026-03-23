#version 330

float randomFromVec(vec2 aVec) {
    return fract(sin(dot(aVec.xy, vec2(12.9898,78.233))) * 43758.5453);
}

float getBrightness(vec3 color) {
    float cR = 0.3086;
    float cG = 0.6084;
    float cB = 0.0820;
    return (color.r * cR + color.g * cG + color.b * cB);
}

vec3 getDesaturatedColor(vec3 color) {
    return vec3(getBrightness(color));
}

vec4 reducePalette(vec4 color, float maxCol) {
    return ceil(color * maxCol - 0.5) / maxCol;
}

vec2 pixelate(vec2 uv, vec2 newRes) {
    vec2 coord = vec2(ceil(uv.x * newRes.x) / newRes.x, ceil(uv.y * newRes.y) / newRes.y);
    return coord;
}

float linearize(float value, float zNear, float zFar) {
    return (2.0 * zNear) / (zFar + zNear - value * (zFar - zNear));
}

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
