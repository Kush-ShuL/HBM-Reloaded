package com.hbm.entity.mob.ai;

import com.hbm.weapon.sedna.gun.GunKey;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.GunState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

/** Original main-hand Sedna AI; the state-switch fallthrough is intentional. */
public final class EntityAIFireGun extends Goal {
    private final Mob host;
    public double attackMoveSpeed = 1.0D;
    public double maxRange = 20;
    public int burstTime = 10;
    public int minWait = 10;
    public int maxWait = 40;
    public float inaccuracy = 30;
    public boolean randomBurst = true;
    private int attackTimer;
    private FireState state = FireState.IDLE;
    private int stateTimer;
    private enum FireState { IDLE, WAIT, FIRING, RELOADING }

    public EntityAIFireGun(Mob host) { this.host = host; }
    @Override public boolean canUse() { return host.getTarget() != null && getYerGun() != null; }
    @Override public boolean canContinueToUse() { return canUse(); }
    @Override public boolean requiresUpdateEveryTick() { return true; }
    @Override public void tick() {
        var target = host.getTarget();
        ItemStack stack = host.getMainHandItem();
        ItemGunBaseNT gun = getYerGun();
        // A native goal can be disarmed or lose its target between scheduler phases.
        if (target == null || gun == null) return;
        gun.inventoryTick(stack, host.level(), host, 0, true);
        double distance = host.distanceToSqr(target.getX(), target.getY(), target.getZ());
        boolean visible = host.getSensing().hasLineOfSight(target);
        if (visible) attackTimer++; else attackTimer = 0;
        if (distance < maxRange * maxRange && attackTimer > 20)
            host.getNavigation().stop();
        else host.getNavigation().moveTo(target, attackMoveSpeed);
        host.getLookControl().setLookAt(target, 30.0F, 30.0F);

        stateTimer--;
        if (stateTimer < 0) {
            stateTimer = 0;
            if (state == FireState.WAIT) updateState(FireState.IDLE, 0, gun, stack);
            else if (state != FireState.IDLE)
                updateState(FireState.WAIT, host.level().random.nextInt(maxWait - minWait) + minWait, gun, stack);
        } else if (state == FireState.FIRING) updateKeybind(gun, stack, GunKey.PRIMARY);

        if (visible && distance < maxRange * maxRange && state == FireState.IDLE) {
            var receiver = gun.getConfig(stack, 0).getReceivers(stack)[0];
            if (receiver.getMagazine(stack).getAmount(stack, null) <= 0)
                updateState(FireState.RELOADING, 20, gun, stack);
            else if (ItemGunBaseNT.getState(stack, 0) == GunState.IDLE)
                updateState(FireState.FIRING, randomBurst ? host.level().random.nextInt(burstTime) : burstTime, gun, stack);
        }
    }
    private void updateState(FireState next, int time, ItemGunBaseNT gun, ItemStack stack) {
        state = next;
        stateTimer = time;
        // X5846 deliberately has no breaks: firing also taps reload, then releases all keys.
        switch (state) {
            case FIRING: updateKeybind(gun, stack, GunKey.PRIMARY);
            case RELOADING: updateKeybind(gun, stack, GunKey.RELOAD);
            default: updateKeybind(gun, stack, null); break;
        }
    }
    private void updateKeybind(ItemGunBaseNT gun, ItemStack stack, GunKey key) {
        if (key != null && key != GunKey.RELOAD) {
            float spread = inaccuracy * (getYerGun().getConfig(stack, 0).getReceivers(stack)[0].getHipfireSpread(stack) * 20);
            host.yHeadRot += (host.level().random.nextFloat() - 0.5F) * spread;
            host.setXRot(host.getXRot() + (host.level().random.nextFloat() - 0.5F) * spread);
            host.setYRot(host.yHeadRot);
        }
        gun.handleKeybind(host, null, stack, GunKey.PRIMARY, key == GunKey.PRIMARY);
        gun.handleKeybind(host, null, stack, GunKey.SECONDARY, key == GunKey.SECONDARY);
        gun.handleKeybind(host, null, stack, GunKey.TERTIARY, key == GunKey.TERTIARY);
        gun.handleKeybind(host, null, stack, GunKey.RELOAD, key == GunKey.RELOAD);
    }
    public ItemGunBaseNT getYerGun() {
        ItemStack stack = host.getMainHandItem();
        return !stack.isEmpty() && stack.getItem() instanceof ItemGunBaseNT gun ? gun : null;
    }
}
