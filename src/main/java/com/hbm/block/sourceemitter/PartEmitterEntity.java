package com.hbm.block.sourceemitter;

import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

/** Four real source modes, using the existing exact gas-flame/tower packet/particle implementations. */
public final class PartEmitterEntity extends BlockEntity {
    public int effect;
    public PartEmitterEntity(BlockPos pos, BlockState state) { super(SourceEmittersContent.PARTICLE_ENTITY.get(), pos, state); }
    public static void tick(Level level, BlockPos pos, BlockState state, PartEmitterEntity emitter) {
        if (!(level instanceof ServerLevel server)) return;
        if (emitter.effect == 1) {
            // Same world RNG evaluation order: x/y/z uniform, then x/z Gaussian. One particle each tick.
            LegacyFluidEffects.gasFlame(server, pos.getX() + server.random.nextDouble(), pos.getY() + 4.5 + server.random.nextDouble(),
                    pos.getZ() + server.random.nextDouble(), server.random.nextGaussian() * .2, .1, server.random.nextGaussian() * .2);
            return;
        }
        double x = pos.getX() + .5, y = pos.getY() + .5, z = pos.getZ() + .5;
        LegacyFluidEffects.Tower tower;
        if (emitter.effect == 2) tower = new LegacyFluidEffects.Tower(5, .25F, 5, 560 + server.random.nextInt(20)).color(0x404040);
        else if (emitter.effect == 3) {
            tower = new LegacyFluidEffects.Tower(.5F, 1, 10, 750 + server.random.nextInt(250));
            x = pos.getX() + .5 + server.random.nextDouble() * 3 - 1.5; y = pos.getY() + 1;
            z = pos.getZ() + .5 + server.random.nextDouble() * 3 - 1.5;
        } else return;
        var packet = new LegacyFluidEffects.Packet(false, x, y, z, 0, 0, 0, 0, tower);
        // Source tower distribution is anchored at the integer block, not the jittered particle position.
        for (var player : server.players()) if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 150D * 150)
            PacketDistributor.sendToPlayer(player, packet);
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.saveAdditional(tag, lookup); tag.putInt("effect", effect); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) { super.loadAdditional(tag, lookup); effect = tag.getInt("effect"); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag = super.getUpdateTag(lookup); tag.putInt("effect", effect); return tag; }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
}
