package com.hbm.block.tritiumlamp;

import com.hbm.api.block.SpotlightSource;
import com.hbm.block.light.SpotlightBlock;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Actual TritiumLamp(blue/green,on/off): active-high redstone, four-tick delayed shutdown and original six beams. */
public final class TritiumLampBlock extends Block implements SpotlightSource {
    private final boolean blue,on;
    public TritiumLampBlock(boolean blue,boolean on){super(Properties.of().mapColor(MapColor.NONE).strength(3F).sound(SoundType.GLASS).lightLevel(state->on?15:0));this.blue=blue;this.on=on;}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override public int getBeamLength(){return 8;}
    private TritiumLampBlock off(){return TritiumLampContent.block(blue,false);}
    private TritiumLampBlock lit(){return TritiumLampContent.block(blue,true);}
    private void beams(Level level,BlockPos pos){if(on)for(Direction direction:Direction.values())SpotlightBlock.propagate(level,pos,direction,8,blue?2:1);}
    private void update(Level level,BlockPos pos){
        if(level.isClientSide)return;
        boolean powered=level.hasNeighborSignal(pos);
        if(on&&!powered)level.scheduleTick(pos,this,4);
        else if(!on&&powered)level.setBlock(pos,lit().defaultBlockState(),Block.UPDATE_CLIENTS);
        beams(level,pos);
    }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){if(!old.is(this))update(level,pos);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){update(level,pos);}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){if(on&&!level.hasNeighborSignal(pos))level.setBlock(pos,off().defaultBlockState(),Block.UPDATE_CLIENTS);}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        super.onRemove(state,level,pos,next,moving);
        if(!level.isClientSide&&!state.is(next.getBlock()))for(Direction direction:Direction.values())SpotlightBlock.unpropagate(level,pos,direction);
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){
        Float radius=params.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        return radius!=null&&params.getLevel().random.nextFloat()>1F/radius?List.of():List.of(new ItemStack(off()));
    }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(off());}
}
