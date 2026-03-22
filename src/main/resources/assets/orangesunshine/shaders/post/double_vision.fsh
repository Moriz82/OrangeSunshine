#version 330

uniform sampler2D InSampler;

layout(std140) uniform DoubleVisionConfig {
    float TotalAlpha;
    float Distance;
    float Stretch;
    float _pad0;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 texel = texture(InSampler, texCoord);
    vec3 newColor = texel.rgb * 0.35;

    newColor += texture(InSampler, vec2(0.5 + (texCoord.s - 0.5) / Stretch + Distance, texCoord.t)).rgb * 0.325;
    newColor += texture(InSampler, vec2(0.5 + (texCoord.s - 0.5) / Stretch - Distance, texCoord.t)).rgb * 0.325;

    fragColor = vec4(mix(texel.rgb, newColor, TotalAlpha), texel.a);
}
