package com.hbm.machine.woodburner;
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
/** Original asymmetric2x2x2 base, ordinary inventory dummies and two rear HE/fluid ports. */
public final class WoodBurnerBlock extends DummyableBlock {
    private static final MapCodec<WoodBurnerBlock> CODEC=simpleCodec(ignored->new WoodBurnerBlock());
    public WoodBurnerBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return CODEC; }
    @Override public int[] dimensions() { return new int[]{1,0,1,0,1,0}; }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) { super.setPlacedBy(level,pos,state,placer,stack); if(!level.isClientSide) for(var port:WoodBurnerBlockEntity.portPositions(pos,state)) setPort(level,port,true); }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return isCore(state)?new WoodBurnerBlockEntity(pos,state):isPort(state)?new WoodBurnerPort(pos,state):new WoodBurnerInventoryProxy(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) { return isCore(state)?createTickerHelper(type,WoodBurnerContent.ENTITY.get(),WoodBurnerBlockEntity::tick):null; }
}
