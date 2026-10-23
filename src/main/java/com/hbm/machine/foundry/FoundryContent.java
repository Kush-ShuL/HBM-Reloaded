package com.hbm.machine.foundry;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.*;
public final class FoundryContent {
    public enum Kind{CHANNEL,TANK,OUTLET,SLAGTAP,MOLD,BASIN,SLAG;public String id(){return this==SLAG?"slag":"foundry_"+name().toLowerCase(Locale.ROOT);}public boolean cast(){return this==MOLD||this==BASIN;}public boolean outlet(){return this==OUTLET||this==SLAGTAP;}}
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final Map<Kind,DeferredBlock<FoundryBlock>> BLOCK_MAP=new LinkedHashMap<>();public static final Map<Kind,DeferredItem<BlockItem>> ITEM_MAP=new LinkedHashMap<>();
    static{for(Kind kind:Kind.values()){var block=BLOCKS.register(kind.id(),()->new FoundryBlock(kind));BLOCK_MAP.put(kind,block);if(kind!=Kind.SLAG)ITEM_MAP.put(kind,ITEMS.registerSimpleBlockItem(kind.id(),block));}}
    public static final DeferredItem<com.hbm.machine.anvil.MoldItem> MOLD=com.hbm.machine.anvil.AnvilContent.MOLD;
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FoundryBlockEntity>> ENTITY=ENTITIES.register("foundry",()->BlockEntityType.Builder.of(FoundryBlockEntity::new,BLOCK_MAP.values().stream().map(DeferredBlock::get).toArray(FoundryBlock[]::new)).build(null));
    public static FoundryBlock block(Kind kind){return BLOCK_MAP.get(kind).get();}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(FoundryContent::capabilities);bus.addListener(FoundryStreamPacket::register);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(foundry,side)->foundry.kind().cast()?new SidedInvWrapper(foundry,side==null?net.minecraft.core.Direction.UP:side):null);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("machine"))return List.of();var result=new ArrayList<ItemStack>();ITEM_MAP.values().forEach(item->result.add(new ItemStack(item.get())));return result;}
    private FoundryContent(){}
}
