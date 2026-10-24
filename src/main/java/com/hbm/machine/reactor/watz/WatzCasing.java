package com.hbm.machine.reactor.watz;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.hazard.HbmHazards;

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

/** Original BlockToolConversion's actual BOLT+four boltDuraSteel transaction, including creative consumption. */
public final class WatzCasing extends Block implements ToolableBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final net.minecraft.world.item.crafting.Ingredient BOLTS=net.minecraft.world.item.crafting.Ingredient.of(com.hbm.registry.HbmItemTags.key("hbm:bolts/dura_steel"));
    public WatzCasing(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends WatzCasing> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,Math.floorMod(context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),16));}
    public static ItemStack stack(int metadata,int amount){var out=new ItemStack(WatzContent.CASING.get(),amount);if(metadata!=0)out.set(HbmHazards.LEGACY_METADATA.get(),metadata);return out;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(stack(Math.abs(state.getValue(META)%2),1));}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return stack(Math.abs(state.getValue(META)%2),1);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(level.isClientSide||tool!=ToolType.BOLT||level.getBlockState(pos).getValue(META)!=0)return false;int needed=4;int[] take=new int[36];for(int i=0;i<36&&needed>0;i++){var stack=player.getInventory().getItem(i);if(BOLTS.test(stack)){take[i]=Math.min(needed,stack.getCount());needed-=take[i];}}if(needed>0)return false;for(int i=0;i<36;i++)if(take[i]>0)player.getInventory().removeItem(i,take[i]);level.setBlock(pos,defaultBlockState().setValue(META,1),UPDATE_ALL);return true;}
}
