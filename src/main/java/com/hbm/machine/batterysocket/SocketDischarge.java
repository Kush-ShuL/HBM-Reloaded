package com.hbm.machine.batterysocket;

import com.hbm.registry.HbmSounds;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;

/** BatterySocket's particular VNT configuration: seven exposure nodes and no block allocator. */
public final class SocketDischarge {
    public static final ResourceKey<DamageType> ELECTRIC=ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.fromNamespaceAndPath("hbm","socket_electric"));
    private SocketDischarge() {}
    public static void discharge(ServerLevel level,BlockPos pos,BlockState state) {
        Direction dir=com.hbm.multiblock.DummyableBlock.facing(state),rot=dir.getClockWise();
        double x=pos.getX()+.5-dir.getStepX()*.5+rot.getStepX()*.5,y=pos.getY()+1;
        // Source intentionally uses rot.offsetX here as well; do not silently change its discharge origin.
        double z=pos.getZ()+.5-dir.getStepZ()*.5+rot.getStepX()*.5;
        var targets=new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class,new AABB(x-15,y-15,z-15,x+15,y+15,z+15)));
        Collections.shuffle(targets);
        for(LivingEntity target:targets) {
            Vec3 initial=new Vec3(target.getX()-x,target.getY()+target.getBbHeight()/2-y,target.getZ()-z);
            if(initial.length()>15) continue;
            initial=initial.normalize();double dominant=Math.max(Math.abs(initial.x),Math.max(Math.abs(initial.y),Math.abs(initial.z)));
            initial=initial.scale(1.125D/dominant);
            var beam=new SocketDischargeBeam(BatterySocketContent.DISCHARGE.get(),level);
            // Original uses core integer coordinates, rather than the calculated x/y/z origin.
            beam.setPos(pos.getX()+initial.x,pos.getY()+initial.y,pos.getZ()+initial.z);
            Vec3 delta=new Vec3(target.getX()-beam.getX(),target.getY()+target.getBbHeight()/2-beam.getY(),target.getZ()-beam.getZ());
            beam.fire(delta);level.addFreshEntity(beam);
        }
        explode(level,new Vec3(x+level.random.nextGaussian()*.5,y+level.random.nextGaussian()*.5,z+level.random.nextGaussian()*.5));
    }
    public static void explode(ServerLevel level,Vec3 center) {
        float size=5F,range=size*2;
        var compat=new Explosion(level,null,center.x,center.y,center.z,size,false,Explosion.BlockInteraction.KEEP);
        var entities=level.getEntities((Entity)null,new AABB(center.x-range-1,center.y-range-1,center.z-range-1,center.x+range+1,center.y+range+1,center.z+range+1));
        EventHooks.onExplosionDetonate(level,compat,entities,range);
        var nodes=new ArrayList<Vec3>(7);for(Direction side:Direction.values()) nodes.add(center.add(side.getStepX(),side.getStepY(),side.getStepZ()));nodes.add(center);
        var damage=new LinkedHashMap<Entity,Float>();
        var source=new ElectricSource(level);
        for(Entity entity:entities) {
            AABB box=entity.getBoundingBox();double dx=axisDistance(center.x,box.minX,box.maxX),dy=axisDistance(center.y,box.minY,box.maxY),dz=axisDistance(center.z,box.minZ,box.maxZ);
            double distanceScaled=Math.sqrt(dx*dx+dy*dy+dz*dz)/range;if(distanceScaled>1) continue;
            Vec3 direction=new Vec3(entity.getX()-center.x,entity.getEyeY()-center.y,entity.getZ()-center.z);if(direction.length()==0)continue;direction=direction.normalize();
            double density=0;for(Vec3 point:nodes) density=Math.max(density,Explosion.getSeenPercent(point,entity));
            double knockback=(1-distanceScaled)*density;
            float amount=density<.125?0:(float)(20F*(1-distanceScaled));damage.merge(entity,amount,Math::max);
            double dampened=legacyBlastKnockback(level,entity,knockback);
            entity.setDeltaMovement(entity.getDeltaMovement().add(direction.scale(dampened)));entity.hasImpulse=true;
            // The original player packet sends undampened float impulses, independently of server velocity.
            if(entity instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player,new SocketKnockback((float)(direction.x*knockback),(float)(direction.y*knockback),(float)(direction.z*knockback)));
        }
        damage.forEach((entity,amount)->{
            if(!entity.isAlive())return;
            if(entity instanceof LivingEntity living) {
                living.invulnerableTime=0;
                // Source EntityDamageUtil.applyArmorCalculationsNT is linear and does not damage armor.
                amount=Math.max(0,amount*(25F-living.getArmorValue())/25F);
            }
            entity.hurt(source,amount);
        });
        // No blockAllocator/blockProcessor was configured in X5846: this does not destroy a sphere of blocks.
        level.playSound(null,center.x,center.y,center.z,SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,4,(1+(level.random.nextFloat()-level.random.nextFloat())*.2F)*.7F);
        var particle=new ClientboundLevelParticlesPacket(ParticleTypes.EXPLOSION_EMITTER,true,(double)(float)center.x,(double)(float)center.y,(double)(float)center.z,0,0,0,0,1);
        for(ServerPlayer player:level.players())if(player.distanceToSqr(center)<62500)player.connection.send(particle);
        level.playSound(null,center.x,center.y,center.z,HbmSounds.get("entity.ufoBlast"),SoundSource.HOSTILE,5,.9F+level.random.nextFloat()*.2F);
    }
    private static double axisDistance(double value,double min,double max) {return value>=min&&value<=max?0:Math.min(Math.abs(value-min),Math.abs(value-max));}
    /** Verified against the cached official1.7.10 EnchantmentProtection bytecode, agi.a(sa,double). */
    private static double legacyBlastKnockback(ServerLevel level,Entity entity,double amount) {
        if(!(entity instanceof LivingEntity living))return amount;
        var enchantment=level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BLAST_PROTECTION);
        int highest=0;for(var armor:living.getArmorSlots())highest=Math.max(highest,EnchantmentHelper.getItemEnchantmentLevel(enchantment,armor));
        // EntityPlayer.getLastActiveItems returns only armor; other living inventories also include held equipment.
        if(!(living instanceof Player))highest=Math.max(highest,EnchantmentHelper.getItemEnchantmentLevel(enchantment,living.getItemBySlot(EquipmentSlot.MAINHAND)));
        return highest>0?amount-net.minecraft.util.Mth.floor(amount*(double)(highest*.15F)):amount;
    }
    private static final class ElectricSource extends DamageSource {
        ElectricSource(ServerLevel level) {super(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ELECTRIC));}
        @Override public boolean is(TagKey<DamageType> tag) {return tag==DamageTypeTags.BYPASSES_ARMOR||super.is(tag);}
        @Override public Component getLocalizedDeathMessage(LivingEntity entity) {return Component.translatable("death.sedna.electric",entity.getDisplayName());}
    }
}
