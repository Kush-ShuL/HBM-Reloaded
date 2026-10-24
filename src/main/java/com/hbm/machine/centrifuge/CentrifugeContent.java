package com.hbm.machine.centrifuge;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Actual solid centrifuge; the gas centrifuge remains a distinct source machine. */
public final class CentrifugeContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<CentrifugeBlock> BLOCK=BLOCKS.register("machine_centrifuge",CentrifugeBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("machine_centrifuge",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CentrifugeBlockEntity>> ENTITY=ENTITIES.register("machine_centrifuge",()->BlockEntityType.Builder.of(CentrifugeBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CentrifugePowerProxy>> PROXY=ENTITIES.register("centrifuge_power_proxy",()->BlockEntityType.Builder.of(CentrifugePowerProxy::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<CentrifugeMenu>> MENU=MENUS.register("centrifuge",()->new MenuType<>((IContainerFactory<CentrifugeMenu>)CentrifugeMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<CentrifugeRecipe>> TYPE=TYPES.register("centrifuge",()->new RecipeType<CentrifugeRecipe>() { @Override public String toString() { return "hbm:centrifuge"; } });
    public static final DeferredHolder<RecipeSerializer<?>,CentrifugeRecipe.Serializer> SERIALIZER=SERIALIZERS.register("centrifuge",CentrifugeRecipe.Serializer::new);
    private static final DeferredRegister<net.neoforged.neoforge.common.crafting.IngredientType<?>> INGREDIENTS=DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,"hbm");
    public static final DeferredHolder<net.neoforged.neoforge.common.crafting.IngredientType<?>,net.neoforged.neoforge.common.crafting.IngredientType<CentrifugeNbtIngredient>> NBT_INGREDIENT=INGREDIENTS.register("centrifuge_legacy_nbt",()->new net.neoforged.neoforge.common.crafting.IngredientType<>(CentrifugeNbtIngredient.CODEC));
    public static final List<DeferredItem<OverdriveUpgradeItem>> OVERDRIVE=new ArrayList<>();
    static { for(int i=1;i<=3;i++) { int tier=i; OVERDRIVE.add(ITEMS.register("upgrade_overdrive_"+i,()->new OverdriveUpgradeItem(tier))); } }
    private CentrifugeContent() {}
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus); INGREDIENTS.register(bus);
        bus.addListener(CentrifugeContent::capabilities); CentrifugeRecipeConfig.register(); bus.addListener(CentrifugeRecipeConfig::imc);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) { event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side)); }
    public static List<ItemStack> creativeItems() { return List.of(new ItemStack(ITEM.get())); }
    public static List<ItemStack> controlItems() { return OVERDRIVE.stream().map(item->new ItemStack(item.get())).toList(); }
}
