#version 120

varying vec2 texCoord;
varying vec2 overlayCoord;
varying vec2 lmCoord;
varying vec3 normalVector;
varying vec4 color;

uniform mat4 modelViewMat;
uniform mat4 modelViewInverseMat;
uniform float smallWaves;
uniform float bigWaves;
uniform float wiggleWaves;
uniform float distantWorldDeformation;
uniform float timePassed;

void main(){
    normalVector = normalize(gl_NormalMatrix * gl_Normal);
    texCoord = (gl_TextureMatrix[0] * gl_MultiTexCoord0).st;
    overlayCoord = (gl_TextureMatrix[1] * gl_MultiTexCoord1).st;
    lmCoord = (gl_TextureMatrix[2] * gl_MultiTexCoord2).st;
    color = gl_Color;

    float ticksPassed = timePassed*20.0;

    vec4 position = gl_ModelViewMatrix * gl_Vertex;

    //position = modelViewInverseMat * position;

    float dist = length(position.xyz);

    if (smallWaves > 0.0) {
        position.y += sin((position.x + ticksPassed * 0.2) * 0.785398163) * sin((position.z + ticksPassed * 0.2) * 0.785398163) * smallWaves * 1.5;
        position.y -= sin((ticksPassed * 0.2) * 0.785398163) * sin((ticksPassed * 0.2) * 0.785398163) * smallWaves * 1.5;

        position.y += sin((position.x + ticksPassed * 0.125) * 0.392699082) * sin((position.z) * 0.392699082) * smallWaves * 3.0;

        position.x = mix(position.x, position.x * (1.0 + dist * 0.05), smallWaves);
        position.y = mix(position.y, position.y * (1.0 + dist * 0.05), smallWaves);
    }

    if (wiggleWaves > 0.0) {
        position.x += sin((position.y + ticksPassed*0.125)*0.125 * 3.14159 * 2.0) * sin((position.z + ticksPassed*0.2) * 0.125 * 3.14159 * 2.0) * wiggleWaves;
    }

    if (distantWorldDeformation > 0.0 && dist > 5.0) {
        position.y += (sin(dist*0.125*3.14159*2.0) + 1.0)*distantWorldDeformation * (dist - 5.0)*0.125;
    }

    if (bigWaves > 0.0) {
        float dist2D = length(position.xz);
        float f = 20.0 * bigWaves + (dist2D*bigWaves - 20.0) * bigWaves * 0.3;
        f *= min(dist2D * 0.05, 1.0);

        float inf1 = sin(ticksPassed * 0.0086465563) * f;
        float inf2 = cos(ticksPassed * 0.0086465563) * f;
        float inf3 = sin(ticksPassed * 0.0091033941) * f;
        float inf4 = cos(ticksPassed * 0.0091033941) * f;
        float inf5 = sin(ticksPassed * 0.0064566190) * f;
        float inf6 = cos(ticksPassed * 0.0064566190) * f;

        float pMul = 1.3;

        position.x += sin(position.z * 0.1 * sin(ticksPassed * 0.001849328) + ticksPassed * 0.014123412) * 0.5 * inf1 * pMul;
        position.y += cos(position.z * 0.1 * sin(ticksPassed * 0.001234728) + ticksPassed * 0.017481893) * 0.4 * inf1 * pMul;

        position.x += sin(position.y * 0.1 * sin(ticksPassed * 0.001523784) + ticksPassed * 0.021823911) * 0.2 * inf2 * pMul;
        position.y += sin(position.x * 0.1 * sin(ticksPassed * 0.001472387) + ticksPassed * 0.023193141) * 0.08 * inf2 * pMul;

        position.x += sin(position.z * 0.15 * sin(ticksPassed * 0.001284923) + ticksPassed * 0.019404289) * 0.25 * inf3 * pMul;
        position.y += cos(position.z * 0.15 * sin(ticksPassed * 0.001482938) + ticksPassed * 0.018491238) * 0.15 * inf3 * pMul;

        position.x += sin(position.y * 0.05 * sin(ticksPassed * 0.001283942) + ticksPassed * 0.012942342) * 0.4 * inf4 * pMul;
        position.y += sin(position.x * 0.05 * sin(ticksPassed * 0.001829482) + ticksPassed * 0.012981328) * 0.35 * inf4 * pMul;

        position.z += sin(position.y * 0.13 * sin(ticksPassed * 0.02834472) + ticksPassed * 0.023482934) * 0.1 * inf5 * pMul;
        position.z += sin(position.x * 0.124 * sin(ticksPassed * 0.00184298) + ticksPassed * 0.018394082) * 0.05 * inf6 * pMul;
    }

    //position = modelViewMat * position;

    gl_Position = gl_ProjectionMatrix * position;
    gl_FrontColor = gl_Color;
    gl_FogFragCoord = length(position.xyz);
}
