#version 330

uniform sampler2D InSampler;
uniform sampler2D DepthSampler;

layout(std140) uniform SimpleEffectsDepthConfig {
    float Ticks;
    float ColorSafeMode;
    float _pad0;
    float _pad1;
    vec4 WorldColorization;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 incolor = texture(InSampler, texCoord);
    vec3 outcolor = incolor.rgb;
    float fogCoord = texture(DepthSampler, texCoord).r;

    if (WorldColorization.a > 0.0) {
        vec3 c1 = outcolor;
        vec3 c2 = WorldColorization.rgb;

        float distR = sqrt((c1.r - c2.r) * (c1.r - c2.r));
        float distG = sqrt((c1.g - c2.g) * (c1.g - c2.g));
        float distB = sqrt((c1.b - c2.b) * (c1.b - c2.b));

        float dist = clamp((distR + distG + distB), 0.0, 1.0);
        for (int i = 0; i < 4; i++) {
            dist *= dist;
        }

        float harmonizeStrength = dist * 3.0;
        harmonizeStrength += (sin((fogCoord - Ticks) * 0.1434234) - 0.4) * 0.8;
        harmonizeStrength += (sin((fogCoord - Ticks) * -0.12313) - 0.2) * 0.8;
        harmonizeStrength += sin((fogCoord - Ticks) * -0.051233) * 0.2 * sin(Ticks * 0.1321334);
        harmonizeStrength = clamp(harmonizeStrength, 0.0, 3.0);

        vec3 harmonizedColor;

        if (harmonizeStrength < 1.0) {
            harmonizedColor = mix(WorldColorization.rgb, vec3(0.5), harmonizeStrength);
        } else {
            harmonizedColor = mix(vec3(0.5), vec3(1.0) - WorldColorization.rgb, (harmonizeStrength - 1.0) * 0.5);
        }

        if (ColorSafeMode != 0.0) {
            harmonizedColor *= incolor.rgb;
        }

        outcolor = mix(outcolor, harmonizedColor, WorldColorization.a);
    }

    fragColor = vec4(outcolor, incolor.a);
}
