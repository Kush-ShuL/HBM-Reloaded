package com.hbm.block.ore;

import java.util.List;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Original basalt and resource-stone metadata behavior, including strict raw metadata tests. */
public final class ResourceOreBlock extends LegacyOreBlock {
    private static final String[] BASALT_DROPS = {"sulfur", "fluorite", "ingot_asbestos", "gem_volcanic", "powder_molysite"};
    private final MapCodec<ResourceOreBlock> resourceCodec;
    public ResourceOreBlock(OreSpec spec) { super(spec); resourceCodec = simpleCodec(ignored -> new ResourceOreBlock(spec)); }
    @Override protected MapCodec<? extends LegacyOreBlock> codec() { return resourceCodec; }
    private boolean basalt() { return spec.id().equals("ore_basalt"); }
    private boolean silk(Level level, ItemStack tool) {
        return EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), tool) > 0;
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null) tool = ItemStack.EMPTY;
        if (silk(context.getLevel(), tool)) return super.getDrops(state, context);
        ItemStack drop;
        if (basalt()) drop = new ItemStack(HbmContent.item(BASALT_DROPS[spec.ordinal(state.getValue(LEGACY_META))]));
        else if (state.getValue(LEGACY_META) == 3) {
            int fortune = EnchantmentHelper.getItemEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), tool);
            drop = com.hbm.item.orechunk.FlatOreChunkContent.stack(1, 3 + fortune + context.getLevel().random.nextInt(fortune + 2)); // Source EnumChunkType.MALACHITE
        } else return super.getDrops(state, context);
        Float radius = context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        return radius != null && context.getLevel().random.nextFloat() > 1F / radius ? List.of() : List.of(drop);
    }
    private boolean releasesAsbestos(BlockState state) { return state.getValue(LEGACY_META) == (basalt() ? 2 : 1); }
    @Override public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity entity, ItemStack tool) {
        if (!level.isClientSide && releasesAsbestos(state) && !silk(level, tool)) level.setBlock(pos, HbmContent.block("gas_asbestos").defaultBlockState(), 3);
        super.playerDestroy(level, player, pos, state, entity, tool);
    }
    @Override protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean experience) {
        if (releasesAsbestos(state) && !silk(level, tool)) level.setBlock(pos, HbmContent.block("gas_asbestos").defaultBlockState(), 3);
        super.spawnAfterBreak(state, level, pos, tool, experience);
    }
    @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (basalt() && state.getValue(LEGACY_META) == 2) OutgassingOreBlock.asbestosWalk(level, pos);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) { }
}
