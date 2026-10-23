package com.hbm.advancement;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.*;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.*;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/** Original ModEventHandler craft/smelt/pickup/first-gneiss-break and real boss death recipient conditions. */
public final class SourceAchievementEvents {
    private record FirstBreak(UUID player,long tick){}
    private final Map<ServerLevel,Map<BlockPos,FirstBreak>> firstGneiss=new WeakHashMap<>();
    @SubscribeEvent public void crafted(PlayerEvent.ItemCraftedEvent event){HbmAdvancements.crafted(event.getEntity(),event.getCrafting());}
    @SubscribeEvent public void smelted(PlayerEvent.ItemSmeltedEvent event){HbmAdvancements.crafted(event.getEntity(),event.getSmelting());}
    @SubscribeEvent public void pickup(ItemEntityPickupEvent.Post event){if(event.getPlayer() instanceof ServerPlayer player&&event.getOriginalStack().is(Items.SLIME_BALL))HbmAdvancements.trigger(player,"slimeball");}
    @SubscribeEvent(priority=EventPriority.LOWEST) public void broken(BlockEvent.BreakEvent event){
        if(!(event.getPlayer() instanceof ServerPlayer player)||!BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()).toString().equals("hbm:stone_gneiss"))return;
        if(HbmAdvancements.trigger(player,"stratum")){
            var level=player.serverLevel();var pending=firstGneiss.computeIfAbsent(level,key->new HashMap<>());pending.entrySet().removeIf(entry->entry.getValue().tick<level.getGameTime()-1);
            // Creative breaking has no real drops/XP, matching native harvesting rather than granting player XP directly.
            if(!player.isCreative())pending.put(event.getPos().immutable(),new FirstBreak(player.getUUID(),level.getGameTime()));
        }
    }
    @SubscribeEvent(priority=EventPriority.LOWEST) public void drops(BlockDropsEvent event){
        var pending=firstGneiss.get(event.getLevel());if(pending==null)return;
        var first=pending.remove(event.getPos());if(first!=null&&event.getBreaker() instanceof ServerPlayer player&&player.getUUID().equals(first.player)&&event.getLevel().getGameTime()-first.tick<=1)event.setDroppedExperience(500);
    }
    @SubscribeEvent public void death(LivingDeathEvent event){
        var entity=event.getEntity();if(!(entity.level() instanceof ServerLevel))return;
        var id=BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());if(!id.getNamespace().equals("hbm"))return;
        switch(id.getPath()){
            case "entity_mob_nuclear_creeper"->HbmAdvancements.around(entity.level(),entity.getBoundingBox().inflate(50),"bossCreeper");
            case "entity_ntm_radiation_blaze"->{if(entity.getMaxHealth()>150)HbmAdvancements.around(entity.level(),entity.getBoundingBox().inflate(50),"bossMeltdown");}
            case "entity_mob_mask_man"->HbmAdvancements.around(entity.level(),entity.getBoundingBox().inflate(100),"bossMaskman");
            case "entity_balls_o_tron"->HbmAdvancements.around(entity.level(),entity.getBoundingBox().inflate(200),"bossWorm");
            // UFO source awards at deathTime19 after the true death explosion, not LivingDeathEvent.
            // Its owner must call all/around at that exact source phase when migrated.
            default->{}
        }
    }
    @SubscribeEvent public void stopped(ServerStoppedEvent event){firstGneiss.clear();}
}
