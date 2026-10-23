package com.hbm.machine.fan;

import com.hbm.api.tool.ToolableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.*;

/** Full source collision, nonopaque model, exact piston placement and four solid sides around the fan axis. */
public final class FanBlock extends BaseEntityBlock implements ToolableBlock {
    public static final IntegerProperty META = IntegerProperty.create("legacy_meta", 0, 15);
    private static final VoxelShape[] SUPPORT = new VoxelShape[3];
    static {
        // Hollow support prisms: all four transverse faces are full; the two axial faces have a hole.
        // Collision itself remains the complete inherited source cube.
        double edge = 1D / 16;
        SUPPORT[0] = Shapes.or(Shapes.box(0,0,0,1,edge,1), Shapes.box(0,1-edge,0,1,1,1), Shapes.box(0,0,0,1,1,edge), Shapes.box(0,0,1-edge,1,1,1));
        SUPPORT[1] = Shapes.or(Shapes.box(0,0,0,edge,1,1), Shapes.box(1-edge,0,0,1,1,1), Shapes.box(0,0,0,1,1,edge), Shapes.box(0,0,1-edge,1,1,1));
        SUPPORT[2] = Shapes.or(Shapes.box(0,0,0,edge,1,1), Shapes.box(1-edge,0,0,1,1,1), Shapes.box(0,0,0,1,edge,1), Shapes.box(0,1-edge,0,1,1,1));
    }
    public FanBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false));
        registerDefaultState(stateDefinition.any().setValue(META,0));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) { builder.add(META); }
    public BlockState fromLegacy(int meta) { return defaultBlockState().setValue(META,meta & 15); }
    /** Source ForgeDirection.getOrientation returns UNKNOWN for metadata6..15, with all offsets zero. */
    public static Direction direction(BlockState state) { int meta=state.getValue(META); return meta<6?Direction.from3DDataValue(meta):null; }
    public static int rotateMeta(int meta,int turns) { if(meta<2||meta>5)return meta;Direction direction=Direction.from3DDataValue(meta);for(int i=0;i<(turns&3);i++)direction=direction.getClockWise();return direction.get3DDataValue(); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return fromLegacy(context.getPlayer()==null?0:placement(context.getClickedPos(),context.getPlayer())); }
    private static int placement(BlockPos pos, LivingEntity entity) {
        // Exact 1.7 BlockPistonBase.determineOrientation thresholds. Modern getY is the old bounding-box bottom;
        // the old posY +1.82 -yOffset therefore becomes getY +1.82, retaining crouch-independent source behavior.
        if(Math.abs((float)entity.getX()-pos.getX())<2 && Math.abs((float)entity.getZ()-pos.getZ())<2) {
            double eye=entity.getY()+1.82;
            if(eye-pos.getY()>2)return 1;
            if(pos.getY()-eye>0)return 0;
        }
        return switch(Mth.floor(entity.getYRot()*4F/360F+.5)&3) { case 0->2;case 1->5;case 2->3;default->4; };
    }
    @Override protected BlockState rotate(BlockState state,Rotation rotation) { return fromLegacy(rotateMeta(state.getValue(META),switch(rotation){case NONE->0;case CLOCKWISE_90->1;case CLOCKWISE_180->2;case COUNTERCLOCKWISE_90->3;})); }
    @Override protected BlockState mirror(BlockState state,Mirror mirror) { int meta=state.getValue(META);if(mirror==Mirror.LEFT_RIGHT&&(meta==2||meta==3)||mirror==Mirror.FRONT_BACK&&(meta==4||meta==5))meta^=1;return fromLegacy(meta); }
    @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos) { Direction direction=direction(state);return direction==null?Shapes.block():SUPPORT[direction.getAxis().ordinal()]; }
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos) { return Shapes.empty(); }
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos) { return true; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return new FanEntity(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) { return createTickerHelper(type,FanContent.ENTITY.get(),FanEntity::tick); }
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool) {
        if(tool==ToolType.SCREWDRIVER) {
            int meta=level.getBlockState(pos).getValue(META);
            if(meta<6)level.setBlock(pos,fromLegacy(meta^1),UPDATE_ALL);
            return true;
        }
        if(tool!=ToolType.HAND_DRILL&&tool!=ToolType.DEFUSER)return false;
        if(level.getBlockEntity(pos) instanceof FanEntity fan) {
            if(tool==ToolType.HAND_DRILL)fan.falloff=!fan.falloff;else fan.suck=!fan.suck;
            fan.setChanged();
            if(!level.isClientSide) {
                FanModePacket.send(player,tool==ToolType.HAND_DRILL?(fan.falloff?0:1):(fan.suck?2:3));
                level.playSound(null,pos,SoundEvents.UI_BUTTON_CLICK.value(),SoundSource.BLOCKS,.5F,.5F);
                fan.sync();
            }
        }
        return true;
    }
}
