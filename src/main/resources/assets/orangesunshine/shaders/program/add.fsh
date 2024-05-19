#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D OtherSampler;

varying vec2 texCoord;

void main() {
    gl_FragColor = vec4(texture2D(DiffuseSampler, texCoord).rgb + texture2D(OtherSampler, texCoord).rgb, 1.0);
}
