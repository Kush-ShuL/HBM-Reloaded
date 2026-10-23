package com.hbm.client.weapon.death;

import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Original HFR skeleton.obj parts, delay/gravity, ground sound and final forty-tick fade. */
public final class SkeletonParticle extends DeathParticle {
    public enum Bone{SKULL("Skull"),TORSO("Torso"),LIMB("Limb"),SKULL_VILLAGER("SkullVillager");final String part;Bone(String part){this.part=part;}}
    static final ResourceLocation MODEL=id("models/effect/skeleton.obj"),TEXTURE=id("textures/particle/skeleton.png"),EXT=id("textures/particle/skoilet.png"),BLOOD=id("textures/particle/skeleton_blood.png"),BLOOD_EXT=id("textures/particle/skoilet_blood.png");
    private final Bone bone;
    float yaw,oldYaw,pitch,oldPitch;
    private final float momentumYaw,momentumPitch;
    private int initialDelay=20;
    private ResourceLocation texture;
    private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    SkeletonParticle(ClientLevel level,double x,double y,double z,float brightness,Bone bone){
        super(level,x,y,z);this.bone=bone;lifetime=1200+random.nextInt(20);rCol=gCol=bCol=brightness;gravity=.02F;
        momentumPitch=random.nextFloat()*5*(random.nextBoolean()?1:-1);momentumYaw=random.nextFloat()*5*(random.nextBoolean()?1:-1);texture=bone==Bone.SKULL_VILLAGER?EXT:TEXTURE;
    }
    void makeGib(boolean alreadySkeleton){initialDelay=-2;texture=bone==Bone.SKULL_VILLAGER?(alreadySkeleton?EXT:BLOOD_EXT):(alreadySkeleton?TEXTURE:BLOOD);gravity=.04F;lifetime=600+random.nextInt(20);}
    @Override public void tick(){
        xo=x;yo=y;zo=z;oldPitch=pitch;oldYaw=yaw;if(initialDelay-->0)return;
        if(initialDelay== -1){xd=random.nextGaussian()*.025;zd=random.nextGaussian()*.025;}
        if(age++>=lifetime)remove();boolean wasOnGround=onGround;yd-=gravity;move(xd,yd,zd);xd*=.98D;yd*=.98D;zd*=.98D;
        if(!onGround){pitch+=momentumPitch;yaw+=momentumYaw;}else{xd=yd=zd=0;if(!wasOnGround)level.playLocalSound(x,y,z,SoundEvents.SKELETON_HURT,SoundSource.NEUTRAL,.25F,.8F+random.nextFloat()*.4F,false);}
    }
    @Override public void render(VertexConsumer vertices,Camera camera,float partial){
        var cam=camera.getPosition();PoseStack poses=new PoseStack();poses.translate(Mth.lerp(partial,xo,x)-cam.x,Mth.lerp(partial,yo,y)-cam.y,Mth.lerp(partial,zo,z)-cam.z);
        poses.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partial,oldYaw,yaw)));poses.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partial,oldPitch,pitch)));poses.mulPose(Axis.YP.rotationDegrees(-90));
        float left=lifetime-(age+partial);alpha=left<40?left/40:1;int color=((int)(alpha*255)<<24)|((int)(rCol*255)<<16)|((int)(gCol*255)<<8)|(int)(bCol*255);
        LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,vertices,getLightColor(partial),OverlayTexture.NO_OVERLAY,color,LegacyObjRenderer.Convention.HFR,bone.part);
    }
    @Override public AABB getRenderBoundingBox(float partial){return new AABB(x-1,y-1,z-1,x+1,y+1,z+1);}
    @Override public ParticleRenderType getRenderType(){return DeathParticleTypes.bones(texture);}
}
