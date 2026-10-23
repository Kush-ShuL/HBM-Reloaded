package com.hbm.client.particle;
import com.hbm.item.tool.WeldingParticlePacket;
import net.minecraft.client.Minecraft;
public final class WeldingEffectsClient {
    public static void receive(WeldingParticlePacket packet){var mc=Minecraft.getInstance();var level=mc.level;if(level==null)return;var random=level.random;for(int i=0;i<packet.count();i++)mc.particleEngine.add(new LegacySparkParticle(level,packet.x(),packet.y(),packet.z(),random.nextGaussian()*.05,.05,random.nextGaussian()*.05).makeSmall(packet.small()));mc.particleEngine.add(new LegacyHadronParticle(level,packet.x(),packet.y(),packet.z()).makeSmall(packet.small()));}
    private WeldingEffectsClient(){}
}
