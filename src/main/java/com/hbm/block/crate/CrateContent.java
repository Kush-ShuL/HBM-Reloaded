package com.hbm.block.crate;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source ammo crate is a stationary block, not one of BlockCrate's falling variants. */
public final class CrateContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<AmmoCrateBlock> AMMO=BLOCKS.register("crate_ammo",AmmoCrateBlock::new);
    public static final DeferredItem<BlockItem> AMMO_ITEM=ITEMS.registerSimpleBlockItem("crate_ammo",AMMO);
    public static final DeferredItem<CrowbarItem> CROWBAR=ITEMS.register("crowbar",CrowbarItem::new);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);bus.addListener(CrateContent::vanillaCreative);}
    private static void vanillaCreative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))event.accept(CROWBAR.get());}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(AMMO_ITEM.get())):List.of();}
    private CrateContent(){}
}
