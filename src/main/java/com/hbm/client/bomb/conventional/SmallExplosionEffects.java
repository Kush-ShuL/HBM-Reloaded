package com.hbm.client.bomb.conventional;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;

import com.hbm.bomb.conventional.ConventionalEffects;
import com.hbm.registry.HbmSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Original ParticleExplosionSmall and 1.7.10 EntityBlockDustFX (blc/bld/bkm) rather than modern explosion sprites. */
public final class SmallExplosionEffects {
    private static final AtomicInteger IDS=new AtomicInteger();
    private static final ParticleRenderType CLOUD=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/block/particle/particle_base.png"),false);
    private SmallExplosionEffects(){}
    public static void receive(ConventionalEffects effect){
        var mc=Minecraft.getInstance();var world=mc.level;var player=mc.player;if(world==null||player==null)return;
        float distance=(float)Math.sqrt(player.distanceToSqr(effect.x(),effect.y(),effect.z()));
        if(distance<=200){var event=HbmSounds.get(distance<=80?"weapon.explosionSmallNear":"weapon.explosionSmallFar");var sound=new SimpleSoundInstance(event,SoundSource.BLOCKS,100F,.9F+world.random.nextFloat()*.2F,RandomSource.create(),effect.x(),effect.y(),effect.z());mc.getSoundManager().playDelayed(sound,(int)(distance/8.575D));}
        for(int i=0;i<effect.clouds();i++)mc.particleEngine.add(new Cloud(world,effect.x(),effect.y(),effect.z(),effect.scale(),effect.speed()));
        BlockState debris=Blocks.AIR.defaultBlockState();var center=BlockPos.containing(effect.x(),effect.y(),effect.z());
        for(Direction side:Direction.values()){debris=world.getBlockState(center.relative(side));if(!debris.is(Blocks.AIR))break;}
        if(!debris.is(Blocks.AIR))for(int i=0;i<15;i++)mc.particleEngine.add(new Dust(world,effect.x(),effect.y()+.1,effect.z(),world.random.nextGaussian()*.2,.5F+world.random.nextDouble()*.7,world.random.nextGaussian()*.2,debris));
    }
    public static void sweat(int entityId){
        var mc=Minecraft.getInstance();if(mc.level==null)return;var entity=mc.level.getEntity(entityId);if(!(entity instanceof net.minecraft.world.entity.LivingEntity))return;
        var box=entity.getBoundingBox();var random=mc.level.random;
        double x=box.minX-.2+(box.maxX-box.minX+.4)*random.nextDouble(),y=box.minY+(box.maxY-box.minY+.2)*random.nextDouble(),z=box.minZ-.2+(box.maxZ-box.minZ+.4)*random.nextDouble();
        mc.particleEngine.add(new Dust(mc.level,x,y,z,0,0,0,Blocks.COAL_BLOCK.defaultBlockState(),false,150+random.nextInt(50)));
    }
    /** ClientProxy vanillaburst/blockdust: three supplied Gaussian velocities,+.2Y and50..99tick age. */
    public static void sourceBlockDustBurst(double x,double y,double z,int count,double motion,BlockState state){
        var mc=Minecraft.getInstance();if(mc.level==null)return;var random=mc.level.random;
        for(int i=0;i<count;i++){double vx=random.nextGaussian()*motion,vy=random.nextGaussian()*motion,vz=random.nextGaussian()*motion;mc.particleEngine.add(new Dust(mc.level,x,y,z,vx,vy+.2D,vz,state,false,50+random.nextInt(50)));}
    }
    /** ClientProxy vanillaExt/blockdust: constructor first, then the world's lifetime draw and explicit gray. */
    public static void sourceBlockDust(double x,double y,double z,double vx,double vy,double vz,BlockState state){
        var mc=Minecraft.getInstance();if(mc.level==null)return;
        var dust=new Dust(mc.level,x,y,z,vx,vy+.2D,vz,state,false,0);
        dust.setLifetime(10+mc.level.random.nextInt(20));dust.setColor(.8F,.8F,.8F);
        mc.particleEngine.add(dust);
    }
    private static final class Cloud extends SingleQuadParticle {
        private final float hue,spin;
        Cloud(ClientLevel world,double x,double y,double z,float scale,float speed){
            super(world,x,y,z);hasPhysics=false;
            // Match the old EntityFX base random consumption before the subclass initialization.
            random.nextFloat();random.nextFloat();
            lifetime=25+random.nextInt(10);quadSize=scale*.9F+random.nextFloat()*.2F;
            xd=world.random.nextGaussian()*speed;zd=world.random.nextGaussian()*speed;yd=0;
            gravity=random.nextFloat()*-.01F;hue=20F+random.nextFloat()*20F;spin=(IDS.getAndIncrement()%2)-.5F;
        }
        @Override public void tick(){xo=x;yo=y;zo=z;if(++age>=lifetime)remove();yd-=gravity;oRoll=roll;roll+=(1-(float)age/lifetime)*5*spin*((float)Math.PI/180F);xd*=.65D;zd*=.65D;move(xd,yd,zd);}
        @Override public float getQuadSize(float partial){double time=age+partial,ageScaled=time/lifetime;return(float)((.25+1-Math.pow(1-ageScaled,4)+time*.02)*quadSize);}
        @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;out=MukeRenderTypes.begin(CLOUD);float f=(age+partial)/lifetime;var color=Color.getHSBColor(hue/255F,Math.max(1-f*2,0),Mth.clamp(1.25F-f*2,hue*.01F-.1F,1));rCol=color.getRed()/255F;gCol=color.getGreen()/255F;bCol=color.getBlue()/255F;alpha=(float)Math.pow(1-Math.min(f,1),.25)*.5F;super.render(out,camera,partial);}
        @Override protected float getU0(){return 0;}@Override protected float getU1(){return 1;}@Override protected float getV0(){return 0;}@Override protected float getV1(){return 1;}
        @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    }
    private static final class Dust extends SingleQuadParticle {
        private final TextureAtlasSprite sprite;private final float u,v;
        Dust(ClientLevel world,double x,double y,double z,double vx,double vy,double vz,BlockState state){
            this(world,x,y,z,vx,vy,vz,state,true,50+world.random.nextInt(20));
        }
        Dust(ClientLevel world,double x,double y,double z,double vx,double vy,double vz,BlockState state,boolean doubled,int life){
            super(world,x,y,z);hasPhysics=true;
            u=random.nextFloat()*3;v=random.nextFloat()*3;quadSize=(random.nextFloat()*.5F+.5F)*2*.1F;random.nextFloat();quadSize*=.5F;if(doubled)quadSize*=2;
            lifetime=life;xd=vx;yd=vy;zd=vz;gravity=1;rCol=gCol=bCol=.6F;
            var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);var quads=model.getQuads(state,Direction.DOWN,RandomSource.create(0));sprite=quads.isEmpty()?model.getParticleIcon():quads.getFirst().getSprite();
            // multipleParticleScaleBy(2) also doubles the original .2 particle collision dimensions.
            setSize(doubled?.4F:.2F,doubled?.4F:.2F); setBoundingBox(getBoundingBox().move(0,-.1D,0));
        }
        @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04D*gravity;move(xd,yd,zd);xd*=.9800000190734863D;yd*=.9800000190734863D;zd*=.9800000190734863D;if(onGround){xd*=.699999988079071D;zd*=.699999988079071D;}}
        @Override public void move(double dx,double dy,double dz){
            var allowed=net.minecraft.world.entity.Entity.collideBoundingBox(null,new net.minecraft.world.phys.Vec3(dx,dy,dz),getBoundingBox(),level,java.util.List.of());
            setBoundingBox(getBoundingBox().move(allowed));setLocationFromBoundingbox();onGround=dy!=allowed.y&&dy<0;
            if(dx!=allowed.x)xd=0;if(dy!=allowed.y)yd=0;if(dz!=allowed.z)zd=0;
        }
        @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=.1D;}
        @Override protected float getU0(){return sprite.getU(u/4);}@Override protected float getU1(){return sprite.getU((u+1)/4);}@Override protected float getV0(){return sprite.getV(v/4);}@Override protected float getV1(){return sprite.getV((v+1)/4);}
        @Override public ParticleRenderType getRenderType(){return ParticleRenderType.TERRAIN_SHEET;}
    }
}
