package com.hbm.client.boltgun;

import com.hbm.client.animation.AnimationClock;
import com.hbm.client.animation.bus.*;
import com.hbm.client.bomb.nuclear.LegacyExplosionEffects;
import com.hbm.item.boltgun.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class EngineeringBoltgunClient {
    @SubscribeEvent public static void extension(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private EngineeringBoltgunRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new EngineeringBoltgunRenderer()):renderer;}},EngineeringBoltgunContent.ITEM.get());}
    public static void receive(EngineeringBoltgunPackets packet){var client=Minecraft.getInstance();if(client.level==null)return;if(packet.slot()>=0){if(client.player==null||packet.slot()>8)return;var hand=packet.offhand()?InteractionHand.OFF_HAND:InteractionHand.MAIN_HAND;var animation=new BusAnimation().addBus("RECOIL",new BusAnimationSequence().addPos(1,0,1,50).addPos(0,0,1,100));HbmAnimations.put(hand,packet.slot(),0,new HbmAnimations.Animation(EngineeringBoltgunContent.ITEM.get().getDescriptionId(),AnimationClock.get_ms(),animation));return;}var world=client.level;var flash=LegacyExplosionEffects.create("largeexplode",world,packet.x(),packet.y(),packet.z(),1,0,0);float shade=1-world.random.nextFloat()*.2F;flash.setColor(shade,.9F*shade,.5F*shade);var secondary=LegacyExplosionEffects.create("explode",world,packet.x(),packet.y(),packet.z(),0,0,0);float gray=(1-world.random.nextFloat()*.5F)*.5F;secondary.setColor(gray,gray,gray);secondary.scale(1);client.particleEngine.add(secondary);client.particleEngine.add(flash);}
    private EngineeringBoltgunClient(){}
}
