package com.hbm.client.railway.train;
import com.hbm.railway.train.RailCarEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
/** Exact original standing-driver vanilla look/input compensation, not client-authoritative train teleportation. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class RailTrainInput {
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){var player=Minecraft.getInstance().player;if(player!=null&&player.getVehicle()instanceof RailCarEntity train&&!train.trailer){player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(),player.getXRot(),player.onGround()));player.connection.send(new ServerboundPlayerInputPacket(player.xxa,player.zza,player.input.jumping,player.input.shiftKeyDown));}}
    private RailTrainInput(){}
}
