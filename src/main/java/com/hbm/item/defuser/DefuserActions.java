package com.hbm.item.defuser;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.weapon.sedna.death.LegacyDeathEffects;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import net.minecraft.world.entity.LivingEntity;
/** Exact source dying nuclear-Glyphid branch, ready for its real entity to implement NuclearGlyphidDefusable. */
public final class DefuserActions {
    static void nuclearGlyphid(LivingEntity glyphid){
        glyphid.discard();
        new ExplosionVNT(glyphid.level(),glyphid.getX(),glyphid.getY(),glyphid.getZ(),5,glyphid)
            .setEntityProcessor(new EntityProcessorCrossSmooth(1,20).setupPiercing(10F,.2F))
            .setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectWeapon(10,2.5F,1F)).explode();
        LegacyDeathEffects.gib(glyphid);glyphid.spawnAtLocation(PistolAmmo.NUKE_DEMO.toStack(),1.5F);
    }
    private DefuserActions(){}
}
