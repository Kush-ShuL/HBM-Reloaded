package com.hbm.machine.reactor;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Source three vertical cells; the control-rod model is rendered only at the real core. */
public final class ResearchReactorBlock extends DummyableBlock {
    private static final MapCodec<ResearchReactorBlock> CODEC=simpleCodec(ignored->new ResearchReactorBlock());
    public ResearchReactorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{2,0,0,0,0,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ResearchReactorBlockEntity(pos,state):isPort(state)?new com.hbm.multiblock.proxy.SourceStandaloneProxy(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ReactorContent.RESEARCH_ENTITY.get(),ResearchReactorBlockEntity::tick):null;}
    @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,net.minecraft.world.entity.player.Player player,net.minecraft.world.phys.BlockHitResult hit){
        if(!level.isClientSide&&!player.isShiftKeyDown()){
            var root=player.getPersistentData();var persistent=root.getCompound(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG);
            persistent.putLong("fbiMark",level.getGameTime()+20*60*20);root.put(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG,persistent);
        }
        return super.useWithoutItem(state,level,pos,player,hit);
    }
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){
        for(var side:Direction.Plane.HORIZONTAL){var at=pos.relative(side);if(!level.getFluidState(at).is(FluidTags.WATER))continue;
            double x=pos.getX()+.5+side.getStepX()+random.nextDouble()-.5,y=pos.getY()+.5+random.nextDouble()-.5,z=pos.getZ()+.5+side.getStepZ()+random.nextDouble()-.5;
            if(side.getStepX()!=0)x=pos.getX()+.5+side.getStepX()*.5+random.nextDouble()*.125*side.getStepX();
            if(side.getStepZ()!=0)z=pos.getZ()+.5+side.getStepZ()*.5+random.nextDouble()*.125*side.getStepZ();
            level.addParticle(ParticleTypes.BUBBLE,x,y,z,0,.2,0);
        }
    }
}
