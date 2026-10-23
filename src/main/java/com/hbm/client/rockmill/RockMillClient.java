package com.hbm.client.rockmill;
import com.hbm.machine.rockmill.*;
import com.hbm.recipe.generic.Blueprints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RockMillClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RockMillEntity.CLIENT_TICK=RockMillClient::tick);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RockMillContent.MENU.get(),RockMillScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RockMillContent.ENTITY.get(),RockMillRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RockMillItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new RockMillItemRenderer();return renderer;}},RockMillContent.ITEM.get());}
 private static void tick(RockMillEntity mill){var world=mill.getLevel();if(world==null)return;var pos=mill.getBlockPos();mill.prevRotation=mill.rotation;mill.rotationSpeed=net.minecraft.util.Mth.clamp(mill.rotationSpeed+.1F*(mill.didProcess?1:-1),0,15);mill.rotation+=mill.rotationSpeed;if(mill.rotation>=360){mill.prevRotation-=360;mill.rotation-=360;}if(world.getGameTime()%20==0)mill.frame=!world.isEmptyBlock(pos.above(3));var player=Minecraft.getInstance().player;if(mill.didProcess&&player!=null&&player.distanceToSqr(pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5)<1225){var state=Blocks.GRAVEL.defaultBlockState();var recipe=mill.module.getRecipe();if(recipe!=null&&recipe.icon().getItem() instanceof BlockItem block){var id=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block.getBlock()).toString();state=com.hbm.world.structure.LegacyStructureStates.resolve(id,Blueprints.metadata(recipe.icon()),0);}double angle=world.random.nextDouble()*Math.PI*2,dx=Math.cos(angle),dz=-Math.sin(angle);world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK,state),pos.getX()+.5+dx*2.25,pos.getY()+1.5,pos.getZ()+.5+dz*2.25,dx*.125,-.1,dz*.125);}}
}
