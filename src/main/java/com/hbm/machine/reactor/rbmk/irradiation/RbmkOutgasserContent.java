package com.hbm.machine.reactor.rbmk.irradiation;
import com.hbm.item.structureloot.StructureFoodItem;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;
public final class RbmkOutgasserContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> RECIPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<RbmkOutgasserBlock> BLOCK=BLOCKS.register("rbmk_outgasser",RbmkOutgasserBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);public static final DeferredItem<IrradiatedSoup> SOUP=ITEMS.register("glowing_stew",IrradiatedSoup::new);public static final DeferredItem<StructureFoodItem> C4=ITEMS.register("ingot_c4",()->new StructureFoodItem("ingot_c4",4,5));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkOutgasserEntity>> ENTITY=ENTITIES.register("rbmk_outgasser",()->BlockEntityType.Builder.of(RbmkOutgasserEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkOutgasserPort>> PORT=ENTITIES.register("rbmk_outgasser_port",()->BlockEntityType.Builder.of(RbmkOutgasserPort::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<RbmkOutgasserMenu>> MENU=MENUS.register("rbmk_outgasser",()->IMenuTypeExtension.create(RbmkOutgasserMenu::new));public static final DeferredHolder<RecipeType<?>,RecipeType<RbmkIrradiationRecipe>> RECIPE=RECIPES.register("irradiation",()->new RecipeType<>(){});public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<RbmkIrradiationRecipe>> SERIALIZER=SERIALIZERS.register("irradiation",RbmkIrradiationRecipe.Serializer::new);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);RECIPES.register(bus);SERIALIZERS.register(bus);bus.addListener(RbmkOutgasserContent::capabilities);RbmkIrradiationRecipes.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(entity,side)->side==null?new InvWrapper(entity):new SidedInvWrapper(entity,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(entity,side)->side==null?new InvWrapper(entity):new SidedInvWrapper(entity,side));}
    public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "machine"->List.of(new ItemStack(ITEM.get()));case "consumable"->List.of(new ItemStack(SOUP.get()));case "parts"->List.of(new ItemStack(C4.get()));default->List.of();};}private RbmkOutgasserContent(){}
}
