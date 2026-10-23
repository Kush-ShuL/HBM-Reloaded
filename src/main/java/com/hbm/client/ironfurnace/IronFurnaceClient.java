package com.hbm.client.ironfurnace;
import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import com.hbm.client.bomb.conventional.grenade.GrenadeParticleEffects;
import com.hbm.machine.ironfurnace.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class IronFurnaceClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(IronFurnaceContent.ENTITY.get(),IronFurnaceRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(IronFurnaceContent.MENU.get(),IronFurnaceScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private IronFurnaceItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new IronFurnaceItemRenderer()):renderer;}},IronFurnaceContent.ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->IronFurnaceEntity.CLIENT_TICK=machine->{
        if(machine.progress<=0||machine.getLevel()==null)return;
        var level=machine.getLevel();var pos=machine.getBlockPos();var dir=ForgeDirection.getOrientation(machine.getBlockState().getValue(DummyableBlock.LEGACY_META)-10);var rot=dir.getRotation(ForgeDirection.UP);
        double offset=machine.progress%2==0?1:.5;
        LegacyExplosionEffects.particle("smoke",pos.getX()+.5-dir.offsetX*offset-rot.offsetX*.1875,pos.getY()+2,pos.getZ()+.5-dir.offsetZ*offset-rot.offsetZ*.1875,0,.01,0);
        if(machine.progress%5==0){double random=level.random.nextDouble();GrenadeParticleEffects.flame(level,pos.getX()+.5+dir.offsetX*.25+rot.offsetX*random,pos.getY()+.25+level.random.nextDouble()*.25,pos.getZ()+.5+dir.offsetZ*.25+rot.offsetZ*random);}
    });}
    private IronFurnaceClient(){}
}
