package com.hbm.machine.strandcaster;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.machine.anvil.MoldItem;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Two source XR cuboids; final-position core, four liquid proxies and four high pouring proxies. */
public final class StrandCasterBlock extends DummyableBlock implements ToolableBlock {
 private static final MapCodec<StrandCasterBlock> CODEC=simpleCodec(ignored->new StrandCasterBlock());
 public StrandCasterBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{0,0,6,0,1,0};}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var d=rotate(new int[]{2,0,1,0,1,0},placementFacing(state));for(var p:BlockPos.betweenClosed(core.offset(-d[4],0,-d[2]),core.offset(d[5],2,d[3])))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var dir=placementFacing(state);var d=rotate(new int[]{2,0,1,0,1,0},dir);safely(()->{for(var p:BlockPos.betweenClosed(pos.offset(-d[4],1,-d[2]),pos.offset(d[5],2,d[3]))){var part=p.immutable();level.setBlock(part,defaultBlockState().setValue(LEGACY_META,Direction.UP.get3DDataValue()),3);}});for(var p:StrandCasterEntity.portPositions(pos,dir))setPort(level,p,true);for(var p:StrandCasterEntity.pourPositions(pos,dir))setPort(level,p,true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new StrandCasterEntity(pos,state):isPort(state)?new StrandCasterPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,StrandCasterContent.ENTITY.get(),StrandCasterEntity::tick):null;}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var core=findCore(level,pos);if(core!=null&&level.getBlockEntity(core) instanceof StrandCasterEntity caster){if(stack.getItem() instanceof MoldItem&&caster.slots.getFirst().isEmpty()){if(!level.isClientSide){caster.slots.set(0,stack.copyWithCount(1));stack.shrink(1);level.playSound(null,pos,com.hbm.registry.HbmSounds.get("item.upgradePlug"),net.minecraft.sounds.SoundSource.BLOCKS,1,1);caster.setChanged();}return ItemInteractionResult.sidedSuccess(level.isClientSide);}if(stack.is(net.minecraft.tags.ItemTags.SHOVELS)){if(!level.isClientSide)give(level,pos,player,caster.removeMetal());return ItemInteractionResult.sidedSuccess(level.isClientSide);}}return super.useItemOn(stack,state,level,pos,player,hand,hit);}
 private static void give(Level level,BlockPos pos,Player player,ItemStack stack){if(!stack.isEmpty()&&!player.getInventory().add(stack))net.minecraft.world.Containers.dropItemStack(level,pos.getX()+.5,pos.getY()+3,pos.getZ()+.5,stack);}
 @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof StrandCasterEntity caster)||caster.slots.getFirst().isEmpty())return false;if(!level.isClientSide){give(level,pos,player,caster.slots.getFirst().copy());caster.slots.set(0,ItemStack.EMPTY);caster.setChanged();}return true;}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){if(!replacement.is(this)&&isCore(state)&&!level.isClientSide&&level.getBlockEntity(pos) instanceof StrandCasterEntity caster){var metal=caster.removeMetal();if(!metal.isEmpty())net.minecraft.world.Containers.dropItemStack(level,pos.getX()+.5,pos.getY()+3,pos.getZ()+.5,metal);}super.onRemove(state,level,pos,replacement,moving);}
}
