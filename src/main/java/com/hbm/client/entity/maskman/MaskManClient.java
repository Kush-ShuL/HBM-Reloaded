package com.hbm.client.entity.maskman;
import com.hbm.entity.mob.maskman.*;import com.hbm.entity.projectile.EntityBulletBaseNT;import net.minecraft.core.particles.*;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MaskManClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(MaskManContent.MASKMAN.get(),MaskManRenderer::new);event.registerEntityRenderer(MaskManContent.NT_BULLET.get(),NpcBulletRenderer::new);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{EntityBulletBaseNT.CLIENT_BEFORE_UPDATE=MaskManClient::before;EntityBulletBaseNT.CLIENT_UPDATE=MaskManClient::effects;});}
 @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){event.register(CloudDashClient.DASH);}
 private static void before(EntityBulletBaseNT bullet){var cfg=bullet.config();var world=bullet.level();var random=world.random;if(cfg==LegacyNpcBullets.METEOR)for(int i=0;i<5;i++)world.addParticle(ParticleTypes.FLAME,bullet.getX()+random.nextDouble()*.5-.25,bullet.getY()+random.nextDouble()*.5-.25,bullet.getZ()+random.nextDouble()*.5-.25,0,0,0);}
 private static void effects(EntityBulletBaseNT bullet){var cfg=bullet.config();var world=bullet.level();var random=world.random;if(!cfg.particles.isEmpty()){var delta=bullet.position().subtract(bullet.xo,bullet.yo,bullet.zo);double length=Math.max(delta.length(),.1);delta=delta.normalize();for(double d=0;d<length;d+=.5){var pos=bullet.position().subtract(delta.scale(d));if(cfg.particles.equals("reddust"))world.addParticle(DustParticleOptions.REDSTONE,pos.x,pos.y,pos.z,0,0,0);else world.addParticle(ParticleTypes.SMOKE,pos.x,pos.y,pos.z,0,0,0);}}}
 private MaskManClient(){}
}
