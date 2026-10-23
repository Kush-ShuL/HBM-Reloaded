package com.hbm.machine.alloy;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
public final class AlloyFurnaceBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty COVERED=BooleanProperty.create("covered");
    private final boolean active; private final MapCodec<AlloyFurnaceBlock> codec;
    public AlloyFurnaceBlock(boolean active) { super(Properties.of().mapColor(MapColor.STONE).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().lightLevel(state->active?15:0)); this.active=active; codec=simpleCodec(ignored->new AlloyFurnaceBlock(active)); registerDefaultState(stateDefinition.any().setValue(FACING,Direction.SOUTH).setValue(COVERED,false)); }
    public boolean active() { return active; }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return codec; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(FACING,COVERED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite()).setValue(COVERED,context.getLevel().getBlockState(context.getClickedPos().above()).is(AlloyContent.EXTENSION.get())); }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) { return state.setValue(FACING,rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state,Mirror mirror) { return rotate(state,mirror.getRotation(state.getValue(FACING))); }
    @Override protected BlockState updateShape(BlockState state,Direction side,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos other) { return side==Direction.UP?state.setValue(COVERED,neighbor.is(AlloyContent.EXTENSION.get())):state; }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new AlloyBlockEntity(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) { return level.isClientSide?null:createTickerHelper(type,AlloyContent.ENTITY.get(),AlloyBlockEntity::tick); }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit) { if(level.isClientSide) return InteractionResult.SUCCESS; if(player.isShiftKeyDown()) return InteractionResult.PASS; if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof AlloyBlockEntity machine) server.openMenu(machine,pos); return InteractionResult.CONSUME; }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) { if(stack.has(DataComponents.CUSTOM_NAME)&&level.getBlockEntity(pos) instanceof AlloyBlockEntity machine) machine.setCustomName(stack.getHoverName()); }
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder loot) { return List.of(new ItemStack(AlloyContent.ITEM.get())); }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state) { return new ItemStack(AlloyContent.ITEM.get()); }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving) { if(replacement.getBlock() instanceof AlloyFurnaceBlock) return; if(level.getBlockEntity(pos) instanceof AlloyBlockEntity inventory) { Containers.dropContents(level,pos,inventory); level.updateNeighbourForOutputSignal(pos,this); } super.onRemove(state,level,pos,replacement,moving); }
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random) {
        if(!active) return; Direction side=state.getValue(FACING); float across=random.nextFloat()*.5F-.25F;
        double x=pos.getX()+.5+(side.getAxis()==Direction.Axis.X?side.getStepX()*.52:across),z=pos.getZ()+.5+(side.getAxis()==Direction.Axis.Z?side.getStepZ()*.52:across);
        double y=pos.getY()+.25+random.nextFloat()*6/16D; level.addParticle(ParticleTypes.FLAME,x,y,z,0,0,0);
        level.addParticle(ParticleTypes.SMOKE,pos.getX()+random.nextFloat()*.375+.3125,pos.getY()+1+(state.getValue(COVERED)?1:0),pos.getZ()+random.nextFloat()*.375+.3125,0,0,0);
    }
}
