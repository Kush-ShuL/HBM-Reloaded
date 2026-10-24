package com.hbm.machine.conveyorpackaging;

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
    // 每族按源等级顺序注册三个物品，创造栏和机器索引共用此表。
    private static final Map<CraneMetaUpgradeItem.Kind,List<DeferredItem<CraneMetaUpgradeItem>>> UPGRADES=registerUpgrades();
    private static Map<CraneMetaUpgradeItem.Kind,List<DeferredItem<CraneMetaUpgradeItem>>> registerUpgrades(){var result=new EnumMap<CraneMetaUpgradeItem.Kind,List<DeferredItem<CraneMetaUpgradeItem>>>(CraneMetaUpgradeItem.Kind.class);for(var kind:CraneMetaUpgradeItem.Kind.values()){var levels=new ArrayList<DeferredItem<CraneMetaUpgradeItem>>();for(int i=0;i<3;i++){int level=i;levels.add(ITEMS.register(kind.id()+"_"+(level+1),()->new CraneMetaUpgradeItem(kind,level)));}result.put(kind,List.copyOf(levels));}return Collections.unmodifiableMap(result);}
    public static CraneMetaUpgradeItem upgrade(CraneMetaUpgradeItem.Kind kind,int level){return UPGRADES.get(kind).get(level).get();}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ConveyorPackagingEntity>> BOXER_ENTITY=ENTITIES.register("crane_boxer",()->BlockEntityType.Builder.of((pos,state)->new ConveyorPackagingEntity(pos,state,false),BOXER.get()).build(null)),UNBOXER_ENTITY=ENTITIES.register("crane_unboxer",()->BlockEntityType.Builder.of((pos,state)->new ConveyorPackagingEntity(pos,state,true),UNBOXER.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ConveyorPackagingMenu>> BOXER_MENU=MENUS.register("crane_boxer",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((IContainerFactory<ConveyorPackagingMenu>)(id,inv,buf)->new ConveyorPackagingMenu(id,inv,false,buf))),UNBOXER_MENU=MENUS.register("crane_unboxer",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((IContainerFactory<ConveyorPackagingMenu>)(id,inv,buf)->new ConveyorPackagingMenu(id,inv,true,buf)));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(ConveyorPackagingControl::register);}
    public static List<ItemStack> creativeStacks(String tab){if(tab.equals("machine"))return List.of(new ItemStack(BOXER_ITEM.get()),new ItemStack(UNBOXER_ITEM.get()));if(tab.equals("control")){var values=new ArrayList<ItemStack>();for(var kind:CraneMetaUpgradeItem.Kind.values())for(int level=0;level<3;level++)values.add(new ItemStack(upgrade(kind,level)));return List.copyOf(values);}return List.of();}
    public static Map<String,Item> indexedItems(){var items=new LinkedHashMap<String,Item>();items.put("crane_boxer",BOXER_ITEM.get());items.put("crane_unboxer",UNBOXER_ITEM.get());for(var kind:CraneMetaUpgradeItem.Kind.values())for(int level=0;level<3;level++)items.put(kind.id()+"_"+(level+1),upgrade(kind,level));return Collections.unmodifiableMap(items);}
    private ConveyorPackagingContent(){}
}
