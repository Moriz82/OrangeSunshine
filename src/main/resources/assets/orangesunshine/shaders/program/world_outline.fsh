#version 120

uniform int fogMode;

varying vec4 color;

const int GL_LINEAR = 9729;
const int GL_EXP = 2048;
const int GL_EXP2 = 2049;

void main() {
    gl_FragColor = color;

    if (fogMode == GL_LINEAR) {
        gl_FragColor.rgb = mix(gl_FragColor.rgb, gl_Fog.color.rgb, clamp((gl_FogFragCoord - gl_Fog.start)*gl_Fog.scale, 0.0, 1.0));
    } else if (fogMode == GL_EXP) {
        gl_FragColor.rgb = mix(gl_Fog.color.rgb, gl_FragColor.rgb, clamp(exp(-gl_Fog.density * gl_FogFragCoord), 0.0, 1.0));
    } else if (fogMode == GL_EXP2) {
        gl_FragColor.rgb = mix(gl_Fog.color.rgb, gl_FragColor.rgb, clamp(exp(-pow(gl_Fog.density * gl_FogFragCoord, 2.0)), 0.0, 1.0));
    }
}