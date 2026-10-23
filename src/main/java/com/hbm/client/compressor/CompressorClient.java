package com.hbm.client.compressor;
import com.hbm.machine.compressor.*;
import com.hbm.registry.HbmSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CompressorClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(CompressorContent.ENTITY.get(),CompressorRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CompressorContent.MENU.get(),CompressorScreen::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private CompressorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new CompressorItemRenderer()):renderer;}},CompressorContent.ITEM.get(),CompressorContent.COMPACT_ITEM.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->CompressorBlockEntity.CLIENT_TICK=CompressorClient::tick);}
 private static void tick(CompressorBlockEntity machine){if(machine.compact())return;machine.prevFanSpin=machine.fanSpin;machine.prevPiston=machine.piston;if(!machine.isOn)return;machine.fanSpin+=15;if(machine.fanSpin>=360){machine.prevFanSpin-=360;machine.fanSpin-=360;}if(machine.pistonDir){machine.piston-=machine.randSpeed;if(machine.piston<=0){var p=machine.getBlockPos();machine.getLevel().playLocalSound(p.getX(),p.getY(),p.getZ(),HbmSounds.get("item.boltgun"),net.minecraft.sounds.SoundSource.BLOCKS,machine.muffled?.05F:.5F,.75F,false);machine.pistonDir=!machine.pistonDir;}}else{machine.piston+=.05F;if(machine.piston>=1){machine.randSpeed=.085F+machine.getLevel().random.nextFloat()*.03F;machine.pistonDir=!machine.pistonDir;}}machine.piston=Mth.clamp(machine.piston,0,1);}
}
