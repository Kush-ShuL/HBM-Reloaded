package com.hbm.machine.turret.rotary;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class RotaryTurretContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final EnumMap<RotaryTurretKind,DeferredBlock<RotaryTurretBlock>> BLOCK_MAP=new EnumMap<>(RotaryTurretKind.class);private static final EnumMap<RotaryTurretKind,DeferredItem<BlockItem>> ITEM_MAP=new EnumMap<>(RotaryTurretKind.class);private static final EnumMap<RotaryTurretKind,DeferredHolder<BlockEntityType<?>,BlockEntityType<RotaryTurretEntity>>> ENTITY_MAP=new EnumMap<>(RotaryTurretKind.class);private static final EnumMap<RotaryTurretKind,DeferredHolder<MenuType<?>,MenuType<RotaryTurretMenu>>> MENU_MAP=new EnumMap<>(RotaryTurretKind.class);
    static{for(var kind:RotaryTurretKind.values()){var block=BLOCKS.register(kind.id,()->new RotaryTurretBlock(kind));BLOCK_MAP.put(kind,block);ITEM_MAP.put(kind,ITEMS.register(kind.id,()->new BlockItem(block.get(),new Item.Properties())));ENTITY_MAP.put(kind,ENTITIES.register(kind.id,()->BlockEntityType.Builder.of((pos,state)->new RotaryTurretEntity(kind,pos,state),block.get()).build(null)));MENU_MAP.put(kind,MENUS.register(kind.id,()->IMenuTypeExtension.create((id,inventory,buffer)->new RotaryTurretMenu(kind,id,inventory,buffer.readBlockPos()))));}}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RotaryTurretProxy>> PROXY=ENTITIES.register("rotary_turret_proxy",()->BlockEntityType.Builder.of(RotaryTurretProxy::new,BLOCK_MAP.values().stream().map(DeferredBlock::get).toArray(net.minecraft.world.level.block.Block[]::new)).build(null));
    public static BlockEntityType<RotaryTurretEntity> entity(RotaryTurretKind kind){return ENTITY_MAP.get(kind).get();}public static MenuType<RotaryTurretMenu> menu(RotaryTurretKind kind){return MENU_MAP.get(kind).get();}public static net.minecraft.world.level.block.Block block(RotaryTurretKind kind){return BLOCK_MAP.get(kind).get();}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}public static List<ItemStack> creativeStacks(String tab){return "weapon".equals(tab)?Arrays.stream(RotaryTurretKind.values()).map(kind->new ItemStack(ITEM_MAP.get(kind).get())).toList():List.of();}
    private RotaryTurretContent(){}
}
