package com.hbm.machine.reactor.icf;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.hazard.HbmHazards;
import com.hbm.recipe.LegacyOreIngredient;
import com.mojang.serialization.MapCodec;
import java.util.List;
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

/** Actual BlockICFComponent tool conversion and six BlockICFLaserComponent source subtypes. */
public final class IcfMaterialBlock extends Block implements ToolableBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public final boolean laser;
    public IcfMaterialBlock(boolean laser) {
        super(Properties.of().strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops());
        this.laser=laser;registerDefaultState(stateDefinition.any().setValue(META,0));
    }
    @Override protected MapCodec<? extends IcfMaterialBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,Math.floorMod(context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),16));}
    public int subtype(int meta){return Math.abs(meta%(laser?6:5));}
    public ItemStack stack(int meta,int count){var stack=new ItemStack(this,count);stack.set(HbmHazards.LEGACY_METADATA.get(),subtype(meta));return stack;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(stack(state.getValue(META),1));}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return stack(state.getValue(META),1);}
    public record Requirement(LegacyOreIngredient ingredient,int count) {
        public Requirement(String tag,int count){this(new LegacyOreIngredient(ResourceLocation.parse("hbm:legacy_ore_dict/"+tag)),count);}
    }
    public List<Requirement> requirements(BlockState state,ToolType tool) {
        if(laser)return List.of();
        int meta=state.getValue(META);
        if(meta==1&&tool==ToolType.TORCH)return List.of(new Requirement("platetripleanybismoidbronze",1));
        if(meta==3&&tool==ToolType.BOLT)return List.of(new Requirement("platetriplesteel",1),new Requirement("boltdurasteel",4));
        return List.of();
    }
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool) {
        if(level.isClientSide)return false;
        var state=level.getBlockState(pos);var needs=requirements(state,tool);if(needs.isEmpty())return false;
        var inventory=player.getInventory();var remaining=new int[36];
        for(int i=0;i<36;i++)remaining[i]=inventory.getItem(i).getCount();
        for(var need:needs){int count=need.count();for(int slot=0;slot<36&&count>0;slot++)if(need.ingredient().test(inventory.getItem(slot))){int take=Math.min(count,remaining[slot]);remaining[slot]-=take;count-=take;}if(count>0)return false;}
        for(int slot=0;slot<36;slot++)inventory.removeItem(slot,inventory.getItem(slot).getCount()-remaining[slot]);
        level.setBlock(pos,state.setValue(META,state.getValue(META)+1),UPDATE_ALL);return true;
    }
}
