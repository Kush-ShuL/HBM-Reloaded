package com.hbm.machine.foundry;

import com.hbm.material.MaterialStack;
import com.hbm.material.NtmMaterial.SmeltingBehavior;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Original CrucibleUtil hitscan, partial transfer and safe/spilling semantics. */
public final class FoundryPouring {
    private FoundryPouring() {}
    public record Target(FoundryAcceptor acceptor,BlockHitResult hit){}
    public static BlockHitResult hit(Level level,Vec3 start,Vec3 end){return level.clip(new ClipContext(start,end,ClipContext.Block.OUTLINE,ClipContext.Fluid.ANY,net.minecraft.world.phys.shapes.CollisionContext.empty()));}
    public static Target target(Level level,Vec3 start,Vec3 end){var hit=hit(level,start,end);return hit.getType()==HitResult.Type.BLOCK&&level.getBlockEntity(hit.getBlockPos()) instanceof FoundryAcceptor acceptor?new Target(acceptor,hit):null;}
    public static MaterialStack tryPour(Target target,MaterialStack stack,Vec3[] impact){
        if(stack.material.smeltingBehavior()!=SmeltingBehavior.SMELTABLE)return null;
        if(!target.acceptor.canAcceptPartialPour(target.hit.getLocation(),target.hit.getDirection(),stack))return null;
        MaterialStack left=target.acceptor.pour(target.hit.getLocation(),target.hit.getDirection(),stack);
        if(impact!=null)impact[0]=target.hit.getLocation();return left==null?new MaterialStack(stack.material,0):left;
    }
    public static MaterialStack spill(BlockHitResult hit,boolean safe,MaterialStack stack,int quanta,Vec3[] impact){if(safe)return null;var lost=new MaterialStack(stack.material,Math.min(stack.amount,quanta));stack.amount-=lost.amount;if(impact!=null&&hit!=null&&hit.getType()==HitResult.Type.BLOCK)impact[0]=hit.getLocation();return lost;}
    public static MaterialStack pourFullStack(Level level,double x,double y,double z,double range,boolean safe,List<MaterialStack> stacks,int quanta,Vec3[] impact){
        if(stacks.isEmpty())return null;Vec3 start=new Vec3(x,y,z),end=new Vec3(x,y-range,z);Target target=target(level,start,end);
        if(target!=null)for(MaterialStack stack:stacks){if(stack.material==null)continue;int amountToPour=Math.min(stack.amount,quanta);MaterialStack left=tryPour(target,new MaterialStack(stack.material,amountToPour),impact);if(left!=null){stack.amount-=amountToPour-left.amount;return new MaterialStack(stack.material,stack.amount-left.amount);}}
        MaterialStack result=spill(target==null?hit(level,start,end):target.hit,safe,stacks.getFirst(),quanta,impact);stacks.removeIf(stack->stack.amount<=0);return result;
    }
    public static MaterialStack pourSingleStack(Level level,double x,double y,double z,double range,boolean safe,MaterialStack stack,int quanta,Vec3[] impact){Vec3 start=new Vec3(x,y,z),end=new Vec3(x,y-range,z);Target target=target(level,start,end);if(target!=null){MaterialStack result=tryPour(target,stack,impact);if(result!=null)return result;}spill(target==null?hit(level,start,end):target.hit,safe,stack,quanta,impact);return stack;}
}
