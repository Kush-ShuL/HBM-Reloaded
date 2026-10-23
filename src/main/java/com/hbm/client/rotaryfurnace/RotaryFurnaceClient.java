package com.hbm.client.rotaryfurnace;
import com.hbm.machine.rotaryfurnace.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RotaryFurnaceClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->RotaryFurnaceEntity.CLIENT_TICK=RotaryFurnaceClient::tick);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RotaryFurnaceContent.MENU.get(),RotaryFurnaceScreen::new);}@SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RotaryFurnaceContent.ENTITY.get(),RotaryFurnaceRenderer::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RotaryFurnaceItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new RotaryFurnaceItemRenderer();return renderer;}},RotaryFurnaceContent.ITEM.get());}
 private static void tick(RotaryFurnaceEntity furnace){var world=furnace.getLevel();if(world==null)return;var pos=furnace.getBlockPos();var dir=DummyableBlock.facing(furnace.getBlockState());var rot=RotaryFurnaceEntity.turnDown(dir);var player=Minecraft.getInstance().player;if(furnace.burnTime>0&&player!=null&&player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<625){world.addParticle(ParticleTypes.FLAME,pos.getX()+.5+dir.getStepX()*.5+rot.getStepX()+world.random.nextGaussian()*.25,pos.getY()+.375,pos.getZ()+.5+dir.getStepZ()*.5+rot.getStepZ()+world.random.nextGaussian()*.25,0,0,0);}if(furnace.isVenting&&world.getGameTime()%2==0)LegacyFluidEffects.tower(world,pos.getX()+.5+rot.getStepX(),pos.getY()+5,pos.getZ()+.5+rot.getStepZ(),new LegacyFluidEffects.Tower(10F,.25F,2.5F,100+world.random.nextInt(20)).color(0x202020));furnace.lastAnim=furnace.anim;if(furnace.isProgressing)furnace.anim+=(int)Math.max(RotaryFurnaceConfig.BURN.getMod(furnace.slots.get(4),RotaryFurnaceConfig.BURN.getModHeat()),1);}
}
