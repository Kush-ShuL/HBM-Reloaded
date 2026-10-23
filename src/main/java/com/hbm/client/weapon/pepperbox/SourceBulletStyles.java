package com.hbm.client.weapon.pepperbox;
import java.util.function.BiConsumer;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
/** Exact source LegoClient standard ballistic colors/fullbright and tapered geometry. */
public final class SourceBulletStyles {
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_STANDARD_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0xFFBF00, 0xFFFFFF, length,0.03125D,0.03125D*0.25D,false);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLECHETTE_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0x8C8C8C, 0xCACACA, length,0.03125D,0.03125D*0.25D,false);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_AP_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0xFF6A00, 0xFFE28D, length,0.03125D,0.03125D*0.25D,false);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FRAGMENTATION = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0xFF6A00, 0xFFE28D, length,0.03125D,0.03125D*0.25D,true);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_EXPRESS_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0x9E082E, 0xFF8A79, length,0.03125D,0.03125D*0.25D,false);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_DU_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0x5CCD41, 0xE9FF8D, length,0.03125D,0.03125D*0.25D,false);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_HE_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0xD8CA00, 0xFFF19D, length,0.03125D,0.03125D*0.25D,true);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_SM_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0x42A8DD, 0xFFFFFF, length,0.03125D,0.03125D*0.25D,true);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_BLACK_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0x000000, 0x7F006E, length,0.03125D,0.03125D*0.25D,true);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_TRACER_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0x9E082E, 0xFF8A79, length,0.03125D,0.03125D*0.25D,true);
	};
	public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_LEGENDARY_BULLET = (bullet, interp) -> {
		double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
		if(length <= 0) return;
		GunBulletRenderer.renderBulletStandard( 0x7F006E, 0xFF7FED, length,0.03125D,0.03125D*0.25D,true);
	};
}
