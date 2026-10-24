package com.hbm.machine.reactor.iter.torus;
import com.hbm.api.tool.ToolableBlock;

import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
/** Real BlockFusionComponent four materialvariants and source TORCH+one cast-steelplate welding. */
public final class FusionComponent extends Block implements ToolableBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final net.minecraft.world.item.crafting.Ingredient PLATE=net.minecraft.world.item.crafting.Ingredient.of(com.hbm.registry.HbmItemTags.key("hbm:plates/cast/steel"));
    public FusionComponent(){super(Properties.of().strength(5,18).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends FusionComponent> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getItemInHand().getItem() instanceof FusionComponentItem item?item.material():0);}
    public static ItemStack stack(int material,int count){return new ItemStack(TorusContent.componentItem(material),count);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(stack(Math.abs(state.getValue(META)%4),1));}@Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return stack(Math.abs(state.getValue(META)%4),1);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float hitX,float hitY,float hitZ,ToolType tool){if(level.isClientSide||tool!=ToolType.TORCH||level.getBlockState(pos).getValue(META)!=0)return false;for(int slot=0;slot<36;slot++)if(PLATE.test(player.getInventory().getItem(slot))){player.getInventory().removeItem(slot,1);level.setBlock(pos,defaultBlockState().setValue(META,1),UPDATE_ALL);return true;}return false;}
}
