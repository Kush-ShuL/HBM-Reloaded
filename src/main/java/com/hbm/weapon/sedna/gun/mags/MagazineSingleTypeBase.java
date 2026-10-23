package com.hbm.weapon.sedna.gun.mags;

import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import java.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Original single-type magazine, including inventory order, partial-type locking, per-item reload count and bag traversal. */
public abstract class MagazineSingleTypeBase implements IMagazine<BulletConfig> {
    public static final String KEY_MAG_COUNT="magcount", KEY_MAG_TYPE="magtype", KEY_MAG_PREV="magprev", KEY_MAG_AFTER="magafter";
    public final List<BulletConfig> acceptedBullets = new ArrayList<>();
    public final int index; public int capacity;
    public MagazineSingleTypeBase(int index, int capacity) { this.index=index; this.capacity=capacity; }
    public MagazineSingleTypeBase addConfigs(BulletConfig... configs) { Collections.addAll(acceptedBullets, configs); return this; }
    @Override public BulletConfig getType(ItemStack stack, Container inventory) {
        String stableName=GunStackData.data(stack).getString("magtype_name"+index);
        if(!stableName.isEmpty()){var config=BulletConfig.byName(stableName);if(config!=null)return acceptedBullets.contains(config)?config:acceptedBullets.get(0);}
        int type=getMagType(stack,index);
        if(type>=0&&type<BulletConfig.configs.size()){var config=BulletConfig.configs.get(type);return acceptedBullets.contains(config)?config:acceptedBullets.get(0);}return null;
    }
    @Override public void setType(ItemStack stack, BulletConfig config) { int id=BulletConfig.configs.indexOf(config);if(id>=0){setMagType(stack,index,id);GunStackData.update(stack,data->data.putString("magtype_name"+index,config.identifier.toString()));} }
    @Override public ItemStack getIconForHUD(ItemStack stack, Player player) { var config=getType(stack,player==null?null:new GunInventory(player));return config==null?ItemStack.EMPTY:config.ammo.toStack(); }
    @Override public String reportAmmoStateForHUD(ItemStack stack, Player player) { return getAmount(stack,null)+" / "+getCapacity(stack); }
    @Override public SpentCasing getCasing(ItemStack stack, Container inventory) { return getType(stack,inventory).casing; }
    @Override public void useUpAmmo(ItemStack stack, Container inventory, int amount) { if(!GunArmorPerks.shouldUseUp(inventory)&&getCapacity(stack)!=1)return;setAmount(stack,getAmount(stack,inventory)-amount);IMagazine.handleAmmoBag(inventory,getType(stack,inventory),amount); }
    @Override public boolean canReload(ItemStack stack, Container inventory) { return getAmount(stack,inventory)<getCapacity(stack)&&(inventory==null||getFirstConfig(stack,inventory)!=null); }
    public void standardReload(ItemStack stack, Container inventory, int loadLimit) {
        if(inventory==null){if(getType(stack,null)==null)setType(stack,acceptedBullets.get(0));setAmount(stack,capacity);return;}
        for(int i=0;i<inventory.getContainerSize();i++){
            if(loadLimit<=0)return;var slot=inventory.getItem(i);if(slot.isEmpty())continue;
            int loaded=loadSlot(stack,inventory,i,slot,loadLimit,false);loadLimit-=loaded;
            if(slot.getItem() instanceof AmmoInventoryProvider provider){
                var lookup=inventory instanceof GunInventory inv?inv.player.level().registryAccess():net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
                var bag=provider.ammoInventory(slot,lookup);
                for(int j=0;j<bag.getContainerSize();j++){var bagSlot=bag.getItem(j);if(!bagSlot.isEmpty())loadLimit-=loadSlot(stack,bag,j,bagSlot,loadLimit,provider.infiniteAmmo(slot));}
            }
        }
    }
    private int loadSlot(ItemStack gun,Container inventory,int index,ItemStack slot,int limit,boolean infinite){
        BulletConfig config=null;
        if(getAmount(gun,null)==0){for(var accepted:acceptedBullets)if(accepted.ammo.matchesRecipe(slot,true)){config=accepted;setType(gun,config);break;}}
        else{config=getType(gun,null);if(config==null){config=acceptedBullets.get(0);setType(gun,config);}if(!config.ammo.matchesRecipe(slot,true))return 0;}
        if(config==null)return 0;
        int current=getAmount(gun,null),wanted=(int)Math.ceil((double)(getCapacity(gun)-current)/config.ammoReloadCount);
        int count=Math.min(Math.min(wanted,infinite?9_999:slot.getCount()),limit);
        setAmount(gun,Math.min(current+count*config.ammoReloadCount,capacity));if(!infinite)inventory.removeItem(index,count);return count;
    }
    public BulletConfig getFirstConfig(ItemStack stack,Container inventory){
        if(inventory==null)return null;
        for(int i=0;i<inventory.getContainerSize();i++){
            var slot=inventory.getItem(i);if(slot.isEmpty())continue;var found=match(stack,slot);if(found!=null)return found;
            if(slot.getItem() instanceof AmmoInventoryProvider provider){
                var lookup=inventory instanceof GunInventory inv?inv.player.level().registryAccess():net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
                var bag=provider.ammoInventory(slot,lookup);for(int j=0;j<bag.getContainerSize();j++){var bagSlot=bag.getItem(j);if(!bagSlot.isEmpty()){found=match(stack,bagSlot);if(found!=null)return found;}}
            }
        }return null;
    }
    private BulletConfig match(ItemStack stack,ItemStack slot){if(getAmount(stack,null)==0){for(var config:acceptedBullets)if(config.ammo.matchesRecipe(slot,true))return config;}else{var config=getType(stack,null);if(config==null){config=acceptedBullets.get(0);setType(stack,config);}if(config.ammo.matchesRecipe(slot,true))return config;}return null;}
    @Override public void initNewType(ItemStack stack,Container inventory){if(inventory!=null){var config=getFirstConfig(stack,inventory);if(config!=null)setType(stack,config);}}
    @Override public int getCapacity(ItemStack stack){return capacity;}
    @Override public int getAmount(ItemStack stack,Container inventory){return getMagCount(stack,index);}
    @Override public void setAmount(ItemStack stack,int amount){setMagCount(stack,index,Math.max(amount,0));}
    @Override public void setAmountBeforeReload(ItemStack stack,int amount){ItemGunBaseNT.setValueInt(stack,KEY_MAG_PREV+index,amount);}
    @Override public int getAmountBeforeReload(ItemStack stack){return ItemGunBaseNT.getValueInt(stack,KEY_MAG_PREV+index);}
    @Override public void setAmountAfterReload(ItemStack stack,int amount){ItemGunBaseNT.setValueInt(stack,KEY_MAG_AFTER+index,amount);}
    @Override public int getAmountAfterReload(ItemStack stack){return ItemGunBaseNT.getValueInt(stack,KEY_MAG_AFTER+index);}
    public static int getMagType(ItemStack stack,int index){return ItemGunBaseNT.getValueInt(stack,KEY_MAG_TYPE+index);}
    public static void setMagType(ItemStack stack,int index,int value){ItemGunBaseNT.setValueInt(stack,KEY_MAG_TYPE+index,value);}
    public static int getMagCount(ItemStack stack,int index){return ItemGunBaseNT.getValueInt(stack,KEY_MAG_COUNT+index);}
    public static void setMagCount(ItemStack stack,int index,int value){ItemGunBaseNT.setValueInt(stack,KEY_MAG_COUNT+index,value);}
}
