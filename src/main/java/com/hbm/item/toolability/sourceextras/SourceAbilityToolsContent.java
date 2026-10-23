package com.hbm.item.toolability.sourceextras;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemToolAbility;
import com.hbm.item.toolability.chlorophyte.UpstreamToolTier;
import com.hbm.item.toolability.mese.MeseTier;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual eight higher siblings and their two source-required ordinary Cobalt tools. The admitted81/86 tool, preset, ability and material engines stay unique. */
public final class SourceAbilityToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    /** Existing CobaltPickaxeItem keeps a private tier; this source value object adds no tool backend or repair item. */
    private enum SourceCobaltTier implements Tier {
        INSTANCE;
        @Override public int getUses() { return 750; }
        @Override public float getSpeed() { return 9F; }
        @Override public float getAttackDamageBonus() { return 2.5F; }
        @Override public int getEnchantmentValue() { return 60; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    }
    public static final DeferredItem<ItemToolAbility> COBALT_AXE = ITEMS.register("cobalt_axe", () ->
            new ItemToolAbility("cobalt_axe", 6F, 0, SourceCobaltTier.INSTANCE, ItemToolAbility.Type.AXE,
                    new AvailableAbilities().add(RECURSION, 1).add(SILK, 0).add(LUCK, 0).add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> COBALT_SHOVEL = ITEMS.register("cobalt_shovel", () ->
            new ItemToolAbility("cobalt_shovel", 3.5F, 0, SourceCobaltTier.INSTANCE, ItemToolAbility.Type.SHOVEL,
                    new AvailableAbilities().add(RECURSION, 1).add(SILK, 0).add(LUCK, 0)));
    public static final DeferredItem<ItemToolAbility> DECORATED_COBALT_AXE = ITEMS.register("cobalt_decorated_axe", () ->
            new ItemToolAbility("cobalt_decorated_axe", 8F, 0, UpstreamToolTier.DECORATED_COBALT, ItemToolAbility.Type.AXE,
                    new AvailableAbilities().add(RECURSION, 1).add(HAMMER, 0).add(HAMMER_FLAT, 0).add(SILK, 0).add(LUCK, 2).add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> DECORATED_COBALT_SHOVEL = ITEMS.register("cobalt_decorated_shovel", () ->
            new ItemToolAbility("cobalt_decorated_shovel", 5F, 0, UpstreamToolTier.DECORATED_COBALT, ItemToolAbility.Type.SHOVEL,
                    new AvailableAbilities().add(RECURSION, 1).add(HAMMER, 0).add(HAMMER_FLAT, 0).add(SILK, 0).add(LUCK, 2)));
    public static final DeferredItem<ItemToolAbility> STARMETAL_AXE = ITEMS.register("starmetal_axe", () ->
            new ItemToolAbility("starmetal_axe", 12F, 0, UpstreamToolTier.STARMETAL, ItemToolAbility.Type.AXE,
                    new AvailableAbilities().add(RECURSION, 3).add(HAMMER, 1).add(HAMMER_FLAT, 1).add(SILK, 0).add(LUCK, 4).add(BEHEADER, 0).add(STUN, 1)));
    public static final DeferredItem<ItemToolAbility> STARMETAL_SHOVEL = ITEMS.register("starmetal_shovel", () ->
            new ItemToolAbility("starmetal_shovel", 7F, 0, UpstreamToolTier.STARMETAL, ItemToolAbility.Type.SHOVEL,
                    new AvailableAbilities().add(RECURSION, 3).add(HAMMER, 1).add(HAMMER_FLAT, 1).add(SILK, 0).add(LUCK, 4).add(STUN, 1)));
    public static final DeferredItem<ItemToolAbility> BISMUTH_AXE = ITEMS.register("bismuth_axe", () ->
            new ItemToolAbility("bismuth_axe", 25F, 0, UpstreamToolTier.BISMUTH, ItemToolAbility.Type.AXE,
                    new AvailableAbilities().add(HAMMER, 1).add(HAMMER_FLAT, 1).add(RECURSION, 1).add(SHREDDER, 0).add(LUCK, 1).add(SILK, 0)
                            .add(STUN, 3).add(VAMPIRE, 1).add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> VOLCANIC_AXE = ITEMS.register("volcanic_axe", () ->
            new ItemToolAbility("volcanic_axe", 25F, 0, UpstreamToolTier.VOLCANIC, ItemToolAbility.Type.AXE,
                    new AvailableAbilities().add(HAMMER, 1).add(HAMMER_FLAT, 1).add(RECURSION, 1).add(SMELTER, 0).add(LUCK, 2).add(SILK, 0)
                            .add(FIRE, 1).add(VAMPIRE, 1).add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> CHLOROPHYTE_AXE = ITEMS.register("chlorophyte_axe", () ->
            new ItemToolAbility("chlorophyte_axe", 50F, 0, UpstreamToolTier.CHLOROPHYTE, ItemToolAbility.Type.AXE,
                    new AvailableAbilities().add(HAMMER, 1).add(HAMMER_FLAT, 1).add(RECURSION, 1).add(LUCK, 3).add(STUN, 4).add(VAMPIRE, 3).add(BEHEADER, 0)));
    public static final DeferredItem<ItemToolAbility> MESE_AXE = ITEMS.register("mese_axe", () ->
            new ItemToolAbility("mese_axe", 75F, 0, MeseTier.INSTANCE, ItemToolAbility.Type.AXE,
                    new AvailableAbilities().add(HAMMER, 2).add(HAMMER_FLAT, 2).add(RECURSION, 2).add(SILK, 0).add(LUCK, 5).add(EXPLOSION, 3)
                            .add(STUN, 4).add(PHOSPHORUS, 1).add(BEHEADER, 0)));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(SourceAbilityToolsContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) for (ItemStack stack : indexedItems()) event.accept(stack);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private SourceAbilityToolsContent() { }
}
