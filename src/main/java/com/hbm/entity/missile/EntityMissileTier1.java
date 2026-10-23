package com.hbm.entity.missile;

import java.util.ArrayList;
import java.util.List;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.missile.completion.MissileContent;
import com.hbm.missile.completion.MissileImpactEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import com.hbm.bomb.conventional.LargeConventionalEffects;

import api.hbm.entity.IRadarDetectableNT;




public abstract class EntityMissileTier1 extends EntityMissileBaseNT {

	public EntityMissileTier1(EntityType<?> type,Level world) { super(type,world); }
	protected EntityMissileTier1(EntityType<?> type,Level world,float x,float y,float z,int a,int b) { super(type,world);initialize(x,y,z,a,b); }

	@Override
	public List<ItemStack> getDebris() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(MissileContent.item("plate_titanium"), 4));
		list.add(new ItemStack(MissileContent.item("thruster_small"), 1));
		return list;
	}

	@Override
	protected float getContrailScale() {
		return 0.5F;
	}

	public static class EntityMissileGeneric extends EntityMissileTier1 {
		public EntityMissileGeneric(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileGeneric(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_generic"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) { this.explodeStandard(15F, 24, false); LargeConventionalEffects.small(level(), getX(), getY(), getZ()); }
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_generic_small")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_generic")); }
	}

	public static class EntityMissileDecoy extends EntityMissileTier1 {
		public EntityMissileDecoy(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileDecoy(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_decoy"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) { MissileImpactEffects.vanilla(level(),this,getX(),getY(),getZ(),4F,false,false); }
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("ingot_steel")); }
		@Override public String getUnlocalizedName() { return "radar.target.tier4"; }
		@Override public int getBlipLevel() { return IRadarDetectableNT.TIER4; }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_decoy")); }
	}

	public static class EntityMissileIncendiary extends EntityMissileTier1 {
		public EntityMissileIncendiary(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileIncendiary(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_incendiary"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) { this.explodeStandard(15F, 24, true); LargeConventionalEffects.small(level(), getX(), getY(), getZ()); }
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_incendiary_small")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_incendiary")); }
	}

	public static class EntityMissileCluster extends EntityMissileTier1 {
		public EntityMissileCluster(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileCluster(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_cluster"),world);initialize(x,y,z,a,b); this.isCluster = true; }
		@Override public void onMissileImpact(HitResult mop) {
			MissileImpactEffects.vanilla(level(),this,getX(), getY(), getZ(), 5F,false,true);
			ExplosionChaos.cluster(level(), getX(), getY(), getZ(), 25, getYRot(), getXRot(), (float) Math.PI * 0.25F, (float) Math.PI * 0.25F, 1F);
		}
		@Override public void cluster() { this.onMissileImpact(null); }
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_cluster_small")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_cluster")); }
	}

	public static class EntityMissileBunkerBuster extends EntityMissileTier1 {
		public EntityMissileBunkerBuster(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileBunkerBuster(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_buster"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) {
			for(int i = 0; i < 15; i++) MissileImpactEffects.vanilla(level(),this,getX(), getY() - i, getZ(), 5F,false,true);
			ExplosionLarge.spawnParticles(level(), getX(), getY(), getZ(), 5);
			ExplosionLarge.spawnShrapnels(level(), getX(), getY(), getZ(), 5);
			ExplosionLarge.spawnRubble(level(), getX(), getY(), getZ(), 5);
		}
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_buster_small")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_buster")); }
	}
}
