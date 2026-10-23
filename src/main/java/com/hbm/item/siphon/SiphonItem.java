package com.hbm.item.siphon;

import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.network.StandardFluidReceiver;
import com.hbm.item.pipette.PipetteItem;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.multiblock.proxy.SourceProxyBindings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** X5846 ItemFluidSiphon: receiving tanks, fixed containers, then a non-laboratory pipette. */
public final class SiphonItem extends Item {
    public SiphonItem(){super(new Properties().stacksTo(1));}

    /** Existing directional dummy chains and persisted source proxies replace the two legacy dummy systems. */
    private static BlockEntity core(Level level,BlockPos position){
        var block=level.getBlockState(position).getBlock();
        if(block instanceof DummyableBlock dummy){
            var target=dummy.findCore(level,position);
            if(target!=null)return level.getBlockEntity(target);
        }
        var machine=level.getBlockEntity(position);
        var binding=SourceProxyBindings.get(machine);
        return binding!=null&&binding.target instanceof BlockEntity target?target:machine;
    }

    @Override public InteractionResult useOn(UseOnContext context){
        var player=context.getPlayer();
        if(player==null)return InteractionResult.PASS;
        var level=context.getLevel();
        var machine=core(level,context.getClickedPos());
        if(!(machine instanceof StandardFluidReceiver receiver))return InteractionResult.PASS;
        // The server owns tank/inventory mutation; do not duplicate the drain on the prediction side.
        if(level.isClientSide)return InteractionResult.SUCCESS;
        boolean hasDrainedTank=false;
        var inventory=player.getInventory();
        for(var tank:receiver.getReceivingTanks()){
            if(tank.getFill()<=0)continue;
            ItemStack availablePipette=null;
            var type=tank.getTankType();
            if(type.hasTrait("unsiphonable"))continue;
            // Source mainInventory only: hotbar and 27 storage slots, excluding armor/offhand.
            for(int slot=0;slot<inventory.items.size();slot++){
                var stack=inventory.items.get(slot);
                if(stack.isEmpty())continue;
                var container=FluidContainerRegistry.getContainer(type,stack);
                if(availablePipette==null&&stack.getItem() instanceof PipetteItem pipette
                        &&!pipette.willFizzle(type)&&pipette.kind()!=PipetteItem.Kind.LABORATORY)
                    availablePipette=stack;
                if(container==null)continue;
                var full=FluidContainerRegistry.getFullContainer(stack,type);
                while(tank.getFill()>=container.content()&&!stack.isEmpty()){
                    hasDrainedTank=true;
                    stack.shrink(1);
                    if(stack.isEmpty())inventory.items.set(slot,ItemStack.EMPTY);
                    tank.setFill(tank.getFill()-container.content());
                    // Preserve source add-to-inventory behavior, including its lack of a drop fallback.
                    inventory.add(full.copy());
                }
            }
            if(availablePipette!=null&&tank.getFill()<1000){
                var pipette=(PipetteItem)availablePipette.getItem();
                if(pipette.acceptsFluid(type,availablePipette)){
                    hasDrainedTank=true;
                    tank.setFill(pipette.tryFill(type,tank.getFill(),availablePipette));
                }
            }
            if(hasDrainedTank){
                machine.setChanged();inventory.setChanged();
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }
}
