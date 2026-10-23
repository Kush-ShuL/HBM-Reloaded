package com.hbm.bomb.fishing;

import com.hbm.block.fluid.LegacyFluidBlock;
import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;

/** Exact source-specific pre-super buoyancy; native ItemEntity owns pickup/storage/merging/lifespan. */
public final class EntityItemBuoyant extends ItemEntity {
    public EntityItemBuoyant(EntityType<? extends EntityItemBuoyant> type,Level level){super(type,level);}
    public EntityItemBuoyant(Level level,double x,double legacyY,double z,ItemStack stack){
        this(FishingDynamiteContent.BUOYANT.get(),level);
        setPos(x,legacyY-getBbHeight()/2F,z);setItem(stack);
        // Old EntityItem constructor used Math.random, not the fishing world's RNG stream.
        setDeltaMovement(Math.random()*.2D-.1D,.2D,Math.random()*.2D-.1D);
        lifespan=stack.getEntityLifespan(level);
    }
    public double legacyY(){return getY()+getBbHeight()/2F;}
    @Override public void tick(){
        var pos=BlockPos.containing(getX(),legacyY()-.0625D,getZ());
        if(level().hasChunkAt(pos)){
            var state=level().getBlockState(pos);
            if(LegacyMaterials.of(state).name().equals("water")){
                int meta=state.getBlock() instanceof LegacyFluidBlock?state.getValue(LegacyFluidBlock.LEVEL):state.getBlock() instanceof LiquidBlock?state.getValue(LiquidBlock.LEVEL):0;
                if(meta<8)setDeltaMovement(getDeltaMovement().add(0,.045D,0));
            }
        }
        super.tick();
    }
    @Override protected double getDefaultGravity(){return (double).04F;}
    /** 1.21's additional vanilla underwater drift is absent from the1.7 EntityItem parent. */
    public void sourceUnderwaterStep(){if(!isNoGravity())setDeltaMovement(getDeltaMovement().add(0,-(double).04F,0));}
}
