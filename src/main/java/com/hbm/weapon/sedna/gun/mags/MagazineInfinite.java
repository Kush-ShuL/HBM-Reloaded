package com.hbm.weapon.sedna.gun.mags;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.registry.HbmContent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Original infinite magazine, including the hidden nothing icon and infinity HUD. */
public final class MagazineInfinite implements IMagazine<BulletConfig> {
    public BulletConfig type;
    public MagazineInfinite(BulletConfig type){this.type=type;}
    @Override public BulletConfig getType(ItemStack stack,Container inventory){return type;}
    @Override public void setType(ItemStack stack,BulletConfig value){}
    @Override public int getCapacity(ItemStack stack){return 9999;}
    @Override public int getAmount(ItemStack stack,Container inventory){return 9999;}
    @Override public void setAmount(ItemStack stack,int value){}
    @Override public void useUpAmmo(ItemStack stack,Container inventory,int amount){}
    @Override public boolean canReload(ItemStack stack,Container inventory){return false;}
    @Override public void initNewType(ItemStack stack,Container inventory){}
    @Override public void reloadAction(ItemStack stack,Container inventory){}
    @Override public ItemStack getIconForHUD(ItemStack stack,Player player){return new ItemStack(HbmContent.item("nothing"));}
    @Override public String reportAmmoStateForHUD(ItemStack stack,Player player){return "∞";}
    @Override public SpentCasing getCasing(ItemStack stack,Container inventory){return type.casing;}
    @Override public void setAmountBeforeReload(ItemStack stack,int value){}
    @Override public int getAmountBeforeReload(ItemStack stack){return 9999;}
    @Override public void setAmountAfterReload(ItemStack stack,int value){}
    @Override public int getAmountAfterReload(ItemStack stack){return 9999;}
}
