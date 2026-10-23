package com.hbm.entity.boat;

import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;

/** Original five-block liquid/selection ray and whole-boat placement; no extra engine, GUI or interaction packet. */
public final class ItemBoatRubber extends Item {
    public ItemBoatRubber(){super(new Properties().stacksTo(1));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);float yaw=player.getYRot(),pitch=player.getXRot();
        float z=Mth.cos(-yaw*.017453292F-(float)Math.PI),x=Mth.sin(-yaw*.017453292F-(float)Math.PI);
        float horizontal=-Mth.cos(-pitch*.017453292F),vertical=Mth.sin(-pitch*.017453292F);
        var eye=new Vec3(player.getX(),player.getY()+1.62D,player.getZ());
        var end=eye.add((double)(x*horizontal)*5D,(double)vertical*5D,(double)(z*horizontal)*5D);
        // Legacy stopOnLiquid=true admits source fluid blocks; normal native selection shapes remain authoritative.
        var hit=level.clip(new ClipContext(eye,end,ClipContext.Block.OUTLINE,ClipContext.Fluid.SOURCE_ONLY,player));
        if(hit.getType()!=HitResult.Type.BLOCK)return InteractionResultHolder.pass(stack);
        var look=player.getViewVector(1F);
        for(var entity:level.getEntities(player,player.getBoundingBox().expandTowards(look.scale(5D)).inflate(1D)))
            if(entity.isPickable()&&entity.getBoundingBox().inflate(entity.getPickRadius()).contains(eye))return InteractionResultHolder.pass(stack);
        var at=hit.getBlockPos();if(level.getBlockState(at).is(Blocks.SNOW))at=at.below();
        var boat=new EntityBoatRubber(level,(double)((float)at.getX()+.5F),(double)((float)at.getY()+1F),(double)((float)at.getZ()+.5F));
        boat.setYRot(((Mth.floor((double)(yaw*4F/360F)+.5D)&3)-1)*90F);
        if(!level.noCollision(boat,boat.getBoundingBox().deflate(.1D)))return InteractionResultHolder.pass(stack);
        if(!level.isClientSide)level.addFreshEntity(boat);
        if(!player.getAbilities().instabuild)stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}
