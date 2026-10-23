package com.hbm.client.animation.bus;

import com.hbm.client.animation.AnimationClock;
import com.hbm.client.model.legacy.LegacyModelRenderContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import com.hbm.weapon.sedna.gun.*;

/** Original nine hotbar timers with eight independent rails per slot. */
public final class HbmAnimations {
    public static final Animation[][] hotbar = new Animation[9][8];
    public static final Animation[] offhand = new Animation[8];
    private static final ThreadLocal<InteractionHand> HAND=ThreadLocal.withInitial(()->InteractionHand.MAIN_HAND);
    public static Scope hand(InteractionHand hand){return new Scope(hand);}
    public static final class Scope implements AutoCloseable{
        private final InteractionHand previous;private final GunVisualState.Scope visual;
        private Scope(InteractionHand hand){previous=HAND.get();HAND.set(hand);var player=Minecraft.getInstance().player;visual=GunVisualState.bind(player==null?ItemStack.EMPTY:player.getItemInHand(hand));}
        @Override public void close(){try{visual.close();}finally{HAND.set(previous);}}
    }
    private HbmAnimations() { }

    public static class Animation {
        public String key;
        public long startMillis;
        public BusAnimation animation;
        public boolean holdLastFrame;
        public java.util.UUID instance;
        public Animation(String key, long startMillis, BusAnimation animation) {
            this.key = key;
            this.startMillis = startMillis;
            this.animation = animation;
        }
        public Animation(String key, long startMillis, BusAnimation animation, boolean holdLastFrame) {
            this(key, startMillis, animation);
            this.holdLastFrame = holdLastFrame;
        }
    }

    public static Animation getRelevantAnim() { return getRelevantAnim(0); }
    public static Animation getRelevantAnim(int index) {return getRelevantAnim(HAND.get(),index);}
    public static Animation getRelevantAnim(InteractionHand hand,int index) {
        var player=Minecraft.getInstance().player;if(player==null||index<0||index>=8)return null;
        ItemStack stack=player.getItemInHand(hand);if(stack.isEmpty())return null;
        Animation animation=hand==InteractionHand.OFF_HAND?offhand[index]:getRelevantAnim(player.getInventory().selected,stack.getItem().getDescriptionId(),index);
        return animation!=null&&animation.key.equals(stack.getItem().getDescriptionId())&&(animation.instance==null||animation.instance.equals(GunHands.identity(stack)))?animation:null;
    }
    public static void put(InteractionHand hand,int slot,int rail,Animation animation){if(hand==InteractionHand.OFF_HAND)offhand[rail]=animation;else hotbar[slot][rail]=animation;}

    /** Explicit key API lets migrated packet/item code retain the same identity decision. */
    public static Animation getRelevantAnim(int slot, String itemKey, int index) {
        if (slot < 0 || slot > 8) slot = Math.abs(slot) % 9;
        Animation animation = hotbar[slot][index];
        return animation != null && animation.key.equals(itemKey) ? animation : null;
    }

    public static double[] getRelevantTransformation(String bus) { return getRelevantTransformation(bus, 0); }
    public static double[] getRelevantTransformation(String bus, int index) {
        return transformation(bus, getRelevantAnim(index), AnimationClock.get_ms());
    }

    public static double[] transformation(String bus, Animation animation, long frameMillis) {
        if (animation != null) {
            BusAnimationSequence sequence = animation.animation.getBus(bus);
            if (sequence != null) {
                double[] result = sequence.getTransformation((int) (frameMillis - animation.startMillis));
                if (result != null) return result;
            }
        }
        return new double[] {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 2};
    }

    public static void applyRelevantTransformation(PoseStack poses, String bus) { applyRelevantTransformation(poses, bus, 0); }
    public static void applyRelevantTransformation(PoseStack poses, String bus, int index) {
        BusTransforms.apply(poses, getRelevantTransformation(bus, index));
    }
    public static void applyRelevantTransformation(String bus) { applyRelevantTransformation(bus, 0); }
    public static void applyRelevantTransformation(String bus, int index) {
        var model = LegacyModelRenderContext.currentOrNull();
        PoseStack poses = model != null ? model.poses() : com.hbm.client.weapon.legendary.LegacyGunGL.currentPose();
        if (poses == null) throw new IllegalStateException("Animation requires an active model or gun pose scope");
        applyRelevantTransformation(poses, bus, index);
    }

    /** The source expires at strict elapsed > duration, and preserves held final frames. */
    public static void expire(long frameMillis) {
        for(int rail=0;rail<offhand.length;rail++){var animation=offhand[rail];if(animation!=null&&!animation.holdLastFrame&&frameMillis-animation.startMillis>animation.animation.getDuration())offhand[rail]=null;}
        for (int slot = 0; slot < hotbar.length; slot++) {
            for (int rail = 0; rail < hotbar[slot].length; rail++) {
                Animation animation = hotbar[slot][rail];
                if (animation != null && !animation.holdLastFrame && frameMillis - animation.startMillis > animation.animation.getDuration()) hotbar[slot][rail] = null;
            }
        }
    }
}
