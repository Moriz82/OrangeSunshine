#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform float Amplitude;
uniform float TimePassed;

varying vec2 texCoord;

#define PI 3.14159265358979323846

// https://gist.github.com/ForeverZer0/f4f3ce84fe8a58d3ab8d16feb73b3509
vec3 hueShift(vec3 color, float hue) {
    const vec3 k = vec3(0.57735, 0.57735, 0.57735);
    float cosAngle = cos(hue);
    return vec3(color * cosAngle + cross(k, color) * sin(hue) + k * dot(k, color) * (1.0 - cosAngle));
}

float linearize(float depth) {
    const float near = 0.1;
    const float far = 1000.0;
    float z = depth*2.0 - 1.0;
    return (2.0 * near * far)/(far + near - z * (far - near));
}

void main() {
    float depth = linearize(texture2D(DiffuseDepthSampler, texCoord).x);
    vec3 color = texture2D(DiffuseSampler, texCoord).rgb;
    vec3 shifted = hueShift(color, 2.0*PI*fract(TimePassed + depth));
    gl_FragColor = vec4(mix(color, shifted, Amplitude), 1.0);
}