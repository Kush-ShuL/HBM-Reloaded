package com.hbm.armor.fueled;
import com.hbm.armor.t51.SourceFullSetArmorItem;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.fluid.*;
import com.hbm.item.battery.BatteryItem;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
/** Exact source ArmorFSBFueled native-fluid storage/filling/damage/drain, with the unique shared FSB lifecycle. */
public class SourceFueledFullSetArmorItem extends SourceFullSetArmorItem implements FillableItem {
    protected final HbmFluidType fuelType;
    public final int maxFuel,fillRate,consumption,drain;
    protected SourceFueledFullSetArmorItem(Holder<ArmorMaterial> material,Type slot,int durability,Properties properties,HbmFluidType fuelType,int maxFuel,int fillRate,int consumption,int drain){super(material,slot,durability,properties);this.fuelType=fuelType;this.maxFuel=maxFuel;this.fillRate=fillRate;this.consumption=consumption;this.drain=drain;}
    @Override public int getFill(ItemStack stack){if(!stack.has(DataComponents.CUSTOM_DATA)){setFill(stack,maxFuel);return maxFuel;}return stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("fuel");}
    public void setFill(ItemStack stack,int fill){var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();data.putInt("fuel",fill);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
    public int getMaxFill(ItemStack stack){return maxFuel;}
    public int getLoadSpeed(ItemStack stack){return fillRate;}
    public int getUnloadSpeed(ItemStack stack){return 0;}
    @Override public void setDamage(ItemStack stack,int damage){setFill(stack,Math.max(getFill(stack)-damage*consumption,0));}
    @Override public boolean legacyUsesPostSetDamage(){return true;}
    @Override public boolean isArmorEnabled(ItemStack stack){return getFill(stack)>0;}
    @Override public void inventoryTick(ItemStack stack,Level world,Entity entity,int slot,boolean selected){if(entity instanceof Player player&&player.getItemBySlot(getEquipmentSlot())==stack&&drain>0&&T51Runtime.fullSet(player,true)&&!player.getAbilities().instabuild&&world.getGameTime()%10==0)setFill(stack,Math.max(getFill(stack)-drain,0));}
    @Override public boolean acceptsFluid(HbmFluidType type,ItemStack stack){return type==fuelType;}
    @Override public int tryFill(HbmFluidType type,int amount,ItemStack stack){if(!acceptsFluid(type,stack))return amount;int fill=Math.min(Math.min(amount,fillRate),maxFuel-getFill(stack));setFill(stack,getFill(stack)+fill);return amount-fill;}
    @Override public boolean providesFluid(HbmFluidType type,ItemStack stack){return false;}
    @Override public int tryEmpty(HbmFluidType type,int amount,ItemStack stack){return 0;}
    @Override public HbmFluidType getFirstFluidType(ItemStack stack){return null;}
    @Override public boolean isBarVisible(ItemStack stack){return getFill(stack)<getMaxFill(stack);}
    @Override public int getBarWidth(ItemStack stack){return Math.round(13F*(float)((double)getFill(stack)/getMaxFill(stack)));}
    @Override public int getBarColor(ItemStack stack){return net.minecraft.util.Mth.hsvToRgb(Math.max(0,(float)((double)getFill(stack)/getMaxFill(stack)))/3F,1,1);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(fuelType.displayName().copy().append(": "+BatteryItem.shortNumber(getFill(stack))+" / "+BatteryItem.shortNumber(getMaxFill(stack))));super.appendHoverText(stack,context,lines,flag);}
}
