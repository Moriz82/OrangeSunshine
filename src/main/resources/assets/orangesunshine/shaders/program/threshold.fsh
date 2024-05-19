#version 120

uniform sampler2D DiffuseSampler;

uniform float Threshold;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);

    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    float contribution = max(0, brightness - Threshold);
    contribution /= max(brightness, 0.0001);
    gl_FragColor = vec4(color.rgb*contribution, 1.0);
}
