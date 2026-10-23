package com.hbm.client.weapon.pepperbox;

import java.util.ArrayDeque;
import com.hbm.weapon.sedna.gun.GunVisualState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** BEWLR has no entity parameter; preserve the actual ItemRenderer caller, including nested renders. */
public final class GunRenderOwner {
    private record Frame(LivingEntity entity,GunVisualState.Scope visual){}
    private static final ThreadLocal<ArrayDeque<Frame>> FRAMES=ThreadLocal.withInitial(ArrayDeque::new);
    public static void push(LivingEntity entity,ItemStack stack){FRAMES.get().push(new Frame(entity,GunVisualState.bind(stack)));}
    public static void pop(){var frames=FRAMES.get();var frame=frames.pop();try{frame.visual.close();}finally{if(frames.isEmpty())FRAMES.remove();}}
    public static LivingEntity current(){var frame=FRAMES.get().peek();return frame==null?null:frame.entity;}
    private GunRenderOwner(){}
}
