package com.hbm.fluid.interop.client;

import com.hbm.fluid.FluidInteropConfig;
import com.hbm.fluid.interop.FluidInterop;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FluidInteropClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        if(!FluidInteropConfig.enabled())return;
        for(var identity:FluidInterop.identities())event.registerFluidType(new IClientFluidTypeExtensions(){
            @Override public ResourceLocation getStillTexture(){return identity.sprite();}
            @Override public ResourceLocation getFlowingTexture(){return identity.sprite();}
            @Override public int getTintColor(){return 0xFF000000|(identity.nativeType().renderWithTint?identity.nativeType().getTint():0xFFFFFF);}
        },identity.type().get());
    }
    private FluidInteropClient(){}
}
