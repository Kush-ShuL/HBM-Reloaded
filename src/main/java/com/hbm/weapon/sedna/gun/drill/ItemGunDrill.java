package com.hbm.weapon.sedna.gun.drill;
import com.hbm.fluid.*;
import com.hbm.item.battery.HbmBattery;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mags.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
public class ItemGunDrill extends ItemGunBaseNT implements FillableItem, HbmBattery {

	public ItemGunDrill(WeaponQuality quality, GunConfig... cfg) {
		super(quality, cfg);
	}
	
    /** Source reports its harvest level for every tool class and canHarvestBlock=true for unclassified NTM blocks. */
    public int sourceHarvestLevel(ItemStack stack) { return XFactoryDrill.getModdableHarvestLevel(stack,2); }
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state) {
        int tier=sourceHarvestLevel(stack);
        return !state.requiresCorrectToolForDrops() || !state.is(tier>=4?BlockTags.INCORRECT_FOR_NETHERITE_TOOL:tier>=3?BlockTags.INCORRECT_FOR_DIAMOND_TOOL:tier>=2?BlockTags.INCORRECT_FOR_IRON_TOOL:tier>=1?BlockTags.INCORRECT_FOR_STONE_TOOL:BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    }
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility ability) {
        return ability.equals(ItemAbilities.PICKAXE_DIG)||ability.equals(ItemAbilities.AXE_DIG)||ability.equals(ItemAbilities.SHOVEL_DIG);
    }

	@Override
	public boolean acceptsFluid(HbmFluidType type, ItemStack stack) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);

		if(mag instanceof MagazineLiquidEngine) {
			MagazineLiquidEngine engine = (MagazineLiquidEngine) mag;
			for(HbmFluidType acc : engine.acceptedTypes) if(type == acc) return true;
		}
		
		return false;
	}

	@Override
	public int tryFill(HbmFluidType type, int amount, ItemStack stack) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(mag instanceof MagazineLiquidEngine) {
			MagazineLiquidEngine engine = (MagazineLiquidEngine) mag;
			int toFill = Math.min(amount, 50);
			toFill = Math.min(toFill, engine.getCapacity(stack) - this.getFill(stack));
			engine.setAmount(stack, this.getFill(stack) + toFill);
			return amount - toFill;
		}
		
		return 0;
	}

	@Override public boolean providesFluid(HbmFluidType type, ItemStack stack) { return false; }
	@Override public int tryEmpty(HbmFluidType type, int amount, ItemStack stack) { return amount; }

	@Override
	public HbmFluidType getFirstFluidType(ItemStack stack) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		if(mag instanceof MagazineLiquidEngine) return ((MagazineLiquidEngine) mag).getType(stack, null);
		return HbmFluids.NONE;
	}
	
	@Override
	public int getFill(ItemStack stack) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(mag instanceof MagazineLiquidEngine) {
			MagazineLiquidEngine engine = (MagazineLiquidEngine) mag;
			return engine.getAmount(stack, null);
		}
		
		return 0;
	}

	@Override
	public void chargeBattery(ItemStack stack, long i) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(mag instanceof MagazineElectricEngine) {
			MagazineElectricEngine engine = (MagazineElectricEngine) mag;
			engine.setAmount(stack, Math.min(engine.capacity, engine.getAmount(stack, null) + (int) i));
		}
	}
	
	@Override
	public void setCharge(ItemStack stack, long i) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(mag instanceof MagazineElectricEngine) {
			MagazineElectricEngine engine = (MagazineElectricEngine) mag;
			engine.setAmount(stack, (int) i);
		}
	}
	
	@Override
	public void dischargeBattery(ItemStack stack, long i) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(mag instanceof MagazineElectricEngine) {
			MagazineElectricEngine engine = (MagazineElectricEngine) mag;
			engine.setAmount(stack, Math.max(0, engine.getAmount(stack, null) - (int) i));
		}
	}
	
	@Override
	public long getCharge(ItemStack stack) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(mag instanceof MagazineElectricEngine) {
			MagazineElectricEngine engine = (MagazineElectricEngine) mag;
			return engine.getAmount(stack, null);
		}
		
		return 0;
	}
	
	@Override
	public long getMaxCharge(ItemStack stack) {
		IMagazine mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack);
		
		if(mag instanceof MagazineElectricEngine) {
			MagazineElectricEngine engine = (MagazineElectricEngine) mag;
			return engine.getCapacity(stack);
		}
		
		return 0;
	}
	
	@Override public long getChargeRate(ItemStack stack) { return 50_000; }
	@Override public long getDischargeRate(ItemStack stack) { return 0; }
}
