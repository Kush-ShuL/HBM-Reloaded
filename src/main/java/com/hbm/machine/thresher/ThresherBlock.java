package com.hbm.machine.thresher;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.machine.woodprocessing.AutosawEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Original single iron cube, yaw metadata3/4/2/5, fuel-identifier control and screwdriver suspension. */
public final class ThresherBlock extends BaseEntityBlock implements ToolableBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    public ThresherBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new ThresherBlock());}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getHorizontalDirection().get3DDataValue());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ThresherEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ThresherContent.ENTITY.get(),ThresherEntity::tick);}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    private InteractionResult activate(Level level,BlockPos pos,Player player){
        if(level.isClientSide||player.isShiftKeyDown())return InteractionResult.SUCCESS;
        var stack=player.getMainHandItem();
        if(stack.getItem() instanceof FluidIdentifier identifier&&level.getBlockEntity(pos) instanceof ThresherEntity machine){
            var type=identifier.getType(level,pos,stack);
            if(AutosawEntity.ACCEPTED_FUELS.contains(type)){machine.tank.setTankType(type);machine.setChanged();player.displayClientMessage(Component.translatable("message.hbm.woodprocessing.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);return InteractionResult.SUCCESS;}
        }
        return InteractionResult.PASS;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return activate(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER||!(level.getBlockEntity(pos) instanceof ThresherEntity machine))return false;machine.isSuspended=!machine.isSuspended;machine.setChanged();return true;}
}
