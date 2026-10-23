package com.hbm.damage;

import static com.hbm.damage.DamageResistanceHandler.*;

/** Exact X5846 default table, including armor not registered by the port yet. */
final class DefaultResistanceStats {
	static void populate() {

		entityStats.put("net.minecraft.entity.monster.EntityCreeper", new ResistanceStats().addCategory(CATEGORY_EXPLOSION, 2F, 0.25F));
		entityStats.put("com.hbm.entity.mob.EntityCreeperNuclear", new ResistanceStats().addCategory(CATEGORY_EXPLOSION, 5F, 0.35F));

		itemStats.put("hbm:jackt", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 1F, 0.20F));
		itemStats.put("hbm:jackt2", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.25F));

		registerSet("hbm:steel_helmet", "hbm:steel_plate", "hbm:steel_legs", "hbm:steel_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.1F));
		registerSet("hbm:titanium_helmet", "hbm:titanium_plate", "hbm:titanium_legs", "hbm:titanium_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 3F, 0.1F));
		registerSet("hbm:alloy_helmet", "hbm:alloy_plate", "hbm:alloy_legs", "hbm:alloy_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.1F));
		registerSet("hbm:cobalt_helmet", "hbm:cobalt_plate", "hbm:cobalt_legs", "hbm:cobalt_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.1F));
		registerSet("hbm:starmetal_helmet", "hbm:starmetal_plate", "hbm:starmetal_legs", "hbm:starmetal_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 3F, 0.25F)
				.setOther(1F, 0.1F));
		registerSet("hbm:zirconium_legs", "hbm:zirconium_legs", "hbm:zirconium_legs", "hbm:zirconium_legs", new ResistanceStats()
				.setOther(0F, 1F));
		registerSet("hbm:dnt_helmet", "hbm:dnt_plate", "hbm:dnt_legs", "hbm:dnt_boots", new ResistanceStats());
		registerSet("hbm:cmb_helmet", "hbm:cmb_plate", "hbm:cmb_legs", "hbm:cmb_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 5F, 0.5F)
				.setOther(5F, 0.25F));
		registerSet("hbm:schrabidium_helmet", "hbm:schrabidium_plate", "hbm:schrabidium_legs", "hbm:schrabidium_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 10F, 0.65F)
				.setOther(5F, 0.5F));
		registerSet("hbm:robes_helmet", "hbm:robes_plate", "hbm:robes_legs", "hbm:robes_boots", new ResistanceStats());

		registerSet("hbm:security_helmet", "hbm:security_plate", "hbm:security_legs", "hbm:security_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 5F, 0.5F)
				.addCategory(CATEGORY_EXPLOSION, 2F, 0.25F));
		registerSet("hbm:steamsuit_helmet", "hbm:steamsuit_plate", "hbm:steamsuit_legs", "hbm:steamsuit_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.15F)
				.addCategory(CATEGORY_FIRE, 0.5F, 0.25F)
				.addExact("fall", 5F, 0.25F)
				.setOther(0F, 0.1F));
		registerSet("hbm:dieselsuit_helmet", "hbm:dieselsuit_plate", "hbm:dieselsuit_legs", "hbm:dieselsuit_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 1F, 0.15F)
				.addCategory(CATEGORY_FIRE, 0.5F, 0.5F)
				.addCategory(CATEGORY_EXPLOSION, 2F, 0.15F)
				.setOther(0F, 0.1F));
		registerSet("hbm:t51_helmet", "hbm:t51_plate", "hbm:t51_legs", "hbm:t51_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.15F)
				.addCategory(CATEGORY_FIRE, 0.5F, 0.35F)
				.addCategory(CATEGORY_EXPLOSION, 5F, 0.25F)
				.addExact("fall", 0F, 1F)
				.setOther(0F, 0.1F));
		registerSet("hbm:ajr_helmet", "hbm:ajr_plate", "hbm:ajr_legs", "hbm:ajr_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 4F, 0.15F)
				.addCategory(CATEGORY_FIRE, 0.5F, 0.35F)
				.addCategory(CATEGORY_EXPLOSION, 7.5F, 0.25F)
				.addExact("fall", 0F, 1F)
				.setOther(0F, 0.15F));
		registerSet("hbm:ajro_helmet", "hbm:ajro_plate", "hbm:ajro_legs", "hbm:ajro_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 4F, 0.15F)
				.addCategory(CATEGORY_FIRE, 0.5F, 0.35F)
				.addCategory(CATEGORY_EXPLOSION, 7.5F, 0.25F)
				.addExact("fall", 0F, 1F)
				.setOther(0F, 0.15F));
		registerSet("hbm:rpa_helmet", "hbm:rpa_plate", "hbm:rpa_legs", "hbm:rpa_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 50F, 0.75F)
				.addCategory(CATEGORY_FIRE, 25F, 0.9F)
				.addCategory(CATEGORY_EXPLOSION, 15F, 0.25F)
				.addCategory(CATEGORY_ENERGY, 30F, 0.8F)
				.addExact("fall", 0F, 1F)
				.setOther(15F, 0.45F));
		registerSet("hbm:ncrpa_helmet", "hbm:ncrpa_plate", "hbm:ncrpa_legs", "hbm:ncrpa_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 25F, 0.65F)
				.addCategory(CATEGORY_FIRE, 10F, 0.9F)
				.addCategory(CATEGORY_EXPLOSION, 15F, 0.25F)
				.addCategory(CATEGORY_ENERGY, 10F, 0.5F)
				.addExact("fall", 0F, 1F)
				.setOther(15F, 0.25F));
		ResistanceStats bj = new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 5F, 0.5F)
				.addCategory(CATEGORY_FIRE, 2.5F, 0.5F)
				.addCategory(CATEGORY_EXPLOSION, 10F, 0.25F)
				.addExact("fall", 0F, 1F)
				.setOther(2F, 0.15F);
		registerSet("hbm:bj_helmet", "hbm:bj_plate", "hbm:bj_legs", "hbm:bj_boots", bj);
		registerSet("hbm:bj_helmet", "hbm:bj_plate_jetpack", "hbm:bj_legs", "hbm:bj_boots", bj);
		registerSet("hbm:envsuit_helmet", "hbm:envsuit_plate", "hbm:envsuit_legs", "hbm:envsuit_boots", new ResistanceStats()
				.addCategory(CATEGORY_FIRE, 2F, 0.75F)
				.addExact("drown", 0F, 1F)
				.addExact("fall", 5F, 0.75F)
				.setOther(0F, 0.1F));
		registerSet("hbm:hev_helmet", "hbm:hev_plate", "hbm:hev_legs", "hbm:hev_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.25F)
				.addCategory(CATEGORY_FIRE, 0.5F, 0.5F)
				.addCategory(CATEGORY_EXPLOSION, 5F, 0.25F)
				.addExact("onFire", 0F, 1F)
				.addExact("fall", 10F, 0F)
				.setOther(2F, 0.25F));
		registerSet("hbm:bismuth_helmet", "hbm:bismuth_plate", "hbm:bismuth_legs", "hbm:bismuth_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.15F)
				.addCategory(CATEGORY_FIRE, 5F, 0.5F)
				.addCategory(CATEGORY_EXPLOSION, 5F, 0.25F)
				.addExact("fall", 0F, 1F)
				.setOther(2F, 0.25F));
		registerSet("hbm:fau_helmet", "hbm:fau_plate", "hbm:fau_legs", "hbm:fau_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 100F, 0.99F)
				.addCategory(CATEGORY_EXPLOSION, 50F, 0.95F)
				.addCategory(CATEGORY_FIRE, 100F, 1F)
				.addExact(DamageClass.LASER.name(), 25F, 0.95F)
				.addExact("fall", 0F, 1F)
				.setOther(100F, 0.99F));
		registerSet("hbm:dns_helmet", "hbm:dns_plate", "hbm:dns_legs", "hbm:dns_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 1000F, 1F)
				.addCategory(CATEGORY_EXPLOSION, 100F, 0.99F)
				.addCategory(CATEGORY_FIRE, 0F, 1F)
				.setOther(1000F, 1F));
		registerSet("hbm:taurun_helmet", "hbm:taurun_plate", "hbm:taurun_legs", "hbm:taurun_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 2F, 0.15F)
				.addCategory(CATEGORY_FIRE, 0F, 0.25F)
				.addCategory(CATEGORY_EXPLOSION, 0F, 0.25F)
				.addExact("fall", 4F, 0.5F)
				.setOther(2F, 0.1F));
		registerSet("hbm:trenchmaster_helmet", "hbm:trenchmaster_plate", "hbm:trenchmaster_legs", "hbm:trenchmaster_boots", new ResistanceStats()
				.addCategory(CATEGORY_PHYSICAL, 5F, 0.5F)
				.addCategory(CATEGORY_FIRE, 5F, 0.5F)
				.addCategory(CATEGORY_EXPLOSION, 5F, 0.25F)
				.addExact(DamageClass.LASER.name(), 15F, 0.9F)
				.addExact("fall", 10F, 0.5F)
				.setOther(5F, 0.25F));

		registerSet("hbm:euphemium_helmet", "hbm:euphemium_plate", "hbm:euphemium_legs", "hbm:euphemium_boots", new ResistanceStats()
				.setOther(1_000_000F, 1F));
		
		registerSet("hbm:hazmat_helmet", "hbm:hazmat_plate", "hbm:hazmat_legs", "hbm:hazmat_boots", new ResistanceStats());
		registerSet("hbm:hazmat_helmet_red", "hbm:hazmat_plate_red", "hbm:hazmat_legs_red", "hbm:hazmat_boots_red", new ResistanceStats());
		registerSet("hbm:hazmat_helmet_grey", "hbm:hazmat_plate_grey", "hbm:hazmat_legs_grey", "hbm:hazmat_boots_grey", new ResistanceStats());
		registerSet("hbm:liquidator_helmet", "hbm:liquidator_plate", "hbm:liquidator_legs", "hbm:liquidator_boots", new ResistanceStats());
		registerSet("hbm:hazmat_paa_helmet", "hbm:hazmat_paa_plate", "hbm:hazmat_paa_legs", "hbm:hazmat_paa_boots", new ResistanceStats());
		registerSet("hbm:asbestos_helmet", "hbm:asbestos_plate", "hbm:asbestos_legs", "hbm:asbestos_boots", new ResistanceStats()
				.addCategory(CATEGORY_FIRE, 10F, 0.9F));
	}
	
}
