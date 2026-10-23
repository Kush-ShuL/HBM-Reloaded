package com.hbm.entity.mob.glyphid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
/** Actual stronger ranged child, preserving its own skin, size, stats, projectile count/damage/spread and speed. */
public final class EntityGlyphidBlaster extends EntityGlyphidBombardier {
    public EntityGlyphidBlaster(EntityType<? extends EntityGlyphidBlaster> type,Level level){super(type,level);}
    @Override public ResourceLocation getSkin(){return ResourceLocation.parse("hbm:textures/entity/glyphid_blaster.png");}
    @Override public double sourceScale(){return 1.25;}
    @Override public GlyphidStats.StatBundle getStats(){return GlyphidStats.getStats().getBlaster();}
    @Override public boolean isArmorBroken(float amount){return random.nextInt(100)<=Math.min(Math.pow(amount*.25,2),100);}
    @Override public float getBombDamage(){return 15;}@Override public int getBombCount(){return 10;}@Override public float getSpreadMult(){return .5F;}@Override public double getV0(){return 1.25;}
}
