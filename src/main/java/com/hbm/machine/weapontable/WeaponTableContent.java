package com.hbm.machine.weapontable;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
/** Source table has a temporary per-player container, not a block entity inventory. */
public final class WeaponTableContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<WeaponTableBlock> BLOCK=BLOCKS.register("machine_weapon_table",WeaponTableBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<MenuType<?>,MenuType<WeaponTableMenu>> MENU=MENUS.register("weapon_table",()->IMenuTypeExtension.create(WeaponTableMenu::new));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);MENUS.register(bus);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?indexedItems():List.of();}
    private WeaponTableContent(){}
}
