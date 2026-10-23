package com.hbm.machine.reactor.rbmk.control;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class RbmkControlsContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final EnumMap<RbmkControlKind,DeferredBlock<RbmkControlBlock>> BLOCKS_BY_KIND=new EnumMap<>(RbmkControlKind.class);
    public static final EnumMap<RbmkControlKind,DeferredItem<BlockItem>> ITEMS_BY_KIND=new EnumMap<>(RbmkControlKind.class);
    static{for(var kind:RbmkControlKind.values()){var block=BLOCKS.register(kind.id,()->new RbmkControlBlock(kind));BLOCKS_BY_KIND.put(kind,block);ITEMS_BY_KIND.put(kind,ITEMS.registerSimpleBlockItem(block));}}
    private static net.minecraft.world.level.block.Block[] blocks(){return BLOCKS_BY_KIND.values().stream().map(DeferredBlock::get).toArray(net.minecraft.world.level.block.Block[]::new);}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkControlEntity>> ENTITY=ENTITIES.register("rbmk_control",()->BlockEntityType.Builder.of(RbmkControlEntity::new,blocks()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkControlPort>> PORT=ENTITIES.register("rbmk_control_port",()->BlockEntityType.Builder.of(RbmkControlPort::new,blocks()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RbmkControlMenu>> MENU=MENUS.register("rbmk_control",()->IMenuTypeExtension.create(RbmkControlMenu::new));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);RbmkControlPacket.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?ITEMS_BY_KIND.values().stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private RbmkControlsContent(){}
}
