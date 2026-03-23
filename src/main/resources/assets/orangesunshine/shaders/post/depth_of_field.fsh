#version 330

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform DepthOfFieldConfig {
    vec2 PixelSize;
    float Vertical;
    float FocalPointNear;
    float FocalPointFar;
    float FocalBlurNear;
    float FocalBlurFar;
    float _pad0;
    vec2 DepthRange;
    float _pad1;
    float _pad2;
};

in vec2 texCoord;

out vec4 fragColor;

float linearize(float value, float zNear, float zFar) {
    return (2.0 * zNear) / (zFar + zNear - value * (zFar - zNear));
}

float getLinearDepth(vec2 newUV) {
    float depth = texture(DepthSampler, newUV).r;
    return linearize(depth, DepthRange.x, DepthRange.y);
}

void main() {
    vec4 texel = texture(InSampler, texCoord.st);
    vec3 newColor = texel.rgb * 0.2;

    float depth = getLinearDepth(texCoord.st);
    float focalDepth = 0.0;
    if (depth < FocalPointNear) {
        focalDepth = (FocalPointNear - depth) / FocalPointNear * FocalBlurNear;
    } else if (depth > FocalPointFar) {
        focalDepth = (depth - FocalPointFar) / FocalPointFar * FocalBlurFar;
    }

    focalDepth = min(focalDepth, 1.0);

    if (focalDepth > 0.0) {
        float xMul = (Vertical == 0.0) ? PixelSize.x : 0.0;
        float yMul = (Vertical == 1.0) ? PixelSize.y : 0.0;

        for (float i = -1.0; i < 2.0; i += 2.0) {
            newColor += texture(InSampler, vec2(texCoord.s + 1.0 * i * xMul, texCoord.t + 1.0 * i * yMul)).rgb * 0.15;
            newColor += texture(InSampler, vec2(texCoord.s + 2.0 * i * xMul, texCoord.t + 2.0 * i * yMul)).rgb * 0.11;
            newColor += texture(InSampler, vec2(texCoord.s + 3.0 * i * xMul, texCoord.t + 3.0 * i * yMul)).rgb * 0.09;
            newColor += texture(InSampler, vec2(texCoord.s + 4.0 * i * xMul, texCoord.t + 4.0 * i * yMul)).rgb * 0.05;
        }

        newColor = mix(texel.rgb, newColor, focalDepth);
    }

    fragColor = vec4(newColor, texel.a);
}
