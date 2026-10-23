package com.hbm.machine.conveyorpackaging;

import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Two actual source hosts and the shared genuine three-level ItemMetaUpgrade families. */
public final class ConveyorPackagingContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<ConveyorPackagingBlock> BOXER=BLOCKS.register("crane_boxer",()->new ConveyorPackagingBlock(false)),UNBOXER=BLOCKS.register("crane_unboxer",()->new ConveyorPackagingBlock(true));
    public static final DeferredItem<CranePackagingItem> BOXER_ITEM=ITEMS.register("crane_boxer",()->new CranePackagingItem(BOXER.get(),"crane_boxer")),UNBOXER_ITEM=ITEMS.register("crane_unboxer",()->new CranePackagingItem(UNBOXER.get(),"crane_unboxer"));
    public static final DeferredItem<CraneMetaUpgradeItem> STACK=ITEMS.register("upgrade_stack",()->new CraneMetaUpgradeItem("upgrade_stack")),EJECTOR=ITEMS.register("upgrade_ejector",()->new CraneMetaUpgradeItem("upgrade_ejector"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ConveyorPackagingEntity>> BOXER_ENTITY=ENTITIES.register("crane_boxer",()->BlockEntityType.Builder.of((pos,state)->new ConveyorPackagingEntity(pos,state,false),BOXER.get()).build(null)),UNBOXER_ENTITY=ENTITIES.register("crane_unboxer",()->BlockEntityType.Builder.of((pos,state)->new ConveyorPackagingEntity(pos,state,true),UNBOXER.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ConveyorPackagingMenu>> BOXER_MENU=MENUS.register("crane_boxer",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((IContainerFactory<ConveyorPackagingMenu>)(id,inv,buf)->new ConveyorPackagingMenu(id,inv,false,buf))),UNBOXER_MENU=MENUS.register("crane_unboxer",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((IContainerFactory<ConveyorPackagingMenu>)(id,inv,buf)->new ConveyorPackagingMenu(id,inv,true,buf)));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(ConveyorPackagingControl::register);}
    public static List<ItemStack> creativeStacks(String tab){if(tab.equals("machine"))return List.of(new ItemStack(BOXER_ITEM.get()),new ItemStack(UNBOXER_ITEM.get()));if(tab.equals("control")){var values=new ArrayList<ItemStack>();for(var item:List.of(STACK.get(),EJECTOR.get()))for(int i=0;i<3;i++){var stack=new ItemStack(item);stack.set(HbmHazards.LEGACY_METADATA.get(),i);values.add(stack);}return List.copyOf(values);}return List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("crane_boxer",BOXER_ITEM.get(),"crane_unboxer",UNBOXER_ITEM.get(),"upgrade_stack",STACK.get(),"upgrade_ejector",EJECTOR.get());}
    private ConveyorPackagingContent(){}
}
