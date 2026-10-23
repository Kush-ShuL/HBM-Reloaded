package com.hbm.client.fireworks;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;

/** The actual30-tick source letter: current font/glyphs, source exponential scale and alpha floor, native scene depth. */
public final class FireworksLetterParticle extends Particle {
    private final int sourceColor;private final char character;
    public FireworksLetterParticle(ClientLevel world,double x,double y,double z,int color,char character){super(world,x,y,z);lifetime=30;sourceColor=color;this.character=character;}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    @Override public void render(VertexConsumer unused,Camera camera,float partial){
        var minecraft=Minecraft.getInstance();if(minecraft.player==null)return;
        var eye=camera.getPosition();var poses=new PoseStack();
        poses.translate((float)(Mth.lerp(partial,xo,x)-eye.x),(float)(Mth.lerp(partial,yo,y)-eye.y),(float)(Mth.lerp(partial,zo,z)-eye.z));
        poses.mulPose(Axis.YP.rotationDegrees(-minecraft.player.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(minecraft.player.getXRot()));poses.scale(-1,-1,1);
        float time=(age+partial)*4F/lifetime;double scale=1D-1D/Math.pow(Math.E,time);alpha=Math.max(0,1F-(age+partial)/(float)lifetime);
        int opacity=Math.max(10,Math.min(255,(int)(alpha*255))),color=sourceColor+(opacity<<24);
        poses.scale((float)scale,(float)scale,(float)scale);
        String text=String.valueOf(character);
        var transform=new org.joml.Matrix4f(poses.last().pose());
        // deferDraw flushes the preceding billboard batch, preserving the mixed source queue order.
        MukeRenderTypes.deferDraw(()->draw(text,color,transform));
    }
    private static void draw(String text,int color,org.joml.Matrix4f transform){
        var font=Minecraft.getInstance().font;
        float[] oldColor=com.mojang.blaze3d.systems.RenderSystem.getShaderColor().clone();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1,1,1,1);
        try(var memory=new ByteBufferBuilder(256)){
            var immediate=MultiBufferSource.immediate(memory);
            MultiBufferSource source=type->immediate.getBuffer(FireworksLetterTypes.map(type));
            font.drawInBatch(text,-(int)(font.width(text)*.5F),-(int)(font.lineHeight*.5F),color,false,new org.joml.Matrix4f(transform),source,Font.DisplayMode.NORMAL,0,240);
            immediate.endBatch();
        }finally{com.mojang.blaze3d.systems.RenderSystem.setShaderColor(oldColor[0],oldColor[1],oldColor[2],oldColor[3]);}
    }
}
