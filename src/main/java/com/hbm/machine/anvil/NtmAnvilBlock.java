package com.hbm.machine.anvil;
import com.hbm.block.falling.*;
import com.hbm.entity.LegacyFallingBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Original NTMAnvil is a custom falling block with no repair/enchanting or vanilla-anvil wear behavior. */
public final class NtmAnvilBlock extends Block implements Fallable {
    public static final IntegerProperty META=LegacyFallingBlock.LEGACY_META;public static boolean fallInstantly;
    private final AnvilContent.Tier tier;private final MapCodec<NtmAnvilBlock> codec;
    public NtmAnvilBlock(AnvilContent.Tier tier){super(Properties.of().mapColor(MapColor.METAL).strength(5,60).sound(SoundType.ANVIL).noOcclusion().requiresCorrectToolForDrops());this.tier=tier;codec=simpleCodec(ignored->new NtmAnvilBlock(tier));registerDefaultState(stateDefinition.any().setValue(META,0));}
    public AnvilContent.Tier tier(){return tier;}
    @Override protected MapCodec<? extends Block> codec(){return codec;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,context.getHorizontalDirection().get3DDataValue());}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);return meta>=2&&meta<=5?state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue()):state;}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);return meta>=2&&meta<=5?rotate(state,mirror.getRotation(Direction.from3DDataValue(meta))):state;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return switch(state.getValue(META)){case 2,3->box(0,0,4,16,12,12);case 4,5->box(4,0,0,12,12,16);default->Shapes.block();};}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server)server.openMenu(new SimpleMenuProvider((id,inventory,viewer)->new AnvilMenu(id,inventory,tier.tier()),Component.translatable("container.anvil",tier.tier())),buffer->buffer.writeInt(tier.tier()));return InteractionResult.CONSUME;}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState previous,boolean moving){if(!previous.is(this))level.scheduleTick(pos,this,2);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){level.scheduleTick(pos,this,2);}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){if(!LegacyFallingRules.isFree(level.getBlockState(pos.below()))||pos.getY()<level.getMinBuildHeight())return;if(!fallInstantly&&level.hasChunksAt(pos.offset(-32,-32,-32),pos.offset(32,32,32)))level.addFreshEntity(new LegacyFallingBlockEntity(level,pos,state));else{level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);BlockPos target=pos;while(LegacyFallingRules.isFree(level.getBlockState(target.below()))&&target.getY()>level.getMinBuildHeight())target=target.below();if(target.getY()>level.getMinBuildHeight())level.setBlock(target,defaultBlockState(),3);}}
}
