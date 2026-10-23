#version 150
#moj_import <fog.glsl>
uniform sampler2D Sampler5;
uniform float HbmCloudLayer;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
in float vertexDistance;
in vec4 vertexColor;
out vec4 fragColor;
void main() {
    vec4 color=vertexColor*ColorModulator*linear_fog_fade(vertexDistance,FogStart,FogEnd);
    if(color.a <= 0.0) discard;
    if (HbmCloudLayer > 0.0) {
        float cloudDepth=texture(Sampler5,gl_FragCoord.xy/vec2(textureSize(Sampler5,0))).r;
        if ((HbmCloudLayer == 1.0 && gl_FragCoord.z > cloudDepth) ||
            (HbmCloudLayer == 2.0 && gl_FragCoord.z <= cloudDepth)) discard;
    }
    fragColor=color;
}
