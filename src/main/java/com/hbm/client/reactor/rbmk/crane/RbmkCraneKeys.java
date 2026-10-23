package com.hbm.client.reactor.rbmk.crane;
import com.hbm.machine.reactor.rbmk.crane.RbmkCraneInput;
import net.minecraft.client.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class RbmkCraneKeys {
    static final KeyMapping UP=new KeyMapping("hbm.key.craneMoveUp",GLFW.GLFW_KEY_UP,"hbm.key"),DOWN=new KeyMapping("hbm.key.craneMoveDown",GLFW.GLFW_KEY_DOWN,"hbm.key"),LEFT=new KeyMapping("hbm.key.craneMoveLeft",GLFW.GLFW_KEY_LEFT,"hbm.key"),RIGHT=new KeyMapping("hbm.key.craneMoveRight",GLFW.GLFW_KEY_RIGHT,"hbm.key"),LOAD=new KeyMapping("hbm.key.craneLoad",GLFW.GLFW_KEY_ENTER,"hbm.key");private static int previous=-1;
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){var game=Minecraft.getInstance();if(game.player==null||game.level==null){previous=-1;return;}int bits=0;if(game.screen==null&&game.isWindowActive()){if(UP.isDown())bits|=1;if(DOWN.isDown())bits|=2;if(LEFT.isDown())bits|=4;if(RIGHT.isDown())bits|=8;if(LOAD.isDown())bits|=16;}RbmkCraneInput.set(game.player,bits);if(bits!=previous){PacketDistributor.sendToServer(new RbmkCraneInput.Keys(bits));previous=bits;}}
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event){if(event.getPlayer()!=null)RbmkCraneInput.set(event.getPlayer(),0);previous=-1;}
    private RbmkCraneKeys(){}
}
