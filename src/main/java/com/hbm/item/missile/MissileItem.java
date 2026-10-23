package com.hbm.item.missile;
import com.hbm.item.CustomLoreItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual source ItemMissile form/tier/fuel/lore semantics; only the true rusted item is registered in this group. */
public class MissileItem extends CustomLoreItem {
	public final MissileFormFactor formFactor;
	public final MissileTier tier;
	public final MissileFuel fuel;
	public int fuelCap;
	public boolean launchable = true;

	public MissileItem(String id,MissileFormFactor form, MissileTier tier) {
		this(id,form, tier, form.defaultFuel);
	}

	public MissileItem(String id,MissileFormFactor form, MissileTier tier, MissileFuel fuel) {
        super(new Properties().stacksTo(1),null,id,false);
		this.formFactor = form;
		this.tier = tier;
		this.fuel = fuel;
		this.setFuelCap(this.fuel.defaultCap);
	}

	public MissileItem notLaunchable() {
		this.launchable = false;
		return this;
	}

	public MissileItem setFuelCap(int fuelCap) {
		this.fuelCap = fuelCap;
		return this;
	}

    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> list,TooltipFlag flag){
        list.add(Component.translatable("item.missile.tier."+tier.name().toLowerCase(java.util.Locale.ROOT)).withStyle(ChatFormatting.ITALIC));
        if(!launchable)list.add(Component.translatable("item.missile.desc.notLaunchable").withStyle(ChatFormatting.RED));
        else{list.add(Component.translatable("item.missile.desc.fuel").append(": ").append(fuel.getDisplay()));if(fuelCap>0)list.add(Component.translatable("item.missile.desc.fuelCapacity").append(": "+fuelCap+"mB"));super.appendHoverText(stack,context,list,flag);}
    }

	public enum MissileFormFactor {
		ABM(MissileFuel.SOLID),
		MICRO(MissileFuel.SOLID),
		V2(MissileFuel.ETHANOL_PEROXIDE),
		STRONG(MissileFuel.KEROSENE_PEROXIDE),
		HUGE(MissileFuel.KEROSENE_LOXY),
		ATLAS(MissileFuel.JETFUEL_LOXY),
		OTHER(MissileFuel.KEROSENE_PEROXIDE);

		protected MissileFuel defaultFuel;

		private MissileFormFactor(MissileFuel defaultFuel) {
			this.defaultFuel = defaultFuel;
		}
	}

	public enum MissileTier {
		TIER0("Tier 0"),
		TIER1("Tier 1"),
		TIER2("Tier 2"),
		TIER3("Tier 3"),
		TIER4("Tier 4");

		public String display;

		private MissileTier(String display) {
			this.display = display;
		}
	}

	public enum MissileFuel {
		SOLID("item.missile.fuel.solid.prefueled", ChatFormatting.GOLD, 0),
		ETHANOL_PEROXIDE("item.missile.fuel.ethanol_peroxide", ChatFormatting.AQUA, 4_000),
		KEROSENE_PEROXIDE("item.missile.fuel.kerosene_peroxide", ChatFormatting.BLUE, 8_000),
		KEROSENE_LOXY("item.missile.fuel.kerosene_loxy", ChatFormatting.LIGHT_PURPLE, 12_000),
		JETFUEL_LOXY("item.missile.fuel.jetfuel_loxy", ChatFormatting.RED, 16_000);

		private final String key;
		public final ChatFormatting color;
		public final int defaultCap;

		private MissileFuel(String key, ChatFormatting color, int defaultCap) {
			this.key = key;
			this.color = color;
			this.defaultCap = defaultCap;
		}

		/** Returns a color localized string for display */
		public Component getDisplay() {
			return Component.translatable(key).withStyle(color);
		}
	}
}
