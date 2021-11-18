#version 120

uniform sampler2D DiffuseSampler;

uniform float Radius;
uniform vec2 BlurDir;

varying vec2 texCoord;
varying vec2 oneTexel;

void main() {
    vec4 sum = vec4(0.0);

    for (float r = -Radius; r <= Radius; r += 1.0) {
        sum += texture2D(DiffuseSampler, texCoord + oneTexel*r*BlurDir);
    }

    gl_FragColor = vec4(sum.rgb/(Radius*2.0 + 1.0), 1.0);
}
