#version 150
uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform sampler2D Sampler5;
uniform float HbmCloudLayer;
in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;
void main() {
    vec4 color=texture(Sampler0,texCoord0)*vertexColor;
    if(color.a*ColorModulator.a <= 0.0) discard;
    if (HbmCloudLayer > 0.0) {
        float cloudDepth=texture(Sampler5,gl_FragCoord.xy/vec2(textureSize(Sampler5,0))).r;
        if ((HbmCloudLayer == 1.0 && gl_FragCoord.z > cloudDepth) ||
            (HbmCloudLayer == 2.0 && gl_FragCoord.z <= cloudDepth)) discard;
    }
    fragColor=color*ColorModulator;
}
