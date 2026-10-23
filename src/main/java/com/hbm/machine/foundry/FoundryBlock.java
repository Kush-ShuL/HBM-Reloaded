package com.hbm.machine.foundry;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.material.MaterialStack;
import com.hbm.machine.foundry.FoundryContent.Kind;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

/** Exact source foundry bounds and manual/tool interactions; no NeoForge fluid entry points. */
public final class FoundryBlock extends BaseEntityBlock implements ToolableBlock {
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
    public final Kind kind;
    public FoundryBlock(Kind kind){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.kind=kind;registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FoundryBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return kind==Kind.SLAG?null:createTickerHelper(type,FoundryContent.ENTITY.get(),FoundryBlockEntity::tick);}
    public boolean connects(BlockGetter level,BlockPos pos,Direction direction){
        if(direction.getAxis()==Direction.Axis.Y)return false;
        var state=level.getBlockState(pos.relative(direction));if(!(state.getBlock() instanceof FoundryBlock other))return false;
        return (other.kind.outlet()&&state.getValue(FACING)==direction)||other.kind==Kind.CHANNEL||other.kind==Kind.MOLD;
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){
        if(kind==Kind.CHANNEL)return box(connects(level,pos,Direction.WEST)?0:5,0,connects(level,pos,Direction.NORTH)?0:5,connects(level,pos,Direction.EAST)?16:11,8,connects(level,pos,Direction.SOUTH)?16:11);
        if(kind.outlet())return switch(state.getValue(FACING)){case WEST->box(10,0,5,16,8,11);case EAST->box(0,0,5,6,8,11);case NORTH->box(5,0,10,11,8,16);default->box(5,0,0,11,8,6);};
        if(kind==Kind.MOLD)return box(0,0,0,16,8,16);
        if(kind==Kind.SLAG){double height=level.getBlockEntity(pos) instanceof FoundryBlockEntity slag?(double)slag.amount/slag.capacity():1;return Shapes.box(0,0,0,1,Math.max(0,height),1);}
        return kind==Kind.BASIN?Shapes.box(0,0,0,1,.999,1):Shapes.block();
    }
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){
        if(kind==Kind.MOLD||kind==Kind.BASIN){int height=kind==Kind.MOLD?8:16;return Shapes.or(box(0,0,0,16,2,16),box(0,0,0,16,height,2),box(0,0,0,2,height,16),box(14,0,0,16,height,16),box(0,0,14,16,height,16));}
        if(kind==Kind.CHANNEL){VoxelShape shape=box(5,0,5,11,8,11);if(connects(level,pos,Direction.EAST))shape=Shapes.or(shape,box(11,0,5,16,8,11));if(connects(level,pos,Direction.WEST))shape=Shapes.or(shape,box(0,0,5,5,8,11));if(connects(level,pos,Direction.SOUTH))shape=Shapes.or(shape,box(5,0,11,11,8,16));if(connects(level,pos,Direction.NORTH))shape=Shapes.or(shape,box(5,0,0,11,8,5));return shape;}
        return getShape(state,level,pos,context);
    }
    private static void give(Level level,BlockPos pos,Player player,ItemStack stack,double height){if(!player.getInventory().add(stack)){level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+height,pos.getZ()+.5,stack));}else player.inventoryMenu.broadcastChanges();}
    private InteractionResult interact(Level level,BlockPos pos,Player player,ItemStack held){
        if(level.isClientSide)return InteractionResult.SUCCESS;if(!(level.getBlockEntity(pos) instanceof FoundryBlockEntity foundry))return InteractionResult.PASS;
        if(kind.outlet()){if(!player.isShiftKeyDown()){if(held.getItem() instanceof ScrapsItem){var material=ScrapsItem.getMaterials(held);if(material!=null)foundry.filter=material.material;}else foundry.invertRedstone=!foundry.invertRedstone;foundry.changed();}return InteractionResult.CONSUME;}
        if(kind.cast()){
            if(!foundry.slots.get(1).isEmpty()){give(level,pos,player,foundry.slots.get(1).copy(),kind==Kind.MOLD?.5:.999);foundry.slots.set(1,ItemStack.EMPTY);foundry.changed();return InteractionResult.CONSUME;}
            if(held.getItem() instanceof com.hbm.machine.anvil.MoldItem&&foundry.slots.getFirst().isEmpty()){var mold=FoundryContent.MOLD.get().mold(held);if(mold.size()==(kind==Kind.BASIN?1:0)){foundry.slots.set(0,held.copyWithCount(1));held.shrink(1);SoundEvent sound=BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath("hbm","item.upgradeplug"));level.playSound(null,pos,sound,SoundSource.BLOCKS,1,1);foundry.changed();return InteractionResult.CONSUME;}}
        }
        if(held.getItem() instanceof ShovelItem&&(kind==Kind.CHANNEL||kind==Kind.TANK||kind.cast())){if(foundry.amount>0){give(level,pos,player,ScrapsItem.create(new MaterialStack(foundry.type,foundry.amount)),kind==Kind.MOLD?.5:1);foundry.amount=0;foundry.type=null;foundry.changed();}return InteractionResult.CONSUME;}
        return InteractionResult.PASS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return interact(level,pos,player,stack)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){
        if(!(level.getBlockEntity(pos) instanceof FoundryBlockEntity foundry))return false;
        if(kind.outlet()){if(tool==ToolType.SCREWDRIVER){if(!level.isClientSide){foundry.filter=null;foundry.invertFilter=false;foundry.changed();}}if(tool==ToolType.HAND_DRILL){if(!level.isClientSide){foundry.invertFilter=!foundry.invertFilter;foundry.changed();}}return false;}
        if(!kind.cast()||tool!=ToolType.SCREWDRIVER||foundry.slots.getFirst().isEmpty()||foundry.amount>0)return false;
        if(!level.isClientSide){give(level,pos,player,foundry.slots.getFirst().copy(),kind==Kind.MOLD?.5:.999);foundry.slots.set(0,ItemStack.EMPTY);foundry.changed();}return true;
    }
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){if(!state.is(next.getBlock())&&!level.isClientSide&&level.getBlockEntity(pos) instanceof FoundryBlockEntity foundry&&kind!=Kind.SLAG){if(foundry.amount>0){level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+(kind==Kind.MOLD?.5:1),pos.getZ()+.5,ScrapsItem.create(new MaterialStack(foundry.type,foundry.amount))));foundry.amount=0;}if(kind.cast())for(ItemStack stack:foundry.slots)if(!stack.isEmpty())level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+(kind==Kind.MOLD?.5:.999),pos.getZ()+.5,stack.copy()));}super.onRemove(state,level,pos,next,moved);}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){if(kind==Kind.SLAG&&level.getBlockEntity(pos) instanceof FoundryBlockEntity slag&&slag.type!=null)return ScrapsItem.create(new MaterialStack(slag.type,slag.amount));return kind==Kind.SLAG?ItemStack.EMPTY:new ItemStack(this);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){if(kind!=Kind.SLAG)return List.of(new ItemStack(this));var be=params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);return be instanceof FoundryBlockEntity slag&&slag.type!=null&&slag.amount>0?List.of(ScrapsItem.create(new MaterialStack(slag.type,slag.amount))):List.of();}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){if(kind==Kind.SLAG){if(level.getBlockEntity(pos) instanceof FoundryBlockEntity slag)slag.tickSlag();else level.removeBlock(pos,false);}}
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(kind.cast()&&level.getBlockEntity(pos) instanceof FoundryBlockEntity foundry&&foundry.amount>0&&foundry.amount>=foundry.capacity())level.addParticle(ParticleTypes.SMOKE,pos.getX()+.25+random.nextDouble()*.5,pos.getY()+(kind==Kind.MOLD?.5:.999),pos.getZ()+.25+random.nextDouble()*.5,0,0,0);}
}
