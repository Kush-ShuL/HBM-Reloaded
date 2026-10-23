package com.hbm.item.dfc;
import com.hbm.LegacyStartup;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Actual core base statistics and all original polaroid11 rarity/foil/lore branches. */
public final class DfcCoreItem extends Item {
    public final DfcMaterials.Entry data;
    public DfcCoreItem(DfcMaterials.Entry data){super(new Properties().stacksTo(1).rarity(data.id().equals("ams_core_thingy")?Rarity.EPIC:Rarity.UNCOMMON));this.data=data;}
    public static long getPowerBase(ItemStack stack){return stack!=null&&stack.getItem()instanceof DfcCoreItem item?item.data.power():0;}
    public static int getHeatBase(ItemStack stack){return stack!=null&&stack.getItem()instanceof DfcCoreItem item?item.data.heat():0;}
    public static int getFuelBase(ItemStack stack){return stack!=null&&stack.getItem()instanceof DfcCoreItem item?item.data.fuel():0;}
    @Override public boolean isFoil(ItemStack stack){return data.id().equals("ams_core_thingy")&&LegacyStartup.polaroidId==11;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var keys=LegacyStartup.polaroidId==11&&!data.specialLore().isEmpty()?data.specialLore():data.lore();for(var key:keys)lines.add(Component.translatable(key));}
}
