package com.hbm.item.meltdowntool;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.zirnox.ZirnoxBlock;
import com.hbm.machine.reactor.zirnox.ZirnoxBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
/** Exact ItemDyatlov: two source reactor actions, no alternate explosion/heat/permission engine. */
public final class MeltdownToolItem extends Item {
    public MeltdownToolItem(){super(new Properties().stacksTo(1));}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var at=context.getClickedPos();
        if(!level.isClientSide){
            var block=level.getBlockState(at).getBlock();
            if(block instanceof RbmkColumnBlock column){
                var core=column.findCore(level,at);
                if(core!=null&&level.getBlockEntity(core)instanceof RbmkColumnEntity reactor)reactor.meltdown();
                // Source alternative heat=100000 is commented out, deliberately not restored.
            }
            if(block instanceof ZirnoxBlock zirnox){
                var core=zirnox.findCore(level,at);
                if(core!=null&&level.getBlockEntity(core)instanceof ZirnoxBlockEntity reactor)reactor.heat=200000;
            }
        }
        return InteractionResult.PASS; // Original onItemUse always returns false, including after the action.
    }
}
