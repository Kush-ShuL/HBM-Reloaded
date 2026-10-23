package com.hbm.item.fertilizer;

import java.util.List;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FertilizerContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemFertilizer> FERTILIZER = ITEMS.register("powder_fertilizer", ItemFertilizer::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        bus.addListener(FertilizerContent::commonSetup);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(FERTILIZER.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                var target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                setSuccess(ItemFertilizer.useFertillizer(stack, source.level(), target.getX(), target.getY(), target.getZ()));
                // Failure retains the item rather than ejecting it; source 1000/1001
                // clicks and the regular dispense animation are native behavior.
                return stack;
            }
        }));
    }

    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(FERTILIZER.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "parts".equals(tab) ? indexedItems() : List.of(); }
    private FertilizerContent() { }
}
