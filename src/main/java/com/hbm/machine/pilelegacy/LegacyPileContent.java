package com.hbm.machine.pilelegacy;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** All original legacy channels and seven ItemPileRod identities remain hidden, exactly as source tab=null. */
public final class LegacyPileContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final EnumMap<LegacyPileKind,DeferredBlock<LegacyGraphiteBlock>> CHANNELS=new EnumMap<>(LegacyPileKind.class);
    public static final EnumMap<LegacyPileKind,DeferredItem<BlockItem>> CHANNEL_ITEMS=new EnumMap<>(LegacyPileKind.class);
    public static final Map<String,DeferredItem<LegacyPileRodItem>> RODS=new LinkedHashMap<>();
    static {
        for(var kind:LegacyPileKind.values()) {
            var block=BLOCKS.register(kind.id(),()->kind==LegacyPileKind.FUEL?new LegacyGraphiteEntityBlock.Fuel():kind.hasEntity()?new LegacyGraphiteEntityBlock(kind):new LegacyGraphiteBlock(kind));
            CHANNELS.put(kind,block);CHANNEL_ITEMS.put(kind,ITEMS.registerSimpleBlockItem(block));
        }
        for(String rod:List.of("uranium","pu239","plutonium","source","boron","lithium","detector"))RODS.put(rod,ITEMS.register("pile_rod_"+rod,()->new LegacyPileRodItem(rod)));
    }
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LegacyPileFuelEntity>> FUEL=ENTITIES.register("pile_legacy_fuel",()->BlockEntityType.Builder.of(LegacyPileFuelEntity::new,CHANNELS.get(LegacyPileKind.FUEL).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LegacyPileSourceEntity>> SOURCE=ENTITIES.register("pile_legacy_source",()->BlockEntityType.Builder.of(LegacyPileSourceEntity::new,CHANNELS.get(LegacyPileKind.SOURCE).get(),CHANNELS.get(LegacyPileKind.PLUTONIUM).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LegacyPileBreedingEntity>> BREEDING=ENTITIES.register("pile_legacy_breedingfuel",()->BlockEntityType.Builder.of(LegacyPileBreedingEntity::new,CHANNELS.get(LegacyPileKind.LITHIUM).get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LegacyPileDetectorEntity>> DETECTOR=ENTITIES.register("pile_legacy_neutrondetector",()->BlockEntityType.Builder.of(LegacyPileDetectorEntity::new,CHANNELS.get(LegacyPileKind.DETECTOR).get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(LegacyPileSmoke::register);LegacyPileNeutrons.register();}
    public static List<ItemStack> allStacks(){var result=new ArrayList<ItemStack>();CHANNEL_ITEMS.values().forEach(item->result.add(new ItemStack(item.get())));RODS.values().forEach(item->result.add(new ItemStack(item.get())));return result;}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    private LegacyPileContent(){}
}
