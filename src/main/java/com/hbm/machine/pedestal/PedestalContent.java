package com.hbm.machine.pedestal;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Source hidden pedestal and clay tablet. The pedestal has no inventory screen or automation in X5846. */
public final class PedestalContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    private static final DeferredRegister<ResourceLocation> STATS=DeferredRegister.create(Registries.CUSTOM_STAT,"hbm");
    public static final DeferredBlock<PedestalBlock> BLOCK=BLOCKS.register("pedestal",()->new PedestalBlock(BlockBehaviour.Properties.of().strength(2,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()));
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredItem<ClayTabletItem> TABLET=ITEMS.register("clay_tablet",ClayTabletItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PedestalBlockEntity>> ENTITY=ENTITIES.register("pedestal",()->BlockEntityType.Builder.of(PedestalBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<RecipeType<?>,RecipeType<PedestalRecipe>> TYPE=TYPES.register("pedestal",()->new RecipeType<PedestalRecipe>(){public String toString(){return "hbm:pedestal";}});
    public static final DeferredHolder<RecipeSerializer<?>,PedestalRecipe.Serializer> SERIALIZER=SERIALIZERS.register("pedestal",PedestalRecipe.Serializer::new);
    public static final DeferredHolder<ResourceLocation,ResourceLocation> LEGENDARY=STATS.register("ntm_legendary",()->ResourceLocation.parse("hbm:ntm_legendary"));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);STATS.register(bus);PedestalMaterials.register(bus);PedestalRecipeConfig.register();PedestalEntries.register();PedestalReputation.register();PedestalDefuser.register();}
    public static List<ItemStack> indexedItems(){var items=new java.util.ArrayList<>(List.of(new ItemStack(ITEM.get()),new ItemStack(TABLET.get())));items.addAll(PedestalMaterials.indexedItems());return items;}
    public static List<ItemStack> creativeStacks(String tab){return PedestalMaterials.creativeStacks(tab);}
    public static void legendary(net.minecraft.world.entity.player.Player player){player.awardStat(Stats.CUSTOM.get(LEGENDARY.get()),1);}
    private PedestalContent(){}
}
