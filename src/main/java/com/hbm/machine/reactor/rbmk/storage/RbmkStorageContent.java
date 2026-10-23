package com.hbm.machine.reactor.rbmk.storage;
import com.hbm.multiblock.Multiblocks;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;
public final class RbmkStorageContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RbmkStorageBlock> BLOCK=BLOCKS.register("rbmk_storage",RbmkStorageBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkStorageEntity>> ENTITY=ENTITIES.register("rbmk_storage",()->BlockEntityType.Builder.of(RbmkStorageEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<RbmkStorageMenu>> MENU=MENUS.register("rbmk_storage",()->IMenuTypeExtension.create(RbmkStorageMenu::new));
    public static void register(IEventBus bus){Multiblocks.addInventoryProxyBlock(BLOCK);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener((RegisterCapabilitiesEvent event)->event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side)));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private RbmkStorageContent(){}
}
