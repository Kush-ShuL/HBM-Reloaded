package com.hbm.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.UseAnim;

/** Uses original boxes, mirrored UVs, inflation and ModelBiped pose math. */
public final class LegacyHazmatArmorModel extends Model {
    private final LegacyBipedModel original;
    private LivingEntity entity;
    private float limbSwing,limbAmount,age,yaw,pitch;
    public LegacyHazmatArmorModel(float inflation){this(new LegacyBipedModel(inflation));}
    public LegacyHazmatArmorModel(LegacyBipedModel original){super(RenderType::armorCutoutNoCull);this.original=original;}
    public void configure(LivingEntity entity,EquipmentSlot slot,HumanoidModel<?> parent){
        this.entity=entity;
        original.isChild=entity.isBaby();original.isRiding=parent.riding;original.isSneak=entity.isCrouching();original.onGround=parent.attackTime;
        original.heldItemLeft=0;
        original.heldItemRight=entity.getMainHandItem().isEmpty()?0:1;original.aimedBow=false;
        if(entity.isUsingItem()){UseAnim animation=entity.getUseItem().getUseAnimation();if(animation==UseAnim.BLOCK)original.heldItemRight=3;else if(animation==UseAnim.BOW)original.aimedBow=true;}
        original.head.showModel=slot==EquipmentSlot.HEAD;original.headwear.showModel=slot==EquipmentSlot.HEAD;
        original.body.showModel=slot==EquipmentSlot.CHEST||slot==EquipmentSlot.LEGS;
        original.rightArm.showModel=original.leftArm.showModel=slot==EquipmentSlot.CHEST;
        original.rightLeg.showModel=original.leftLeg.showModel=slot==EquipmentSlot.LEGS||slot==EquipmentSlot.FEET;
    }
    public void animate(float limbSwing,float limbAmount,float age,float yaw,float pitch){this.limbSwing=limbSwing;this.limbAmount=limbAmount;this.age=age;this.yaw=yaw;this.pitch=pitch;}
    @Override public void renderToBuffer(PoseStack poses,VertexConsumer vertices,int light,int overlay,int color){original.renderToBuffer(poses,vertices,light,overlay,color,entity,limbSwing,limbAmount,age,yaw,pitch,1F/16F);}
}
