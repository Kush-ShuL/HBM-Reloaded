package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.FoundryStreamPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.awt.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Exact source ParticleFoundry 20-tick mesh, widths, texture scroll and brightening. */
public final class FoundryStreamParticle extends Particle {
 private final int color;private final Direction dir;private final double length,base,offset;
 private static final ParticleRenderType TYPE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,ResourceLocation.fromNamespaceAndPath("hbm","textures/block/lava_gray.png"));RenderSystem.setShaderColor(1,1,1,1);RenderSystem.disableBlend();RenderSystem.disableCull();RenderSystem.depthMask(true);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};
 public FoundryStreamParticle(ClientLevel level,FoundryStreamPacket packet){super(level,packet.x(),packet.y(),packet.z());color=packet.color();dir=packet.direction();length=packet.length();base=packet.base();offset=packet.offset();lifetime=20;}
 @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();}
 @Override public void render(VertexConsumer out,Camera camera,float interp){var eye=camera.getPosition();float pX=(float)(Mth.lerp(interp,xo,x)-eye.x),pY=(float)(Mth.lerp(interp,yo,y)-eye.y),pZ=(float)(Mth.lerp(interp,zo,z)-eye.z);
		Direction rot = dir.getClockWise();
		double width = 0.0625 + ((this.age + interp) / this.lifetime) * 0.0625;
		double girth = 0.125 * (1 - ((this.age + interp) / this.lifetime));
		
		Color color = new Color(this.color).brighter();
		double brightener = 0.7D;
		int r = (int) (255D - (255D - color.getRed()) * brightener);
		int g = (int) (255D - (255D - color.getGreen()) * brightener);
		int b = (int) (255D - (255D - color.getBlue()) * brightener);

		rCol=r/255F;gCol=g/255F;bCol=b/255F;

		

		double dirXG = dir.getStepX() * girth;
		double dirZG = dir.getStepZ() * girth;
		double rotXW = rot.getStepX() * width;
		double rotZW = rot.getStepZ() * width;
		
		double uMin = 0.5 - width;
		double uMax = 0.5 + width;
		double vMin = 0;
		double vMax = length;
		
		double add = (int)(System.currentTimeMillis() / 100 % 16) / 16D;

		//lower back
		vertex(out, pX, pY, pZ, rotXW,		girth,		rotZW,	uMax,	vMax + add + girth);
		vertex(out, pX, pY, pZ, -rotXW,	girth,		-rotZW,	uMin,	vMax + add + girth);
		vertex(out, pX, pY, pZ, -rotXW,	-length,	-rotZW,	uMin,	vMin + add);
		vertex(out, pX, pY, pZ, rotXW,		-length,	rotZW,	uMax,	vMin + add);

		//lower front
		vertex(out, pX, pY, pZ, dirXG + rotXW,	0,			dirZG + rotZW,	uMax,	vMax + add);
		vertex(out, pX, pY, pZ, dirXG - rotXW,	0,			dirZG - rotZW,	uMin,	vMax + add);
		vertex(out, pX, pY, pZ, dirXG - rotXW,	-length,	dirZG - rotZW,	uMin,	vMin + add);
		vertex(out, pX, pY, pZ, dirXG + rotXW,	-length,	dirZG + rotZW,	uMax,	vMin + add);
		
		double wMin = 0;
		double wMax = girth;
		
		//lower left
		vertex(out, pX, pY, pZ, rotXW,			girth,		rotZW,			wMin, vMax + add + girth);
		vertex(out, pX, pY, pZ, dirXG + rotXW,	0,			dirZG + rotZW,	wMax, vMax + add);
		vertex(out, pX, pY, pZ, dirXG + rotXW,	-length,	dirZG + rotZW,	wMax, vMin + add);
		vertex(out, pX, pY, pZ, rotXW,			-length,	rotZW,			wMin, vMin + add);
		
		//lower right
		vertex(out, pX, pY, pZ, -rotXW,		girth,		-rotZW,			wMin, vMax + add + girth);
		vertex(out, pX, pY, pZ, dirXG - rotXW,	0,			dirZG - rotZW,	wMax, vMax + add);
		vertex(out, pX, pY, pZ, dirXG - rotXW,	-length,	dirZG - rotZW,	wMax, vMin + add);
		vertex(out, pX, pY, pZ, -rotXW,		-length,	-rotZW,			wMin, vMin + add);

		double dirOX = dir.getStepX() * offset;
		double dirOZ = dir.getStepZ() * offset;
		
		vMax = offset;

		//upper back
		vertex(out, pX, pY, pZ, rotXW,				0,		rotZW,			uMax, vMax - add);
		vertex(out, pX, pY, pZ, -rotXW,			0,		-rotZW,			uMin, vMax - add);
		vertex(out, pX, pY, pZ, -rotXW - dirOX,	base,	-rotZW - dirOZ,	uMin, vMin - add);
		vertex(out, pX, pY, pZ, rotXW - dirOX,		base,	rotZW - dirOZ,	uMax, vMin - add);
		
		//upper front
		vertex(out, pX, pY, pZ, rotXW,				girth,			rotZW,			uMax, vMax - add + 0.25);
		vertex(out, pX, pY, pZ, -rotXW,			girth,			-rotZW,			uMin, vMax - add + 0.25);
		vertex(out, pX, pY, pZ, -rotXW - dirOX,	base + girth,	-rotZW - dirOZ,	uMin, vMin - add + 0.25);
		vertex(out, pX, pY, pZ, rotXW - dirOX,		base + girth,	rotZW - dirOZ,	uMax, vMin - add + 0.25);
		
		//upper left
		vertex(out, pX, pY, pZ, rotXW,			0,				rotZW,			wMax, vMax - add + 0.75);
		vertex(out, pX, pY, pZ, rotXW,			girth,			rotZW,			wMin, vMax - add + 0.75);
		vertex(out, pX, pY, pZ, rotXW - dirOX,	base + girth,	rotZW - dirOZ,	wMin, vMin - add + 0.75);
		vertex(out, pX, pY, pZ, rotXW - dirOX,	base,			rotZW - dirOZ,	wMax, vMin - add + 0.75);
		
		//upper right
		vertex(out, pX, pY, pZ, -rotXW,			0,				-rotZW,			wMax, vMax - add + 0.75);
		vertex(out, pX, pY, pZ, -rotXW,			girth,			-rotZW,			wMin, vMax - add + 0.75);
		vertex(out, pX, pY, pZ, -rotXW - dirOX,	base + girth,	-rotZW - dirOZ,	wMin, vMin - add + 0.75);
		vertex(out, pX, pY, pZ, -rotXW - dirOX,	base,			-rotZW - dirOZ, wMax, vMin - add + 0.75);
		
		vMax = 0.125F;
		
		//bend
		vertex(out, pX, pY, pZ, dirXG + rotXW,	0,		dirZG + rotZW,	uMax,	vMin + add + 0.75);
		vertex(out, pX, pY, pZ, dirXG - rotXW,	0,		dirZG - rotZW,	uMin,	vMin + add + 0.75);
		vertex(out, pX, pY, pZ, -rotXW,		girth,	-rotZW,			uMin,	vMax + add + 0.75);
		vertex(out, pX, pY, pZ, rotXW,			girth,	rotZW,			uMax,	vMax + add + 0.75);
 }
 private void vertex(VertexConsumer out,float px,float py,float pz,double x,double y,double z,double u,double v){out.addVertex((float)x+px,(float)y+py,(float)z+pz).setUv((float)u,(float)v).setColor(rCol,gCol,bCol,1F).setLight(240);}
 @Override public AABB getRenderBoundingBox(float partial){double extent=offset+.25;return new AABB(x-extent,y-length,z-extent,x+extent,y+base+.25,z+extent);}
 @Override public ParticleRenderType getRenderType(){return TYPE;}
}
