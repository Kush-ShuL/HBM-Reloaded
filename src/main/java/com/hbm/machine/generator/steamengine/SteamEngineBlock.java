package com.hbm.machine.generator.steamengine;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Actual7x3x2 offset1 steam-engine body, three shared power/fluid extras on its right upper side. */
public final class SteamEngineBlock extends DummyableBlock {
    private static final MapCodec<SteamEngineBlock> CODEC=simpleCodec(ignored->new SteamEngineBlock());
    public SteamEngineBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{1,0,5,1,1,1};}@Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new SteamEngineBlockEntity(pos,state):isPort(state)?new SteamEngineProxy(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,SteamEngineContent.ENTITY.get(),SteamEngineBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core!=null)for(var port:SteamEngineBlockEntity.portPositions(core,level.getBlockState(core)))setPort(level,port,true);}
}
