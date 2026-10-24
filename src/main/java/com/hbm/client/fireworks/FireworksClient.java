package com.hbm.client.fireworks;

import com.hbm.bomb.fireworks.FireworksContent;
import com.hbm.bomb.fireworks.FireworksEffects;
import com.hbm.client.weapon.accelerator.CoilFireworkTrail;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FireworksClient {
    @SubscribeEvent public static void entities(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(FireworksContent.BALL.get(),NoopRenderer::new);}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:fireworks_letter"),DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),shader->FireworksLetterTypes.normal=shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:fireworks_letter_intensity"),DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),shader->FireworksLetterTypes.intensity=shader);
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->FireworksEffects.CLIENT=FireworksClient::receive);}
    private static void receive(FireworksEffects.Effect packet){
        var client=Minecraft.getInstance();var level=client.level;if(level==null)return;
        if(packet.launch()){com.hbm.client.bomb.conventional.grenade.GrenadeParticleEffects.flame(level,packet.x(),packet.y(),packet.z());return;}
        client.particleEngine.add(new FireworksLetterParticle(level,packet.x(),packet.y(),packet.z(),packet.color(),(char)packet.character()));
        for(int i=0;i<50;i++){
            double dx=.4*level.random.nextGaussian(),dy=.4*level.random.nextGaussian(),dz=.4*level.random.nextGaussian();
            CoilFireworkTrail.spawn(packet.x(),packet.y(),packet.z(),dx,dy,dz,packet.color());
        }
    }
}
