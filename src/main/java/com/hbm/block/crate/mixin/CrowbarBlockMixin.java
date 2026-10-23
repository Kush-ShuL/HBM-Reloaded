package com.hbm.block.crate.mixin;

import com.hbm.item.legacysword.LegacySwordBlocking;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Matches old EntityPlayer.damageEntity immediately before armor calculations. */
@Mixin(value=Player.class,remap=false)
public abstract class CrowbarBlockMixin {
    @ModifyArg(method="actuallyHurt",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/player/Player;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"),index=1)
    private float hbm$oldSwordBlock(DamageSource source,float amount){
        //SEDNA's independent damageEntityNT never invokes this vanilla player method.
        var player=(Player)(Object)this;
        return amount>0&&!source.is(DamageTypeTags.BYPASSES_ARMOR)&&player.isUsingItem()&&player.getUseItem().getItem() instanceof LegacySwordBlocking?(1+amount)*.5F:amount;
    }
}
