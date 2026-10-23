package com.hbm.door.generic;
import com.hbm.config.SourceServerConfig;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.*;
public final class GenericDoorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>>ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<GenericDoorBlock>SILO=BLOCKS.register("silo_hatch",()->new GenericDoorBlock(GenericDoorDeclaration.SILO_HATCH)),CARGO=BLOCKS.register("cargo_door",()->new GenericDoorBlock(GenericDoorDeclaration.CARGO_DOOR));
    public static final DeferredItem<BlockItem>SILO_ITEM=ITEMS.register("silo_hatch",()->new BlockItem(SILO.get(),new Item.Properties())),CARGO_ITEM=ITEMS.register("cargo_door",()->new BlockItem(CARGO.get(),new Item.Properties()));
    public static final DeferredBlock<GenericDoorBlock>LARGE_SILO=BLOCKS.register("silo_hatch_large",()->new GenericDoorBlock(GenericDoorDeclaration.SILO_HATCH_LARGE));
    public static final DeferredItem<BlockItem>LARGE_SILO_ITEM=ITEMS.register("silo_hatch_large",()->new BlockItem(LARGE_SILO.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<GenericDoorBlockEntity>>ENTITY=ENTITIES.register("generic_door",()->BlockEntityType.Builder.of(GenericDoorBlockEntity::new,com.hbm.door.transition.TransitionSealContent.withHost(com.hbm.door.vault.VaultDoorContent.withHost(com.hbm.door.family.GenericDoorFamilyContent.withHosts(SILO.get(),CARGO.get(),LARGE_SILO.get(),com.hbm.door.fire.FireDoorContent.BLOCK.get(),com.hbm.door.slidingblast.SlidingBlastDoorContent.BLOCK.get())))).build(null));
    public static void register(IEventBus bus,ModContainer container){SourceServerConfig.register(container);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(GenericDoorStatePacket::register);}
    public static List<ItemStack>creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(SILO_ITEM.get()),new ItemStack(CARGO_ITEM.get()),new ItemStack(LARGE_SILO_ITEM.get())):List.of();}
    private GenericDoorContent(){}
}
