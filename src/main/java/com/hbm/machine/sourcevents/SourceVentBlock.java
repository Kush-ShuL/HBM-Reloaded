package com.hbm.machine.sourcevents;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

/** BlockVent/BlockClorineSeal are normal full cubes with source stone sound, independent of their iron material. */
public final class SourceVentBlock extends BaseEntityBlock {
    public enum Kind{CHLORINE,CLOUD,PINK,SEAL}
    private final Kind kind;
    public SourceVentBlock(Kind kind){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());this.kind=kind;}
    public Kind kind(){return kind;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return kind==Kind.SEAL?new ChlorineSealEntity(pos,state):new SourceVentEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){
        if(level.isClientSide)return null;
        return kind==Kind.SEAL?createTickerHelper(type,SourceVentContent.SEAL_ENTITY.get(),ChlorineSealEntity::tick):createTickerHelper(type,SourceVentContent.VENT_ENTITY.get(),SourceVentEntity::tick);
    }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return kind==Kind.SEAL?super.getDrops(state,context):List.of();}
}
