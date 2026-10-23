package com.hbm.client.entity.pigeon;

import com.hbm.entity.mob.pigeon.PigeonContent;
import com.hbm.entity.mob.pigeon.PigeonEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PigeonClient {
    private static final java.util.Random VISUAL_RANDOM=new java.util.Random();
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(PigeonContent.PIGEON.get(),PigeonRenderer::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->PigeonEffects.CLIENT=PigeonClient::sweat);}
    private static void sweat(PigeonEffects.Sweat packet){
        var client=Minecraft.getInstance();if(client.level==null||!(client.level.getEntity(packet.entity()) instanceof LivingEntity entity))return;
        var box=entity.getBoundingBox();
        for(int i=0;i<3;i++){
            double x=box.minX-.2+(box.maxX-box.minX+.4)*VISUAL_RANDOM.nextDouble(),y=box.minY+(box.maxY-box.minY+.2)*VISUAL_RANDOM.nextDouble(),z=box.minZ-.2+(box.maxZ-box.minZ+.4)*VISUAL_RANDOM.nextDouble();
            var particle=client.particleEngine.createParticle(new BlockParticleOption(ParticleTypes.BLOCK,Blocks.WHITE_WOOL.defaultBlockState()),x,y,z,0,0,0);
            int lifetime=150+VISUAL_RANDOM.nextInt(50);if(particle!=null)particle.setLifetime(lifetime);
        }
    }
}
