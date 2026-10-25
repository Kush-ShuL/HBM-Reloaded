#version 150
#moj_import <fog.glsl>
uniform sampler2D Sampler0;
uniform float AlphaCutoff;
uniform float Effect;
uniform float CapturedFogStart;
uniform float CapturedFogEnd;
uniform sampler2D Sampler6;
uniform sampler2D Sampler7;
uniform sampler2D Sampler8;
uniform float HbmSmokeCutoff;
in float sourceFogDistance;
in vec4 vertexColour;
in vec2 textureCoord;
out vec4 fragColor;
void main() {
    vec4 colour = texture(Sampler0, textureCoord) * vertexColour;
    if (colour.a < AlphaCutoff) discard;
    if (Effect > 2.5 && Effect < 3.5) colour *= linear_fog_fade(sourceFogDistance, CapturedFogStart, CapturedFogEnd);
    // A/S/B are independent current-frame snapshots, never a sampled draw attachment.
    // Keep every original additive layer while preserving genuinely nearer water.
    float sourceDepth = gl_FragCoord.z;
    gl_FragDepth = sourceDepth;
    if (HbmSmokeCutoff > 0.5) {
        ivec2 pixel = ivec2(gl_FragCoord.xy);
        float originalScene = texelFetch(Sampler6, pixel, 0).r;
        float sourceSurface = texelFetch(Sampler7, pixel, 0).r;
        float currentScene = texelFetch(Sampler8, pixel, 0).r;
        float cutoff = currentScene < sourceSurface ? currentScene : originalScene;
        if (sourceDepth > cutoff) discard;
        // Hardware LEQUAL must not reject the farther sheets behind our own early depth.
        gl_FragDepth = min(sourceDepth, sourceSurface);
    }
    // Opaque geometry retains real cutout/depth and erases additive colour
    // covered by later source draws; its real Iris-lit colour remains intact.
    fragColor = Effect > 0.5 ? colour : vec4(0.0);
}
