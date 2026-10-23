package com.hbm.client.steelfurnace;
import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import com.hbm.machine.steelfurnace.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SteelFurnaceClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(SteelFurnaceContent.ENTITY.get(),SteelFurnaceRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(SteelFurnaceContent.MENU.get(),SteelFurnaceScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private SteelFurnaceItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new SteelFurnaceItemRenderer()):renderer;}},SteelFurnaceContent.ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->SteelFurnaceEntity.CLIENT_TICK=machine->{
        if(!machine.wasOn||machine.getLevel()==null)return;var level=machine.getLevel();var p=machine.getBlockPos();var dir=ForgeDirection.getOrientation(machine.getBlockState().getValue(DummyableBlock.LEGACY_META)-10);var rot=dir.getRotation(ForgeDirection.UP);
        LegacyExplosionEffects.particle("smoke",p.getX()+.5-dir.offsetX*1.125-rot.offsetX*.75,p.getY()+2.625,p.getZ()+.5-dir.offsetZ*1.125-rot.offsetZ*.75,0,.05,0);
        if(level.random.nextInt(20)==0)level.addParticle(ParticleTypes.CLOUD,p.getX()+.5+dir.offsetX*.75,p.getY()+2,p.getZ()+.5+dir.offsetZ*.75,0,.05,0);
        if(level.random.nextInt(15)==0)level.addParticle(ParticleTypes.LAVA,p.getX()+.5+dir.offsetX*1.5+rot.offsetX*(level.random.nextDouble()-.5),p.getY()+.75,p.getZ()+.5+dir.offsetZ*1.5+rot.offsetZ*(level.random.nextDouble()-.5),dir.offsetX*.5,.05,dir.offsetZ*.5);
    });}
    private SteelFurnaceClient(){}
}
