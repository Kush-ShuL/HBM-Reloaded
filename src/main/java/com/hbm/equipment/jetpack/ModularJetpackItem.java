package com.hbm.equipment.jetpack;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.fluid.FillableItem;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.hbm.equipment.jetpack.JetpackPartItem.Part;

/** Upstream modular tanks, thrusters and flight models, shared by worn and installed packs. */
public final class ModularJetpackItem extends ArmorModItem implements FillableItem {
    private static final String PARTS = "jetpack_parts";
    public ModularJetpackItem() { super(ArmorModStorage.CHEST, false, true, false, false); }
    @Override public EquipmentSlot getEquipmentSlot(ItemStack stack) { return EquipmentSlot.CHEST; }
    @Override public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) { return slot == EquipmentSlot.CHEST; }
    public static boolean isPartInstalled(ItemStack stack, Part part) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getCompound(PARTS).getBoolean(part.id());
    }
    public static void togglePart(ItemStack stack, Part part, boolean installed) {
        var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
        var parts=data.getCompound(PARTS);parts.putBoolean(part.id(),installed);data.put(PARTS,parts);
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));
    }
    public static Part getInstalledTank(ItemStack stack) {
        for(var part:new Part[]{Part.TANK_JETFUEL,Part.TANK_KEROSENE,Part.TANK_COALGAS})if(isPartInstalled(stack,part))return part;
        return null;
    }
    public static Part getInstalledFlightModel(ItemStack stack) {
        if(isPartInstalled(stack,Part.STABILIZER))return Part.STABILIZER;
        return isPartInstalled(stack,Part.WINGS)?Part.WINGS:null;
    }
    public static Part getInstalledThruster(ItemStack stack) {
        if(isPartInstalled(stack,Part.THRUSTER_LARGE))return Part.THRUSTER_LARGE;
        return isPartInstalled(stack,Part.THRUSTERS)?Part.THRUSTERS:null;
    }
    public static int getFuel(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("fuel"); }
    public static void setFuel(ItemStack stack,int fuel) {
        var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();data.putInt("fuel",fuel);
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));
    }
    public static int getMaxFuel(ItemStack stack) {
        var tank=getInstalledTank(stack);
        return tank==null?0:tank.tankSize+(isPartInstalled(stack,Part.EXTENSION_TANKS)?Part.EXTENSION_TANKS.tankSize:0);
    }
    public static boolean canOperate(ItemStack stack) { return getInstalledTank(stack)!=null&&getInstalledThruster(stack)!=null&&getFuel(stack)>0; }
    @Override public boolean acceptsFluid(HbmFluidType type,ItemStack stack) {
        var tank = getInstalledTank(stack);
        return tank != null && type == HbmFluids.fromName(tank.tankType);
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        var tank = getInstalledTank(stack);
        if (tank != null) {
            lines.add(Component.translatable("tooltip.hbm.jetpack.fuel",
                    HbmFluids.fromName(tank.tankType).displayName(), getFuel(stack), getMaxFuel(stack))
                    .withStyle(ChatFormatting.AQUA));
            lines.add(Component.empty());
        }
        super.appendHoverText(stack, context, lines, flag);
    }
    @Override public int tryFill(HbmFluidType type,int amount,ItemStack stack) {
        if(!acceptsFluid(type,stack))return amount;
        int fill=getFuel(stack),transferred=Math.min(amount,Math.max(0,getMaxFuel(stack)-fill));
        setFuel(stack,fill+transferred);return amount-transferred;
    }
    @Override public boolean providesFluid(HbmFluidType type,ItemStack stack) { return false; }
    @Override public int tryEmpty(HbmFluidType type,int amount,ItemStack stack) { return 0; }
    @Override public HbmFluidType getFirstFluidType(ItemStack stack) { return null; }
    @Override public int getFill(ItemStack stack) { return 0; }
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected) {
        if(entity instanceof Player player&&player.getItemBySlot(EquipmentSlot.CHEST)==stack)armorTick(player,stack);
    }
    @Override public void modUpdate(LivingEntity entity,ItemStack armor) {
        if(!(entity instanceof Player player))return;
        var stack=ArmorModStorage.pryMod(armor,ArmorModStorage.CHEST,player.level().registryAccess());
        if(stack.getItem()==this)armorTick(player,stack);
    }
    public static void armorTick(Player player,ItemStack stack) {
        var model=getInstalledFlightModel(stack);var velocity=player.getDeltaMovement();
        boolean active=canOperate(stack)&&JetpackNetwork.enabled(player)&&JetpackNetwork.jump(player);
        if(active){
            var direction=model==Part.STABILIZER?new Vec3(0,1,0):heading(player,model==Part.WINGS);
            velocity=velocity.add(direction.scale(getInstalledThruster(stack).velocity));
        }
        if(model==Part.WINGS&&!player.onGround()){
            double speed=velocity.length(),glide=Math.clamp((speed-.2)/2,0,1);
            var direction = heading(player, true);
            velocity = velocity.scale(1 - glide).add(direction.scale(speed * glide));
            double verticalDrag = .95 + Math.max(direction.y, 0) * .15;
            velocity = velocity.multiply(1 / .95, 1 / verticalDrag, 1 / .95);
        }
        player.setDeltaMovement(velocity);
        if(active||model==Part.WINGS&&!player.onGround())JetpackRuntime.resetFlight(player);
    }
    private static Vec3 heading(Player player,boolean wings) {
        double pitch=Math.toRadians(-player.getXRot()-(wings?90:0)),yaw=Math.toRadians(-player.getYRot()+90);
        double x=Math.sin(pitch);
        return new Vec3(x*Math.cos(yaw),Math.cos(pitch),-x*Math.sin(yaw));
    }
}
