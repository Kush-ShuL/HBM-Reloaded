package com.hbm.conveyor;

import api.hbm.conveyor.IConveyorPackage;
import api.hbm.conveyor.IEnterableBlock;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** True source EntityMovingPackage; shares the one conveyor motion/interpolation/cramming engine. */
public final class MovingPackage extends MovingConveyorObject implements IConveyorPackage {
    protected ItemStack[] contents=new ItemStack[0];
    public MovingPackage(EntityType<? extends MovingPackage> type,Level world){super(type,world);}
    public MovingPackage(Level world){this(MovingPackageContent.PACKAGE.get(),world);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){}
    public void setItemStacks(ItemStack[] stacks){
        contents=new ItemStack[stacks.length];
        for(int i=0;i<stacks.length;i++)contents[i]=stacks[i]==null?ItemStack.EMPTY:stacks[i].copy();
    }
    @Override public ItemStack[] getItemStacks(){return contents;}
    @Override public InteractionResult interact(Player player,InteractionHand hand){
        if(!level().isClientSide&&!isRemoved()){
            // Source tries a COPY but drops the ORIGINAL full stack on false,
            // including the original partial-insertion behavior. Do not turn it into a remainder transfer.
            for(ItemStack stack:contents)if(!player.getInventory().add(stack.copy()))level().addFreshEntity(new ItemEntity(level(),getX(),getY()+.125,getZ(),stack));
            discard();
        }
        return InteractionResult.PASS;
    }
    @Override public boolean skipAttackInteraction(Entity attacker){
        if(!level().isClientSide&&!isRemoved()){
            discard();
            for(ItemStack stack:contents)level().addFreshEntity(new ItemEntity(level(),getX(),getY()+.125,getZ(),stack));
        }
        return false;
    }
    @Override public boolean hurt(DamageSource damage,float amount){skipAttackInteraction(damage.getEntity());return true;}
    @Override public void enterBlock(IEnterableBlock target,BlockPos pos,Direction side){
        if(isRemoved())return;
        if(target.canPackageEnter(level(),pos.getX(),pos.getY(),pos.getZ(),side,this)){target.onPackageEnter(level(),pos.getX(),pos.getY(),pos.getZ(),side,this);discard();}
    }
    @Override public boolean onLeaveConveyor(){
        discard();
        var motion=getDeltaMovement();
        for(ItemStack stack:contents){
            var item=new ItemEntity(level(),getX()+motion.x*2,getY()+motion.y*2,getZ()+motion.z*2,stack);
            item.setDeltaMovement(motion.x*2,.1,motion.z*2);item.hurtMarked=true;level().addFreshEntity(item);
        }
        // The source package uses ordinary item lifetime, not MovingItem's1200tick override.
        return true;
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){
        var list=new ListTag();
        for(int i=0;i<contents.length;i++)if(!contents[i].isEmpty()){
            var entry=(CompoundTag)contents[i].save(registryAccess());entry.putByte("slot",(byte)i);list.add(entry);
        }
        tag.put("contents",list);tag.putInt("count",contents.length);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag){
        contents=new ItemStack[tag.getInt("count")];Arrays.fill(contents,ItemStack.EMPTY);
        var list=tag.getList("contents",10);
        for(int i=0;i<list.size();i++){var entry=list.getCompound(i);int slot=entry.getByte("slot")&255;if(slot<contents.length)contents[slot]=ItemStack.parseOptional(registryAccess(),entry);}
    }
}
