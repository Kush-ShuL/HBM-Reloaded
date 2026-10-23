package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.RubbleBurstEffects;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Original EffectRenderer's 4x4x4 block break grid and EntityDiggingFX motion, lifetime, UVs and collisions. */
public final class RubbleBurstParticles {
    private RubbleBurstParticles(){}
    public static void receive(RubbleBurstEffects packet){var mc=Minecraft.getInstance();var level=mc.level;if(level==null)return;var pos=new BlockPos(packet.x(),packet.y(),packet.z());var state=Block.stateById(packet.state());if(state.isAir())return;for(int x=0;x<4;x++)for(int y=0;y<4;y++)for(int z=0;z<4;z++){double px=pos.getX()+(x+.5D)/4,py=pos.getY()+(y+.5D)/4,pz=pos.getZ()+(z+.5D)/4;mc.particleEngine.add(new Digging(level,px,py,pz,px-pos.getX()-.5,py-pos.getY()-.5,pz-pos.getZ()-.5,state,pos));}}
    private static final class Digging extends SingleQuadParticle {
        private final TextureAtlasSprite sprite;private final float u,v;
        Digging(ClientLevel level,double x,double y,double z,double vx,double vy,double vz,BlockState state,BlockPos pos){super(level,x,y,z);setSize(.2F,.2F);setBoundingBox(getBoundingBox().move(0,-(double).1F,0));u=random.nextFloat()*3;v=random.nextFloat()*3;quadSize=(random.nextFloat()*.5F+.5F)*2F*.1F;quadSize/=2;lifetime=(int)(4F/(random.nextFloat()*.9F+.1F));hasPhysics=true;
            xd=vx+(double)((float)(Math.random()*2D-1D)*.4F);yd=vy+(double)((float)(Math.random()*2D-1D)*.4F);zd=vz+(double)((float)(Math.random()*2D-1D)*.4F);float speed=(float)(Math.random()+Math.random()+1D)*.15F,length=(float)Math.sqrt(xd*xd+yd*yd+zd*zd);xd=xd/length*speed*(double).4F;yd=yd/length*speed*(double).4F+(double).1F;zd=zd/length*speed*(double).4F;
            rCol=gCol=bCol=.6F;if(!state.is(Blocks.GRASS_BLOCK)){int color=Minecraft.getInstance().getBlockColors().getColor(state,level,pos,0);if(color!=-1){rCol*=((color>>16)&255)/255F;gCol*=((color>>8)&255)/255F;bCol*=(color&255)/255F;}}
            var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);var faces=model.getQuads(state,Direction.DOWN,RandomSource.create(0));sprite=faces.isEmpty()?model.getParticleIcon():faces.getFirst().getSprite();
        }
        @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04D;move(xd,yd,zd);xd*=.9800000190734863D;yd*=.9800000190734863D;zd*=.9800000190734863D;if(onGround){xd*=.699999988079071D;zd*=.699999988079071D;}}
        @Override public void move(double dx,double dy,double dz){var delta=Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of());setBoundingBox(getBoundingBox().move(delta));setLocationFromBoundingbox();onGround=dy!=delta.y&&dy<0;if(dx!=delta.x)xd=0;if(dy!=delta.y)yd=0;if(dz!=delta.z)zd=0;}
        @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=(double).1F;}
        @Override protected float getU0(){return sprite.getU(u/4);}@Override protected float getU1(){return sprite.getU((u+1)/4);}@Override protected float getV0(){return sprite.getV(v/4);}@Override protected float getV1(){return sprite.getV((v+1)/4);}
        @Override public ParticleRenderType getRenderType(){return ParticleRenderType.TERRAIN_SHEET;}
    }
}
