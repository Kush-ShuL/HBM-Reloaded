package com.hbm.explosion.vanillant.standard;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.hbm.explosion.ExplosionKnockbackExempt;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.ICustomDamageHandler;
import com.hbm.explosion.vanillant.interfaces.IEntityProcessor;
import com.hbm.explosion.vanillant.interfaces.IEntityRangeMutator;


import com.hbm.explosion.LegacyExplosionMath;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.event.EventHooks;


/** The amount of good decisions in NTM is few and far between, but the VNT explosion surely is one of them. */
public class EntityProcessorCross implements IEntityProcessor {

	protected double nodeDist = 2D;
	protected IEntityRangeMutator range;
	protected ICustomDamageHandler damage;
	protected double knockbackMult = 1D;
	protected boolean allowSelfDamage = false;
	
	public EntityProcessorCross() {
		this(0);
	}
	
	public EntityProcessorCross(double nodeDist) {
		this.nodeDist = nodeDist;
	}
	
	public EntityProcessorCross setAllowSelfDamage() {
		this.allowSelfDamage = true;
		return this;
	}
	
	public EntityProcessorCross setKnockback(double mult) {
		this.knockbackMult = mult;
		return this;
	}

	@Override
	public HashMap<Player, Vec3> process(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {

		HashMap<Player, Vec3> affectedPlayers = new HashMap();

		size *= 2.0F;
		
		if(range != null) {
			size = range.mutateRange(explosion, size);
		}
		
		double minX = x - (double) size - 1.0D;
		double maxX = x + (double) size + 1.0D;
		double minY = y - (double) size - 1.0D;
		double maxY = y + (double) size + 1.0D;
		double minZ = z - (double) size - 1.0D;
		double maxZ = z + (double) size + 1.0D;
		
		List list = world.getEntities(allowSelfDamage ? null : explosion.exploder, new AABB(minX, minY, minZ, maxX, maxY, maxZ));
		
		EventHooks.onExplosionDetonate(world, explosion.compat, list, size);
		
		Vec3[] nodes;
		
		if(this.nodeDist > 0) {
			nodes = new Vec3[7];
			for(int i = 0; i < 7; i++) {
				Direction dir = i < 6 ? Direction.from3DDataValue(i) : null;
                nodes[i] = dir == null ? new Vec3(x, y, z) : new Vec3(x + dir.getStepX() * nodeDist, y + dir.getStepY() * nodeDist, z + dir.getStepZ() * nodeDist);
			}
		} else {
			nodes = new Vec3[1];
			nodes[0] = new Vec3(x, y, z);
		}
		
		HashMap<Entity, Float> damageMap = new HashMap();

		for(int index = 0; index < list.size(); ++index) {
			
			Entity entity = (Entity) list.get(index);
			
			double xDist = (entity.getBoundingBox().minX <= x && entity.getBoundingBox().maxX >= x) ? 0 : Math.min(Math.abs(entity.getBoundingBox().minX - x), Math.abs(entity.getBoundingBox().maxX - x));
			double yDist = (entity.getBoundingBox().minY <= y && entity.getBoundingBox().maxY >= y) ? 0 : Math.min(Math.abs(entity.getBoundingBox().minY - y), Math.abs(entity.getBoundingBox().maxY - y));
			double zDist = (entity.getBoundingBox().minZ <= z && entity.getBoundingBox().maxZ >= z) ? 0 : Math.min(Math.abs(entity.getBoundingBox().minZ - z), Math.abs(entity.getBoundingBox().maxZ - z));
			double dist = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
			double distanceScaled = dist / size;

			if(distanceScaled <= 1.0D) {
				
				double deltaX = entity.getX() - x;
				double deltaY = entity.getEyeY() - y;
				double deltaZ = entity.getZ() - z;
				double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

				if(distance != 0.0D) {
					
					deltaX /= distance;
					deltaY /= distance;
					deltaZ /= distance;
					
					double density = 0;
					
					for(Vec3 vec : nodes) {
						double d = Explosion.getSeenPercent(vec, entity);
						if(d > density) {
							density = d;
						}
					}
					
					double knockback = (1.0D - distanceScaled) * density;

					float dmg = calculateDamage(distanceScaled, density, knockback, size);
					if(!damageMap.containsKey(entity) || damageMap.get(entity) < dmg) damageMap.put(entity, dmg);
					double enchKnockback = LegacyExplosionMath.dampenBlast(world, entity, knockback);
					
					if(shouldDealKnockback(entity)) {
						entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * enchKnockback * knockbackMult, deltaY * enchKnockback * knockbackMult, deltaZ * enchKnockback * knockbackMult));
					}

					if(entity instanceof Player) {
						affectedPlayers.put((Player) entity, new Vec3(deltaX * knockback * knockbackMult, deltaY * knockback * knockbackMult, deltaZ * knockback * knockbackMult));
					}
				}
			}
		}
		
		for(Entry<Entity, Float> entry : damageMap.entrySet()) {
			
			Entity entity = entry.getKey();
			attackEntity(entity, explosion, entry.getValue());
			
			if(damage != null) {
				double xDist = (entity.getBoundingBox().minX <= x && entity.getBoundingBox().maxX >= x) ? 0 : Math.min(Math.abs(entity.getBoundingBox().minX - x), Math.abs(entity.getBoundingBox().maxX - x));
				double yDist = (entity.getBoundingBox().minY <= y && entity.getBoundingBox().maxY >= y) ? 0 : Math.min(Math.abs(entity.getBoundingBox().minY - y), Math.abs(entity.getBoundingBox().maxY - y));
				double zDist = (entity.getBoundingBox().minZ <= z && entity.getBoundingBox().maxZ >= z) ? 0 : Math.min(Math.abs(entity.getBoundingBox().minZ - z), Math.abs(entity.getBoundingBox().maxZ - z));
				double dist = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
				double distanceScaled = dist / size;
				damage.handleAttack(explosion, entity, distanceScaled);
			}
		}
		
		return affectedPlayers;
	}
	
	public static boolean shouldDealKnockback(Entity entity) {
		if(entity instanceof ExplosionKnockbackExempt) return false;
		return true;
	}
	
	public void attackEntity(Entity entity, ExplosionVNT source, float amount) {
		entity.hurt(setExplosionSource(source.world, source.compat), amount);
	}
	
	public float calculateDamage(double distanceScaled, double density, double knockback, float size) {
		return (float) ((int) ((knockback * knockback + knockback) / 2.0D * 8.0D * size + 1.0D));
	}

	public static DamageSource setExplosionSource(Level world, Explosion explosion) {
        return world.damageSources().explosion(explosion);
    }
	
	public EntityProcessorCross withRangeMod(float mod) {
		range = new IEntityRangeMutator() {
			@Override
			public float mutateRange(ExplosionVNT explosion, float range) {
				return range * mod;
			}
		};
		return this;
	}
	
	public EntityProcessorCross withDamageMod(ICustomDamageHandler damage) {
		this.damage = damage;
		return this;
	}
}
