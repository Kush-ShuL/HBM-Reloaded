package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.RbmkMushEffects;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.phys.AABB;

/** Original ParticleRBMKMush's stationary50tick/30frame animation and exact y+scale billboard. */
public final class RbmkMushParticle extends Particle {
    private static final ParticleRenderType MATERIAL=MukeRenderTypes.additive("rbmk_mush");
    private final float scale;
    private RbmkMushParticle(ClientLevel level,double x,double y,double z,float scale){super(level,x,y,z);this.scale=scale;lifetime=50;rCol=gCol=bCol=0;hasPhysics=false;}
    public static void receive(RbmkMushEffects packet){var client=Minecraft.getInstance();if(client.level!=null)client.particleEngine.add(new RbmkMushParticle(client.level,packet.x(),packet.y(),packet.z(),packet.scale()));}
    @Override public void tick(){xo=x;yo=y;zo=z;if(++age==lifetime)remove();}
    @Override public void render(VertexConsumer ignored,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;var out=MukeRenderTypes.begin(MATERIAL);var eye=camera.getPosition();int frame=age*30/lifetime;MukeClient.billboard(out,camera,(float)(x-eye.x),(float)(y-eye.y)+scale,(float)(z-eye.z),scale,1,1,1,1,0,1,frame/30F,(frame+1)/30F,false,false);}
    @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
}
