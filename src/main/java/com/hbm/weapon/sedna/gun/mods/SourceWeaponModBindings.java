package com.hbm.weapon.sedna.gun.mods;

import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.BulletConfig;
import java.util.function.IntFunction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Source definitions are shared by several factories; later admissions append without replacing earlier guns. */
public final class SourceWeaponModBindings {
    private SourceWeaponModBindings(){}
    public static XWeaponModManager.WeaponModDefinition definition(String item,int metadata){
        var stack=new ItemStack(HbmContent.item(item));stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);
        return definition(stack);
    }
    private static XWeaponModManager.WeaponModDefinition definition(ItemStack stack){
        var existing=XWeaponModManager.stackToMod.get(new XWeaponModManager.ComparableStack(stack));
        return existing!=null?existing:new XWeaponModManager.WeaponModDefinition(stack);
    }
    public static <T extends IWeaponMod>T handler(int id,Class<T> type,IntFunction<T> factory){
        var existing=XWeaponModManager.idToMod.get(id);
        if(existing==null)return factory.apply(id);
        if(!type.isInstance(existing))throw new IllegalStateException("Source weapon mod "+id+" already has another actual handler");
        return type.cast(existing);
    }
    /** Source caliber definitions are appended by the factory that owns the actual ammunition. */
    public static void caliber(int metadata,Item gun,int id,int capacity,float damage,BulletConfig... ammunition){
        if(ammunition.length==0)throw new IllegalArgumentException("Caliber mod requires its actual ammunition");
        for(var config:ammunition)java.util.Objects.requireNonNull(config,"Unbound source caliber ammunition");
        definition(com.hbm.weapon.sedna.gun.mods.caliber.FlatCaliberContent.stack(metadata,1)).addMod(gun,handler(id,WeaponModCaliber.class,key->new WeaponModCaliber(key,capacity,damage,ammunition)));
    }
}
