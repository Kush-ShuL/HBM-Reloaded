package com.hbm.bomb.conventional;

import com.hbm.api.block.IFuckingExplode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Source DetCord: full collision box and zero-fuse sympathetic ignition, with thin connected visual geometry. */
public final class DetCordBlock extends Block implements IFuckingExplode, IDetConnectible {
    public DetCordBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(.1F,0).requiresCorrectToolForDrops().noOcclusion()); }
    public static int connections(BlockGetter level, BlockPos pos) {
        int mask=0;
        for(Direction side:Direction.values()) if(IDetConnectible.isConnectible(level,pos.relative(side),side.getOpposite())) mask |= bit(side);
        return mask;
    }
    public static int bit(Direction side) { return switch(side) {case EAST->32;case WEST->16;case UP->8;case DOWN->4;case SOUTH->2;case NORTH->1;}; }
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving) {
        if(level.hasNeighborSignal(pos)){explodeAt(level,pos);return;}
        for(Direction side:Direction.values()){var adjacent=level.getBlockState(pos.relative(side));if(adjacent.is(Blocks.FIRE)||adjacent.is(Blocks.SOUL_FIRE)){ignite(level,pos,null);return;}}
    }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState previous,boolean moving){if(!previous.is(this)&&level.hasNeighborSignal(pos))explodeAt(level,pos);}
    /** User-requested modern ignition interaction; retains the original cord's zero-fuse propagation. */
    private void ignite(Level level,BlockPos pos,LivingEntity igniter){if(level.isClientSide||!level.getBlockState(pos).is(this))return;level.removeBlock(pos,false);var entity=new PrimedExplosive(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,igniter,this);entity.setFuse(0);level.addFreshEntity(entity);level.playSound(null,pos,SoundEvents.TNT_PRIMED,SoundSource.BLOCKS,1,1);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!stack.is(Items.FLINT_AND_STEEL)&&!stack.is(Items.FIRE_CHARGE))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        ignite(level,pos,player);if(!level.isClientSide){if(stack.is(Items.FLINT_AND_STEEL))stack.hurtAndBreak(1,player,LivingEntity.getSlotForHand(hand));else if(!player.getAbilities().instabuild)stack.shrink(1);}return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void onCaughtFire(BlockState state,Level level,BlockPos pos,Direction face,LivingEntity igniter){ignite(level,pos,igniter);}
    @Override protected void onProjectileHit(Level level,BlockState state,BlockHitResult hit,Projectile projectile){if(projectile.isOnFire())ignite(level,hit.getBlockPos(),projectile.getOwner() instanceof LivingEntity living?living:null);}
    public void explodeAt(Level level,BlockPos pos) {
        if(level.isClientSide) return;
        level.removeBlock(pos,false);
        level.explode(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,1.5F,Level.ExplosionInteraction.TNT);
    }
    @Override public boolean dropFromExplosion(Explosion explosion) {return false;}
    @Override public void wasExploded(Level level,BlockPos pos,Explosion explosion) {
        if(level.isClientSide)return;
        var entity=new PrimedExplosive(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,explosion==null?null:explosion.getIndirectSourceEntity(),this);
        entity.setFuse(0);level.addFreshEntity(entity);
    }
    @Override public void explodeEntity(Level level,double x,double y,double z,PrimedExplosive entity) {explodeAt(level,BlockPos.containing(x,y,z));}
}
