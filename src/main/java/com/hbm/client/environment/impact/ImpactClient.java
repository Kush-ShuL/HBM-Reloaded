package com.hbm.client.environment.impact;
import com.hbm.environment.impact.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ImpactClient {
    private static ShaderInstance skyShader;
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:impact_sky"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->skyShader=shader);}
    public static ShaderInstance skyShader(){return skyShader;}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){ImpactSync.bindClient(packet->{var level=Minecraft.getInstance().level;if(level!=null)ImpactState.receive(level,packet);});}
    private ImpactClient(){}
}
