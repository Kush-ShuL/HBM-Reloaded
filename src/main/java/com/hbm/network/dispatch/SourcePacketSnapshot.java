package com.hbm.network.dispatch;

import com.hbm.bomb.conventional.*;
import com.hbm.bomb.conventional.grenade.*;
import com.hbm.bomb.fireworks.FireworksEffects;
import com.hbm.bomb.nuclear.*;
import com.hbm.disease.DiseaseVisuals;
import com.hbm.entity.mob.hunterchopper.HunterChopperEffects;
import com.hbm.entity.mob.pigeon.PigeonEffects;
import com.hbm.entity.mob.ufo.UfoGroundCloud;
import com.hbm.equipment.jetpack.JetpackNetwork;
import com.hbm.explosion.ExplosionVisuals;
import com.hbm.exposure.HbmExposure;
import com.hbm.item.boltgun.EngineeringBoltgunPackets;
import com.hbm.item.medicine.MedicineDisease;
import com.hbm.item.medicine.MedicineVisuals;
import com.hbm.item.powernettool.PowerNetToolNotice;
import com.hbm.item.redroom.ShieldInfusion;
import com.hbm.item.surveyscanner.SurveyScannerMarker;
import com.hbm.item.tool.WeldingParticlePacket;
import com.hbm.machine.foundry.FoundryStreamPacket;
import com.hbm.machine.pilelegacy.LegacyPileSmoke;
import com.hbm.machine.reactor.pwr.PwrPackets;
import com.hbm.machine.reactor.rbmk.boiler.RbmkBoilerEffects;
import com.hbm.machine.reactor.rbmk.failure.RbmkFailureEffects;
import com.hbm.machine.reactor.zirnox.ZirnoxFlameEffect;
import com.hbm.machine.turret.TurretEffects;
import com.hbm.machine.woodprocessing.WoodEffects;
import com.hbm.missile.completion.volcano.VolcanoSmokeEffects;
import com.hbm.network.cargo.CargoEffectPacket;
import com.hbm.network.debug.NetworkPortDebug;
import com.hbm.particle.LegacyFluidEffects;
import com.hbm.weapon.sedna.death.DeathParticlePacket;
import com.hbm.weapon.sedna.gun.GunNetwork;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import com.hbm.weapon.specialvehicle.VehicleShockEffects;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.payload.SyncAttachmentsPayload;

/** Positive source packet-pool correspondence; every other native packet keeps its original path. */
public final class SourcePacketSnapshot {
    private SourcePacketSnapshot() {}

