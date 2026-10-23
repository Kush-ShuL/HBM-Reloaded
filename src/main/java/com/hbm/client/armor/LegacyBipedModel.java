package com.hbm.client.armor;

import com.hbm.client.model.legacy.ModelBase;
import com.hbm.client.model.legacy.ModelRenderer;
import com.hbm.client.model.legacy.LegacyModelRenderContext;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** Exact official 1.7.10 ModelBiped box/UV/pose math; explicit legacy render context. */

public class LegacyBipedModel extends ModelBase {
   public ModelRenderer head;
   public ModelRenderer headwear;
   public ModelRenderer body;
   public ModelRenderer rightArm;
   public ModelRenderer leftArm;
   public ModelRenderer rightLeg;
   public ModelRenderer leftLeg;
   public ModelRenderer ears;
   public ModelRenderer cloak;
   public int heldItemLeft;
   public int heldItemRight;
   public boolean isSneak;
   public boolean aimedBow;

   public LegacyBipedModel() {
      this(0.0F);
   }

   public LegacyBipedModel(float var1) {
      this(var1, 0.0F, 64, 32);
   }

   public LegacyBipedModel(float var1, float var2, int var3, int var4) {
      this.textureWidth = var3;
      this.textureHeight = var4;
      this.cloak = new ModelRenderer(this, 0, 0);
      this.cloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, var1);
      this.ears = new ModelRenderer(this, 24, 0);
      this.ears.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, var1);
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1);
      this.head.setRotationPoint(0.0F, 0.0F + var2, 0.0F);
      this.headwear = new ModelRenderer(this, 32, 0);
      this.headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1 + 0.5F);
      this.headwear.setRotationPoint(0.0F, 0.0F + var2, 0.0F);
      this.body = new ModelRenderer(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1);
      this.body.setRotationPoint(0.0F, 0.0F + var2, 0.0F);
      this.rightArm = new ModelRenderer(this, 40, 16);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.rightArm.setRotationPoint(-5.0F, 2.0F + var2, 0.0F);
      this.leftArm = new ModelRenderer(this, 40, 16);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.leftArm.setRotationPoint(5.0F, 2.0F + var2, 0.0F);
      this.rightLeg = new ModelRenderer(this, 0, 16);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.rightLeg.setRotationPoint(-1.9F, 12.0F + var2, 0.0F);
      this.leftLeg = new ModelRenderer(this, 0, 16);
      this.leftLeg.mirror = true;
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.leftLeg.setRotationPoint(1.9F, 12.0F + var2, 0.0F);
   }

   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
      if (this.isChild) {
         float var8 = 2.0F;
         LegacyModelRenderContext.current().poses().pushPose();
         LegacyModelRenderContext.current().poses().scale(1.5F / var8, 1.5F / var8, 1.5F / var8);
         LegacyModelRenderContext.current().poses().translate(0.0F, 16.0F * var7, 0.0F);
         this.head.render(var7);
         LegacyModelRenderContext.current().poses().popPose();
         LegacyModelRenderContext.current().poses().pushPose();
         LegacyModelRenderContext.current().poses().scale(1.0F / var8, 1.0F / var8, 1.0F / var8);
         LegacyModelRenderContext.current().poses().translate(0.0F, 24.0F * var7, 0.0F);
         this.body.render(var7);
         this.rightArm.render(var7);
         this.leftArm.render(var7);
         this.rightLeg.render(var7);
         this.leftLeg.render(var7);
         this.headwear.render(var7);
         LegacyModelRenderContext.current().poses().popPose();
      } else {
         this.head.render(var7);
         this.body.render(var7);
         this.rightArm.render(var7);
         this.leftArm.render(var7);
         this.rightLeg.render(var7);
         this.leftLeg.render(var7);
         this.headwear.render(var7);
      }
   }

   public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.head.rotateAngleY = var4 / (180.0F / (float)Math.PI);
      this.head.rotateAngleX = var5 / (180.0F / (float)Math.PI);
      this.headwear.rotateAngleY = this.head.rotateAngleY;
      this.headwear.rotateAngleX = this.head.rotateAngleX;
      this.rightArm.rotateAngleX = Mth.cos(var1 * 0.6662F + (float) Math.PI) * 2.0F * var2 * 0.5F;
      this.leftArm.rotateAngleX = Mth.cos(var1 * 0.6662F) * 2.0F * var2 * 0.5F;
      this.rightArm.rotateAngleZ = 0.0F;
      this.leftArm.rotateAngleZ = 0.0F;
      this.rightLeg.rotateAngleX = Mth.cos(var1 * 0.6662F) * 1.4F * var2;
      this.leftLeg.rotateAngleX = Mth.cos(var1 * 0.6662F + (float) Math.PI) * 1.4F * var2;
      this.rightLeg.rotateAngleY = 0.0F;
      this.leftLeg.rotateAngleY = 0.0F;
      if (this.isRiding) {
         this.rightArm.rotateAngleX += (float) (-Math.PI / 5);
         this.leftArm.rotateAngleX += (float) (-Math.PI / 5);
         this.rightLeg.rotateAngleX = (float) (-Math.PI * 2.0 / 5.0);
         this.leftLeg.rotateAngleX = (float) (-Math.PI * 2.0 / 5.0);
         this.rightLeg.rotateAngleY = (float) (Math.PI / 10);
         this.leftLeg.rotateAngleY = (float) (-Math.PI / 10);
      }

      if (this.heldItemLeft != 0) {
         this.leftArm.rotateAngleX = this.leftArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemLeft;
      }

      if (this.heldItemRight != 0) {
         this.rightArm.rotateAngleX = this.rightArm.rotateAngleX * 0.5F - (float) (Math.PI / 10) * this.heldItemRight;
      }

      this.rightArm.rotateAngleY = 0.0F;
      this.leftArm.rotateAngleY = 0.0F;
      if (this.onGround > -9990.0F) {
         float var8 = this.onGround;
         this.body.rotateAngleY = Mth.sin(Mth.sqrt(var8) * (float) Math.PI * 2.0F) * 0.2F;
         this.rightArm.rotationPointZ = Mth.sin(this.body.rotateAngleY) * 5.0F;
         this.rightArm.rotationPointX = -Mth.cos(this.body.rotateAngleY) * 5.0F;
         this.leftArm.rotationPointZ = -Mth.sin(this.body.rotateAngleY) * 5.0F;
         this.leftArm.rotationPointX = Mth.cos(this.body.rotateAngleY) * 5.0F;
         this.rightArm.rotateAngleY = this.rightArm.rotateAngleY + this.body.rotateAngleY;
         this.leftArm.rotateAngleY = this.leftArm.rotateAngleY + this.body.rotateAngleY;
         this.leftArm.rotateAngleX = this.leftArm.rotateAngleX + this.body.rotateAngleY;
         var8 = 1.0F - this.onGround;
         var8 *= var8;
         var8 *= var8;
         var8 = 1.0F - var8;
         float var9 = Mth.sin(var8 * (float) Math.PI);
         float var10 = Mth.sin(this.onGround * (float) Math.PI) * -(this.head.rotateAngleX - 0.7F) * 0.75F;
         this.rightArm.rotateAngleX = (float)(this.rightArm.rotateAngleX - (var9 * 1.2 + var10));
         this.rightArm.rotateAngleY = this.rightArm.rotateAngleY + this.body.rotateAngleY * 2.0F;
         this.rightArm.rotateAngleZ = Mth.sin(this.onGround * (float) Math.PI) * -0.4F;
      }

      if (this.isSneak) {
         this.body.rotateAngleX = 0.5F;
         this.rightArm.rotateAngleX += 0.4F;
         this.leftArm.rotateAngleX += 0.4F;
         this.rightLeg.rotationPointZ = 4.0F;
         this.leftLeg.rotationPointZ = 4.0F;
         this.rightLeg.rotationPointY = 9.0F;
         this.leftLeg.rotationPointY = 9.0F;
         this.head.rotationPointY = 1.0F;
         this.headwear.rotationPointY = 1.0F;
      } else {
         this.body.rotateAngleX = 0.0F;
         this.rightLeg.rotationPointZ = 0.1F;
         this.leftLeg.rotationPointZ = 0.1F;
         this.rightLeg.rotationPointY = 12.0F;
         this.leftLeg.rotationPointY = 12.0F;
         this.head.rotationPointY = 0.0F;
         this.headwear.rotationPointY = 0.0F;
      }

      this.rightArm.rotateAngleZ = this.rightArm.rotateAngleZ + (Mth.cos(var3 * 0.09F) * 0.05F + 0.05F);
      this.leftArm.rotateAngleZ = this.leftArm.rotateAngleZ - (Mth.cos(var3 * 0.09F) * 0.05F + 0.05F);
      this.rightArm.rotateAngleX = this.rightArm.rotateAngleX + Mth.sin(var3 * 0.067F) * 0.05F;
      this.leftArm.rotateAngleX = this.leftArm.rotateAngleX - Mth.sin(var3 * 0.067F) * 0.05F;
      if (this.aimedBow) {
         float var15 = 0.0F;
         float var16 = 0.0F;
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.rightArm.rotateAngleY = -(0.1F - var15 * 0.6F) + this.head.rotateAngleY;
         this.leftArm.rotateAngleY = 0.1F - var15 * 0.6F + this.head.rotateAngleY + 0.4F;
         this.rightArm.rotateAngleX = (float) (-Math.PI / 2) + this.head.rotateAngleX;
         this.leftArm.rotateAngleX = (float) (-Math.PI / 2) + this.head.rotateAngleX;
         this.rightArm.rotateAngleX -= var15 * 1.2F - var16 * 0.4F;
         this.leftArm.rotateAngleX -= var15 * 1.2F - var16 * 0.4F;
         this.rightArm.rotateAngleZ = this.rightArm.rotateAngleZ + (Mth.cos(var3 * 0.09F) * 0.05F + 0.05F);
         this.leftArm.rotateAngleZ = this.leftArm.rotateAngleZ - (Mth.cos(var3 * 0.09F) * 0.05F + 0.05F);
         this.rightArm.rotateAngleX = this.rightArm.rotateAngleX + Mth.sin(var3 * 0.067F) * 0.05F;
         this.leftArm.rotateAngleX = this.leftArm.rotateAngleX - Mth.sin(var3 * 0.067F) * 0.05F;
      }
   }

   public void renderEars(float var1) {
      this.ears.rotateAngleY = this.head.rotateAngleY;
      this.ears.rotateAngleX = this.head.rotateAngleX;
      this.ears.rotationPointX = 0.0F;
      this.ears.rotationPointY = 0.0F;
      this.ears.render(var1);
   }

   public void renderCloak(float var1) {
      this.cloak.render(var1);
   }
}
