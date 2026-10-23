package com.hbm.world.structure.templates.microwave;
import java.util.List;
import com.hbm.registry.HbmOreDictionary;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class MicrowaveContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<MicrowaveBlock> BLOCK=BLOCKS.register("machine_microwave",MicrowaveBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_microwave",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MicrowaveBlockEntity>> ENTITY=ENTITIES.register("machine_microwave",()->BlockEntityType.Builder.of(MicrowaveBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<MicrowaveMenu>> MENU=MENUS.register("machine_microwave",()->new MenuType<>((IContainerFactory<MicrowaveMenu>)MicrowaveMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(MicrowaveControl::register);bus.addListener(MicrowaveContent::capabilities);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->HbmOreDictionary.registerName("paneGlass")));}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private MicrowaveContent(){}
}
