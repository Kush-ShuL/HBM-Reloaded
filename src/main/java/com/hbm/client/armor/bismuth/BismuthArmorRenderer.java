package com.hbm.client.armor.bismuth;

import com.hbm.armor.bismuth.*;
import com.hbm.client.armor.t51.T51ArmorRenderer;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;

/** Source ModelArmorBismuth8 real OBJ groups/pivots; one existing FSB parent-pose/armor-foil bridge. */
public final class BismuthArmorRenderer {
    public static final ResourceLocation MODEL=BismuthContent.id("models/armor/bismuth.obj"),TEXTURE=BismuthContent.id("textures/armor/bismuth.png");
    public static boolean render(PoseStack poses,MultiBufferSource buffers,int light,LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){
        var stack=entity.getItemBySlot(slot);if(!(stack.getItem() instanceof ArmorBismuth item)||item.getEquipmentSlot()!=slot)return false;
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);boolean foil=stack.hasFoil();
        switch(slot){
            case HEAD->T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.head,0,0,0,0,"Head");
            case CHEST->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.leftArm,5,2,0,0,"LeftArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.rightArm,-5,2,0,0,"RightArm");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.body,0,0,0,0,"Body");}
            case LEGS->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.leftLeg,1.9F,12,0,0,"LeftLeg");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.rightLeg,-1.9F,12,0,0,"RightLeg");}
            case FEET->{T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.leftLeg,1.9F,12,0,0,"LeftFoot");T51ArmorRenderer.part(mesh,poses,buffers,light,foil,TEXTURE,parent.rightLeg,-1.9F,12,0,0,"RightFoot");}
            default->{}
        }
        return true;
    }
    private BismuthArmorRenderer(){}
}
