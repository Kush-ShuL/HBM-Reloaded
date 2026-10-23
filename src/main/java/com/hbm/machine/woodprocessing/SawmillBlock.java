package com.hbm.machine.woodprocessing;
import com.hbm.hazard.HbmHazards;
import com.hbm.multiblock.*;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
/** Source three precise collision cuboids, offset1 placement and four inventory-only physical ports. */
public final class SawmillBlock extends DummyableBlock {
    private static final List<AABB> BOUNDS=List.of(new AABB(-1.5,0,-1.5,1.5,1,1.5),new AABB(-1.25,1,-.5,-.625,1.875,.5),new AABB(-.625,1,-1,1.375,2,1));
    public SawmillBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().dynamicShape());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new SawmillBlock());}@Override public int[] dimensions(){return new int[]{1,0,1,1,1,1};}@Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new SawmillEntity(pos,state):isPort(state)?new DummyInventoryProxy(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,WoodProcessingContent.SAWMILL_ENTITY.get(),SawmillEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var side:Direction.Plane.HORIZONTAL)setPort(level,core.relative(side),true);if(level.getBlockEntity(core) instanceof SawmillEntity saw&&WoodMachineItem.broken(stack)){saw.hasBlade=false;saw.setChanged();}}
    public SawmillEntity core(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof SawmillEntity saw?saw:null;}
    /** Exact BlockDummyable.getAABBRotationOffset(facing.getRotation(UP)), not the OBJ's independent yaw. */
    public static AABB rotateBox(AABB a,Direction facing){return switch(facing){case NORTH->new AABB(-a.maxZ,a.minY,a.minX,-a.minZ,a.maxY,a.maxX);case SOUTH->new AABB(a.minZ,a.minY,-a.maxX,a.maxZ,a.maxY,-a.minX);case EAST->new AABB(-a.maxX,a.minY,-a.maxZ,-a.minX,a.maxY,-a.minZ);default->a;};}
    private VoxelShape shape(BlockState state,BlockGetter level,BlockPos pos){var core=findCore(level,pos);if(core==null)return Shapes.empty();var facing=DummyableBlock.facing(level.getBlockState(core));VoxelShape shape=Shapes.empty();for(var bounds:BOUNDS)shape=Shapes.or(shape,Shapes.create(rotateBox(bounds,facing).move(core.getX()+.5-pos.getX(),core.getY()-pos.getY(),core.getZ()+.5-pos.getZ())));return shape;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return shape(state,level,pos);}@Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return shape(state,level,pos);}
    private InteractionResult activate(Level level,BlockPos pos,Player player){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;var saw=core(level,pos);if(saw==null)return InteractionResult.PASS;var held=player.getMainHandItem();
        if(!saw.hasBlade&&held.is(WoodProcessingContent.item("sawblade"))){held.shrink(1);saw.hasBlade=true;saw.setChanged();level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,HbmSounds.get("item.upgradePlug"),SoundSource.BLOCKS,1.5F,.75F);return InteractionResult.SUCCESS;}
        if(!saw.slots.get(1).isEmpty()||!saw.slots.get(2).isEmpty()){for(int i=1;i<3;i++)if(!saw.slots.get(i).isEmpty()){if(!player.getInventory().add(saw.slots.get(i).copy()))player.drop(saw.slots.get(i).copy(),false);saw.slots.set(i,ItemStack.EMPTY);}player.inventoryMenu.broadcastChanges();saw.setChanged();return InteractionResult.SUCCESS;}
        if(saw.slots.get(0).isEmpty()&&!held.isEmpty()&&!saw.getOutput(held).isEmpty()){saw.slots.set(0,held.copyWithCount(1));held.shrink(1);saw.setChanged();player.inventoryMenu.broadcastChanges();return InteractionResult.SUCCESS;}return InteractionResult.PASS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var tool=super.useItemOn(stack,state,level,pos,player,hand,hit);if(tool!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return tool;return activate(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean harvest,net.minecraft.world.level.material.FluidState fluid){if(!level.isClientSide&&harvest&&core(level,pos)!=null)return true;return super.onDestroyedByPlayer(state,level,pos,player,harvest,fluid);}
    @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity original,ItemStack tool){var saw=core(level,pos);try{super.playerDestroy(level,player,pos,state,saw==null?original:saw,tool);}finally{if(saw!=null&&core(level,pos)==saw)level.removeBlock(pos,false);}}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){var saw=params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof SawmillEntity found?found:null;var origin=params.getOptionalParameter(LootContextParams.ORIGIN);if(saw==null&&origin!=null)saw=core(params.getLevel(),BlockPos.containing(origin));var stack=new ItemStack(this);if(saw!=null&&!saw.hasBlade)stack.set(HbmHazards.LEGACY_METADATA.get(),1);return List.of(stack);}
}
