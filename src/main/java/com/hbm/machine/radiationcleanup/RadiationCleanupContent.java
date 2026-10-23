package com.hbm.machine.radiationcleanup;

import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class RadiationCleanupContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<AbsorberBlock> ABSORBER=BLOCKS.register("rad_absorber",AbsorberBlock::new);
    public static final DeferredBlock<DeconBlock> DECON=BLOCKS.register("decon",DeconBlock::new);
    public static final DeferredItem<AbsorberItem> ABSORBER_ITEM=ITEMS.register("rad_absorber",()->new AbsorberItem(ABSORBER.get()));
    public static final DeferredItem<BlockItem> DECON_ITEM=ITEMS.registerSimpleBlockItem("decon",DECON);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DeconBlockEntity>> DECON_ENTITY=ENTITIES.register("decon",()->BlockEntityType.Builder.of(DeconBlockEntity::new,DECON.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
    public static ItemStack absorberStack(int meta){var stack=new ItemStack(ABSORBER_ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),meta);return stack;}
    public static List<ItemStack> absorberStacks(){var stacks=new ArrayList<ItemStack>();for(int meta=0;meta<4;meta++)stacks.add(absorberStack(meta));return stacks;}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("machine"))return List.of();var stacks=new ArrayList<>(absorberStacks());stacks.add(new ItemStack(DECON_ITEM.get()));return stacks;}
    private RadiationCleanupContent(){}
}
