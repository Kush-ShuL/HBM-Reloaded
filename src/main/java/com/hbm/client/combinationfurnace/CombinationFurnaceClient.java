package com.hbm.client.combinationfurnace;
import com.hbm.machine.combinationfurnace.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CombinationFurnaceClient {
    static ShaderInstance FIRE;
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:combination_furnace184_fire"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->FIRE=shader);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(CombinationFurnaceContent.ENTITY.get(),CombinationFurnaceRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CombinationFurnaceContent.MENU.get(),CombinationFurnaceScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private CombinationFurnaceItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new CombinationFurnaceItemRenderer()):renderer;}},CombinationFurnaceContent.ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->CombinationFurnaceEntity.CLIENT_TICK=machine->{var level=machine.getLevel();if(level!=null&&machine.wasOn&&level.random.nextInt(15)==0){var pos=machine.getBlockPos();level.addParticle(ParticleTypes.LAVA,pos.getX()+.5+level.random.nextGaussian()*.5,pos.getY()+2,pos.getZ()+.5+level.random.nextGaussian()*.5,0,0,0);}});}
    private CombinationFurnaceClient(){}
}
