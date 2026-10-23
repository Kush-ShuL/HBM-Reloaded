package com.hbm.machine.reactor.watz;

import com.hbm.item.BasicBlockItem;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Exactly the source six-block Watz family, with no second registration for its true fuel/tool/effect dependencies. */
public final class WatzContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<WatzStruct> STRUCT=BLOCKS.register("struct_watz_core",WatzStruct::new);
    public static final DeferredBlock<Block> ELEMENT=BLOCKS.register("watz_element",()->new Block(Block.Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops())),COOLER=BLOCKS.register("watz_cooler",()->new Block(Block.Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final DeferredBlock<WatzCasing> CASING=BLOCKS.register("watz_end",WatzCasing::new);
    public static final DeferredBlock<WatzBlock> REACTOR=BLOCKS.register("watz",WatzBlock::new);public static final DeferredBlock<WatzPump> PUMP=BLOCKS.register("watz_pump",WatzPump::new);
    public static final List<DeferredItem<? extends BlockItem>> ITEMS_ALL=List.of(item("struct_watz_core",STRUCT,"BlockWatzStruct"),item("watz_element",ELEMENT,"BlockPillar"),item("watz_cooler",COOLER,"BlockPillar"),ITEMS.register("watz_end",WatzCasingItem::new),item("watz",REACTOR,"Watz"),item("watz_pump",PUMP,"WatzPump"));
    private static DeferredItem<BasicBlockItem> item(String id,DeferredBlock<? extends Block> block,String source){return ITEMS.register(id,()->new BasicBlockItem(block.get(),new Item.Properties(),source,id));}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WatzStructEntity>> STRUCT_ENTITY=ENTITIES.register("watz_struct",()->BlockEntityType.Builder.of(WatzStructEntity::new,STRUCT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WatzEntity>> ENTITY=ENTITIES.register("watz",()->BlockEntityType.Builder.of(WatzEntity::new,REACTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WatzPort>> PORT=ENTITIES.register("watz_port",()->BlockEntityType.Builder.of(WatzPort::new,REACTOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WatzPumpEntity>> PUMP_ENTITY=ENTITIES.register("watz_pump",()->BlockEntityType.Builder.of(WatzPumpEntity::new,PUMP.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<WatzMenu>> MENU=MENUS.register("watz",()->new MenuType<>((IContainerFactory<WatzMenu>)WatzMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(WatzControl::register);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("machine"))return List.of();var result=new ArrayList<ItemStack>();for(var item:ITEMS_ALL)result.add(new ItemStack(item.get()));result.add(WatzCasing.stack(1,1));return result;}
    private WatzContent(){}
}
