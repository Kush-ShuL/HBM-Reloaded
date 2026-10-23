package com.hbm.machine.fluidtank;

import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.item.tool.*;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class IndustrialFluidTankContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<IndustrialFluidTankBlock> BLOCK=BLOCKS.register("machine_fluidtank",IndustrialFluidTankBlock::new);
    public static final DeferredItem<IndustrialFluidTankItem> ITEM=ITEMS.register("machine_fluidtank",()->new IndustrialFluidTankItem(BLOCK.get()));
    public static final DeferredItem<BlowtorchItem> BLOWTORCH=ITEMS.register("blowtorch",()->new BlowtorchItem(false)),ACETYLENE=ITEMS.register("acetylene_torch",()->new BlowtorchItem(true));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IndustrialFluidTankBlockEntity>> ENTITY=ENTITIES.register("industrial_fluid_tank",()->BlockEntityType.Builder.of(IndustrialFluidTankBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IndustrialFluidTankPort>> PORT=ENTITIES.register("industrial_fluid_tank_port",()->BlockEntityType.Builder.of(IndustrialFluidTankPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<IndustrialFluidTankMenu>> MENU=MENUS.register("industrial_fluid_tank",()->new MenuType<>((IContainerFactory<IndustrialFluidTankMenu>)IndustrialFluidTankMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(IndustrialFluidTankContent::setup);bus.addListener(IndustrialFluidTankContent::capabilities);bus.addListener(IndustrialFluidTankModePacket::register);bus.addListener(WeldingParticlePacket::register);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{ToolType.TORCH.register(new ItemStack(BLOWTORCH.get()));ToolType.TORCH.register(new ItemStack(ACETYLENE.get()));});}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "machine"->List.of(new ItemStack(ITEM.get()));case "control"->List.of(new ItemStack(BLOWTORCH.get()),new ItemStack(ACETYLENE.get()));default->List.of();};}
    private IndustrialFluidTankContent(){}
}
