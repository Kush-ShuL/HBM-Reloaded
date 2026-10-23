package com.hbm.weapon.sedna.gun.mods;
import java.util.*;
import java.util.function.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.weapon.sedna.gun.particle.SpentCasing.CasingType;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunFactory;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunAnimations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
public class WeaponModCaliber extends WeaponModBase {
	private final Map<Integer,MagazineSingleReload> single = new HashMap<>();
    private final Map<Integer,MagazineFullReload> full = new HashMap<>();
    private MagazineBelt belt;
    protected final List<BulletConfig> cfg = new ArrayList();
	protected final int count;
	protected final float baseDamage;

	public WeaponModCaliber(int id, int count, float baseDamage, BulletConfig... cfg) {
		super(id, "CALIBER");
		this.setPriority(PRIORITY_SET);
		for(BulletConfig conf : cfg) this.cfg.add(conf);
		this.count = count;
		this.baseDamage = baseDamage;
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == Receiver.O_MAGAZINE) {
			if(base instanceof MagazineSingleReload) {
				MagazineSingleReload original = (MagazineSingleReload) base;
				return (T) single.computeIfAbsent(original.index, index -> { var magazine = new MagazineSingleReload(index,count); magazine.acceptedBullets.addAll(cfg); return magazine; });
			}
			if(base instanceof MagazineFullReload) {
				MagazineFullReload original = (MagazineFullReload) base;
				return (T) full.computeIfAbsent(original.index, index -> { var magazine = new MagazineFullReload(index,count); magazine.acceptedBullets.addAll(cfg); return magazine; });
			}
			if(base instanceof MagazineBelt) {
				if(belt == null) { belt = new MagazineBelt(); belt.acceptedBullets.addAll(cfg); }
                return (T) belt;
			}
		}
		if(key == Receiver.F_BASEDAMAGE) {
			return cast(baseDamage, base);
		}
		return base;
	}
	
	/* adding or removing a caliber mod annihilates the loaded rounds */
	@Override public void onInstall(ItemStack gun, ItemStack mod, int index) { XWeaponModManager.changedMagState(); }
	@Override public void onUninstall(ItemStack gun, ItemStack mod, int index) { XWeaponModManager.changedMagState(); }
}
