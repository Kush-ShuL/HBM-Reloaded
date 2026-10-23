package com.hbm.machine.vacuumdistill;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class VacuumDistillContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<VacuumDistillBlock> BLOCK=BLOCKS.register("machine_vacuum_distill",VacuumDistillBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<VacuumDistillBlockEntity>> ENTITY=ENTITIES.register("vacuumdistill",()->BlockEntityType.Builder.of(VacuumDistillBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<VacuumDistillPort>> PORT=ENTITIES.register("vacuumdistill_port",()->BlockEntityType.Builder.of(VacuumDistillPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<VacuumDistillMenu>> MENU=MENUS.register("vacuumdistill",()->new MenuType<>((IContainerFactory<VacuumDistillMenu>)VacuumDistillMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<VacuumDistillRecipe>> TYPE=TYPES.register("vacuumdistill",()->new RecipeType<VacuumDistillRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,VacuumDistillRecipe.Serializer> SERIALIZER=SERIALIZERS.register("vacuumdistill",VacuumDistillRecipe.Serializer::new);
    private VacuumDistillContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);VacuumDistillRecipeConfig.register();bus.addListener(VacuumDistillContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}
