package com.hbm.entity.missile;

import java.util.ArrayList;
import java.util.List;

import com.hbm.entity.logic.EntityEMP;
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




public abstract class EntityMissileTier2 extends EntityMissileBaseNT {

	public EntityMissileTier2(EntityType<?> type,Level world) { super(type,world); }
	protected EntityMissileTier2(EntityType<?> type,Level world,float x,float y,float z,int a,int b) { super(type,world);initialize(x,y,z,a,b); }

	@Override
	public List<ItemStack> getDebris() {
		List<ItemStack> list = new ArrayList<ItemStack>();

		list.add(new ItemStack(MissileContent.item("plate_steel"), 10));
		list.add(new ItemStack(MissileContent.item("plate_titanium"), 6));
		list.add(new ItemStack(MissileContent.item("thruster_medium"), 1));
		
		return list;
	}

	@Override
	public String getUnlocalizedName() {
		return "radar.target.tier2";
	}

	@Override
	public int getBlipLevel() {
		return IRadarDetectableNT.TIER2;
	}

	public static class EntityMissileStrong extends EntityMissileTier2 {
		public EntityMissileStrong(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileStrong(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_strong"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) { this.explodeStandard(30F, 32, false); LargeConventionalEffects.standard(level(), getX(), getY(), getZ()); }
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_generic_medium")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_strong")); }
	}

	public static class EntityMissileIncendiaryStrong extends EntityMissileTier2 {
		public EntityMissileIncendiaryStrong(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileIncendiaryStrong(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_incendiary_strong"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) {
			this.explodeStandard(30F, 32, true);
			LargeConventionalEffects.standard(level(), getX(), getY(), getZ());
			ExplosionChaos.igniteFlammableBlocks(level(), (int)((float)getX() + 0.5F), (int)((float)getY() + 0.5F), (int)((float)getZ() + 0.5F), 25);
		}
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_incendiary_medium")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_incendiary_strong")); }
	}

	public static class EntityMissileClusterStrong extends EntityMissileTier2 {
		public EntityMissileClusterStrong(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileClusterStrong(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_cluster_strong"),world);initialize(x,y,z,a,b); this.isCluster = true; }
		@Override public void onMissileImpact(HitResult mop) {
			MissileImpactEffects.vanilla(level(),this,getX(), getY(), getZ(), 15F,false,true);
			ExplosionChaos.cluster(level(), getX(), getY(), getZ(), 50, getYRot(), getXRot(), (float) Math.PI * 0.25F, (float) Math.PI * 0.25F, 1F);
		}
		@Override public void cluster() { this.onMissileImpact(null); }
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_cluster_medium")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_cluster_strong")); }
	}

	public static class EntityMissileBusterStrong extends EntityMissileTier2 {
		public EntityMissileBusterStrong(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileBusterStrong(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_buster_strong"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) {
			for(int i = 0; i < 20; i++) MissileImpactEffects.vanilla(level(),this,getX(), getY() - i, getZ(), 7.5F,false,true);
			ExplosionLarge.spawnParticles(level(), getX(), getY(), getZ(), 8);
			ExplosionLarge.spawnShrapnels(level(), getX(), getY(), getZ(), 8);
			ExplosionLarge.spawnRubble(level(), getX(), getY(), getZ(), 8);
		}
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_buster_medium")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_buster_strong")); }
	}

	public static class EntityMissileEMPStrong extends EntityMissileTier2 {
		public EntityMissileEMPStrong(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileEMPStrong(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_emp_strong"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) {
			EntityEMP emp = new EntityEMP(level());
			emp.setPos(getX(),getY(),getZ());
			level().addFreshEntity(emp);
		}
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_generic_medium")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_emp_strong")); }
	}
}
