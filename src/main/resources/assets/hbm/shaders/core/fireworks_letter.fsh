#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler5;
uniform float HbmCloudLayer;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    if (HbmCloudLayer > 0.0) {
        float cloudDepth = texture(Sampler5, gl_FragCoord.xy / vec2(textureSize(Sampler5, 0))).r;
        if ((HbmCloudLayer == 1.0 && gl_FragCoord.z > cloudDepth) ||
            (HbmCloudLayer == 2.0 && gl_FragCoord.z <= cloudDepth)) discard;
    }
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a <= 0.0) {
        discard;
    }
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
