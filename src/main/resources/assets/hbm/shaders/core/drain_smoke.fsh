#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler5;
uniform float HbmCloudLayer;
uniform sampler2D Sampler6;
uniform sampler2D Sampler7;
uniform sampler2D Sampler8;
uniform float HbmSmokeCutoff;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    gl_FragDepth = gl_FragCoord.z;
    if (HbmSmokeCutoff > 0.0) {
        ivec2 pixel = ivec2(gl_FragCoord.xy);
        float sceneBefore = texelFetch(Sampler6, pixel, 0).r;
        float smokeNearest = texelFetch(Sampler7, pixel, 0).r;
        float sceneNow = texelFetch(Sampler8, pixel, 0).r;
        float cutoff = sceneNow < smokeNearest ? sceneNow : sceneBefore;
        // Foreground rejection uses original z; only surviving colour bypasses our own depth.
        if (gl_FragCoord.z > cutoff) discard;
        gl_FragDepth = min(gl_FragCoord.z, smokeNearest);
    }
    if (HbmCloudLayer > 0.0) {
        float cloudDepth = texture(Sampler5, gl_FragCoord.xy / vec2(textureSize(Sampler5, 0))).r;
        if ((HbmCloudLayer == 1.0 && gl_FragCoord.z > cloudDepth) ||
            (HbmCloudLayer == 2.0 && gl_FragCoord.z <= cloudDepth)) discard;
    }
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    // Official1.7.10 EffectRenderer: glAlphaFunc(GL_GREATER,1/255), not the modern0.1 particle cutoff.
    if (color.a <= 0.003921569) {
        discard;
    }
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
