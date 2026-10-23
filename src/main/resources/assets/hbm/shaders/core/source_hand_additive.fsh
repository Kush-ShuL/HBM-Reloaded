#version 150
#moj_import <fog.glsl>
uniform sampler2D Sampler0;
uniform float AlphaCutoff;
uniform float Effect;
uniform float CapturedFogStart;
uniform float CapturedFogEnd;
in float sourceFogDistance;
in vec4 vertexColour;
in vec2 textureCoord;
out vec4 fragColor;
void main() {
    vec4 colour = texture(Sampler0, textureCoord) * vertexColour;
    if (colour.a < AlphaCutoff) discard;
    if (Effect > 2.5 && Effect < 3.5) colour *= linear_fog_fade(sourceFogDistance, CapturedFogStart, CapturedFogEnd);
    // Opaque geometry retains real cutout/depth and erases additive colour
    // covered by later source draws; its real Iris-lit colour remains intact.
    fragColor = Effect > 0.5 ? colour : vec4(0.0);
}
