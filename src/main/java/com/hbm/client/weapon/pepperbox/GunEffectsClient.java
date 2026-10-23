package com.hbm.client.weapon.pepperbox;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

/** Old EffectRenderer uses alpha>1/255, verified in official1.7.10 bytecode; native0.1 would truncate low-alpha smoke. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GunEffectsClient {
    private static ShaderInstance particleShader;
    public static ShaderInstance particleShader(){com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.setupShader(particleShader);return particleShader;}
    @SubscribeEvent public static void register(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:sedna_black_powder"),DefaultVertexFormat.PARTICLE),shader->particleShader=shader);}
    private GunEffectsClient(){}
}
