package com.hbm.client.bomb.nuclear.fallout;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

/** Fixed-function RenderFallout texture/lightmap/alpha/fog, using the project's one source world pass. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FalloutRainClient {
    private static ShaderInstance shader;
    static ShaderInstance shader(){return shader;}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:fallout_rain341"),DefaultVertexFormat.PARTICLE),loaded->shader=loaded);
    }
    private FalloutRainClient(){}
}
