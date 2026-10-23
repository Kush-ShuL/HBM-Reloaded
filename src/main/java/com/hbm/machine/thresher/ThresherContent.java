package com.hbm.machine.thresher;
import com.hbm.machine.woodprocessing.WoodMachineItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class ThresherContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<ThresherBlock> BLOCK=BLOCKS.register("machine_thresher",ThresherBlock::new);
    public static final DeferredItem<WoodMachineItem> ITEM=ITEMS.register("machine_thresher",()->new WoodMachineItem(BLOCK.get(),"machine_thresher"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ThresherEntity>> ENTITY=ENTITIES.register("machine_thresher",()->BlockEntityType.Builder.of(ThresherEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ITEM.get()));}
    private ThresherContent(){}
}
