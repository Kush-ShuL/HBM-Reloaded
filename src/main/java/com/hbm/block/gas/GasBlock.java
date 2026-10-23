package com.hbm.block.gas;

import com.hbm.damage.HbmDamageSources;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.exposure.HbmExposure;
import com.hbm.hazard.HbmHazards;
import com.hbm.radiation.HbmRadiation;
import com.mojang.serialization.MapCodec;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** X5846 gas cells move as whole blocks. Metadata is a wake marker, not concentration. */
public final class GasBlock extends Block {
    public static final IntegerProperty WAKE_MARKER=IntegerProperty.create("wake_marker",0,15);
    private static final Set<Block> FIRE_SOURCES=new HashSet<>(List.of(Blocks.FIRE,Blocks.LAVA,Blocks.TORCH,Blocks.WALL_TORCH,Blocks.JACK_O_LANTERN));
    private final GasKind kind;
    private final MapCodec<GasBlock> codec;
    public GasBlock(GasKind kind){
        super(Properties.of().mapColor(MapColor.NONE).strength(0,0).replaceable().noCollission().noOcclusion().noLootTable()
                .pushReaction(PushReaction.BLOCK).isSuffocating((state,level,pos)->false).isViewBlocking((state,level,pos)->false));
        this.kind=kind;codec=simpleCodec(ignored->new GasBlock(kind));
        registerDefaultState(stateDefinition.any().setValue(WAKE_MARKER,0));
    }
    public GasKind kind(){return kind;}
    public static void addFireSource(Block block){FIRE_SOURCES.add(block);}
    @Override protected MapCodec<GasBlock> codec(){return codec;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(WAKE_MARKER);}
    @Override protected RenderShape getRenderShape(BlockState state){return kind==GasKind.CHLORINE?RenderShape.MODEL:RenderShape.INVISIBLE;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        return defaultBlockState().setValue(WAKE_MARKER,context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&15);
    }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){
        if(!level.isClientSide&&!old.is(this)&&state.getValue(WAKE_MARKER)==0)level.scheduleTick(pos,this,10);
    }
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){
        if(level.isClientSide)return;
        if(kind.flammable()){
            if(nearFire(level,pos))level.scheduleTick(pos,this,2);
        }else if(state.getValue(WAKE_MARKER)!=0){
            level.setBlock(pos,state.setValue(WAKE_MARKER,0),Block.UPDATE_INVISIBLE);
            level.scheduleTick(pos,this,10);
        }
    }
    private static boolean available(Level level,BlockPos pos){return !level.isOutsideBuildHeight(pos)&&level.hasChunkAt(pos);}
    private static boolean air(Level level,BlockPos pos){return available(level,pos)&&level.getBlockState(pos).is(Blocks.AIR);}
    private static boolean nearFire(Level level,BlockPos pos){
        for(Direction direction:Direction.values()){
            BlockPos adjacent=pos.relative(direction);
            if(available(level,adjacent)&&FIRE_SOURCES.contains(level.getBlockState(adjacent).getBlock()))return true;
        }
        return false;
    }
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
        if(kind.flammable()&&nearFire(level,pos)){combust(level,pos);return;}
        if(kind==GasKind.RADON_DENSE&&random.nextInt(20)==0&&level.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK))GasTerrainEffects.wasteEarth(level,pos.below());
        if(kind==GasKind.RADON_TOMB&&random.nextInt(10)==0){
            BlockPos below=pos.below();BlockState ground=level.getBlockState(below);
            if(ground.is(Blocks.GRASS_BLOCK)){
                if(random.nextInt(5)==0)level.setBlock(below,Blocks.COARSE_DIRT.defaultBlockState(),Block.UPDATE_ALL);
                else GasTerrainEffects.wasteEarth(level,below);
            }
            Block block=ground.getBlock();
            if((block instanceof BushBlock||block instanceof LeavesBlock||block instanceof VineBlock||block instanceof SugarCaneBlock)&&!ground.isSolidRender(level,below))level.removeBlock(below,false);
        }
        if(kind==GasKind.MELTDOWN){
            Direction direction=Direction.from3DDataValue(random.nextInt(6));BlockPos adjacent=pos.relative(direction);
            if(random.nextInt(7)==0&&air(level,adjacent))level.setBlock(adjacent,GasContent.BLOCKS_BY_KIND.get(GasKind.RADON_DENSE).get().defaultBlockState(),Block.UPDATE_ALL);
            if(level.canSeeSky(pos))HbmRadiation.incrementRad(level,pos.getX(),pos.getY(),pos.getZ(),5);
        }
        if(random.nextInt(kind.decayChance())==0&&(!kind.flammable()||air(level,pos.below()))){
            level.removeBlock(pos,false);
            if(kind==GasKind.RADON_DENSE)GasTerrainEffects.fallout(level,pos);
            return;
        }
        Direction first=kind.firstDirection(level.random);
        if(!tryMove(level,pos,first)&&!tryMove(level,pos,GasKind.horizontal(level.random)))level.scheduleTick(pos,this,kind.delay(level.random));
    }
    private boolean tryMove(ServerLevel level,BlockPos pos,Direction direction){
        BlockPos target=pos.relative(direction);
        if(!air(level,target))return false;
        level.removeBlock(pos,false);level.setBlock(target,defaultBlockState(),Block.UPDATE_ALL);
        // Keep the source onPlace(10) then movement-delay scheduling order.
        level.scheduleTick(target,this,kind.delay(level.random));return true;
    }
    private void combust(ServerLevel level,BlockPos pos){
        level.setBlock(pos,Blocks.FIRE.defaultBlockState(),Block.UPDATE_ALL);
        if(kind==GasKind.EXPLOSIVE)level.explode(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,3,true,Level.ExplosionInteraction.NONE);
    }
    @Override public boolean isFlammable(BlockState state,BlockGetter level,BlockPos pos,Direction face){return kind.flammable();}
    @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){
        if(!(level instanceof ServerLevel server))return;
        if(kind.flammable()&&entity.isOnFire())combust(server,pos);
        if(!(entity instanceof LivingEntity living))return;
        switch(kind){
            case RADON -> {
                if(!HbmExposure.protectAndUseFilter(living,Hazard.PARTICLE_FINE,1)){
                    HbmExposure.contaminate(living,HbmExposure.Contamination.RAD_BYPASS,.05F);HbmExposure.incrementAsbestos(living,1);
                }
            }
            case RADON_DENSE -> {
                if(!HbmExposure.protectAndUseFilter(living,Hazard.PARTICLE_FINE,1)){
                    HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,.5F);
                    living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION,300,0));HbmExposure.incrementAsbestos(living,5);
                }
            }
            case RADON_TOMB -> {
                living.removeEffect(ExposureEffects.RADAWAY);living.removeEffect(ExposureEffects.RADX);
                HbmExposure.contaminate(living,HbmExposure.Contamination.RAD_BYPASS,.5F);HbmExposure.incrementAsbestos(living,10);
            }
            case MELTDOWN -> {
                HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,.5F);
                living.addEffect(new MobEffectInstance(ExposureEffects.RADIATION,1200,2));
                if(!HbmExposure.protectAndUseFilter(living,Hazard.PARTICLE_FINE,1))HbmExposure.incrementAsbestos(living,5);
            }
            case ASBESTOS -> {if(!HbmExposure.protectedFrom(living,Hazard.PARTICLE_FINE))HbmExposure.incrementAsbestos(living,1);}
            case COAL -> {if(!HbmExposure.protectedFrom(living,Hazard.PARTICLE_COARSE))HbmExposure.incrementBlackLung(living,10);}
            case MONOXIDE -> {if(!HbmExposure.protectAndUseFilter(living,Hazard.GAS_MONOXIDE,1))living.hurt(HbmDamageSources.source(level,HbmDamageSources.monoxide),1);}
            case CHLORINE -> {
                if(!HbmExposure.protectAndUseFilter(living,Hazard.GAS_LUNG,1)){
                    living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,100,0));living.addEffect(new MobEffectInstance(MobEffects.POISON,400,2));
                    living.addEffect(new MobEffectInstance(MobEffects.WITHER,20,1));living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,600,1));
                    living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,600,2));
                }
            }
            default -> {}
        }
    }
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){GasVisuals.display(level,pos,kind,random);}
}
