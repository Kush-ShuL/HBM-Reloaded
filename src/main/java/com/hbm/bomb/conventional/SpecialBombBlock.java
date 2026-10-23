package com.hbm.bomb.conventional;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionEmp;
import com.hbm.explosion.ExplosionThermo;
import com.hbm.interfaces.IBomb;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.sounds.SoundSource;

/** Source BombThermo and BombFloat triggers; each effect delegates to its real original shared algorithm. */
public final class SpecialBombBlock extends Block implements IBomb {
    public enum Kind { ENDO, EXO, FLOAT, EMP }
    private final Kind kind;
    public SpecialBombBlock(Kind kind){super(Properties.of().mapColor(MapColor.METAL).strength(5,120).requiresCorrectToolForDrops());this.kind=kind;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(level.hasNeighborSignal(pos))explode(level,pos.getX(),pos.getY(),pos.getZ());}
    @Override public BombReturnCode explode(Level level,int x,int y,int z){
        if(kind==Kind.ENDO||kind==Kind.EXO){
            level.removeBlock(new BlockPos(x,y,z),false);
            if(kind==Kind.ENDO){ExplosionThermo.freeze(level,x,y,z,15);ExplosionThermo.freezer(level,x,y,z,20);}
            else{ExplosionThermo.scorch(level,x,y,z,15);ExplosionThermo.setEntitiesOnFire(level,x,y,z,20);}
            level.explode(null,x,y,z,5,Level.ExplosionInteraction.TNT);
        }else{
            var sound=HbmSounds.get("weapon.sparkShoot");float pitch=level.random.nextFloat()*.2F+.9F;
            if(level.isClientSide)level.playLocalSound(x,y,z,sound,SoundSource.BLOCKS,5,pitch,false);else level.playSound(null,x,y,z,sound,SoundSource.BLOCKS,5,pitch);
            if(!level.isClientSide){level.removeBlock(new BlockPos(x,y,z),false);
                if(kind==Kind.FLOAT){ExplosionChaos.floater(level,x,y,z,15,50);ExplosionChaos.move(level,x,y,z,15,0,50,0);}
                else{ExplosionEmp.empBlast(level,x,y,z,50);var wave=new com.hbm.entity.effect.EntityEMPBlast(level,50);wave.setPos(x+.5,y+.5,z+.5);level.addFreshEntity(wave);}
            }
        }
        return BombReturnCode.DETONATED;
    }
}
