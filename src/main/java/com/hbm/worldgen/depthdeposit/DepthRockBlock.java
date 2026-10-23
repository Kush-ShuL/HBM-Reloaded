package com.hbm.worldgen.depthdeposit;

import com.hbm.block.ore.DepthRockTool;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Original BlockDepth: unbreakable rock, source resistance10, special-tool50-tick progress. */
public final class DepthRockBlock extends Block {
    private static final MapCodec<DepthRockBlock> CODEC=simpleCodec(DepthRockBlock::new);
    public DepthRockBlock(){this(Properties.of().mapColor(MapColor.STONE).strength(-1F,6F).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    private DepthRockBlock(Properties properties){super(properties);}
    @Override protected MapCodec<? extends Block> codec(){return CODEC;}
    @Override protected float getDestroyProgress(BlockState state,Player player,BlockGetter level,BlockPos pos){
        var held=player.getMainHandItem();if(!held.isEmpty()&&held.getItem() instanceof DepthRockTool tool&&tool.canBreakRock(level,player,held,state,pos))return (float)(1D/50D);
        return super.getDestroyProgress(state,player,level,pos);
    }
}
