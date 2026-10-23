package com.hbm.railway.track;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Exact eleven source block/item registrations. Source null tab and commented recipes remain hidden/unmanufacturable. */
public final class SourceRailContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> BES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final Map<SourceRailKind,DeferredBlock<SourceRailBlock>> RAILS=new LinkedHashMap<>();
    public static final Map<SourceRailKind,DeferredItem<BlockItem>> RAIL_ITEMS=new LinkedHashMap<>();
    static {for(var kind:SourceRailKind.values()){var rail=BLOCKS.register(kind.id,()->new SourceRailBlock(kind));RAILS.put(kind,rail);RAIL_ITEMS.put(kind,ITEMS.registerSimpleBlockItem(rail));}}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RailSwitchEntity>> SWITCH_ENTITY=BES.register("rail_switch",()->BlockEntityType.Builder.of(RailSwitchEntity::new,RAILS.get(SourceRailKind.SWITCH).get(),RAILS.get(SourceRailKind.SWITCH_FLIPPED).get()).build(null));
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    public static List<ItemStack> indexedItems(){return RAIL_ITEMS.values().stream().map(item->new ItemStack(item.get())).toList();}
    private SourceRailContent(){}
}
