package com.hbm.mixin;

import com.hbm.armor.LegacyArmorDurability;
import com.hbm.armor.LegacyArmorDurabilityRules;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/** Only marked HBM armor uses the original Unbreaking draws and strict > break. */
// Target 1.21.1 NeoForge uses official Mojang names in production and development.
@Mixin(value=ItemStack.class,remap=false)
public abstract class LegacyArmorDurabilityMixin {
    @Unique private static final String HURT="hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V";

    @Redirect(method=HURT,at=@At(value="INVOKE",target="Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processDurabilityChange(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"),require=1,allow=1)
    private int hbm$sourceUnbreaking(ServerLevel level,ItemStack stack,int amount,int requestedDamage,ServerLevel enclosingLevel,LivingEntity holder,Consumer<Item> onBroken){
        if(stack.getItem() instanceof LegacyArmorDurability&&holder!=null)return LegacyArmorDurabilityRules.damageAfterUnbreaking(level,stack,amount,holder.getRandom());
        return EnchantmentHelper.processDurabilityChange(level,stack,amount);
    }

    @Inject(method=HURT,at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;getMaxDamage()I"),cancellable=true,locals=LocalCapture.CAPTURE_FAILHARD,require=1,allow=1)
    private void hbm$keepOriginalFinalPoint(int amount,ServerLevel level,LivingEntity holder,Consumer<Item> onBroken,CallbackInfo callback,int resultingDamage){
        ItemStack stack=(ItemStack)(Object)this;
        // Bytecode offset96: resultingDamage is int local5, already written by
        // setDamageValue at90. The callback/stack decrement are the only tail
        // operations. Ordinary source armor keeps strict >; powered FSB uses
        // actual post-setDamage durability, since its setter consumes HE instead.
        // This also avoids max+1 overflow and never changes public max values.
        if(stack.getItem() instanceof LegacyArmorDurability&&(resultingDamage==stack.getMaxDamage()||((LegacyArmorDurability)stack.getItem()).legacyUsesPostSetDamage()&&stack.getDamageValue()<=stack.getMaxDamage()))callback.cancel();
    }
}
