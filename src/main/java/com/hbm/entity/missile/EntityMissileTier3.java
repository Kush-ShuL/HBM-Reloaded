package com.hbm.entity.missile;

import java.util.ArrayList;
import java.util.List;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNT;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.missile.completion.MissileContent;
import com.hbm.missile.completion.MissileImpactEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import com.hbm.bomb.conventional.LargeConventionalEffects;

import api.hbm.entity.IRadarDetectableNT;





public abstract class EntityMissileTier3 extends EntityMissileBaseNT {

	public EntityMissileTier3(EntityType<?> type,Level world) { super(type,world); }
	protected EntityMissileTier3(EntityType<?> type,Level world,float x,float y,float z,int a,int b) { super(type,world);initialize(x,y,z,a,b); }

	@Override
	public List<ItemStack> getDebris() {
		List<ItemStack> list = new ArrayList<ItemStack>();

		list.add(new ItemStack(MissileContent.item("plate_steel"), 16));
		list.add(new ItemStack(MissileContent.item("plate_titanium"), 10));
		list.add(new ItemStack(MissileContent.item("thruster_large"), 1));
		
		return list;
	}

	@Override
	public String getUnlocalizedName() {
		return "radar.target.tier3";
	}

	@Override
	public int getBlipLevel() {
		return IRadarDetectableNT.TIER3;
	}

	@Override
	protected void spawnContrail() {
		
		com.hbm.bomb.nuclear.LegacyVector thrust = com.hbm.bomb.nuclear.LegacyVector.createVectorHelper(0, 0, 0.5);
		thrust.rotateAroundY((getYRot() + 90) * (float) Math.PI / 180F);
		rotateAroundX(thrust,getXRot() * (float) Math.PI / 180F);
		thrust.rotateAroundY(-(getYRot() + 90) * (float) Math.PI / 180F);

		this.spawnContrailWithOffset(thrust.xCoord, thrust.yCoord, thrust.zCoord);
		this.spawnContrailWithOffset(-thrust.zCoord, thrust.yCoord, thrust.xCoord);
		this.spawnContrailWithOffset(-thrust.xCoord, -thrust.zCoord, -thrust.zCoord);
		this.spawnContrailWithOffset(thrust.zCoord, -thrust.zCoord, -thrust.xCoord);
	}
	
	public static class EntityMissileBurst extends EntityMissileTier3 {
		public EntityMissileBurst(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileBurst(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_burst"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) {
			this.explodeStandard(50F, 48, false);
			LargeConventionalEffects.large(level(), getX(), getY(), getZ());
		}
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_generic_large")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_burst")); }
	}
	
	public static class EntityMissileInferno extends EntityMissileTier3 {
		public EntityMissileInferno(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileInferno(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_inferno"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) {
			this.explodeStandard(50F, 48, true);
			LargeConventionalEffects.large(level(), getX(), getY(), getZ());
			ExplosionChaos.igniteAllBlocks(level(), (int)getX(), (int)getY(), (int)getZ(), 10);
			ExplosionChaos.igniteFlammableBlocks(level(), (int)getX(), (int)getY(), (int)getZ(), 25);
		}
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_incendiary_large")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_inferno")); }
	}

	public static class EntityMissileRain extends EntityMissileTier3 {
		public EntityMissileRain(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileRain(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_rain"),world);initialize(x,y,z,a,b); this.isCluster = true; }
		@Override public void onMissileImpact(HitResult mop) {
			MissileImpactEffects.vanilla(level(),this,getX(), getY(), getZ(), 25F,false,true);
			ExplosionChaos.cluster(level(), getX(), getY(), getZ(), 100, getYRot(), getXRot(), (float) Math.PI * 0.25F, (float) Math.PI * 0.25F, 1F);
		}
		@Override public void cluster() { this.onMissileImpact(null); }
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_cluster_large")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_rain")); }
	}
	
	public static class EntityMissileDrill extends EntityMissileTier3 {
		public EntityMissileDrill(EntityType<?> type,Level world) { super(type,world); }
		public EntityMissileDrill(Level world,float x,float y,float z,int a,int b) { this(MissileContent.type("missile_drill"),world);initialize(x,y,z,a,b); }
		@Override public void onMissileImpact(HitResult mop) {
			for(int i = 0; i < 30; i++) {
				ExplosionNT explosion = new ExplosionNT(level(), this, getX(), getY() - i, getZ(), 10F);
				explosion.addAllAttrib(ExAttrib.ERRODE);
				explosion.explode(); //an explosion exploded!
			}
			ExplosionLarge.spawnParticles(level(), getX(), getY(), getZ(), 25);
			ExplosionLarge.spawnShrapnels(level(), getX(), getY(), getZ(), 12);
			ExplosionLarge.jolt(level(), getX(), getY(), getZ(), 10, 50, 1);
		}
		@Override public ItemStack getDebrisRareDrop() { return new ItemStack(MissileContent.item("warhead_buster_large")); }
		@Override public ItemStack getMissileItemForInfo() { return new ItemStack(MissileContent.item("missile_drill")); }
	}
}
