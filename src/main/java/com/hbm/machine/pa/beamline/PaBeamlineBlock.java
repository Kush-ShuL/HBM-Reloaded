package com.hbm.machine.pa.beamline;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
public final class PaBeamlineBlock extends DummyableBlock implements ToolableBlock{
    public PaBeamlineBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new PaBeamlineBlock());}@Override public int[] dimensions(){return new int[]{0,0,0,0,1,1};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new PaBeamlineEntity(pos,state):null;}
    @Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,PaBeamlineContent.ENTITY.get(),PaBeamlineEntity::tick):null;}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;if(level.isClientSide)return true;var core=findCore(level,pos);if(core!=null&&level.getBlockEntity(core) instanceof PaBeamlineEntity beamline){beamline.window=!beamline.window;beamline.setChanged();}return false;}
}
