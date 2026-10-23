package com.hbm.weapon.sedna.gun.chemthrower;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.hbm.entity.projectile.EntityChemical;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import com.hbm.weapon.sedna.gun.GunConfig;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.Receiver;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import com.hbm.weapon.sedna.gun.mags.MagazineFluid;
import com.hbm.weapon.sedna.gun.GunAnimation;

import com.hbm.fluid.FillableItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.hbm.bomb.nuclear.LegacyVector;

public class ItemGunChemthrower extends ItemGunBaseNT implements FillableItem {

	public static final int CONSUMPTION = 3;

	public ItemGunChemthrower(WeaponQuality quality, GunConfig... cfg) {
		super(quality, cfg);
	}

	@Override
	public boolean acceptsFluid(HbmFluidType type, ItemStack stack) {
		return getFluidType(stack) == type || this.getMagCount(stack) == 0;
	}

	public static final int transferSpeed = 50;

	@Override
	public int tryFill(HbmFluidType type, int amount, ItemStack stack) {

		if(!acceptsFluid(type, stack)) return amount;
		if(this.getMagCount(stack) == 0) this.setMagType(stack, type.getID());

		int fill = this.getMagCount(stack);
		int req = this.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getCapacity(stack) - fill;
		int toFill = Math.min(amount, req);
		toFill = Math.min(toFill, transferSpeed);
		this.setMagCount(stack, fill + toFill);

		return amount - toFill;
	}

	public HbmFluidType getFluidType(ItemStack stack) {
		return HbmFluids.fromID(this.getMagType(stack));
	}

	@Override
	public boolean providesFluid(HbmFluidType type, ItemStack stack) {
		return getFluidType(stack) == type;
	}

	@Override
	public int tryEmpty(HbmFluidType type, int amount, ItemStack stack) {
		int fill = this.getMagCount(stack);
		int toUnload = Math.min(fill, amount);
		toUnload = Math.min(toUnload, transferSpeed);
		this.setMagCount(stack, fill - toUnload);
		return toUnload;
	}

	@Override public HbmFluidType getFirstFluidType(ItemStack stack) { return HbmFluids.fromID(this.getMagType(stack)); }
	@Override public int getFill(ItemStack stack) { return this.getMagCount(stack); }

	public static int getMagType(ItemStack stack) { return ItemGunBaseNT.getValueInt(stack, MagazineFluid.KEY_MAG_TYPE + 0); }
	public static void setMagType(ItemStack stack, int value) { ItemGunBaseNT.setValueInt(stack, MagazineFluid.KEY_MAG_TYPE + 0, value); }
	public static int getMagCount(ItemStack stack) { return ItemGunBaseNT.getValueInt(stack, MagazineFluid.KEY_MAG_COUNT + 0); }
	public static void setMagCount(ItemStack stack, int value) { ItemGunBaseNT.setValueInt(stack, MagazineFluid.KEY_MAG_COUNT + 0, value); }

	public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_CAN_FIRE = (stack, ctx) -> { return ctx.config.getReceivers(stack)[0].getMagazine(stack).getAmount(stack, ctx.inventory) >= CONSUMPTION; };

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_FIRE = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		Player player = ctx.getPlayer();
		int index = ctx.configIndex;
		ItemGunBaseNT.playAnimation(player, stack, GunAnimation.CYCLE, ctx.configIndex);

		Receiver primary = ctx.config.getReceivers(stack)[0];
		IMagazine mag = primary.getMagazine(stack);

		LegacyVector offset = primary.getProjectileOffset(stack);
		double forwardOffset = offset.xCoord;
		double heightOffset = offset.yCoord;
		double sideOffset = offset.zCoord;

		EntityChemical chem = new EntityChemical(entity.level(), entity, sideOffset, heightOffset, forwardOffset);
		chem.setFluid((HbmFluidType) mag.getType(stack, ctx.inventory));
		entity.level().addFreshEntity(chem);

		mag.useUpAmmo(stack, ctx.inventory, CONSUMPTION);
		ItemGunBaseNT.setWear(stack, index, Math.min(ItemGunBaseNT.getWear(stack, index) + 1F, ctx.config.getDurability(stack)));
	};
}
