package com.hbm.machine.coolingtower;
import java.util.*;import net.minecraft.core.registries.Registries;import net.minecraft.world.item.*;import net.minecraft.world.level.block.Block;import net.minecraft.world.level.block.entity.BlockEntityType;import net.minecraft.world.level.block.state.BlockState;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.*;
public final class CoolingTowerContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> BE=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final Map<CoolingTowerKind,DeferredBlock<? extends Block>> BLOCK=new LinkedHashMap<>();public static final Map<CoolingTowerKind,DeferredItem<BlockItem>> ITEM=new LinkedHashMap<>();public static final Map<CoolingTowerKind,DeferredHolder<BlockEntityType<?>,BlockEntityType<CoolingTowerBlockEntity>>> ENTITIES=new LinkedHashMap<>();
 static {for(var kind:CoolingTowerKind.values()){var block=BLOCKS.register(kind.id,()->kind==CoolingTowerKind.CONDENSER?new CondenserBlock():new CoolingTowerBlock(kind));BLOCK.put(kind,block);ITEM.put(kind,ITEMS.registerSimpleBlockItem(kind.id,block));ENTITIES.put(kind,BE.register(kind.id,()->BlockEntityType.Builder.of(CoolingTowerBlockEntity::new,block.get()).build(null)));}}
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CoolingTowerProxy>> PROXY=BE.register("cooling_tower_proxy",()->BlockEntityType.Builder.of(CoolingTowerProxy::new,BLOCK.get(CoolingTowerKind.SMALL).get(),BLOCK.get(CoolingTowerKind.LARGE).get()).build(null));
 public static CoolingTowerKind kind(BlockState state){return state.getBlock() instanceof CoolingTowerBlock tower?tower.kind:CoolingTowerKind.CONDENSER;}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BE.register(bus);}
 public static List<ItemStack> allStacks(){return ITEM.values().stream().map(item->new ItemStack(item.get())).toList();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?allStacks():List.of();}
 private CoolingTowerContent(){}
}
