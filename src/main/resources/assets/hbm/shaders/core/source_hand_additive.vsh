#version 150
#moj_import <light.glsl>
#moj_import <fog.glsl>
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;
uniform mat4 CapturedMVP;
uniform vec4 CapturedColour;
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;
uniform sampler2D SourceLightmap;
uniform float Effect;
uniform int CapturedFogShape;
out float sourceFogDistance;
out vec4 vertexColour;
out float sourceAlpha;
out vec2 textureCoord;
void main() {
    gl_Position = CapturedMVP * vec4(Position, 1.0);
    vertexColour = Color * CapturedColour;
    sourceAlpha = Color.a;
    // Preserve the real source normal/brightness240 and the source hand's
    // configured light directions, rather than making the whole gun fullbright.
    if (Effect > 0.5 && Effect < 1.5) {
        vertexColour = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color)
                     * CapturedColour * texelFetch(SourceLightmap, UV2 / 16, 0);
    }
    sourceFogDistance = fog_distance(Position, CapturedFogShape);
    textureCoord = UV0;
}
