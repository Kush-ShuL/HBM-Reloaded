package com.hbm.world.structure.vending;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class VendingMachineContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<VendingMachineBlock> BLOCK=BLOCKS.register("vending_machine",VendingMachineBlock::new);
    public static final DeferredItem<VendingMachineItem> SODA=ITEMS.register("vending_machine_soda",()->new VendingMachineItem(BLOCK.get(),0));
    public static final DeferredItem<VendingMachineItem> SNACKS=ITEMS.register("vending_machine_snacks",()->new VendingMachineItem(BLOCK.get(),1));
    public static VendingMachineItem item(int variant){return switch(variant){case 0->SODA.get();case 1->SNACKS.get();default->throw new IllegalArgumentException("Unknown vending item "+variant);};}
    public static boolean isItemId(ResourceLocation id){return id.equals(SODA.getId())||id.equals(SNACKS.getId());}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<VendingMachineBlockEntity>> ENTITY=ENTITIES.register("vending_machine",()->BlockEntityType.Builder.of(VendingMachineBlockEntity::new,BLOCK.get()).build(null));
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(VendingMachineItem.stack(0),VendingMachineItem.stack(1)):List.of();}
    private VendingMachineContent(){}
}
