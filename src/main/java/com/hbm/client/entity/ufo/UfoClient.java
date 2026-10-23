package com.hbm.client.entity.ufo;
import com.hbm.client.bomb.nuclear.MukeCloud;
import com.hbm.entity.mob.ufo.*;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
/** Source AuxParticlePacketNT ufo branch, retaining the original zero motion and two gaussian draws. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class UfoClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerEntityRenderer(UfoContent.UFO.get(),UfoRenderer::new);}
 public static void groundCloud(UfoGroundCloud packet){var minecraft=Minecraft.getInstance();var world=minecraft.level;if(world==null)return;double x=world.random.nextGaussian()*0D,z=world.random.nextGaussian()*0D;minecraft.particleEngine.add(new MukeCloud(world,packet.x(),packet.y(),packet.z(),x,0,z,false));}
 private UfoClient(){}
}
