package com.hbm.client.oilflare;
import com.hbm.fluid.FluidTraits;
import com.hbm.client.bomb.nuclear.FlareSmokeParticle;
import com.hbm.machine.oilflare.*;
import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
/** Source client-side vent tower and no-clip 50tick smoke; gasfire stays the sole existing server payload. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FlareClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(FlareContent.ENTITY.get(),FlareRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(FlareContent.MENU.get(),FlareScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private FlareItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new FlareItemRenderer()):renderer;}},FlareContent.ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->FlareEntity.clientTick=FlareClient::tick);}
    private static void tick(FlareEntity f){if(!f.isOn||f.tank.getFill()<=0)return;var level=f.getLevel();var pos=f.getBlockPos();var type=f.tank.getTankType();boolean burns=type.hasTrait(FluidTraits.Flammable.class);if((!f.doesBurn||!burns)&&FlareEntity.gaseous(type))LegacyFluidEffects.tower(level,pos.getX()+.5,pos.getY()+11,pos.getZ()+.5,new LegacyFluidEffects.Tower(1,.25F,3,150+level.random.nextInt(20)).color(type.getColor()));var minecraft=Minecraft.getInstance();if(f.doesBurn&&burns&&minecraft.player!=null&&minecraft.player.distanceToSqr(pos.getX(),pos.getY()+10,pos.getZ())<=1024){boolean even=level.getGameTime()%2==0;minecraft.particleEngine.add(new FlareSmokeParticle(minecraft.level,pos.getX()+(even?1.5:1.125),pos.getY()+(even?10.75:11.75),pos.getZ()+(even?1.5:-.5)));}}
}
