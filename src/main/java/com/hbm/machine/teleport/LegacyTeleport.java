package com.hbm.machine.teleport;

import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

/** The original distinguishes same-world setPositionAndRotation from cross-world setLocationAndAngles. */
final class LegacyTeleport {
    // Explicit original constructor yOffset values. These are not passenger attachment offsets or modern eye heights.
    private static final Map<String,Double> SOURCE_Y_OFFSET=Map.ofEntries(
        Map.entry("minecraft:item",.125),Map.entry("minecraft:experience_orb",.25),Map.entry("minecraft:tnt",(double)(.98F/2)),
        Map.entry("minecraft:falling_block",(double)(.98F/2)),Map.entry("minecraft:boat",(double)(.6F/2)),
        Map.entry("minecraft:chest_boat",(double)(.6F/2)),Map.entry("minecraft:minecart",(double)(.7F/2)),
        Map.entry("minecraft:chest_minecart",(double)(.7F/2)),Map.entry("minecraft:furnace_minecart",(double)(.7F/2)),
        Map.entry("minecraft:tnt_minecart",(double)(.7F/2)),Map.entry("minecraft:hopper_minecart",(double)(.7F/2)),
        Map.entry("minecraft:spawner_minecart",(double)(.7F/2)),Map.entry("minecraft:command_block_minecart",(double)(.7F/2)),
        Map.entry("hbm:entity_ntm_tnt_primed",(double)(.98F/2)),Map.entry("hbm:entity_falling_block_nt",(double)(.98F/2)),
        Map.entry("hbm:entity_ntm_cart_crate",(double)(.7F/2)),Map.entry("hbm:entity_ntm_cart_destroyer",(double)(.7F/2)),
        Map.entry("hbm:entity_ntm_cart_ore",(double)(.7F/2)),Map.entry("hbm:entity_ntm_cart_powder",(double)(.7F/2)),
        Map.entry("hbm:entity_ntm_cart_semtex",(double)(.7F/2)),Map.entry("hbm:entity_rubber_boat",(double)(.6F/2)),
        Map.entry("hbm:entity_siege_tunneler",.5),Map.entry("hbm:entity_coin",.5));
    static double offset(Entity entity,boolean crossing){
        // Official1.7 Entity/EntityPlayer/Skeleton getYOffset bytecode: yOffset / (float)(yOffset-.5F) / super-.5D.
        // Same-world setters subtract yOffset from the bounding box; the cross-world helper's setter adds it back.
        double nativeOrigin=entity.getY()-entity.getBoundingBox().minY;
        if(entity instanceof ServerPlayer||entity instanceof AbstractSkeleton)return nativeOrigin-.5;
        if(!crossing)return nativeOrigin;
        double sourceYOffset;
        if(entity instanceof com.hbm.bomb.nuclear.NuclearFallingBlock falling)sourceYOffset=falling.legacyY()-falling.getY();
        else if(entity instanceof com.hbm.bomb.conventional.PrimedExplosive tnt)sourceYOffset=tnt.legacyY()-tnt.getY();
        else sourceYOffset=SOURCE_Y_OFFSET.getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(),0D);
        return nativeOrigin+sourceYOffset;
    }
    static boolean move(Entity entity,double x,double y,double z,ResourceKey<Level> dimension){
        if(!(entity.level() instanceof ServerLevel source)||dimension==null)return false;
        ServerLevel destination=source.getServer().getLevel(dimension);
        if(destination==null)return false;
        if(entity instanceof ServerPlayer player){
            if(source==destination)player.connection.teleport(x,y,z,player.getYRot(),player.getXRot());
            else{
                // The original helper reassigns the world even when dead, but only sets the target coordinates while alive.
                Vec3 arrival=player.isAlive()?new Vec3(x,y,z):player.position();
                player.changeDimension(new DimensionTransition(destination,arrival,player.getDeltaMovement(),player.getYRot(),player.getXRot(),DimensionTransition.DO_NOTHING));
            }
            return true;
        }
        if(source==destination){
            entity.moveTo(x,y,z,entity.getYRot(),entity.getXRot());
            source.getChunkSource().broadcast(entity,new ClientboundTeleportEntityPacket(entity));
            return true;
        }
        // Entity.changeDimension recursively transports passengers; the original teleporter moves ONLY each selected entity.
        // Use its real type/NBT clone and native level insertion instead, preserving the source's independent charged attempts.
        Entity replacement=entity.getType().create(destination);
        if(replacement!=null){
            replacement.restoreFrom(entity);
            replacement.moveTo(x,y,z,replacement.getYRot(),replacement.getXRot());
            destination.getChunk(((int)Math.floor(x))>>4,((int)Math.floor(z))>>4);
        }
        entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        if(replacement!=null)destination.addDuringTeleport(replacement);
        destination.resetEmptyTime();
        return true;
    }
    private LegacyTeleport(){}
}
