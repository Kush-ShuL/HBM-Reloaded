package com.hbm.machine.precisionassembler;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Source3x3x3, core one block behind clicked placement, eight PIFIFO perimeter proxies. Ordinary dummies have no BE. */
public final class PrecisionAssemblerBlock extends DummyableBlock {
    private static final MapCodec<PrecisionAssemblerBlock> CODEC=simpleCodec(p->new PrecisionAssemblerBlock());
    public PrecisionAssemblerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,18).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{2,0,1,1,1,1};}@Override public int offset(){return 1;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        var direction=placementFacing(state);
        super.setPlacedBy(level,pos,state,placer,stack);
        if(!level.isClientSide){BlockPos core=pos.relative(direction,-offset());for(var port:PrecisionAssemblerBlockEntity.portPositions(core))setPort(level,port,true);}
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new PrecisionAssemblerBlockEntity(pos,state):isPort(state)?new PrecisionAssemblerPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,PrecisionAssemblerContent.ENTITY.get(),PrecisionAssemblerBlockEntity::tick):null;}
}
