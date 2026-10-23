// Generated from the user-supplied X5846 archive by tools/port_java_models.py.
// Source SHA-256: 3c0f537782b32746b47a8c38d4729fd8ba651167e7362eed2c6b8362bce06326
package com.hbm.client.model.legacy.generated;

import com.hbm.client.model.legacy.ModelBase;
import com.hbm.client.model.legacy.ModelRenderer;
import net.minecraft.world.entity.Entity;

public class ModelChopperMine extends ModelBase {

	ModelRenderer bullet;

	public ModelChopperMine() {
		this.textureWidth = 32;
		this.textureHeight = 16;

		this.bullet = new ModelRenderer(this, 0, 0);
		this.bullet.addBox(0F, 0F, 0F, 8, 8, 8);
		this.bullet.setRotationPoint(-4F, -4F, -4F);
		this.bullet.setTextureSize(32, 16);
		this.bullet.mirror = true;
		setRotation(this.bullet, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {

		this.renderAll(scaleFactor);
	}

	public void renderAll(float scaleFactor) {

		this.bullet.render(scaleFactor);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {

		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
