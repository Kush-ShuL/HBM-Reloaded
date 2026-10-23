package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearCraterBiomes;
import com.hbm.particle.LegacyParticles;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** The original local-player town-aura particles in the two central crater biomes. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearCraterAtmosphere {
 private NuclearCraterAtmosphere(){}
 @SubscribeEvent public static void tick(EntityTickEvent.Post event){
  var player=Minecraft.getInstance().player;if(player==null||event.getEntity()!=player)return;
  var biome=player.level().getBiome(player.blockPosition());if(!biome.is(NuclearCraterBiomes.CRATER)&&!biome.is(NuclearCraterBiomes.INNER))return;
  var random=player.getRandom();for(int i=0;i<3;i++)player.level().addParticle(LegacyParticles.AURA.get(),player.getX()+random.nextGaussian()*3,player.getY()+random.nextGaussian()*2,player.getZ()+random.nextGaussian()*3,0,0,0);
 }
}
