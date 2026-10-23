package com.hbm.bomb.conventional;

import com.hbm.api.block.IFuckingExplode;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.interfaces.IBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Source ExplosiveCharge (det_charge) and DetMiner, retaining their separate explosion resolutions/drops. */
public final class DetonationChargeBlock extends Block implements IBomb,IFuckingExplode,IDetConnectible {
    public enum Kind { STANDARD,MINER,NUCLEAR }
    private final boolean mining;
    private final boolean nuclear;
    public DetonationChargeBlock(boolean mining){this(mining?Kind.MINER:Kind.STANDARD);}
    public DetonationChargeBlock(Kind kind){super(Properties.of().mapColor(MapColor.METAL).strength(.1F,0).requiresCorrectToolForDrops());this.mining=kind==Kind.MINER;this.nuclear=kind==Kind.NUCLEAR;}
    @Override public boolean canConnectToDetCord(BlockGetter level,BlockPos pos,Direction face){return !mining;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(level.hasNeighborSignal(pos))explode(level,pos.getX(),pos.getY(),pos.getZ());}
    @Override public BombReturnCode explode(Level level,int x,int y,int z){
        if(!level.isClientSide){level.removeBlock(new BlockPos(x,y,z),false);
            if(mining){var explosion=new ExplosionNT(level,null,x+.5,y+.5,z+.5,4);explosion.addAllAttrib(ExAttrib.ALLDROP,ExAttrib.NOHURT);explosion.doExplosionA();explosion.doExplosionB(false);SmokeBurstEffects.cloud(level,x+.5,y+.5,z+.5,30);}
            else if(nuclear){int radius=com.hbm.bomb.nuclear.NuclearConfig.radius("missile");level.addFreshEntity(com.hbm.bomb.nuclear.NuclearExplosionMK5.statFac(level,radius,x+.5,y+.5,z+.5));com.hbm.bomb.nuclear.NuclearTorex.statFacStandard(level,x+.5,y+.5,z+.5,radius);}
            else{new ExplosionNT(level,null,x+.5,y+.5,z+.5,15).overrideResolution(64).explode();LargeConventionalEffects.standard(level,x+.5,y+1,z+.5);}
        }
        return BombReturnCode.DETONATED;
    }
    @Override public boolean dropFromExplosion(Explosion explosion){return false;}
    @Override public void wasExploded(Level level,BlockPos pos,Explosion explosion){if(!level.isClientSide){var entity=new PrimedExplosive(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,explosion==null?null:explosion.getIndirectSourceEntity(),this);entity.setFuse(0);level.addFreshEntity(entity);}}
    @Override public void explodeEntity(Level level,double x,double y,double z,PrimedExplosive entity){var pos=BlockPos.containing(x,y,z);explode(level,pos.getX(),pos.getY(),pos.getZ());}
}
