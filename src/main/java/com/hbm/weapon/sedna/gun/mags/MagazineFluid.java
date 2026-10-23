package com.hbm.weapon.sedna.gun.mags;

import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class MagazineFluid implements IMagazine<HbmFluidType> {

	public static final String KEY_MAG_COUNT = "magcount";
	public static final String KEY_MAG_TYPE = "magtype";
	public static final String KEY_MAG_PREV = "magprev";
	public static final String KEY_MAG_AFTER = "magafter";
	
	/** A number so the gun tell multiple mags apart */
	public int index;
	/** How much ammo this mag can hold */
	public int capacity;
	
	public MagazineFluid(int index, int capacity) {
		this.index = index;
		this.capacity = capacity;
	}

	@Override
	public HbmFluidType getType(ItemStack stack, Container inventory) {
		int id = this.getMagType(stack, index);
		return HbmFluids.fromID(id);
	}

	@Override
	public void setType(ItemStack stack, HbmFluidType type) {
		this.setMagType(stack, index, type.getID());
	}

	@Override
	public int getCapacity(ItemStack stack) {
		return capacity;
	}

	@Override
	public void useUpAmmo(ItemStack stack, Container inventory, int amount) {
		this.setAmount(stack, this.getAmount(stack, inventory) - amount);
	}
	
	@Override public int getAmount(ItemStack stack, Container inventory) { return getMagCount(stack, index); }
	@Override public void setAmount(ItemStack stack, int amount) { setMagCount(stack, index, amount); }

	@Override public boolean canReload(ItemStack stack, Container inventory) { return false; }
	@Override public void initNewType(ItemStack stack, Container inventory) { }
	@Override public void reloadAction(ItemStack stack, Container inventory) { }
	@Override public SpentCasing getCasing(ItemStack stack, Container inventory) { return null; }

	@Override public ItemStack getIconForHUD(ItemStack stack, Player player) { return FluidIconItem.make(HbmFluids.fromID(this.getMagType(stack,index)),0); }
	@Override public String reportAmmoStateForHUD(ItemStack stack, Player player) { return getAmount(stack, new com.hbm.weapon.sedna.gun.GunInventory(player)) + "mB"; }
	
	@Override public void setAmountBeforeReload(ItemStack stack, int amount) { ItemGunBaseNT.setValueInt(stack, KEY_MAG_PREV + index, amount); }
	@Override public int getAmountBeforeReload(ItemStack stack) { return ItemGunBaseNT.getValueInt(stack, KEY_MAG_PREV + index); }
	@Override public void setAmountAfterReload(ItemStack stack, int amount) { ItemGunBaseNT.setValueInt(stack, KEY_MAG_AFTER + index, amount); }
	@Override public int getAmountAfterReload(ItemStack stack) { return ItemGunBaseNT.getValueInt(stack, KEY_MAG_AFTER + index); }
	
	public static int getMagType(ItemStack stack, int index) { return ItemGunBaseNT.getValueInt(stack, KEY_MAG_TYPE + index); }
	public static void setMagType(ItemStack stack, int index, int value) { ItemGunBaseNT.setValueInt(stack, KEY_MAG_TYPE + index, value); }
	public static int getMagCount(ItemStack stack, int index) { return ItemGunBaseNT.getValueInt(stack, KEY_MAG_COUNT + index); }
	public static void setMagCount(ItemStack stack, int index, int value) { ItemGunBaseNT.setValueInt(stack, KEY_MAG_COUNT + index, value); }
}
