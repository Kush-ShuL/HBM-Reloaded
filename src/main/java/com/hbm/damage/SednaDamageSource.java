package com.hbm.damage;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/** Original BulletConfig.getDamage and both SEDNA death-message variants. */
public final class SednaDamageSource extends DamageSource {
    private final boolean withAttacker;
    private SednaDamageSource(Level level,Entity projectile,LivingEntity shooter,DamageResistanceHandler.DamageClass damageClass){
        super(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.fromNamespaceAndPath("hbm","sedna_"+damageClass.name().toLowerCase(Locale.US)))),shooter==null?null:projectile,shooter);
        withAttacker=shooter!=null;
    }
    public static DamageSource create(Level level,Entity projectile,LivingEntity shooter,DamageResistanceHandler.DamageClass damageClass){return new SednaDamageSource(level,projectile,shooter,damageClass);}
    @Override public Component getLocalizedDeathMessage(LivingEntity died){
        String key="death.sedna."+getMsgId();
        if(!withAttacker)return Component.translatable(key,died.getDisplayName());
        Component name=getEntity()==null?Component.translatable("damage.hbm.unknown_attacker").withStyle(ChatFormatting.OBFUSCATED):getEntity().getDisplayName();
        return Component.translatable(key+".attacker",died.getDisplayName(),name);
    }
}
