package com.hbm.client.drain;
import com.hbm.client.particle.*;
import com.hbm.machine.drain.*;
import com.hbm.multiblock.DummyableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class DrainClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->DrainEntity.CLIENT_TICK=DrainClient::particles);}
 @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(DrainContent.ENTITY.get(),DrainRenderer::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private DrainItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new DrainItemRenderer()):renderer;}},DrainContent.ITEM.get());}
 private static void particles(DrainEntity drain){
  if(!(drain.getLevel() instanceof ClientLevel level))return;
  // Consume at most one actual positive-state observation, even when later empty packets arrived first.
  var type=drain.consumeClientEmissionType();var mc=Minecraft.getInstance();
  if(type==null||mc.player==null||mc.player.distanceToSqr(drain.getBlockPos().getX(),drain.getBlockPos().getY(),drain.getBlockPos().getZ())>=10000)return;
  var dir=DummyableBlock.facing(drain.getBlockState());
  double x=drain.getBlockPos().getX()+.5-dir.getStepX()*2.5,y=drain.getBlockPos().getY()+.5,z=drain.getBlockPos().getZ()+.5-dir.getStepZ()*2.5;
  int color=type.getColor(),setting=mc.options.particles().get().getId();
  if(type.hasTrait("gaseous")){
   int life=100+level.random.nextInt(50);
   // Source ClientProxy.effectNT applies the same ALL/DECREASED/MINIMAL gate to tower and splash.
   if(setting!=0&&(setting!=1||!level.random.nextBoolean()))return;
   var particle=new DrainSmokeParticle(level,x,y,z);particle.setLift(.5F);particle.setBaseScale(.375F);particle.setMaxScale(3);particle.setLife(life/(setting+1));
   particle.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F);mc.particleEngine.add(particle);
  }else if(setting==0||setting==1&&level.random.nextBoolean())mc.particleEngine.add(new DrainSplashParticle(level,x,y,z,color));
 }
 @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
 public static final class WorldStage {
  @SubscribeEvent public static void stage(RenderLevelStageEvent event){if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_SKY)DrainSmokeParticle.resetFrame();}
  @SubscribeEvent public static void unload(net.neoforged.neoforge.event.level.LevelEvent.Unload event){if(event.getLevel().isClientSide())DrainSmokeParticle.resetFrame();}
 }

}
