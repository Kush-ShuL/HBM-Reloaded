package com.hbm.client.network.cargo;
import com.hbm.client.network.rtty.RadioTorchBakedModel;
import com.hbm.network.cargo.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CargoDroneClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{CargoVisuals.smoke=(x,y,z,vx,vy,vz)->com.hbm.client.bomb.nuclear.LegacyExplosionEffects.particle("smoke",x,y,z,vx,vy,vz);CargoVisuals.line=CargoDroneEffects::add;CargoVisuals.dust=pos->{var level=Minecraft.getInstance().level;if(level!=null)level.addParticle(DustParticleOptions.REDSTONE,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,0,0,0);};ItemProperties.register(CargoDroneContent.DRONE.get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->CargoDroneContent.DRONE.get().ordinal(stack));});}
 @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(CargoDroneContent.PATROL_ENTITY.get(),CargoDroneRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CargoDroneContent.CRATE_MENU.get(),DroneCrateScreen::new);}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{if(!id.id().equals(ResourceLocation.parse("hbm:drone_waypoint"))||!id.getVariant().startsWith("legacy_meta="))return model;var sprite=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/drone_waypoint")));return new RadioTorchBakedModel(model,"drone_waypoint",Integer.parseInt(id.getVariant().substring(12)),ignored->sprite,entity->false);});}
}
