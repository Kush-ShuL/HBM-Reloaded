package com.hbm.block.sourceemitter;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.bomb.nuclear.NuclearPlasmaBlastEffects;
import com.hbm.multiblock.MuffledMachine;
import java.awt.Color;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source color/girth/effect and transient beam; only a cold read scan waits through the sole FULL lease. */
public final class EmitterEntity extends BlockEntity implements MuffledMachine {
    public int color, beam, effect;
    public float girth = .5F;
    public boolean muffled, tilted;
    private NuclearChunkAccess access;
    private int scan, scanMeta;
    private CompoundTag lastSent;
    public EmitterEntity(BlockPos pos, BlockState state) { super(SourceEmittersContent.LIGHT_ENTITY.get(), pos, state); }
    public static void tick(Level level, BlockPos pos, BlockState state, EmitterEntity emitter) {
        if (!(level instanceof ServerLevel server)) return;
        emitter.serverTick(server);
    }
    private void serverTick(ServerLevel server) {
        int meta = getBlockState().getValue(EmitterBlock.META);
        if (scan > 0 && scanMeta != meta) closeScan();
        if (scan == 0 && server.getGameTime() % 20 == 0) {
            scan = 1; scanMeta = meta; access = new NuclearChunkAccess(); SourceEmittersContent.OPEN_SCANS.add(this);
        }
        if (scan > 0) {
            try {
                int dx = dx(scanMeta), dy = dy(scanMeta), dz = dz(scanMeta);
                while (scan <= 100) {
                    var pos = worldPosition.offset(dx * scan, dy * scan, dz * scan);
                    var chunk = access.get(server, pos.getX() >> 4, pos.getZ() >> 4);
                    if (chunk == null) break;
                    // Official1.7 Block.isBlockSolid(IBlockAccess,x,y,z,side) returns Material.isSolid.
                    // Do not use normalCube, face-sturdy, opacity, collision or a new ray algorithm.
                    if (LegacyMaterials.of(chunk.getBlockState(pos)).isSolid() || scan == 100) {
                        beam = scan; closeScan(); break;
                    }
                    scan++;
                }
            } catch (RuntimeException | Error failure) { closeScan(); throw failure; }
        }
        if (effect == 4 && beam > 0 && server.getGameTime() % 5 == 0) {
            // Source parentheses and signed remainder are deliberate: no abs/floorMod or replacement motion.
            double x = (int) (worldPosition.getX() + dx(meta) * (server.getGameTime() / 5L) % beam) + .5;
            double y = (int) (worldPosition.getY() + dy(meta) * (server.getGameTime() / 5L) % beam) + .5;
            double z = (int) (worldPosition.getZ() + dz(meta) * (server.getGameTime() / 5L) % beam) + .5;
            int rgb = color == 0 ? Color.HSBtoRGB(server.getGameTime() / 50.0F, .5F, .25F) & 0xffffff : color;
            float pitch = meta == 2 || meta == 4 ? 90 : meta == 3 || meta == 5 ? -90 : 0;
            float yaw = meta == 4 || meta == 5 ? 90 : 0;
            NuclearPlasmaBlastEffects.send(server, x, y, z, (rgb >> 16 & 255) / 256F, (rgb >> 8 & 255) / 256F,
                    (rgb & 255) / 256F, girth * 5, pitch, yaw, 100);
        }
        var tag = getUpdateTag(server.registryAccess());
        if (!tag.equals(lastSent) || server.getGameTime() % 20 == 0) {
            lastSent = tag.copy(); var packet = getUpdatePacket();
            for (var player : server.players())
                if (player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < 150D * 150) player.connection.send(packet);
        }
    }
    public static int dx(int meta) { return meta == 4 ? -1 : meta == 5 ? 1 : 0; }
    public static int dy(int meta) { return meta == 0 ? -1 : meta == 1 ? 1 : 0; }
    public static int dz(int meta) { return meta == 2 ? -1 : meta == 3 ? 1 : 0; }
    void closeScan() {
        try { if (access != null) access.close(); }
        finally { access = null; scan = 0; SourceEmittersContent.OPEN_SCANS.remove(this); }
    }
    @Override public void onChunkUnloaded() { closeScan(); super.onChunkUnloaded(); }
    @Override public void setRemoved() { closeScan(); super.setRemoved(); }
    @Override public boolean isMuffled() { return muffled; }
    @Override public void setMuffled(boolean value) { muffled = value; }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup); tag.putInt("color", color); tag.putFloat("girth", girth); tag.putInt("effect", effect);
        tag.putBoolean("muffled", muffled); tag.putBoolean("tilted", tilted);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup); color = tag.getInt("color"); girth = tag.getFloat("girth"); effect = tag.getInt("effect");
        muffled = tag.getBoolean("muffled"); tilted = tag.getBoolean("tilted");
        if (tag.contains("beam")) beam = tag.getInt("beam");
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        var tag = super.getUpdateTag(lookup); saveAdditional(tag, lookup); tag.putInt("beam", beam); return tag;
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
}
