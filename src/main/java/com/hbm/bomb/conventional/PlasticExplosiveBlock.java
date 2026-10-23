package com.hbm.bomb.conventional;

import com.hbm.api.block.IFuckingExplode;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.interfaces.IBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;

/** Original storage-block plastic explosive; intentionally zero fire chance and no explosion drops. */
public final class PlasticExplosiveBlock extends Block implements IBomb,IFuckingExplode {
    public static final DirectionProperty FACING=DirectionProperty.create("facing");
    public PlasticExplosiveBlock() {super(Properties.of().mapColor(MapColor.FIRE).strength(2,1.2F).sound(SoundType.METAL));registerDefaultState(defaultBlockState().setValue(FACING,Direction.DOWN));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        var player=context.getPlayer();var pos=context.getClickedPos();Direction facing=Direction.DOWN;
        if(player!=null) {
            double eyes=player.getY()+1.82D;
            if(Math.abs((float)player.getX()-pos.getX())<2F&&Math.abs((float)player.getZ()-pos.getZ())<2F&&eyes-pos.getY()>2D)facing=Direction.DOWN;
            else if(Math.abs((float)player.getX()-pos.getX())<2F&&Math.abs((float)player.getZ()-pos.getZ())<2F&&pos.getY()-eyes>0D)facing=Direction.UP;
            else facing=switch(Mth.floor(player.getYRot()*4F/360F+.5D)&3){case 0->Direction.SOUTH;case 1->Direction.WEST;case 2->Direction.NORTH;default->Direction.EAST;};
        }
        return defaultBlockState().setValue(FACING,facing);
    }
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(level.hasNeighborSignal(pos))explode(level,pos.getX(),pos.getY(),pos.getZ());}
    @Override public BombReturnCode explode(Level level,int x,int y,int z) {
        if(!level.isClientSide)new ExplosionVNT(level,x+.5,y+.5,z+.5,20).makeStandard().setBlockProcessor(new BlockProcessorStandard().setNoDrop()).explode();
        return BombReturnCode.DETONATED;
    }
    @Override public boolean dropFromExplosion(Explosion explosion){return false;}
    @Override public void wasExploded(Level level,BlockPos pos,Explosion explosion){
        if(level.isClientSide)return;
        var primed=new PrimedExplosive(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,explosion==null?null:explosion.getIndirectSourceEntity(),this);primed.setFuse(0);level.addFreshEntity(primed);
    }
    @Override public void explodeEntity(Level level,double x,double y,double z,PrimedExplosive entity){var pos=BlockPos.containing(x,y,z);explode(level,pos.getX(),pos.getY(),pos.getZ());}
}
