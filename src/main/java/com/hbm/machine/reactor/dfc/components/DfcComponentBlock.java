package com.hbm.machine.reactor.dfc.components;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
/** Original CoreComponent: full collision, zero opacity, all six piston-facing orientations. */
public final class DfcComponentBlock extends BaseEntityBlock {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);public final DfcComponentKind kind;
 public DfcComponentBlock(DfcComponentKind kind){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.kind=kind;registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends DfcComponentBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var player=context.getPlayer();int meta=0;if(player!=null){var pos=context.getClickedPos();boolean vertical=false;if(Math.abs((float)player.getX()-pos.getX())<2F&&Math.abs((float)player.getZ()-pos.getZ())<2F){double sourceEye=player.getY()+1.82D;if(sourceEye-pos.getY()>2){meta=1;vertical=true;}else if(pos.getY()-sourceEye>0){meta=0;vertical=true;}}if(!vertical)meta=new int[]{2,5,3,4}[Mth.floor(player.getYRot()*4F/360F+.5D)&3];}return defaultBlockState().setValue(META,meta);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return switch(kind){case EMITTER->new DfcEmitterEntity(pos,state);case INJECTOR->new DfcInjectorEntity(pos,state);case RECEIVER->new DfcReceiverEntity(pos,state);case STABILIZER->new DfcStabilizerEntity(pos,state);};}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:(world,pos,s,tile)->{if(tile instanceof DfcComponentEntity component&&world instanceof net.minecraft.server.level.ServerLevel server)component.tickServer(server);};}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof DfcComponentEntity tile)server.openMenu(tile,pos);return InteractionResult.SUCCESS;}
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
}
