package com.hbm.armor.neutrinolens;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.item.surveyscanner.SurveyScannerMarker;
import com.hbm.registry.HbmContent;
import com.hbm.satellite.SatelliteFrequency;
import com.hbm.satellite.SatelliteSavedData;
import com.hbm.satellite.types.SatelliteScanner;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Actual source depth-scanner lens. The real orbit/frequency and sole source marker queue remain authoritative. */
public final class ItemModLens extends ArmorModItem implements SatelliteFrequency {
    private record Target(Block block, int chance, String label, int color) { }
    private static List<Target> targets;
    private static List<Target> targets() {
        if (targets == null) targets = List.of(
            new Target(HbmContent.block("ore_alexandrite"), 1, "marker.hbm.lens.alexandrite", 0x00ffff),
            new Target(HbmContent.block("ore_oil"), 300, "marker.hbm.lens.oil", 0xa0a0a0),
            new Target(HbmContent.block("ore_bedrock_oil"), 300, "marker.hbm.lens.bedrock_oil", 0xa0a0a0),
            new Target(HbmContent.block("ore_coltan"), 5, "marker.hbm.lens.coltan", 0xa0a000),
            new Target(HbmContent.block("stone_gneiss"), 5000, "marker.hbm.lens.schist", 0x8080ff),
            new Target(HbmContent.block("ore_australium"), 1000, "marker.hbm.lens.australium", 0xffff00),
            new Target(Blocks.END_PORTAL_FRAME, 1, "marker.hbm.lens.end_portal", 0x40b080),
            new Target(HbmContent.block("volcano_core"), 1, "marker.hbm.lens.volcano_core", 0xff4000),
            new Target(NeutrinoLensContent.PINK_LOG.get(), 1, "marker.hbm.lens.pink_log", 0xff00ff),
            new Target(HbmContent.block("bobblehead"), 1, "marker.hbm.lens.treasure", 0xff0000),
            new Target(HbmContent.block("deco_loot"), 1, "", 0x800000),
            new Target(HbmContent.block("crate_ammo"), 1, "", 0x800000),
            new Target(HbmContent.block("crate_can"), 1, "", 0x800000),
            new Target(HbmContent.block("ore_bedrock"), 1, "marker.hbm.lens.bedrock_ore", 0xff0000));
        return targets;
    }
    public ItemModLens() { super(ArmorModStorage.EXTRA, true, false, false, false); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.neutrino_lens.name"); }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!(entity instanceof ServerPlayer player)) return;
        var level = player.serverLevel();
        var lens = ArmorModStorage.pryMod(armor, ArmorModStorage.EXTRA, player.registryAccess());
        if (lens.isEmpty() || !(SatelliteSavedData.getData(level).getSatFromFreq(getFreq(lens)) instanceof SatelliteScanner)) return;
        int x = Mth.floor(player.getX()), y = Mth.floor(player.getY()), z = Mth.floor(player.getZ());
        int height = Math.max(Math.min(y + 10, 255), 64);
        int seg = (int) (level.getGameTime() % height), hits = 0;
        var pos = new BlockPos.MutableBlockPos();
        var orderedTargets = targets();
        for (int chunkX = (x >> 4) - 3; chunkX <= (x >> 4) + 3; chunkX++) {
            for (int chunkZ = (z >> 4) - 3; chunkZ <= (z >> 4) + 3; chunkZ++) {
                var chunk = level.getChunk(chunkX, chunkZ);
                for (int ix = 0; ix < 16; ix++) for (int iz = 0; iz < 16; iz++) {
                    pos.set((chunkX << 4) + ix, seg, (chunkZ << 4) + iz);
                    var block = chunk.getBlockState(pos).getBlock();
                    for (var target : orderedTargets) {
                        // Source short-circuit: nextInt is consumed only for the exact matching block,
                        // including nextInt(1). Do not add arbitrary host equivalences or pre-roll RNG.
                        if (target.block == block && player.getRandom().nextInt(target.chance) == 0) {
                            SurveyScannerMarker.send(player, pos.immutable(), target.color, 15_000, 300D, target.label);
                            hits++;
                        }
                    }
                    if (hits > 100) return;
                }
            }
        }
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.neutrino_lens.installed", stack.getHoverName(),
                Component.translatable("item.neutrino_lens.description.frequency"), getFreq(stack)).withStyle(ChatFormatting.AQUA);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.neutrino_lens.frequency", Component.translatable("satchip.frequency"), getFreq(stack)).withStyle(ChatFormatting.AQUA));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
