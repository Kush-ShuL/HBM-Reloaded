package com.hbm.machine.launchpadsoyuz;

import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Actual independent LaunchpadSoyuz: source explicit XR check/fill origins, twelve true Combo ports and elevated tower. */
public final class LaunchpadSoyuzBlock extends DummyableBlock {
    public LaunchpadSoyuzBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(properties->new LaunchpadSoyuzBlock());}
    @Override public int[] dimensions(){return new int[]{2,0,2,2,2,2};}
    @Override public int offset(){return 2;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        var facing=context.getHorizontalDirection().getOpposite();var core=coreForPlacement(context);
        for(var cell:LaunchpadSoyuzLayout.checkedCells(core,facing))
            if(!context.getLevel().isInWorldBounds(cell)||!context.getLevel().hasChunkAt(cell)||!cell.equals(context.getClickedPos())&&!context.getLevel().getBlockState(cell).canBeReplaced())return null;
        return defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue());
    }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());
        safely(()->{
            if(!pos.equals(core))level.removeBlock(pos,false);
            for(var entry:LaunchpadSoyuzLayout.placement(core,facing).entrySet())level.setBlock(entry.getKey(),defaultBlockState().setValue(LEGACY_META,entry.getValue()),UPDATE_ALL);
            // Source makeExtra only upgrades real already placed cells; it never invents an absent proxy.
            for(var port:LaunchpadSoyuzLayout.ports(core,facing))setPort(level,port.position(),true);
        });
        if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)&&level.getBlockEntity(core) instanceof NamedMultiblock named)named.setCustomName(stack.getHoverName());
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new LaunchpadSoyuzEntity(pos,state):isPort(state)?new LaunchpadSoyuzPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,LaunchpadSoyuzContent.ENTITY.get(),LaunchpadSoyuzEntity::tick):null;}
}
