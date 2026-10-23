package com.hbm.client.bomb.nuclear.fallout;

import com.hbm.bomb.nuclear.NuclearFalloutRain;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.Random;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Matrix4f;

/** X5846 RenderFallout's actual player-centred precipitation quads, not a second fallout entity or weather provider. */
public final class FalloutRainRenderer extends EntityRenderer<NuclearFalloutRain> {
    public static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/fallout.png");
    private final Random random=new Random();
    private float[] rainX,rainZ;
    private long lastTime=System.nanoTime();
    public FalloutRainRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(NuclearFalloutRain entity){return TEXTURE;}
    @Override public void render(NuclearFalloutRain entity,float yaw,float partial,PoseStack poses,MultiBufferSource ignored,int packedLight){
        var mc=Minecraft.getInstance();var camera=mc.getCameraEntity();if(mc.level==null||mc.player==null||camera==null)return;
        // The original gate is a 3D sphere around the entity, measured from camera ENTITY feet, not an infinite rain cylinder.
        if(camera.position().distanceTo(entity.position())>entity.getScale())return;
        long time=System.nanoTime();float interpolation=(time-lastTime)/50_000_000L; // Original long division, intentionally not smooth partialTicks.
        if(interpolation>1F)interpolation=1F;
        rain(entity,partial,interpolation,poses);lastTime=time;
    }
    private void directions(){
        if(rainX!=null)return;rainX=new float[1024];rainZ=new float[1024];
        for(int z=0;z<32;z++)for(int x=0;x<32;x++){
            float dx=x-16,dz=z-16,length=Mth.sqrt(dx*dx+dz*dz);
            // Source centre 0/0 is a degenerate quad. Do not invent a new direction or consume RNG for it.
            rainX[z<<5|x]=-dz/length;rainZ[z<<5|x]=dx/length;
        }
    }
    private void rain(NuclearFalloutRain entity,float partial,float interpolation,PoseStack poses){
        directions();var mc=Minecraft.getInstance();var level=mc.level;var camera=mc.getCameraEntity();
        int timer=mc.player.tickCount,xCenter=Mth.floor(camera.getX()),yCenter=Mth.floor(camera.getY()),zCenter=Mth.floor(camera.getZ());
        var sourceEye=camera.getPosition(interpolation);int height=Mth.floor(sourceEye.y);
        int radius=mc.options.graphicsMode().get()==GraphicsStatus.FAST?5:10;
        // Native entity poses already contain entityOrigin-cameraEye. Convert to the source
        // camera-feet interpolation, keeping the actual native first/third-person eye offset.
        var entityOrigin=entity.getPosition(partial);var normalEye=camera.getPosition(partial);
        poses.pushPose();float[] before=RenderSystem.getShaderColor().clone();
        try(var memory=new ByteBufferBuilder(FalloutRainRenderType.RAIN.bufferSize())){
            poses.translate(normalEye.x-sourceEye.x,normalEye.y-sourceEye.y,normalEye.z-sourceEye.z);
            Matrix4f pose=new Matrix4f(poses.last().pose());var buffers=MultiBufferSource.immediate(memory);var vertices=buffers.getBuffer(FalloutRainRenderType.RAIN);
            RenderSystem.setShaderColor(1,1,1,1);
            for(int z=zCenter-radius;z<=zCenter+radius;z++)for(int x=xCenter-radius;x<=xCenter+radius;x++){
                int index=(z-zCenter+16)*32+x-xCenter+16;float dx=rainX[index]*.5F,dz=rainZ[index]*.5F;
                // Source getPrecipitationHeight includes blocking material/liquid/leaves; native MOTION_BLOCKING is that predicate.
                int precipitation=level.getHeight(Heightmap.Types.MOTION_BLOCKING,x,z),low=Math.max(yCenter-radius,precipitation),high=Math.max(yCenter+radius,precipitation);
                int lightY=Math.max(precipitation,height);if(low==high)continue;
                // Int overflow/XOR/seed order are original Java operations, not long-promoted hashing.
                random.setSeed(x*x*3121+x*45238971^z*z*418711+z*13761);
                float sway=((timer&511)+interpolation)/512F,u=.4F+random.nextFloat()*.2F,v=random.nextFloat();
                double distanceX=x+.5F-camera.getX(),distanceZ=z+.5F-camera.getZ();float distance=(float)Math.sqrt(distanceX*distanceX+distanceZ*distanceZ)/radius;
                float alpha=(1F-distance*distance)*.3F+.5F;
                var lightPos=new BlockPos(x,lightY,z);
                int sourceLight=LightTexture.pack(level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK,lightPos),level.getBrightness(net.minecraft.world.level.LightLayer.SKY,lightPos));
                int light=(sourceLight*3+15728880)/4;
                vertex(vertices,pose,entityOrigin,x-dx+.5D,low,z-dz+.5D,u,low/4F+sway+v,alpha,light);
                vertex(vertices,pose,entityOrigin,x+dx+.5D,low,z+dz+.5D,1F+u,low/4F+sway+v,alpha,light);
                vertex(vertices,pose,entityOrigin,x+dx+.5D,high,z+dz+.5D,1F+u,high/4F+sway+v,alpha,light);
                vertex(vertices,pose,entityOrigin,x-dx+.5D,high,z-dz+.5D,u,high/4F+sway+v,alpha,light);
            }
            buffers.endBatch(FalloutRainRenderType.RAIN); //173 retains actual primitives once under pack, with its owned copy/cleanup.
        }finally{RenderSystem.setShaderColor(before[0],before[1],before[2],before[3]);poses.popPose();}
    }
    private static void vertex(VertexConsumer out,Matrix4f pose,net.minecraft.world.phys.Vec3 entityOrigin,double x,int y,double z,float u,float v,float alpha,int light){out.addVertex(pose,(float)(x-entityOrigin.x),(float)(y-entityOrigin.y),(float)(z-entityOrigin.z)).setUv(u,v).setColor(1F,1F,1F,alpha).setLight(light);}
}
