package com.hbm.machine.oilmixer;
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
public final class MixerContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<MixerBlock> BLOCK=BLOCKS.register("machine_mixer",MixerBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MixerEntity>> ENTITY=ENTITIES.register("oil_mixer",()->BlockEntityType.Builder.of(MixerEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MixerPort>> PORT=ENTITIES.register("oil_mixer_port",()->BlockEntityType.Builder.of(MixerPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<MixerMenu>> MENU=MENUS.register("oil_mixer",()->new MenuType<>((IContainerFactory<MixerMenu>)MixerMenu::new,FeatureFlags.DEFAULT_FLAGS));public static final DeferredHolder<RecipeType<?>,RecipeType<MixerRecipe>> TYPE=TYPES.register("oil_mixer",()->new RecipeType<MixerRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,MixerRecipe.Serializer> SERIALIZER=SERIALIZERS.register("oil_mixer",MixerRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(MixerControl::register);bus.addListener(MixerContent::capabilities);MixerRecipeConfig.register();}
 private static void capabilities(RegisterCapabilitiesEvent e){e.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->side==null?new InvWrapper(be):new SidedInvWrapper(be,side));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private MixerContent(){}
}
