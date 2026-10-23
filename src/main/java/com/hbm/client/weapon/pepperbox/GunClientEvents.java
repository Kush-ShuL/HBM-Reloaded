package com.hbm.client.weapon.pepperbox;

import com.hbm.client.weapon.pepperbox.mixin.*;
import com.hbm.client.animation.bus.HbmAnimations;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.weapon.sedna.gun.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

/** Original key edges, recoil and HUD hooks. Only validated server callbacks fire or consume ammunition. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class GunClientEvents {
    private static final boolean[][] PRESSED=new boolean[2][GunKey.values().length];
    private static final java.util.UUID[] previousIdentity=new java.util.UUID[2];
    private static boolean previousDual;
    private static int previousSlot=-1;
    private static float cameraYaw,oldCameraYaw,cameraPitch,oldCameraPitch;
    private static net.minecraft.client.player.LocalPlayer cameraPlayer;
    private static final ByteBufferBuilder HAND_MEMORY=new ByteBufferBuilder(16384);
    private static final net.minecraft.client.renderer.MultiBufferSource.BufferSource HAND_BUFFERS=net.minecraft.client.renderer.MultiBufferSource.immediate(HAND_MEMORY);
    @SubscribeEvent public static void beforeTick(ClientTickEvent.Pre event){
        var client=Minecraft.getInstance();var player=client.player;if(player==null)return;
        if(player!=cameraPlayer){cameraPlayer=player;cameraYaw=oldCameraYaw=cameraPitch=oldCameraPitch=0;}
        oldCameraYaw=cameraYaw;oldCameraPitch=cameraPitch;
        var stack=player.getMainHandItem();var highlight=(GunHighlightAccess)client.gui;
        if(stack.getItem() instanceof ItemGunBaseNT||GunHands.operable(player,InteractionHand.OFF_HAND)){
            if(highlight.hbm$lastHighlightedGun().is(stack.getItem()))highlight.hbm$lastHighlightedGun(stack);
            // Only active native gun bindings suppress their overlapping vanilla action; non-gun input is untouched.
            for(var vanilla:java.util.List.of(client.options.keyAttack,client.options.keyUse,client.options.keyPickItem))for(var gunKey:GunClient.bindings())if(gunKey.getKeyConflictContext().isActive()&&vanilla.getKey().equals(gunKey.getKey()))vanilla.setDown(false);
            if(GunHands.dual(player))ItemGunBaseNT.prevAimingProgress=ItemGunBaseNT.aimingProgress=0;
        }
    }
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){
        var client=Minecraft.getInstance();var player=client.player;
        if(player==null){previousSlot=-1;java.util.Arrays.fill(previousIdentity,null);for(var states:PRESSED)java.util.Arrays.fill(states,false);ItemGunBaseNT.prevAimingProgress=ItemGunBaseNT.aimingProgress=0;return;}
        // EntityPlayer.onLivingUpdate in official 1.7.10: horizontal .4 and airborne atan(-motionY*.2)*15 with .8 interpolation.
        var motion=player.getDeltaMovement();float horizontal=Mth.sqrt((float)(motion.x*motion.x+motion.z*motion.z));if(horizontal>.1F)horizontal=.1F;
        float vertical=(float)Math.atan(-motion.y*.20000000298023224D)*15F;
        if(!player.onGround()||player.getHealth()<=0)horizontal=0;if(player.onGround()||player.getHealth()<=0)vertical=0;
        cameraYaw+=(horizontal-cameraYaw)*.4F;cameraPitch+=(vertical-cameraPitch)*.8F;
        int slot=player.getInventory().selected;boolean dual=GunHands.dual(player);
        if(slot!=previousSlot||dual!=previousDual){for(var states:PRESSED)java.util.Arrays.fill(states,false);previousSlot=slot;previousDual=dual;}
        boolean enabled=client.screen==null&&client.isWindowActive()&&!player.isSpectator()&&player.isAlive()&&GunSettings.enabled();
        for(var hand:InteractionHand.values()){
            int side=hand.ordinal();var stack=player.getItemInHand(hand);var identity=GunHands.identity(stack);
            if(!java.util.Objects.equals(identity,previousIdentity[side])){java.util.Arrays.fill(PRESSED[side],false);previousIdentity[side]=identity;}
            if(!(stack.getItem() instanceof ItemGunBaseNT gun)||identity==null||!GunHands.operable(player,hand))continue;
            boolean active=enabled&&(hand==InteractionHand.MAIN_HAND||dual||!(player.getMainHandItem().getItem() instanceof ItemGunBaseNT));
            boolean fire=active&&down(dual?(hand==InteractionHand.OFF_HAND?GunClient.DUAL_OFF:GunClient.DUAL_MAIN):GunClient.PRIMARY);
            // A standard single gun's original ADS callback is TERTIARY. Intrinsic akimbo keeps its actual SECONDARY trigger.
            boolean sourceSecondary=gun.isBuiltInDualWield()||gun.getConfig(stack,0).getPressSecondary(stack)!=null||gun.getConfig(stack,0).getReleaseSecondary(stack)!=null;
            boolean secondary=active&&!dual&&sourceSecondary&&down(GunClient.SECONDARY);
            boolean tertiary=active&&!dual&&(down(GunClient.TERTIARY)||!sourceSecondary&&down(GunClient.SECONDARY));
            boolean[] current={fire,secondary,tertiary,active&&down(GunClient.RELOAD)};
            for(var key:GunKey.values()){int i=key.ordinal();if(current[i]!=PRESSED[side][i]){PacketDistributor.sendToServer(new GunNetwork.Input(slot,hand,BuiltInRegistries.ITEM.getKey(stack.getItem()),identity,key,current[i]));PRESSED[side][i]=current[i];}}
        }
        if(dual||GunHands.viewStack(player).isEmpty())ItemGunBaseNT.prevAimingProgress=ItemGunBaseNT.aimingProgress=0;
        if(GunClientSettings.visualRecoil()){
            ItemGunBaseNT.offsetVertical+=ItemGunBaseNT.recoilVertical;ItemGunBaseNT.offsetHorizontal+=ItemGunBaseNT.recoilHorizontal;
            player.setXRot(player.getXRot()-ItemGunBaseNT.recoilVertical);player.setYRot(player.getYRot()-ItemGunBaseNT.recoilHorizontal);
            ItemGunBaseNT.recoilVertical*=ItemGunBaseNT.recoilDecay;ItemGunBaseNT.recoilHorizontal*=ItemGunBaseNT.recoilDecay;
            float verticalReturn=ItemGunBaseNT.offsetVertical*ItemGunBaseNT.recoilRebound,horizontalReturn=ItemGunBaseNT.offsetHorizontal*ItemGunBaseNT.recoilRebound;
            ItemGunBaseNT.offsetVertical-=verticalReturn;ItemGunBaseNT.offsetHorizontal-=horizontalReturn;
            player.setXRot(player.getXRot()+verticalReturn);player.setYRot(player.getYRot()+horizontalReturn);
        }else ItemGunBaseNT.recoilVertical=ItemGunBaseNT.recoilHorizontal=ItemGunBaseNT.offsetVertical=ItemGunBaseNT.offsetHorizontal=0;
    }
    @SubscribeEvent public static void interact(InputEvent.InteractionKeyMappingTriggered event){
        var player=Minecraft.getInstance().player;if(player==null||!(player.getMainHandItem().getItem() instanceof ItemGunBaseNT)&&!GunHands.operable(player,InteractionHand.OFF_HAND))return;
        // Preserve unrelated vanilla actions when their native key does not overlap an active gun binding.
        for(var key:GunClient.bindings())if(key.getKeyConflictContext().isActive()&&key.getKey().equals(event.getKeyMapping().getKey())){event.setCanceled(true);event.setSwingHand(false);return;}
    }
    @SubscribeEvent public static void fov(ViewportEvent.ComputeFov event){
        var client=Minecraft.getInstance();if(!event.usedConfiguredFov()||client.player==null||GunHands.viewStack(client.player).isEmpty())return;
        float partial=(float)event.getPartialTick();var stack=GunHands.viewStack(client.player);var renderer=net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer();if(renderer instanceof GunFirstPersonRenderer firstPerson)event.setFOV(firstPerson.viewFov(stack,(float)event.getFOV(),partial));
    }
    @SubscribeEvent public static void hud(RenderGuiLayerEvent.Pre event){
        var client=Minecraft.getInstance();var player=client.player;if(player==null||client.options.hideGui)return;
        boolean dual=GunHands.dual(player);var stack=player.getMainHandItem();
        if(!(stack.getItem() instanceof ItemGunBaseNT)){stack=player.getOffhandItem();if(!GunHands.operable(player,InteractionHand.OFF_HAND))return;}
        var gun=(ItemGunBaseNT)stack.getItem();
        var graphics=event.getGuiGraphics();
        if(event.getName().equals(VanillaGuiLayers.CROSSHAIR)){
            event.setCanceled(true);var config=gun.getConfig(stack,0);var crosshair=config.getCrosshair(stack);
            if(crosshair==null||crosshair==Crosshair.NONE||config.getHideCrosshair(stack)&&ItemGunBaseNT.aimingProgress>=1)return;
            graphics.flush();RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
            graphics.blit(GunHudComponents.OVERLAY,graphics.guiWidth()/2-crosshair.size/2,graphics.guiHeight()/2-crosshair.size/2,crosshair.x,crosshair.y,crosshair.size,crosshair.size,256,256);graphics.flush();RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();
        }
        if(event.getName().equals(VanillaGuiLayers.HOTBAR)){
            renderGunHud(graphics,player,stack,dual&&player.getMainArm()==net.minecraft.world.entity.HumanoidArm.LEFT);
            if(dual)renderGunHud(graphics,player,player.getOffhandItem(),player.getMainArm()==net.minecraft.world.entity.HumanoidArm.RIGHT);
        }
    }
    private static void renderGunHud(net.minecraft.client.gui.GuiGraphics graphics,net.minecraft.world.entity.player.Player player,ItemStack stack,boolean left){
        var gun=(ItemGunBaseNT)stack.getItem();graphics.pose().pushPose();
        try{
            int center=graphics.guiWidth()/2,width=GunHudComponents.PANEL_WIDTH;
            int originalX=center+GunHudComponents.PANEL_OFFSET;
            int panelX=left?center-GunHudComponents.PANEL_OFFSET-width:originalX,panelY=0;
            boolean offhandLeft=player.getMainArm()==net.minecraft.world.entity.HumanoidArm.RIGHT;
            if(!player.getOffhandItem().isEmpty()&&left==offhandLeft){
                // Gui.renderItemHotbar draws the occupied offhand frame at
                // center-91-29 or center+91, with size 29x24 at height-23.
                int outside=91+29+4;
                int clearX=left?center-outside-width:center+outside;
                if(clearX>=4&&clearX+width<=graphics.guiWidth()-4)panelX=clearX;
                else panelY=-24; // Keep the complete panel visible on a narrow GUI.
            }
            graphics.pose().translate(panelX-originalX,panelY,0);
            for(int i=0;i<gun.getConfigCount();i++){
                var components=gun.getConfig(stack,i).getHUDComponents(stack);if(components==null)continue;int bottom=0;
                for(var component:components){component.renderHUDComponent(graphics,player,stack,bottom,i);bottom+=component.getComponentHeight(player,stack);}
            }
        }finally{graphics.pose().popPose();}
    }
    @SubscribeEvent public static void hands(RenderHandEvent event){
        var client=Minecraft.getInstance();var player=client.player;if(player==null)return;
        var stack=event.getItemStack();if(!(stack.getItem() instanceof ItemGunBaseNT))return;
        var renderer=net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer();
        if(!(renderer instanceof GunFirstPersonRenderer firstPerson))return;
        event.setCanceled(true);if(!client.options.getCameraType().isFirstPerson()||player.isSleeping()||client.options.hideGui||player.isSpectator())return;
        int shaderHand=GunShaderCompat.handState();
        if(shaderHand==GunShaderCompat.IRIS_TRANSLUCENT)return;
        if(event.getMultiBufferSource() instanceof net.minecraft.client.renderer.MultiBufferSource.BufferSource existing)existing.endBatch();
        float partial=event.getPartialTick();float fov=GunClientSettings.modelFov()?client.options.fov().get():firstPerson.modelFov(stack,partial);
        if(player.getHealth()<=0){float dying=player.deathTime+partial;fov/=((1-500F/(dying+500F))*2+1);}
        if(client.level.getFluidState(client.gameRenderer.getMainCamera().getBlockPosition()).is(FluidTags.WATER))fov*=60F/70F;
        // The second hand must retain the first hand's depth, including a non-gun item.
        if(shaderHand==GunShaderCompat.VANILLA&&event.getHand()==InteractionHand.MAIN_HAND)RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT,Minecraft.ON_OSX);
        RenderSystem.backupProjectionMatrix();
        var modelView=RenderSystem.getModelViewStack();modelView.pushMatrix().identity();RenderSystem.applyModelViewMatrix();
        var shaderItem=GunShaderCompat.item(stack,shaderHand!=GunShaderCompat.VANILLA);
        try{
            RenderSystem.setProjectionMatrix(GunShaderCompat.projection(fov,(float)client.getWindow().getWidth()/client.getWindow().getHeight(),client.options.getEffectiveRenderDistance()*32F,shaderHand!=GunShaderCompat.VANILLA),VertexSorting.DISTANCE_TO_ORIGIN);
            float pitch=Mth.lerp(partial,player.xRotO,player.getXRot()),yaw=Mth.lerp(partial,player.yRotO,player.getYRot());
            var lightRotation=new Matrix4f().rotateX((float)Math.toRadians(pitch)).rotateY((float)Math.toRadians(yaw));
            RenderSystem.setShaderLights(lightRotation.transformDirection(new Vector3f(.2F,1,-.7F).normalize()),lightRotation.transformDirection(new Vector3f(-.2F,1,.7F).normalize()));
            var pose=new PoseStack();float turn=firstPerson.turnMagnitude(stack);
            boolean left=(event.getHand()==InteractionHand.MAIN_HAND?player.getMainArm():player.getMainArm().getOpposite())==net.minecraft.world.entity.HumanoidArm.LEFT;
            try(var animation=HbmAnimations.hand(event.getHand());var reflection=LegacyObjRenderer.mirrorX(pose,left)){
            pose.mulPose(Axis.XP.rotationDegrees((player.getXRot()-Mth.lerp(partial,player.xBobO,player.xBob))*.1F*turn));pose.mulPose(Axis.YP.rotationDegrees((player.getYRot()-Mth.lerp(partial,player.yBobO,player.yBob))*.1F*turn));pose.mulPose(Axis.YP.rotationDegrees(180));
            float distance=-(player.walkDist+(player.walkDist-player.walkDistO)*partial),camYaw=Mth.lerp(partial,oldCameraYaw,cameraYaw),camPitch=Mth.lerp(partial,oldCameraPitch,cameraPitch),sway=firstPerson.swayMagnitude(stack),period=firstPerson.swayPeriod(stack);
            pose.translate(Mth.sin(distance*(float)Math.PI*period)*camYaw*.5F*sway,-Math.abs(Mth.cos(distance*(float)Math.PI*period)*camYaw)*sway,0);
            pose.mulPose(Axis.ZP.rotationDegrees(Mth.sin(distance*(float)Math.PI*period)*camYaw*3));pose.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(distance*(float)Math.PI*period-.2F)*camYaw)*5));pose.mulPose(Axis.XP.rotationDegrees(camPitch));
            firstPerson.renderFirstPerson(stack,pose,HAND_BUFFERS,event.getPackedLight(),net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,partial);HAND_BUFFERS.endBatch();
            }
        }finally{
            try{HAND_BUFFERS.endBatch();}
            finally{
                try{modelView.popMatrix();RenderSystem.applyModelViewMatrix();RenderSystem.restoreProjectionMatrix();Lighting.setupLevel();}
                finally{shaderItem.close();}
            }
        }
    }
    private static boolean down(net.minecraft.client.KeyMapping mapping){var key=mapping.getKey();long window=Minecraft.getInstance().getWindow().getWindow();if(key.getValue()<0)return false;return key.getType()==InputConstants.Type.MOUSE?org.lwjgl.glfw.GLFW.glfwGetMouseButton(window,key.getValue())==org.lwjgl.glfw.GLFW.GLFW_PRESS:key.getType()==InputConstants.Type.KEYSYM?InputConstants.isKeyDown(window,key.getValue()):mapping.isDown();}
    private GunClientEvents(){}
}
