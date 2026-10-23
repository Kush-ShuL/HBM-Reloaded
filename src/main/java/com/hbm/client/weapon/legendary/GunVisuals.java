package com.hbm.client.weapon.legendary;
import com.hbm.client.weapon.pepperbox.GunClient;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
/** Each real gun supplies its original model, FOV and arm-turn behavior to the existing common hand path. */
public final class GunVisuals {
    private static final Map<Item,LegacyGunRenderer> RENDERERS=new IdentityHashMap<>();
    public static void register(Item item,LegacyGunRenderer renderer){RENDERERS.put(item,renderer);}
    public static float viewFov(ItemStack stack,float fov,float partial){var renderer=RENDERERS.get(stack.getItem());return renderer!=null?renderer.viewFov(stack,fov,partial):fov*(1-Mth.lerp(partial,ItemGunBaseNT.prevAimingProgress,ItemGunBaseNT.aimingProgress)*.33F);}
    public static float turn(ItemStack stack){var renderer=RENDERERS.get(stack.getItem());return renderer!=null?renderer.turnMagnitude(stack):ItemGunBaseNT.getIsAiming(stack)?2.5F:-.5F;}
    public static void firstPerson(ItemStack stack,PoseStack poses,MultiBufferSource buffers,int light,int overlay,float partial){var renderer=RENDERERS.get(stack.getItem());if(renderer!=null)renderer.firstPerson(stack,poses,buffers,light,overlay,partial);else GunClient.renderer().renderFirstPerson(stack,poses,buffers,light,overlay,partial);}
    private GunVisuals(){}
}