    /** Called only on the actual sending thread, before any work is handed to the pool. */
    public static Packet<?> freeze(MinecraftServer server, Packet<?> packet) {
        if (packet instanceof ClientboundBlockEntityDataPacket state) {
            return SourceLoadedBlockEntityTypes.contains(state.getType())
                    ? copy(server, state, ClientboundBlockEntityDataPacket.STREAM_CODEC) : null;
        }
        if (!(packet instanceof ClientboundCustomPayloadPacket custom)) return null;
        var payload=custom.payload();
        if (payload instanceof SyncAttachmentsPayload state) {
            // Initial single-type sync has the SAME structure as periodic sync: producer context is required.
            if (!PacketDispatch.isAttachmentUpdate(packet)
                    || !(state.target() instanceof SyncAttachmentsPayload.EntityTarget target)
                    || state.types().size()!=1) return null;
            var type=state.types().getFirst();
            if (type!=HbmExposure.DATA.get() && type!=ShieldInfusion.DATA.get() && type!=MedicineDisease.DATA.get()) return null;
            return new ClientboundCustomPayloadPacket(new SyncAttachmentsPayload(
                    new SyncAttachmentsPayload.EntityTarget(target.entity()),List.of(type),state.syncPayload().clone()));
        }
        // AuxParticlePacketNT -> the actual existing typed consumer and its existing native codec.
        if (payload instanceof RbmkMushEffects value) return effect(server,value,RbmkMushEffects.CODEC);
        if (payload instanceof NuclearPlasmaBlastEffects value) return effect(server,value,NuclearPlasmaBlastEffects.CODEC);
        if (payload instanceof NuclearMukeEffects value) return effect(server,value,NuclearMukeEffects.CODEC);
        if (payload instanceof WoodEffects value) return effect(server,value,WoodEffects.CODEC);
        if (payload instanceof UfoGroundCloud value) return effect(server,value,UfoGroundCloud.CODEC);
        if (payload instanceof JetpackNetwork.Exhaust value) return effect(server,value,JetpackNetwork.Exhaust.CODEC);
        if (payload instanceof PigeonEffects.Sweat value) return effect(server,value,PigeonEffects.Sweat.CODEC);
        if (payload instanceof VolcanoSmokeEffects value) return effect(server,value,VolcanoSmokeEffects.CODEC);
        if (payload instanceof LegacyFluidEffects.Packet value) return effect(server,value,LegacyFluidEffects.Packet.CODEC);
        if (payload instanceof FireworksEffects.Effect value) return effect(server,value,FireworksEffects.Effect.CODEC);
        if (payload instanceof HunterChopperEffects value) return effect(server,value,HunterChopperEffects.CODEC);
        if (payload instanceof NetworkPortDebug value) return effect(server,value,NetworkPortDebug.CODEC);
        if (payload instanceof SmokeBurstEffects value) return effect(server,value,SmokeBurstEffects.CODEC);
        if (payload instanceof OilDripEffects value) return effect(server,value,OilDripEffects.CODEC);
        if (payload instanceof LargeConventionalEffects value) return effect(server,value,LargeConventionalEffects.CODEC);
        if (payload instanceof GrenadeParticleEffects value) return effect(server,value,GrenadeParticleEffects.CODEC);
        if (payload instanceof CargoEffectPacket value) return effect(server,value,CargoEffectPacket.CODEC);
        if (payload instanceof FlameEffects value) return effect(server,value,FlameEffects.CODEC);
        if (payload instanceof FoamSplashEffects value) return effect(server,value,FoamSplashEffects.CODEC);
        if (payload instanceof ConventionalEffects value) return effect(server,value,ConventionalEffects.CODEC);
        if (payload instanceof FoundryStreamPacket value) return effect(server,value,FoundryStreamPacket.CODEC);
        if (payload instanceof WeldingParticlePacket value) return effect(server,value,WeldingParticlePacket.CODEC);
        if (payload instanceof VehicleShockEffects value) return effect(server,value,VehicleShockEffects.CODEC);
        if (payload instanceof LegacyPileSmoke value) return effect(server,value,LegacyPileSmoke.CODEC);
        if (payload instanceof ZirnoxFlameEffect value) return effect(server,value,ZirnoxFlameEffect.CODEC);
        if (payload instanceof RbmkBoilerEffects value) return effect(server,value,RbmkBoilerEffects.CODEC);
        if (payload instanceof PwrPackets.Marker value) return effect(server,value,PwrPackets.Marker.CODEC);
        if (payload instanceof SurveyScannerMarker value) return effect(server,value,SurveyScannerMarker.CODEC);
        if (payload instanceof DeathParticlePacket value) return effect(server,value,DeathParticlePacket.CODEC);
        if (payload instanceof CasingEffects.Packet value) return effect(server,value,CasingEffects.Packet.CODEC);
        if (payload instanceof GunNetwork.BlackPowder value) return effect(server,value,GunNetwork.BlackPowder.CODEC);
        if (payload instanceof TurretEffects.Packet value) return effect(server,value,TurretEffects.Packet.CODEC);
        if (payload instanceof PowerNetToolNotice value) return effect(server,value,PowerNetToolNotice.CODEC);
        if (payload instanceof ExplosionVisuals value) return value.effect()==ExplosionVisuals.AMAT
                ? effect(server,value,ExplosionVisuals.CODEC) : null;
        if (payload instanceof DiseaseVisuals.Effect value) return value.mode()>=DiseaseVisuals.NORMAL && value.mode()<=DiseaseVisuals.SMOKE
                ? effect(server,value,DiseaseVisuals.Effect.CODEC) : null;
        if (payload instanceof MedicineVisuals.Effect value) return value.kind()>=1 && value.kind()<=3
                ? effect(server,value,MedicineVisuals.Effect.CODEC) : null;
        if (payload instanceof EngineeringBoltgunPackets value) return value.slot()==-1
                ? effect(server,value,EngineeringBoltgunPackets.CODEC) : null;
        if (payload instanceof RbmkFailureEffects.Packet value) return value.flame()
                ? effect(server,value,RbmkFailureEffects.Packet.CODEC) : null;
        return SourceLoadedTypedSnapshots.freeze(server,payload);
    }

    private static <T extends CustomPacketPayload> Packet<?> effect(MinecraftServer server,T value,
            StreamCodec<? super RegistryFriendlyByteBuf,T> codec) {
        return new ClientboundCustomPayloadPacket(copy(server,value,codec));
    }

    static <T> T copy(MinecraftServer server,T value,StreamCodec<? super RegistryFriendlyByteBuf,T> codec) {
        var buffer=new RegistryFriendlyByteBuf(Unpooled.buffer(),server.registryAccess(),ConnectionType.NEOFORGE);
        try {
            codec.encode(buffer,value);
            return codec.decode(buffer);
        } finally {
            buffer.release();
        }
    }
}
