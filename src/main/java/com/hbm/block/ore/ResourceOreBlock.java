package com.hbm.block.ore;

import java.util.List;
import java.util.Map;
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

/** Fixed source minerals; gas, Silk Touch, Fortune and explosion RNG remain on this backend. */
public final class ResourceOreBlock extends Block {
    public static final Map<String,List<String>> TYPES = Map.of(
            "ore_basalt", List.of("sulfur","fluorite","asbestos","gem","molysite"),
            "stone_resource", List.of("sulfur","asbestos","hematite","malachite","limestone","bauxite"));
    private static final String[] BASALT_DROPS = {"sulfur", "fluorite", "ingot_asbestos", "gem_volcanic", "powder_molysite"};
    private final String family;
    private final int ordinal;
    private final MapCodec<ResourceOreBlock> resourceCodec;

    public ResourceOreBlock(OreSpec spec) {
        super(spec.properties());
        family = familyOf(spec.id());
        if (family == null) throw new IllegalArgumentException("Unknown fixed resource ore " + spec.id());
        ordinal = TYPES.get(family).indexOf(spec.id().substring(family.length() + 1));
        resourceCodec = simpleCodec(ignored -> new ResourceOreBlock(spec));
    }
    public static String familyOf(String id) {
        String path = id.startsWith("hbm:") ? id.substring(4) : id;
        for (var row : TYPES.entrySet())
            if (path.startsWith(row.getKey() + "_") && row.getValue().contains(path.substring(row.getKey().length() + 1))) return row.getKey();
        return null;
    }
    public static String sourceId(String family, int ordinal) {
        var names = TYPES.get(family);
        if (names == null || ordinal < 0 || ordinal >= names.size()) throw new IllegalArgumentException("Unknown resource ore " + family + ":" + ordinal);
        return family + "_" + names.get(ordinal);
    }
    public String descriptionId() { return "block.hbm." + family + (basalt() ? "_" : ".") + TYPES.get(family).get(ordinal); }
    @Override public String getDescriptionId() { return descriptionId(); }
    @Override protected MapCodec<? extends ResourceOreBlock> codec() { return resourceCodec; }
    private boolean basalt() { return family.equals("ore_basalt"); }
    private boolean silk(Level level, ItemStack tool) {
        return EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), tool) > 0;
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null) tool = ItemStack.EMPTY;
        // The original silk branch bypasses ordinary drop/explosion evaluation.
        if (silk(context.getLevel(), tool)) return List.of(new ItemStack(this));
        ItemStack drop;
        if (basalt()) drop = new ItemStack(HbmContent.item(BASALT_DROPS[ordinal]));
        else if (ordinal == 3) {
            int fortune = EnchantmentHelper.getItemEnchantmentLevel(context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), tool);
            drop = com.hbm.item.orechunk.FlatOreChunkContent.stack(1, 3 + fortune + context.getLevel().random.nextInt(fortune + 2)); // Source EnumChunkType.MALACHITE
        } else drop = new ItemStack(this);
        Float radius = context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        return radius != null && context.getLevel().random.nextFloat() > 1F / radius ? List.of() : List.of(drop);
    }
    private boolean releasesAsbestos() { return ordinal == (basalt() ? 2 : 1); }
    @Override public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity entity, ItemStack tool) {
        if (!level.isClientSide && releasesAsbestos() && !silk(level, tool)) level.setBlock(pos, HbmContent.block("gas_asbestos").defaultBlockState(), 3);
        super.playerDestroy(level, player, pos, state, entity, tool);
    }
    @Override protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean experience) {
        if (releasesAsbestos() && !silk(level, tool)) level.setBlock(pos, HbmContent.block("gas_asbestos").defaultBlockState(), 3);
        super.spawnAfterBreak(state, level, pos, tool, experience);
    }
    @Override public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (basalt() && ordinal == 2) OutgassingOreBlock.asbestosWalk(level, pos);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) { }
}
