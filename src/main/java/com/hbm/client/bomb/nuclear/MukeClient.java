package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearMukeEffects;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

/** Original ClientProxy muke/tinytot/bf branches and the old ActiveRenderInfo billboard basis. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MukeClient {
    static ShaderInstance cloudShader,additiveShader;
    private MukeClient(){}
    /** Original unlit additive shader: retain all alpha above zero. */
    public static ShaderInstance additiveShader(){NuclearTransparencyLayers.setupShader(additiveShader);return additiveShader;}
    public static ShaderInstance cloudShader(){NuclearTransparencyLayers.setupShader(cloudShader);return cloudShader;}
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class WorldStage {
        private WorldStage(){}
        @SubscribeEvent public static void draw(net.neoforged.neoforge.client.event.RenderLevelStageEvent event){MukeRenderTypes.renderDeferred(event);}
    }
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:muke_cloud"),DefaultVertexFormat.PARTICLE),shader->cloudShader=shader);event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:muke_additive"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->additiveShader=shader);}
    public static void receive(NuclearMukeEffects packet){var mc=Minecraft.getInstance();var level=mc.level;if(level==null)return;double x=packet.x(),y=packet.y(),z=packet.z();var random=level.random;
        switch(packet.effect()){
            case "muke"->{mc.particleEngine.add(new MukeWave(level,x,y,z));mc.particleEngine.add(new MukeFlash(level,x,y,z,packet.balefire()));}
            case "tinytot"->{mc.particleEngine.add(new MukeWave(level,x,y,z));for(double d=0;d<=1.6D;d+=.1D)mc.particleEngine.add(new MukeCloud(level,x,y,z,random.nextGaussian()*.05,d+random.nextGaussian()*.02,random.nextGaussian()*.05,false));for(int i=0;i<50;i++)mc.particleEngine.add(new MukeCloud(level,x,y+.5,z,random.nextGaussian()*.5,random.nextInt(5)==0?.02:0,random.nextGaussian()*.5,false));for(int i=0;i<15;i++){double vx=random.nextGaussian()*.2,vz=random.nextGaussian()*.2;if(vx*vx+vz*vz>.75){vx*=.5;vz*=.5;}double vy=1.6+(random.nextDouble()*2-1)*(.75-(vx*vx+vz*vz))*.5;mc.particleEngine.add(new MukeCloud(level,x,y,z,vx,vy+random.nextGaussian()*.02,vz,false));}}
            case "bf"->{mc.particleEngine.add(new MukeCloud(level,x,y,z,0,0,0,true));return;}
            default->throw new IllegalArgumentException("Unknown source Muke effect "+packet.effect());
        }
        if(mc.player!=null){mc.player.animateHurt(0);mc.player.hurtTime=15;mc.player.hurtDuration=15;}
    }
    static void billboard(VertexConsumer out,Camera camera,float px,float py,float pz,float scale,float r,float g,float b,float alpha,float u0,float u1,float v0,float v1,boolean constantHeight,boolean light){
        float yaw=camera.getYRot()*(float)Math.PI/180F,pitch=camera.getXRot()*(float)Math.PI/180F,x=Mth.cos(yaw),z=Mth.sin(yaw),tx=-z*Mth.sin(pitch),tz=x*Mth.sin(pitch),vertical=constantHeight?1:Mth.cos(pitch);
        vertex(out,px-x*scale-tx*scale,py-vertical*scale,pz-z*scale-tz*scale,u1,v1,r,g,b,alpha,light);vertex(out,px-x*scale+tx*scale,py+vertical*scale,pz-z*scale+tz*scale,u1,v0,r,g,b,alpha,light);vertex(out,px+x*scale+tx*scale,py+vertical*scale,pz+z*scale+tz*scale,u0,v0,r,g,b,alpha,light);vertex(out,px+x*scale-tx*scale,py-vertical*scale,pz+z*scale-tz*scale,u0,v1,r,g,b,alpha,light);
    }
    private static void vertex(VertexConsumer out,float x,float y,float z,float u,float v,float r,float g,float b,float alpha,boolean light){var vertex=out.addVertex(x,y,z).setUv(u,v).setColor(r,g,b,alpha);if(light)vertex.setLight(240);}
}
