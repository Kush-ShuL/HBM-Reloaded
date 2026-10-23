package com.hbm.client.poweredcondenser;
import com.hbm.machine.poweredcondenser.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PoweredCondenserClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(PoweredCondenserContent.ENTITY.get(),PoweredCondenserRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private PoweredCondenserItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new PoweredCondenserItemRenderer()):renderer;}},PoweredCondenserContent.ITEM.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->PoweredCondenserEntity.clientTick=PoweredCondenserClient::tick);}
 private static void tick(PoweredCondenserEntity machine){var level=machine.getLevel();if(level==null||machine.waterTimer<=0||level.getGameTime()%4!=0)return;var p=machine.getBlockPos();var dir=DummyableBlock.facing(machine.getBlockState());level.addParticle(ParticleTypes.CLOUD,p.getX()+.5+dir.getStepX()*1.5,p.getY()+1.5,p.getZ()+.5+dir.getStepZ()*1.5,dir.getStepX()*.1,0,dir.getStepZ()*.1);level.addParticle(ParticleTypes.CLOUD,p.getX()+.5-dir.getStepX()*1.5,p.getY()+1.5,p.getZ()+.5-dir.getStepZ()*1.5,dir.getStepX()*-.1,0,dir.getStepZ()*-.1);}
}
