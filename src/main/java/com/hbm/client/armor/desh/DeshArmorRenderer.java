package com.hbm.client.armor.desh;
import com.hbm.armor.desh.ArmorDesh;
import com.hbm.armor.desh.DeshContent;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
/** Exact ModelArmorDesh source eight OBJ groups, four skins and original bone pivots. */
public final class DeshArmorRenderer {
    public static final ResourceLocation MODEL=DeshContent.id("models/armor/steamsuit.obj");
    public static final ResourceLocation HELMET=texture("helmet"),CHEST=texture("chest"),ARM=texture("arm"),LEG=texture("leg");
    private static ResourceLocation texture(String part){return DeshContent.id("textures/armor/steamsuit_"+part+".png");}
    public static boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){
        var stack=entity.getItemBySlot(slot);if(!(stack.getItem()instanceof ArmorDesh armor)||armor.getEquipmentSlot()!=slot)return false;
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);boolean foil=stack.hasFoil();
        switch(slot){
            case HEAD->T51ArmorRenderer.part(mesh,poses,buffers,light,foil,HELMET,parent.head,0,0,0,0,"Head");
            case CHEST->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,CHEST,parent.body,0,0,0,0,"Body");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,ARM,parent.leftArm,5,2,0,0,"LeftArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,ARM,parent.rightArm,-5,2,0,0,"RightArm");}
            case LEGS->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.leftLeg,1.9F,12,0,0,"LeftLeg");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.rightLeg,-1.9F,12,0,0,"RightLeg");}
            case FEET->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.leftLeg,1.9F,12,0,0,"LeftBoot");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,LEG,parent.rightLeg,-1.9F,12,0,0,"RightBoot");}
            default->{}
        }return true;
    }
    private DeshArmorRenderer(){}
}
