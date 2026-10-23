package com.hbm.machine.reactor.rbmk.column;

import com.hbm.advancement.HbmAdvancements;
import com.hbm.api.machine.IOverpressurable;
import com.hbm.bomb.nuclear.RbmkMushEffects;
import com.hbm.fluid.network.*;
import com.hbm.machine.reactor.rbmk.RbmkDials;
import com.hbm.machine.reactor.rbmk.core.RbmkFluxColumn;
import com.hbm.machine.reactor.rbmk.failure.DigammaSpear;
import com.hbm.machine.reactor.rbmk.residue.*;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Original connected-column melt, pressure failure, RBMK cloud and optional DRX spear; no generic nuclear substitute. */
public final class RbmkMeltdown {
    private static final Set<RbmkColumnEntity> COLUMNS = new HashSet<>();
    private static final RbmkPressureNetworkSet PIPES = new RbmkPressureNetworkSet();
    static void run(RbmkColumnEntity origin) {
        if (!(origin.getLevel() instanceof ServerLevel world)) return;
        RbmkColumnBlock.dropLids = false; COLUMNS.clear(); PIPES.clear();
        try {
            flood(world, origin.getBlockPos());
            int minX = origin.getBlockPos().getX(), maxX = minX, minZ = origin.getBlockPos().getZ(), maxZ = minZ;
            for (var column : COLUMNS) { var p = column.getBlockPos(); minX = Math.min(minX, p.getX()); maxX = Math.max(maxX, p.getX()); minZ = Math.min(minZ, p.getZ()); maxZ = Math.max(maxZ, p.getZ()); }
            for (var column : COLUMNS) {
                var p = column.getBlockPos(); int distance = Math.min(p.getX() - minX, Math.min(maxX - p.getX(), Math.min(p.getZ() - minZ, maxZ - p.getZ())));
                column.collectOverpressureNetworks(PIPES); column.onMelt(distance + 1);
            }
            for (var column : COLUMNS) if (column instanceof RbmkFluxColumn.Fuel && BuiltInRegistries.BLOCK.getKey(world.getBlockState(column.getBlockPos()).getBlock()).toString().equals("hbm:corium_block")) {
                var p = column.getBlockPos();
                for (int x = p.getX() - 1; x <= p.getX() + 1; x++) for (int y = p.getY() - 1; y <= p.getY() + 1; y++) for (int z = p.getZ() - 1; z <= p.getZ() + 1; z++) {
                    var target = new BlockPos(x, y, z); var block = world.getBlockState(target).getBlock();
                    if (world.random.nextInt(3) == 0 && (block == RbmkResidueContent.RESIDUES.get(RbmkResidueBlock.Kind.NORMAL).get() || block == RbmkResidueContent.RESIDUES.get(RbmkResidueBlock.Kind.BURNING).get()))
                        world.setBlock(target, RbmkResidueContent.RESIDUES.get(RbmkColumnBlock.digamma ? RbmkResidueBlock.Kind.DIGAMMA : RbmkResidueBlock.Kind.RADIATING).get().defaultBlockState(), 3);
                }
            }
            if (RbmkDials.getOverpressure(world) && !PIPES.isEmpty()) pressure(world);
            int small = Math.min(maxX - minX, maxZ - minZ), x = minX + (maxX - minX) / 2, z = minZ + (maxZ - minZ) / 2, y = origin.getBlockPos().getY();
            RbmkMushEffects.send(world, x + .5, y + 1, z + .5, small);
            world.playSound(null, x + .5, y + 1, z + .5, HbmSounds.get("block.rbmk_explosion"), SoundSource.BLOCKS, 50F, 1F);
            HbmAdvancements.meltdown(world, origin.getBlockPos(), "achRBMKBoom", 50);
            if (RbmkColumnBlock.digamma) { var spear = new DigammaSpear(world); spear.setPos(x + .5, y + 100, z + .5); world.addFreshEntity(spear); }
        } finally { RbmkColumnBlock.dropLids = true; RbmkColumnBlock.digamma = false; COLUMNS.clear(); PIPES.clear(); }
    }
    private static void flood(ServerLevel world, BlockPos start) {
        var work = new ArrayDeque<BlockPos>(); work.push(start);
        while (!work.isEmpty()) {
            var pos = work.pop();
            if (!world.hasChunkAt(pos) || !(world.getBlockEntity(pos) instanceof RbmkColumnEntity column) || !COLUMNS.add(column)) continue;
            // Stack reversal retains the original recursive +X,-X,+Z,-Z discovery order without an unbounded Java call stack.
            work.push(pos.north()); work.push(pos.south()); work.push(pos.west()); work.push(pos.east());
        }
    }
    private static void pressure(ServerLevel world) {
        Set<FluidNode> blocks = new HashSet<>(); Set<FluidReceiver> receivers = new HashSet<>();
        for (var net : PIPES) { blocks.addAll(PIPES.links(net)); receivers.addAll(PIPES.receivers(net)); }
        int count = 0, max = Math.min(blocks.size() / 5, 100);
        for (var node : blocks) { if (count >= max) break; for (var pos : node.positions()) if (world.getBlockEntity(pos) != null) world.removeBlock(pos, false); count++; }
        for (var receiver : receivers) if (receiver instanceof BlockEntity tile) {
            if (receiver instanceof IOverpressurable pressure) pressure.explode(world, tile.getBlockPos());
            else { world.removeBlock(tile.getBlockPos(), false); var p = tile.getBlockPos(); world.explode(null, p.getX() + .5, p.getY() + .5, p.getZ() + .5, 5F, false, Level.ExplosionInteraction.NONE); }
        }
    }
    private RbmkMeltdown() { }
}
