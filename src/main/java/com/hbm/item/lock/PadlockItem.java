package com.hbm.item.lock;

import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

/** Source padlock consumes one configured lock and sets its exact picking modifier. */
public final class PadlockItem extends KeyPinItem {
    public final double lockMod;
    public PadlockItem(double modifier){super(false);lockMod=modifier;}
    @Override public boolean isKey(){return false;}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();if(player==null||getPins(context.getItemInHand())==0)return InteractionResult.PASS;
        if(level.getBlockState(pos).getBlock() instanceof DummyableBlock block){pos=block.findCore(level,pos);if(pos==null)return InteractionResult.PASS;}
        if(level.getBlockEntity(pos) instanceof com.hbm.door.sourceblast.SourceBlastDoorDummy dummy){if(!com.hbm.door.sourceblast.SourceBlastDoorContent.full(level,dummy.parent()))return InteractionResult.PASS;pos=dummy.parent();}
        if(!(level.getBlockEntity(pos) instanceof LockableBlockEntity locked)||locked.isLocked())return InteractionResult.PASS;
        if(!level.isClientSide){locked.setPins(getPins(context.getItemInHand()));locked.lock();locked.setMod(lockMod);level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("block.lockHang"),SoundSource.PLAYERS,1,1);context.getItemInHand().shrink(1);}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
