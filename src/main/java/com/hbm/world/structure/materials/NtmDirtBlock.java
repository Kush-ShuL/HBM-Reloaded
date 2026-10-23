package com.hbm.world.structure.materials;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Source BlockNTMDirt inherits all old dirt sprites but drops vanilla dirt. */
public final class NtmDirtBlock extends Block {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public static final BooleanProperty SNOWY=BooleanProperty.create("snowy");
    public NtmDirtBlock(){super(Properties.of().mapColor(state->state.getValue(META)==2?MapColor.PODZOL:MapColor.DIRT).strength(.5F).sound(SoundType.GRAVEL));registerDefaultState(stateDefinition.any().setValue(META,0).setValue(SNOWY,false));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META,SNOWY);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(SNOWY,context.getLevel().getBlockState(context.getClickedPos().above()).is(BlockTags.SNOW));}
    @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos neighborPos){return direction==Direction.UP?state.setValue(SNOWY,neighbor.is(BlockTags.SNOW)):state;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
        var tool=context.getOptionalParameter(LootContextParams.TOOL);if(tool==null)tool=ItemStack.EMPTY;
        var enchantments=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if(EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH),tool)>0)return List.of(new ItemStack(this));
        var radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        if(radius!=null&&context.getLevel().random.nextFloat()>1F/radius)return List.of();
        return List.of(new ItemStack(Items.DIRT));
    }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(this);}
}
