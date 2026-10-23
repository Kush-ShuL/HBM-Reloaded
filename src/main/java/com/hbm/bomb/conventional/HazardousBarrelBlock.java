package com.hbm.bomb.conventional;

import com.hbm.api.block.IFuckingExplode;
import com.hbm.explosion.ExplosionThermo;
import com.hbm.weapon.sedna.ShotSensitiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Original RedBarrel plus BlockDetonatable/BlockFlammable triggers, including LOX's distinct blast/freezer. */
public class HazardousBarrelBlock extends Block implements IFuckingExplode,ShotSensitiveBlock {
    public enum Kind { RED, PINK, LOX, YELLOW, VITRIFIED, TAINT }
    private final Kind kind;
    private final java.util.Random radiationRandom=new java.util.Random();
    public HazardousBarrelBlock(Kind kind){super(Properties.of().mapColor(MapColor.METAL).strength(.5F,1.5F).requiresCorrectToolForDrops().noOcclusion());this.kind=kind;}
    public Kind kind(){return kind;}
    private boolean flammable(){return kind==Kind.RED||kind==Kind.PINK;}
    private boolean radioactive(){return kind==Kind.YELLOW||kind==Kind.VITRIFIED;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return box(2,0,2,14,16,14);}
    @Override public int getFlammability(BlockState state,BlockGetter level,BlockPos pos,Direction face){return flammable()?15:0;}
    @Override public int getFireSpreadSpeed(BlockState state,BlockGetter level,BlockPos pos,Direction face){return flammable()?2:0;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(!level.isClientSide&&flammable())for(var side:Direction.values())if(level.getBlockState(pos.relative(side)).is(Blocks.FIRE)){level.removeBlock(pos,false);wasExploded(level,pos,null);return;}}
    @Override public boolean dropFromExplosion(Explosion explosion){return false;}
    @Override public void wasExploded(Level level,BlockPos pos,Explosion explosion){if(!level.isClientSide&&kind!=Kind.VITRIFIED){var primed=new PrimedExplosive(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,explosion==null?null:explosion.getIndirectSourceEntity(),this);primed.setFuse(level.random.nextInt(100)+50);primed.detonateOnCollision=true;level.addFreshEntity(primed);}}
    @Override public void onShot(Level level,BlockPos pos){if(!flammable())return;level.removeBlock(pos,false);explodeEntity(level,pos.getX(),pos.getY(),pos.getZ(),null);}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){super.onPlace(state,level,pos,old,moving);if(radioactive())level.scheduleTick(pos,this,20);}
    @Override protected void tick(BlockState state,net.minecraft.server.level.ServerLevel level,BlockPos pos,net.minecraft.util.RandomSource random){if(radioactive()){com.hbm.radiation.HbmRadiation.incrementRad(level,pos.getX(),pos.getY(),pos.getZ(),kind==Kind.YELLOW?5F:.5F);level.scheduleTick(pos,this,20);}}
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,net.minecraft.util.RandomSource random){if(radioactive())level.addParticle(com.hbm.particle.LegacyParticles.AURA.get(),pos.getX()+random.nextFloat()*.5F+.25F,pos.getY()+1.1F,pos.getZ()+random.nextFloat()*.5F+.25F,0,0,0);}
    @Override public void explodeEntity(Level level,double x,double y,double z,PrimedExplosive entity){
        var pos=BlockPos.containing(x,y,z);
        if(flammable())level.explode(entity,x,y,z,2.5F,true,Level.ExplosionInteraction.TNT);
        else if(kind==Kind.LOX){
            level.explode(entity,x,y,z,1,false,Level.ExplosionInteraction.NONE);
            ExplosionThermo.freeze(level,pos.getX(),pos.getY(),pos.getZ(),7);
            ExplosionThermo.snow(level,pos.getX(),pos.getY(),pos.getZ(),7);
            ExplosionThermo.freezer(level,pos.getX(),pos.getY(),pos.getZ(),7);
        }
        else{
            if(radiationRandom.nextInt(3)==0)level.setBlockAndUpdate(pos,com.hbm.block.fluid.HbmWorldFluids.block("toxic_block").defaultBlockState());
            else level.explode(entity,x,y,z,12,Level.ExplosionInteraction.TNT);
            com.hbm.explosion.ExplosionNukeGeneric.waste(level,pos.getX(),pos.getY(),pos.getZ(),35);
            for(int i=-5;i<=5;i++)for(int j=-5;j<=5;j++)for(int k=-5;k<=5;k++){var target=pos.offset(i,j,k);if(level.random.nextInt(5)==0&&level.getBlockState(target).isAir())level.setBlockAndUpdate(target,com.hbm.block.gas.GasContent.BLOCKS_BY_KIND.get(com.hbm.block.gas.GasKind.RADON_DENSE).get().defaultBlockState());}
            if(level instanceof net.minecraft.server.level.ServerLevel server)com.hbm.radiation.HbmRadiation.incrementRad(server,pos.getX(),pos.getY(),pos.getZ(),35);
        }
    }
}
