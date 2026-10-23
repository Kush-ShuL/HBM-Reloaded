package com.hbm.machine.ammopress;
import com.hbm.multiblock.Multiblocks;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;
public final class AmmoPressContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<AmmoPressBlock> BLOCK=BLOCKS.register("machine_ammo_press",AmmoPressBlock::new);
 public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_ammo_press",BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AmmoPressEntity>> ENTITY=ENTITIES.register("machine_ammo_press",()->BlockEntityType.Builder.of(AmmoPressEntity::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<AmmoPressMenu>> MENU=MENUS.register("machine_ammo_press",()->IMenuTypeExtension.create(AmmoPressMenu::new));
 public static final DeferredHolder<RecipeType<?>,RecipeType<AmmoPressRecipe>> TYPE=TYPES.register("ammo_press",()->new RecipeType<>(){public String toString(){return "hbm:ammo_press";}});
 public static final DeferredHolder<RecipeSerializer<?>,RecipeSerializer<AmmoPressRecipe>> SERIALIZER=SERIALIZERS.register("ammo_press",AmmoPressRecipe.Serializer::new);
 public static void register(IEventBus bus){Multiblocks.addInventoryProxyBlock(BLOCK);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(AmmoPressControl::register);bus.addListener(AmmoPressContent::capabilities);AmmoPressRecipes.register();}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}private AmmoPressContent(){}
}
