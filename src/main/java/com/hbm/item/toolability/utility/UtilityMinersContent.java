package com.hbm.item.toolability.utility;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.SteelTier;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.legacymelee.LegacyMeleeMaterials;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import java.util.List;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class UtilityMinersContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    /** Real ModItems4232 material, not an invented ability or mining engine. */
    private enum DwarvenTier implements Tier {
        INSTANCE;
        @Override public int getUses() { return 0; }
        @Override public float getSpeed() { return 4F; }
        @Override public float getAttackDamageBonus() { return 0F; }
        @Override public int getEnchantmentValue() { return 10; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(HbmContent.item("ingot_copper")); }
    }
    public static final DeferredItem<UtilityMinerItem> DWARVEN = ITEMS.register("dwarven_pickaxe", () ->
        new UtilityMinerItem("dwarven_pickaxe", 5F, -.1, DwarvenTier.INSTANCE, 250,
            new AvailableAbilities().add(HAMMER, 0).add(HAMMER_FLAT, 0)));
    public static final DeferredItem<UtilityMinerItem> SMASHING_HAMMER = ITEMS.register("smashing_hammer", () ->
        new UtilityMinerItem("smashing_hammer", 12F, -.1, SteelTier.INSTANCE, 2500, new AvailableAbilities().add(SHREDDER, 0)));
    public static final DeferredItem<UtilityMinerItem> CENTRIFUGE_STICK = ITEMS.register("centri_stick", () ->
        new UtilityMinerItem("centri_stick", 3F, 0, LegacyMeleeMaterials.ELECTRIC, 50, new AvailableAbilities().add(CENTRIFUGE, 0)));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(UtilityMinersContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private UtilityMinersContent() { }
}
