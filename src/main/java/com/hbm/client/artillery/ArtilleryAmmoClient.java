package com.hbm.client.artillery;
import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import com.hbm.weapon.artillery.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ArtilleryAmmoClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(ArtilleryAmmoContent.SHELL.get(),ArtilleryShellRenderer::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ItemProperties.register(ArtilleryAmmoContent.AMMO.get(),ResourceLocation.parse("hbm:legacy_meta"),(stack,world,living,seed)->ArtilleryAmmoItem.metadata(stack));ItemProperties.register(ArtilleryAmmoContent.AMMO.get(),ResourceLocation.parse("hbm:arty_cargo"),(stack,world,living,seed)->ArtilleryAmmoItem.hasCargo(stack)?1:0);ArtilleryShellEntity.clientSmoke=shell->{var client=Minecraft.getInstance();if(client.gameRenderer.getMainCamera().getPosition().distanceToSqr(shell.getX(),shell.getY()+.5,shell.getZ())<=256)LegacyExplosionEffects.particle("smoke",shell.getX(),shell.getY()+.5,shell.getZ(),0,.1,0);};});}
}
