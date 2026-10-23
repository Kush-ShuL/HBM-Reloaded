// Generated from the user-supplied X5846 archive by tools/port_java_models.py.
// Source SHA-256: 01e92e85ec67ad22c2ffad19651cae099281caa7de8ab1cb01e3bb6dd1ac2af8
package com.hbm.client.model.legacy.generated;

import com.hbm.client.model.legacy.ModelBase;
import com.hbm.client.model.legacy.ModelRenderer;
import net.minecraft.world.entity.Entity;

public class ModelBullet extends ModelBase {

	ModelRenderer bullet;

	public ModelBullet() {
		this.textureWidth = 8;
		this.textureHeight = 4;

		this.bullet = new ModelRenderer(this, 0, 0);
		this.bullet.addBox(0F, 0F, 0F, 2, 1, 1);
		this.bullet.setRotationPoint(1F, -0.5F, -0.5F);
		this.bullet.setTextureSize(8, 4);
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
