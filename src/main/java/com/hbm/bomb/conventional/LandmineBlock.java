package com.hbm.bomb.conventional;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.interfaces.IBomb;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Source Landmine variants, sharing TileEntityLandmine's clearance arming. */
public final class LandmineBlock extends Block implements EntityBlock,IBomb {
    public enum Kind { AP, HE, SHRAP, NAVAL, FAT }
    public static boolean safeMode;
    private final Kind kind;
    public LandmineBlock(Kind kind){super(Properties.of().mapColor(MapColor.METAL).strength(1).requiresCorrectToolForDrops().noOcclusion());this.kind=kind;}
    public Kind kind(){return kind;}
    public double range(){return switch(kind){case AP,SHRAP->1.5;case HE->2;case NAVAL,FAT->2.5;};}
    public double height(){return kind==Kind.HE?5:1;}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return switch(kind){case AP,SHRAP->box(5,0,5,11,1,11);case HE->box(4,0,4,12,2,12);case NAVAL->box(0,0,0,16,16,16);case FAT->box(5,0,4,11,6,12);};}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){var below=pos.below();var support=level.getBlockState(below);return support.isFaceSturdy(level,below,Direction.UP)||support.getBlock() instanceof FenceBlock;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){
        if(level.hasNeighborSignal(pos))explode(level,pos.getX(),pos.getY(),pos.getZ());
        if(!canSurvive(state,level,pos)){if(!safeMode)explode(level,pos.getX(),pos.getY(),pos.getZ());else level.removeBlock(pos,false);}
    }
    @Override public BlockState playerWillDestroy(Level level,BlockPos pos,BlockState state,Player player){if(!safeMode)explode(level,pos.getX(),pos.getY(),pos.getZ());return super.playerWillDestroy(level,pos,state,player);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new LandmineBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide||type!=LandmineContent.ENTITY.get()?null:(world,pos,block,entity)->LandmineBlockEntity.tick(world,pos,block,(LandmineBlockEntity)entity);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("hbm:defuser"))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        safeMode=true;try{level.removeBlock(pos,false);
            float x=level.random.nextFloat()*.6F+.2F,y=level.random.nextFloat()*.2F,z=level.random.nextFloat()*.6F+.2F;
            var drop=new ItemEntity(level,pos.getX()+x,pos.getY()+y+1,pos.getZ()+z,new ItemStack(this));drop.setDeltaMovement((float)level.random.nextGaussian()*.05F,(float)level.random.nextGaussian()*.05F+.2F,(float)level.random.nextGaussian()*.05F);if(!level.isClientSide)level.addFreshEntity(drop);
        }finally{safeMode=false;}
        // Source Landmine checks the item directly; it does not consume tooling durability.
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public BombReturnCode explode(Level level,int x,int y,int z){
        if(!level.isClientSide){safeMode=true;try{level.destroyBlock(new BlockPos(x,y,z),false);}finally{safeMode=false;}
            var explosion=kind==Kind.NAVAL?new ExplosionVNT(level,x+5,y+5,z+5,25F):new ExplosionVNT(level,x+.5,y+.5,z+.5,kind==Kind.FAT?10F:kind==Kind.HE?4F:3F);
            if(kind==Kind.AP)explosion.setEntityProcessor(new EntityProcessorCrossSmooth(.5,LandmineContent.AP_DAMAGE.get().floatValue()).setupPiercing(5F,.2F)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(5,1,.5F));
            else if(kind==Kind.HE)explosion.setBlockAllocator(new BlockAllocatorStandard()).setBlockProcessor(new BlockProcessorStandard()).setEntityProcessor(new EntityProcessorCrossSmooth(1,LandmineContent.HE_DAMAGE.get().floatValue()).setupPiercing(15F,.2F)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(15,3.5F,1.25F));
            else if(kind==Kind.SHRAP)explosion.setEntityProcessor(new EntityProcessorCrossSmooth(.5,LandmineContent.SHRAP_DAMAGE.get().floatValue())).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(5,1,.5F));
            else if(kind==Kind.FAT)explosion.setBlockAllocator(new BlockAllocatorStandard(64)).setBlockProcessor(new BlockProcessorStandard()).setEntityProcessor(new EntityProcessorCrossSmooth(2,LandmineContent.NUKE_DAMAGE.get().floatValue()).withRangeMod(1.5F)).setPlayerProcessor(new PlayerProcessorStandard());
            else explosion.setBlockAllocator(new BlockAllocatorWater(32)).setBlockProcessor(new BlockProcessorStandard()).setEntityProcessor(new EntityProcessorCrossSmooth(.5,LandmineContent.NAVAL_DAMAGE.get().floatValue()).setupPiercing(5,.2F)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,1,.5F));
            explosion.explode();
            if(kind==Kind.SHRAP){ExplosionLarge.spawnShrapnelShower(level,x+.5,y+.5,z+.5,0,1,0,45,.2D);ExplosionLarge.spawnShrapnels(level,x+.5,y+.5,z+.5,5);}
            if(kind==Kind.NAVAL){ExplosionLarge.spawnParticlesRadial(level,x+.5,y+2,z+.5,30);ExplosionLarge.spawnRubble(level,x+.5,y+.5,z+.5,5);if(isWaterAbove(level,x,y,z))ExplosionLarge.spawnFoam(level,x+.5,y+.5,z+.5,60);}
            if(kind==Kind.FAT){com.hbm.bomb.nuclear.SatelliteBursts.reportEvent(level,com.hbm.bomb.nuclear.SatelliteBursts.DURATION_LOW,com.hbm.bomb.nuclear.SatelliteBursts.BurstIntensity.LOW,x,z);ConventionalRadiation.incrementRad(level,x,y,z,1.5F);com.hbm.bomb.nuclear.NuclearMukeEffects.send(level,x+.5,y+.5,z+.5,"muke",com.hbm.LegacyStartup.polaroidId==11||level.random.nextInt(100)==0);level.playSound(null,x+.5,y+.5,z+.5,HbmSounds.get("weapon.mukeExplosion"),SoundSource.BLOCKS,25,.9F);}
        }
        return BombReturnCode.DETONATED;
    }
    private boolean isWaterAbove(Level level,int x,int y,int z){for(int ox=-1;ox<=1;ox++)for(int oz=-1;oz<=1;oz++)if(level.getBlockState(new BlockPos(x+ox,y+1,z+oz)).is(Blocks.WATER))return true;return false;}
}
