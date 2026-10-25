package com.hbm.player;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.equipment.jetpack.JetpackNetwork;
import com.hbm.equipment.jetpack.JetpackRuntime;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Actual X5846 onWingFlop identity abilities, separate from equipment and cosmetic renderers. */
@EventBusSubscriber(modid="hbm")
public final class SourceAuthorMovement {
    private static final UUID HBM=UUID.fromString("192af5d7-ed0f-48d8-bd89-9d41af8524f8");
    private static final UUID NCR=UUID.fromString("28ae585f-4431-4491-9ce8-3def6126e3c6");
    private static final UUID PHEO=UUID.fromString("b04cf173-cff0-4acd-aa19-3d835224b43d");
    private static final UUID PEEP=UUID.fromString("433c2bb7-018c-4d51-acfe-27f907432b5e");
    private static final ResourceLocation FOP_SPEED=ResourceLocation.parse("hbm:fop_speed");

    @SubscribeEvent public static void tick(PlayerTickEvent.Pre event){
        var player=event.getEntity();
        if(player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()&&!player.onGround()){
            if(matches(player,PHEO,"pheo7"))pheo(player);
            boolean bob=matches(player,HBM,"HbMinecraft"),other=matches(player,NCR,"the_NCR");
            if(bob||other)wings(player,bob,other);
        }
        if(matches(player,PEEP,"LePeeperSauvage")){
            var speed=player.getAttribute(Attributes.MOVEMENT_SPEED);
            if(speed!=null){
                speed.removeModifier(FOP_SPEED);
                if(player.isSprinting())speed.addTransientModifier(new AttributeModifier(FOP_SPEED,.5D,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    private static void pheo(Player player){
        JetpackRuntime.resetFlight(player);
        var motion=player.getDeltaMovement();
        if(JetpackNetwork.enabled(player)&&JetpackNetwork.jump(player)){
            if(motion.y<.4D)motion=motion.add(0,.1D,0);
            var look=player.getLookAngle();
            if((double)(float)Math.sqrt(motion.lengthSqr())<2D){
                motion=motion.add(look.x*.2D,look.y*.2D,look.z*.2D);
                if(look.y>0)player.fallDistance=0;
            }
        }else if(JetpackNetwork.enabled(player)&&!player.isShiftKeyDown()){
            if(motion.y<-.2D)motion=motion.add(0,.075D,0);
            if(player.fallDistance>0)player.fallDistance=0;
        }
        player.setDeltaMovement(motion);
    }

    private static void wings(Player player,boolean bob,boolean other){
        JetpackRuntime.resetFlight(player);
        if(player.fallDistance>0)player.fallDistance=0;
        var motion=player.getDeltaMovement();
        if(motion.y<-.4D)motion=new Vec3(motion.x,-.4D,motion.z);
        boolean enabled=JetpackNetwork.enabled(player);
        if(bob||player.getFoodData().getFoodLevel()>6){
            if(enabled&&JetpackNetwork.jump(player)){
                double cap=bob?.8D:.4D;
                motion=new Vec3(motion.x,motion.y<cap?motion.y+.15D:cap+.15D,motion.z);
                if(other)player.causeFoodExhaustion(player.getFoodData().getSaturationLevel()>0?4F:.2F);
            }else if(enabled&&!player.isShiftKeyDown()){
                double y=motion.y;
                if(y< -1)y+=.4D;else if(y<-.1D)y+=.2D;else if(y<0)y=0;
                motion=new Vec3(motion.x,y,motion.z);
                if(other&&!player.onGround())player.causeFoodExhaustion(player.getFoodData().getSaturationLevel()>0?4F:.04F);
            }else if(!enabled&&player.isShiftKeyDown()&&motion.y<-.08D){
                double amount=motion.y*(bob?-.6D:-.4D);
                motion=motion.add(0,amount,0);
                var look=player.getLookAngle();
                motion=motion.add(look.x*amount,look.y*amount,look.z*amount);
            }
        }
        var original=player.getLookAngle();
        var look=LegacyVector.createVectorHelper(original.x,0,original.z).normalize();
        double multiplier=enabled?(bob?.5D:.25D):.125D;
        if(player.zza!=0)motion=motion.add(look.xCoord*.35D*player.zza*multiplier,0,look.zCoord*.35D*player.zza*multiplier);
        if(player.xxa!=0){
            look.rotateAroundY((float)Math.PI*.5F);
            motion=motion.add(look.xCoord*.15D*player.xxa*multiplier,0,look.zCoord*.15D*player.xxa*multiplier);
        }
        player.setDeltaMovement(motion);
    }

    private static boolean matches(Player player,UUID id,String name){return player.getUUID().equals(id)||player.getDisplayName().getString().equals(name);}
    private SourceAuthorMovement(){}
}
