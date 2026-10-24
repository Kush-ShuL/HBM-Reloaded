package com.hbm.client.wiki;

import com.hbm.commands.CommandWikiRender;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

/** Two source developer shortcuts, configurable in native Controls and using the existing image exporter. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class WikiAuthoringShortcuts {
    public static final KeyMapping PREVIEW=new KeyMapping("key.hbm.item_preview",KeyConflictContext.GUI,KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_LEFT_ALT,"key.categories.hbm");
    public static final KeyMapping EXPORT_GUNS=new KeyMapping("key.hbm.wiki_export_guns",KeyConflictContext.UNIVERSAL,KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_1,"key.categories.hbm");
    public static final KeyMapping EXPORT_COMPANION=new KeyMapping("key.hbm.wiki_export_companion",KeyConflictContext.UNIVERSAL,
            InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_0,"key.categories.hbm");

    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){
        event.register(PREVIEW);event.register(EXPORT_GUNS);event.register(EXPORT_COMPANION);
    }

    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
    public static final class Events {
        private static boolean bulkHeld;

        @SubscribeEvent public static void tick(ClientTickEvent.Pre event){
            var client=Minecraft.getInstance();
            boolean bulk=down(EXPORT_GUNS)&&down(EXPORT_COMPANION);
            if(!bulk)bulkHeld=false;
            if(client.level==null||client.player==null)return;
            if(down(PREVIEW)&&client.screen instanceof AbstractContainerScreen<?> container){
                var slot=container.getSlotUnderMouse();
                if(slot!=null&&slot.hasItem()){
                    var stack=slot.getItem().copyWithCount(1);
                    client.player.closeContainer();
                    client.setScreen(new ItemPreviewScreen(stack));
                }
            }
            if(bulk&&!bulkHeld){
                bulkHeld=true;
                LogUtils.getLogger().info("Taking a screenshot of all HBM guns");
                var stacks=CommandWikiRender.collectVariants(item->item instanceof ItemGunBaseNT);
                client.player.closeContainer();
                client.setScreen(new WikiRenderScreen(stacks,"Gun ",CommandWikiRender.OUTPUT_DIRECTORY,16));
            }
        }
    }

    private static boolean down(KeyMapping mapping){
        var context=mapping.getKeyConflictContext();
        if(!context.isActive()||!mapping.getKeyModifier().isActive(context))return false;
        var key=mapping.getKey();
        if(key.getValue()<0)return false;
        long window=Minecraft.getInstance().getWindow().getWindow();
        return key.getType()==InputConstants.Type.MOUSE?GLFW.glfwGetMouseButton(window,key.getValue())==GLFW.GLFW_PRESS
                :key.getType()==InputConstants.Type.KEYSYM?InputConstants.isKeyDown(window,key.getValue()):mapping.isDown();
    }

    private WikiAuthoringShortcuts(){}
}
