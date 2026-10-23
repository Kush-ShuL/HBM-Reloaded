package com.hbm.machine.woodprocessing;
import com.hbm.multiblock.Multiblocks;
import java.util.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.*;
public final class WoodProcessingContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<SawmillBlock> SAWMILL=BLOCKS.register("machine_sawmill",SawmillBlock::new);public static final DeferredBlock<AutosawBlock> AUTOSAW=BLOCKS.register("machine_autosaw",AutosawBlock::new);
    public static final DeferredItem<WoodMachineItem> SAWMILL_ITEM=ITEMS.register("machine_sawmill",()->new WoodMachineItem(SAWMILL.get(),"machine_sawmill"));public static final DeferredItem<WoodMachineItem> AUTOSAW_ITEM=ITEMS.register("machine_autosaw",()->new WoodMachineItem(AUTOSAW.get(),"machine_autosaw"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SawmillEntity>> SAWMILL_ENTITY=TILES.register("machine_sawmill",()->BlockEntityType.Builder.of(SawmillEntity::new,SAWMILL.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AutosawEntity>> AUTOSAW_ENTITY=TILES.register("machine_autosaw",()->BlockEntityType.Builder.of(AutosawEntity::new,AUTOSAW.get()).build(null));
    public static final DeferredHolder<EntityType<?>,EntityType<SawbladeEntity>> SAWBLADE=ENTITIES.register("entity_stray_saw",()->EntityType.Builder.<SawbladeEntity>of(SawbladeEntity::new,MobCategory.MISC).sized(1,1).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_stray_saw"));
    public static void register(IEventBus bus){Multiblocks.addInventoryProxyBlock(SAWMILL);BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);ENTITIES.register(bus);bus.addListener(WoodProcessingContent::capabilities);bus.addListener(WoodEffects::register);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,SAWMILL_ENTITY.get(),(saw,side)->new SidedInvWrapper(saw,side==null?net.minecraft.core.Direction.DOWN:side));}
    public static Item item(String id){return BuiltInRegistries.ITEM.get(ResourceLocation.parse("hbm:"+id));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(SAWMILL_ITEM.get()),new ItemStack(AUTOSAW_ITEM.get())):List.of();}
    public static List<ItemStack> allStacks(){var broken=new ItemStack(SAWMILL_ITEM.get());broken.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),1);return List.of(new ItemStack(SAWMILL_ITEM.get()),broken,new ItemStack(AUTOSAW_ITEM.get()));}
    private WoodProcessingContent(){}
}
