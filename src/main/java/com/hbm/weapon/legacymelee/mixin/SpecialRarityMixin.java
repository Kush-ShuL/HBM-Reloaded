package com.hbm.weapon.legacymelee.mixin;
import com.hbm.weapon.legacymelee.LegacySpecialWeaponItem;
import com.hbm.item.bottle.BottleOpenerItem;
import com.hbm.item.meteorgavel.LegacyGavelItem;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Source WeaponSpecial overrides rarity instead of vanilla's enchanted-sword rarity. */
@Mixin(value=ItemStack.class,remap=false)
public abstract class SpecialRarityMixin {
    @Inject(method="getRarity",at=@At("RETURN"),cancellable=true)
    private void hbm$sourceRarity(CallbackInfoReturnable<Rarity> cir){var item=((ItemStack)(Object)this).getItem();
        if(item instanceof com.hbm.item.toolability.SourceAbilityCombat tool&&tool.sourceRarityOverride()!=null)cir.setReturnValue(tool.sourceRarityOverride());
        else if(item instanceof LegacySpecialWeaponItem special)cir.setReturnValue(special.sourceRarity());
        else if(item instanceof BottleOpenerItem||item instanceof LegacyGavelItem gavel&&gavel.kind()!=LegacyGavelItem.Kind.MESE)cir.setReturnValue(Rarity.COMMON);
    }
}
