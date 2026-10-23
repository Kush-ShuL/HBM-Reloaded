package com.hbm.client.blastfurnace;

import com.hbm.machine.blastfurnace.*;
import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BlastFurnaceClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->BlastFurnaceBlockEntity.CLIENT_TICK=BlastFurnaceClient::tick);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(BlastFurnaceContent.MENU.get(),BlastFurnaceScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(BlastFurnaceContent.ENTITY.get(),BlastFurnaceRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private BlastFurnaceItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new BlastFurnaceItemRenderer();return renderer;}},BlastFurnaceContent.ITEM.get());}
    private static void tick(BlastFurnaceBlockEntity furnace){var level=furnace.getLevel();var pos=furnace.getBlockPos();if(level==null||!furnace.isProgressing||level.getGameTime()%2!=0||!level.isEmptyBlock(pos.above(7)))return;var random=level.random;level.addParticle(ParticleTypes.LAVA,pos.getX()+.25+random.nextDouble()*.5,pos.getY()+7.25,pos.getZ()+.25+random.nextDouble()*.5,0,0,0);var player=Minecraft.getInstance().player;if(furnace.tanks[1].getFill()>=1000&&player!=null&&player.distanceToSqr(pos.getX()+.5,pos.getY()+7,pos.getZ()+.5)<10000)LegacyFluidEffects.tower(level,pos.getX()+.5,pos.getY()+7,pos.getZ()+.5,new LegacyFluidEffects.Tower(10,.25F,2.5F,100+random.nextInt(20)).color(0x202020));}
}
