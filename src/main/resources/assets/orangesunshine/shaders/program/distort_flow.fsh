#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D DistortSampler;

varying vec2 texCoord;

uniform float TimePassed;
uniform float Influence;

const vec2 jump = vec2(0.24, 0.2083333);

// https://catlikecoding.com/unity/tutorials/flow/texture-distortion/
vec3 flow(vec2 flowVector, float time, float phaseOffset) {
    float progress = fract(time + phaseOffset);
    vec3 uvw;
    uvw.xy = mix(texCoord, fract(texCoord - flowVector*progress + phaseOffset + (time - progress)*jump), Influence);
    uvw.z = 1.0 - abs(1.0 - 2.0*progress);
    return uvw;
}

void main() {
    vec3 flowMap = texture2D(DistortSampler, texCoord).rga;
    flowMap.xy = (flowMap.xy * 2.0 - 1.0);

    float time = TimePassed + flowMap.z;
    vec3 uvwA = flow(flowMap.xy, time, 0.0);
    vec3 uvwB = flow(flowMap.xy, time, 0.5);

    vec3 texA = texture2D(DiffuseSampler, uvwA.xy).rgb * uvwA.z;
    vec3 texB = texture2D(DiffuseSampler, uvwB.xy).rgb * uvwB.z;

    gl_FragColor = vec4(texA + texB, 1.0);
}
