package com.hbm.explosion.vanillant.standard;

import java.util.HashMap;
import java.util.List;

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
import net.neoforged.neoforge.event.EventHooks;

@Deprecated // an inferior version to the cross processors, so there is no actual reason to ever use this one
public class EntityProcessorStandard implements IEntityProcessor {

	protected IEntityRangeMutator range;
	protected ICustomDamageHandler damage;
	protected boolean allowSelfDamage = false;

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
		Vec3 vec3 = new Vec3(x, y, z);

		for(int index = 0; index < list.size(); ++index) {
			
			Entity entity = (Entity) list.get(index);
			double distanceScaled = Math.sqrt(entity.distanceToSqr(x, y, z)) / size;

			if(distanceScaled <= 1.0D) {
				
				double deltaX = entity.getX() - x;
				double deltaY = entity.getEyeY() - y;
				double deltaZ = entity.getZ() - z;
				double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

				if(distance != 0.0D) {
					
					deltaX /= distance;
					deltaY /= distance;
					deltaZ /= distance;
					
					double density = Explosion.getSeenPercent(vec3, entity);
					double knockback = (1.0D - distanceScaled) * density;
					
					entity.hurt(setExplosionSource(world, explosion.compat), calculateDamage(distanceScaled, density, knockback, size));
					double enchKnockback = LegacyExplosionMath.dampenBlast(world, entity, knockback);
					
					entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * enchKnockback, deltaY * enchKnockback, deltaZ * enchKnockback));

					if(entity instanceof Player) {
						affectedPlayers.put((Player) entity, new Vec3(deltaX * knockback, deltaY * knockback, deltaZ * knockback));
					}
					
					if(damage != null) {
						damage.handleAttack(explosion, entity, distanceScaled);
					}
				}
			}
		}

		return affectedPlayers;
	}
	
	public float calculateDamage(double distanceScaled, double density, double knockback, float size) {
		return (float) ((int) ((knockback * knockback + knockback) / 2.0D * 8.0D * size + 1.0D));
	}

	public static DamageSource setExplosionSource(Level world, Explosion explosion) {
        return world.damageSources().explosion(explosion);
    }
	
	public EntityProcessorStandard withRangeMod(float mod) {
		range = new IEntityRangeMutator() {
			@Override
			public float mutateRange(ExplosionVNT explosion, float range) {
				return range * mod;
			}
		};
		return this;
	}
	
	public EntityProcessorStandard withDamageMod(ICustomDamageHandler damage) {
		this.damage = damage;
		return this;
	}
	
	public EntityProcessorStandard allowSelfDamage() {
		this.allowSelfDamage = true;
		return this;
	}
}
