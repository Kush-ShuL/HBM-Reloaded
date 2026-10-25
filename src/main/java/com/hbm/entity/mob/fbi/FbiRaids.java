package com.hbm.entity.mob.fbi;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
/** Actual BossSpawnHandler raid branch, including surprising expired/absent mark eligibility. */
public final class FbiRaids {
    public static void update(ServerLevel world){
        if(!FbiConfig.ENABLED.get()||world.getGameTime()%FbiConfig.DELAY.get()!=0)return;
        // Preserve source nextInt consumption before surface/player checks.
        if(world.random.nextInt(FbiConfig.CHANCE.get())!=0||world.players().isEmpty()||!world.dimensionType().natural())return;
        var player=world.players().get(world.random.nextInt(world.players().size()));
        if(player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).getLong("fbiMark")>=world.getGameTime())return;
        player.sendSystemMessage(Component.literal("FBI, OPEN UP!").withStyle(ChatFormatting.RED));var vector=new Vec3(FbiConfig.DISTANCE.get(),0,0).yRot((float)Math.PI*2*world.random.nextFloat());
        for(int i=0;i<FbiConfig.AMOUNT.get();i++)spawn(world,player,vector,false);
        for(int i=0;i<FbiConfig.DRONES.get();i++)spawn(world,player,vector,true);
    }
    private static void spawn(ServerLevel world,Player player,Vec3 vector,boolean drone){double x=player.getX()+vector.x+world.random.nextGaussian()*5,z=player.getZ()+vector.z+world.random.nextGaussian()*5;double y=com.hbm.world.SourceWorldHeight.get(world,(int)x,(int)z);Mob mob=drone?com.hbm.satellite.resources.SatelliteResources113.DRONE.get().create(world):com.hbm.satellite.resources.SatelliteResources113.AGENT.get().create(world);mob.moveTo((float)x,(float)y+(drone?10:0),(float)z,world.random.nextFloat()*360,0);var check=new MobSpawnEvent.PositionCheck(mob,world,MobSpawnType.EVENT,null);NeoForge.EVENT_BUS.post(check);if(check.getResult()!=MobSpawnEvent.PositionCheck.Result.FAIL){world.addFreshEntity(mob);EventHooks.finalizeMobSpawn(mob,world,world.getCurrentDifficultyAt(mob.blockPosition()),MobSpawnType.EVENT,null);}}
    private FbiRaids(){}
}
