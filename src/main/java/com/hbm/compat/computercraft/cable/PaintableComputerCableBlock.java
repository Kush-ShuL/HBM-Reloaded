package com.hbm.compat.computercraft.cable;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmOreDictionary;
import com.hbm.world.structure.logic.LogicDisguiseState;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
/** Full source cube; metadata toggle is retained independently of the different HE-cable overlay switch. */
public final class PaintableComputerCableBlock extends BaseEntityBlock implements ToolableBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final MapCodec<PaintableComputerCableBlock> CODEC=simpleCodec(ignored->new PaintableComputerCableBlock());
    public PaintableComputerCableBlock(){super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(5,6).sound(SoundType.STONE));registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<PaintableComputerCableBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PaintableComputerCableEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,PaintableComputerCableContent.ENTITY.get(),PaintableComputerCableEntity::tick);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos neighborPos,boolean moved){if(!level.isClientSide&&level.getBlockEntity(pos) instanceof PaintableComputerCableEntity cable)cable.connectionsChanged();super.neighborChanged(state,level,pos,neighbor,neighborPos,moved);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!(level.getBlockEntity(pos) instanceof PaintableComputerCableEntity cable))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(stack.getItem() instanceof BlockItem item){
            var block=item.getBlock();int meta=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())&15;
            var paint=LogicDisguiseState.resolve(BuiltInRegistries.BLOCK.getKey(block).toString(),meta);if(paint==null)return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            var properties=stack.get(DataComponents.BLOCK_STATE);if(properties!=null)paint=properties.apply(paint);
            if(block!=this&&paint.isSolidRender(level,pos)&&Block.isShapeFullBlock(paint.getShape(level,pos))&&paint.getRenderShape()==RenderShape.MODEL&&cable.paint()==null){if(!level.isClientSide)cable.setPaint(paint,meta);return ItemInteractionResult.sidedSuccess(level.isClientSide);}
        }else if(HbmOreDictionary.names(stack).contains("dye")){if(!level.isClientSide)cable.setColor(SourceCableColors.fromDye(stack));}
        // Source dye operation deliberately falls through false and does not consume dye.
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){
        if(!(level.getBlockEntity(pos) instanceof PaintableComputerCableEntity cable))return false;
        if(tool==ToolType.SCREWDRIVER&&cable.paint()!=null){if(!level.isClientSide)cable.setPaint(null,0);return true;}
        if(tool==ToolType.DEFUSER){if(!level.isClientSide){var state=level.getBlockState(pos);level.setBlock(pos,state.setValue(META,state.getValue(META)==0?1:0),Block.UPDATE_ALL);}return true;}
        return false;
    }
}
