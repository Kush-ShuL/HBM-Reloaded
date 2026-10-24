package com.hbm.machine.converter;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConverterContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<ConverterBlock> HE_TO_RF=BLOCKS.register("machine_converter_he_rf",()->new ConverterBlock(true));
    public static final DeferredBlock<ConverterBlock> RF_TO_HE=BLOCKS.register("machine_converter_rf_he",()->new ConverterBlock(false));
    public static final DeferredItem<BlockItem> HE_TO_RF_ITEM=ITEMS.register("machine_converter_he_rf",()->new BlockItem(HE_TO_RF.get(),new Item.Properties()));
    public static final DeferredItem<BlockItem> RF_TO_HE_ITEM=ITEMS.register("machine_converter_rf_he",()->new BlockItem(RF_TO_HE.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ConverterBlockEntity>> ENTITY=ENTITIES.register("energy_converter",()->BlockEntityType.Builder.<ConverterBlockEntity>of((pos,state)->((ConverterBlock)state.getBlock()).heToRf?new HeToRfConverter(pos,state):new RfToHeConverter(pos,state),HE_TO_RF.get(),RF_TO_HE.get()).build(null));
    private ConverterContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(ConverterContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,ENTITY.get(),(converter,side)->converter.isLoaded()?converter.forgeEnergy():null);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(HE_TO_RF_ITEM.get()),new ItemStack(RF_TO_HE_ITEM.get())):List.of();}
}
