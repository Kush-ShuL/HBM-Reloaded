#version 150
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;
in vec2 texCoord;
out vec4 fragColor;
void main(){
 vec4 scene=texture(Sampler0,texCoord);
 vec4 cloud=texture(Sampler1,texCoord);
 vec4 front=texture(Sampler2,texCoord);
 vec4 behind=texture(Sampler3,texCoord);
 // The front target begins with scene depth. Only depth-writing effects such as
 // drain smoke can make it nearer than the captured cloud surface. Their source
 // shader already discards alpha <= 1/255, so transparent sprite corners do not occlude.
 float frontDepth=texture(Sampler4,texCoord).r;
 float cloudDepth=texture(Sampler5,texCoord).r;
 if(frontDepth<cloudDepth) cloud=vec4(0.0);
 // Each fragment was split at the actual native cloud surface, even when
 // the mushroom passes through that surface. Colours are premultiplied;
 // additive flashes carry colour without absorbing the background.
 vec3 color=front.rgb+(cloud.rgb+(behind.rgb+scene.rgb*(1.0-behind.a))*(1.0-cloud.a))*(1.0-front.a);
 fragColor=vec4(color,scene.a);
}
