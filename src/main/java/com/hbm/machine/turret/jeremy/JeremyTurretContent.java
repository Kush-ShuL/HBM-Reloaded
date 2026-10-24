package com.hbm.machine.turret.jeremy;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class JeremyTurretContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final EnumMap<JeremyTurretKind,DeferredBlock<JeremyTurretBlock>> BLOCK_MAP=new EnumMap<>(JeremyTurretKind.class);private static final EnumMap<JeremyTurretKind,DeferredItem<BlockItem>> ITEM_MAP=new EnumMap<>(JeremyTurretKind.class);private static final EnumMap<JeremyTurretKind,DeferredHolder<BlockEntityType<?>,BlockEntityType<JeremyTurretEntity>>> ENTITY_MAP=new EnumMap<>(JeremyTurretKind.class);private static final EnumMap<JeremyTurretKind,DeferredHolder<MenuType<?>,MenuType<JeremyTurretMenu>>> MENU_MAP=new EnumMap<>(JeremyTurretKind.class);
    static{for(var kind:JeremyTurretKind.values()){var block=BLOCKS.register(kind.id,()->new JeremyTurretBlock(kind));BLOCK_MAP.put(kind,block);ITEM_MAP.put(kind,ITEMS.register(kind.id,()->new BlockItem(block.get(),new Item.Properties())));ENTITY_MAP.put(kind,ENTITIES.register(kind.id,()->BlockEntityType.Builder.of((pos,state)->new JeremyTurretEntity(kind,pos,state),block.get()).build(null)));MENU_MAP.put(kind,MENUS.register(kind.id,()->IMenuTypeExtension.create((id,inventory,buffer)->new JeremyTurretMenu(kind,id,inventory,buffer.readBlockPos()))));}}
    private static final List<DeferredItem<CannonAmmoItem>> AMMO_ITEMS=registerAmmo();
    private static List<DeferredItem<CannonAmmoItem>> registerAmmo(){var items=new ArrayList<DeferredItem<CannonAmmoItem>>();for(int i=0;i<CannonAmmoItem.NAMES.size();i++){int kind=i;items.add(ITEMS.register(CannonAmmoItem.NAMES.get(i),()->new CannonAmmoItem(kind)));}return List.copyOf(items);}
    public static CannonAmmoItem ammo(int sourceOrdinal){return AMMO_ITEMS.get(sourceOrdinal).get();}
    public static List<CannonAmmoItem> ammoItems(){return AMMO_ITEMS.stream().map(holder->holder.get()).toList();}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<JeremyTurretProxy>> PROXY=ENTITIES.register("jeremy_turret_proxy",()->BlockEntityType.Builder.of(JeremyTurretProxy::new,BLOCK_MAP.values().stream().map(DeferredBlock::get).toArray(net.minecraft.world.level.block.Block[]::new)).build(null));
    public static BlockEntityType<JeremyTurretEntity> entity(JeremyTurretKind kind){return ENTITY_MAP.get(kind).get();}public static MenuType<JeremyTurretMenu> menu(JeremyTurretKind kind){return MENU_MAP.get(kind).get();}public static net.minecraft.world.level.block.Block block(JeremyTurretKind kind){return BLOCK_MAP.get(kind).get();}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event)->event.enqueueWork(JeremyBallistics::init));}public static List<ItemStack> creativeStacks(String tab){if(!"weapon".equals(tab))return List.of();var stacks=new ArrayList<ItemStack>();for(var kind:JeremyTurretKind.values())stacks.add(new ItemStack(ITEM_MAP.get(kind).get()));for(var ammo:ammoItems())stacks.add(new ItemStack(ammo));return stacks;}
    private JeremyTurretContent(){}
}
