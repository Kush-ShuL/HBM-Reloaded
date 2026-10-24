package com.hbm.client.network.debug;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.network.debug.NetworkPortDebug;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/** Original ParticleDebug icons, fixed .05 half-size/10 ticks/no collision and .98 drag. */
public final class NetworkPortDebugParticle extends Particle {
    private static final ParticleRenderType POWER=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/particle/debug_power.png"),false);
    private static final ParticleRenderType FLUID=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/particle/debug_fluid.png"),false);
    private final boolean fluid;
    public NetworkPortDebugParticle(ClientLevel level,NetworkPortDebug packet){
        super(level,packet.x(),packet.y(),packet.z());fluid=packet.fluid();xd=packet.mx();yd=packet.my();zd=packet.mz();lifetime=10;hasPhysics=false;gravity=0;
        int color=fluid?packet.color():0xffffff;rCol=(color>>16&255)/255F;gCol=(color>>8&255)/255F;bCol=(color&255)/255F;
    }
    @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();move(xd,yd,zd);xd*=.9800000190734863D;yd*=.9800000190734863D;zd*=.9800000190734863D;}
    @Override public void move(double dx,double dy,double dz){setPos(x+dx,y+dy,z+dz);}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
    @Override public void render(VertexConsumer ignored,Camera camera,float partial){
        if(MukeRenderTypes.defer(this,partial))return;var out=MukeRenderTypes.begin(fluid?FLUID:POWER);var eye=camera.getPosition();
        float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y),pz=(float)(Mth.lerp(partial,zo,z)-eye.z);
        var corners=new Vector3f[]{new Vector3f(-1,-1,0),new Vector3f(-1,1,0),new Vector3f(1,1,0),new Vector3f(1,-1,0)};
        float[] us={1,1,0,0},vs={1,0,0,1};
        for(int i=0;i<4;i++){var at=corners[i].rotate(camera.rotation()).mul(.05F).add(px,py,pz);out.addVertex(at.x,at.y,at.z).setUv(us[i],vs[i]).setColor(rCol,gCol,bCol,alpha).setLight(240);}
    }
}
