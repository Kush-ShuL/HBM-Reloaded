package com.hbm.machine.turret.howard;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class HowardTurretContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final EnumMap<HowardTurretKind,DeferredBlock<HowardTurretBlock>> BLOCK_MAP=new EnumMap<>(HowardTurretKind.class);private static final EnumMap<HowardTurretKind,DeferredItem<BlockItem>> ITEM_MAP=new EnumMap<>(HowardTurretKind.class);private static final EnumMap<HowardTurretKind,DeferredHolder<BlockEntityType<?>,BlockEntityType<HowardTurretEntity>>> ENTITY_MAP=new EnumMap<>(HowardTurretKind.class);private static final EnumMap<HowardTurretKind,DeferredHolder<MenuType<?>,MenuType<HowardTurretMenu>>> MENU_MAP=new EnumMap<>(HowardTurretKind.class);
    static{for(var kind:HowardTurretKind.values()){var block=BLOCKS.register(kind.id,()->new HowardTurretBlock(kind));BLOCK_MAP.put(kind,block);ITEM_MAP.put(kind,ITEMS.register(kind.id,()->new BlockItem(block.get(),new Item.Properties())));ENTITY_MAP.put(kind,ENTITIES.register(kind.id,()->BlockEntityType.Builder.of((pos,state)->new HowardTurretEntity(kind,pos,state),block.get()).build(null)));if(!kind.damaged)MENU_MAP.put(kind,MENUS.register(kind.id,()->IMenuTypeExtension.create((id,inventory,buffer)->new HowardTurretMenu(kind,id,inventory,buffer.readBlockPos()))));}}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HowardTurretProxy>> PROXY=ENTITIES.register("howard_turret_proxy",()->BlockEntityType.Builder.of(HowardTurretProxy::new,BLOCK_MAP.get(HowardTurretKind.HOWARD).get()).build(null));
    public static BlockEntityType<HowardTurretEntity> entity(HowardTurretKind kind){return ENTITY_MAP.get(kind).get();}public static MenuType<HowardTurretMenu> menu(HowardTurretKind kind){return MENU_MAP.get(kind).get();}public static net.minecraft.world.level.block.Block block(HowardTurretKind kind){return BLOCK_MAP.get(kind).get();}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event)->event.enqueueWork(HowardBallistics::init));}public static List<ItemStack> creativeStacks(String tab){return "weapon".equals(tab)?Arrays.stream(HowardTurretKind.values()).map(kind->new ItemStack(ITEM_MAP.get(kind).get())).toList():List.of();}
    private HowardTurretContent(){}
}
