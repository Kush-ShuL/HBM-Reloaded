package com.hbm.weapon.sedna.gun.mags;

import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Source belt magazine: no imaginary loaded inventory; real player slots and nested ammunition bags supply every shot. */
public final class MagazineBelt implements IMagazine<BulletConfig> {
    public static final String KEY_MAG_TYPE="magtype";
    public final List<BulletConfig> acceptedBullets=new ArrayList<>();
    public MagazineBelt addConfigs(BulletConfig... configs){Collections.addAll(acceptedBullets,configs);return this;}
    private static HolderLookup.Provider lookup(Container inventory){return inventory instanceof GunInventory inv?inv.player.level().registryAccess():RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);}
    @Override public BulletConfig getType(ItemStack stack,Container inventory){var config=getFirstConfig(stack,inventory);if(getMagType(stack)!=config.id)setMagType(stack,config.id);return config;}
    public BulletConfig getFirstConfig(ItemStack stack,Container inventory){
        if(inventory==null)return acceptedBullets.get(0);
        if(inventory!=null)for(int i=0;i<inventory.getContainerSize();i++){
            var slot=inventory.getItem(i);if(slot.isEmpty())continue;
            for(var config:acceptedBullets)if(config.ammo.matchesRecipe(slot,true))return config;
            if(slot.getItem() instanceof AmmoInventoryProvider provider){var bag=provider.ammoInventory(slot,lookup(inventory));for(int j=0;j<bag.getContainerSize();j++){var inside=bag.getItem(j);if(!inside.isEmpty())for(var config:acceptedBullets)if(config.ammo.matchesRecipe(inside,true))return config;}}
        }
        String name=GunStackData.data(stack).getString("magtype_belt_name");var cached=name.isEmpty()?null:BulletConfig.byName(name);
        int id=getMagType(stack);if(cached==null&&id>=0&&id<BulletConfig.configs.size())cached=BulletConfig.configs.get(id);
        return inventory==null?acceptedBullets.get(0):acceptedBullets.contains(cached)?cached:acceptedBullets.get(0);
    }
    @Override public void useUpAmmo(ItemStack stack,Container inventory,int amount){
        if(inventory==null||!GunArmorPerks.shouldUseUp(inventory))return;var first=getFirstConfig(stack,inventory);
        for(int i=0;i<inventory.getContainerSize();i++){
            var slot=inventory.getItem(i);if(slot.isEmpty())continue;
            if(first.ammo.matchesRecipe(slot,true)){int remove=Math.min(slot.getCount(),amount);amount-=remove;inventory.removeItem(i,remove);IMagazine.handleAmmoBag(inventory,first,remove);if(amount<=0)return;}
            if(slot.getItem() instanceof AmmoInventoryProvider provider){boolean infinite=provider.infiniteAmmo(slot);var bag=provider.ammoInventory(slot,lookup(inventory));for(int j=0;j<bag.getContainerSize();j++){var inside=bag.getItem(j);if(!inside.isEmpty()&&first.ammo.matchesRecipe(inside,true)){int remove=Math.min(inside.getCount(),amount);amount-=remove;if(!infinite)bag.removeItem(j,remove);IMagazine.handleAmmoBag(inventory,first,remove);if(amount<=0)return;}}}
        }
    }
    @Override public int getAmount(ItemStack stack,Container inventory){if(inventory==null)return 1;var first=getFirstConfig(stack,inventory);int count=0;
        for(int i=0;i<inventory.getContainerSize();i++){var slot=inventory.getItem(i);if(slot.isEmpty())continue;if(first.ammo.matchesRecipe(slot,true))count+=slot.getCount();
            if(slot.getItem() instanceof AmmoInventoryProvider provider){boolean infinite=provider.infiniteAmmo(slot);var bag=provider.ammoInventory(slot,lookup(inventory));for(int j=0;j<bag.getContainerSize();j++){var inside=bag.getItem(j);if(!inside.isEmpty()&&first.ammo.matchesRecipe(inside,true)){if(infinite)return 9999;count+=inside.getCount();}}}
        }return count;
    }
    @Override public ItemStack getIconForHUD(ItemStack stack,Player player){return getFirstConfig(stack,player==null?null:new GunInventory(player)).ammo.toStack();}
    @Override public String reportAmmoStateForHUD(ItemStack stack,Player player){return "x"+getAmount(stack,player==null?null:new GunInventory(player));}
    @Override public SpentCasing getCasing(ItemStack stack,Container inventory){return getFirstConfig(stack,inventory).casing;}
    // A belt has no stored capacity, load action, or before/after reload counters in the source.
    @Override public void setType(ItemStack stack,BulletConfig type){}@Override public int getCapacity(ItemStack stack){return 0;}@Override public void setAmount(ItemStack stack,int amount){}
    @Override public boolean canReload(ItemStack stack,Container inventory){return false;}@Override public void initNewType(ItemStack stack,Container inventory){}@Override public void reloadAction(ItemStack stack,Container inventory){}
    @Override public void setAmountBeforeReload(ItemStack stack,int amount){}@Override public int getAmountBeforeReload(ItemStack stack){return 0;}@Override public void setAmountAfterReload(ItemStack stack,int amount){}@Override public int getAmountAfterReload(ItemStack stack){return 0;}
    public static int getMagType(ItemStack stack){return ItemGunBaseNT.getValueInt(stack,KEY_MAG_TYPE);}
    public static void setMagType(ItemStack stack,int value){ItemGunBaseNT.setValueInt(stack,KEY_MAG_TYPE,value);GunStackData.update(stack,data->data.putString("magtype_belt_name",BulletConfig.configs.get(value).identifier.toString()));}
}
