#version 330
#moj_import <orangesunshine:get_rotated_color.glsl>
#moj_import <orangesunshine:get_intensified_color.glsl>
#moj_import <orangesunshine:get_desaturated_color.glsl>
#moj_import <orangesunshine:get_inverted_color.glsl>

uniform sampler2D InSampler;

layout(std140) uniform SimpleEffectsConfig {
    float Ticks;
    float SlowColorRotation;
    float QuickColorRotation;
    float ColorIntensification;
    float Desaturation;
    float Inversion;
};

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 texel = texture(InSampler, texCoord);
    vec3 outcolor = texel.rgb;
    float ticks = Ticks;

    if (SlowColorRotation > 0.0) {
        outcolor = mix(outcolor, getRotatedColor(outcolor, mod(ticks, 300.0) / 300.0), SlowColorRotation / 2.0);
    }

    if (QuickColorRotation > 0.0) {
        outcolor = mix(outcolor, getRotatedColor(outcolor, mod(ticks, 50.0) / 50.0), clamp(QuickColorRotation * 1.5, 0.0, 1.0));
    }

    if (ColorIntensification != 0.0) {
        outcolor = mix(outcolor, getIntensifiedColor(outcolor), ColorIntensification);
    }

    if (Desaturation != 0.0) {
        outcolor = mix(outcolor, getDesaturatedColor(outcolor), Desaturation);
    }

    if (Inversion > 0.0) {
        outcolor = mix(outcolor, getInvertedColor(outcolor), Inversion);
    }

    fragColor = vec4(outcolor, texel.a);
}
