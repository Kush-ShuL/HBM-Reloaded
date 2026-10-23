package com.hbm.machine.anvil;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
/** Source ItemHot's shared static heat ends at100 after the registered chainsteel item; dusted's hidden static method is separate. */
public final class HotSmithingItem extends Item {
    private final String symbol; private final boolean dusted;
    public HotSmithingItem(String symbol,boolean dusted) { super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0)); this.symbol=symbol; this.dusted=dusted; }
    public String symbol() { return symbol; }
    public static int maxHeat(ItemStack stack) { return 100; }
    public static int dustedMaxHeat(ItemStack stack) { return maxHeat(stack)-stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())*10; }
    public static double heat(ItemStack stack) { return stack.getItem() instanceof HotSmithingItem?(double)stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("heat")/100D:0; }
    public static ItemStack heatUp(ItemStack stack,double fraction) { if(stack.getItem() instanceof HotSmithingItem) CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putInt("heat",(int)(fraction*100)));return stack; }
    @Override public void inventoryTick(ItemStack stack,Level level,Entity owner,int slot,boolean selected) { if(level.isClientSide||!(stack.has(DataComponents.CUSTOM_DATA)||stack.has(DataComponents.CUSTOM_NAME)||stack.has(DataComponents.LORE))) return;var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();int heat=tag.getInt("heat");if(heat>0) {tag.putInt("heat",heat-1);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));} else { stack.remove(DataComponents.CUSTOM_DATA); stack.remove(DataComponents.CUSTOM_NAME); stack.remove(DataComponents.LORE); stack.remove(DataComponents.ENCHANTMENTS); stack.remove(DataComponents.REPAIR_COST); } }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item."+symbol+".name"); }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag) { if(dusted) tooltip.add(Component.translatable("item.hot_dusted.forged",stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()))); }
}
