package com.hbm.item.dfc;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source catalyst statistics are carried even where the original DFC only uses their colour. */
public final class DfcCatalystItem extends Item {
    public final DfcMaterials.Entry data;
    public DfcCatalystItem(DfcMaterials.Entry data){super(new Properties().stacksTo(1));this.data=data;}
    public int getColor(){return data.color();}
    public static long getPowerAbs(ItemStack stack){return stack!=null&&stack.getItem()instanceof DfcCatalystItem item?item.data.power():0;}
    public static float getPowerMod(ItemStack stack){return stack!=null&&stack.getItem()instanceof DfcCatalystItem item?item.data.powerMod():0;}
    public static float getHeatMod(ItemStack stack){return stack!=null&&stack.getItem()instanceof DfcCatalystItem item?item.data.heatMod():0;}
    public static float getFuelMod(ItemStack stack){return stack!=null&&stack.getItem()instanceof DfcCatalystItem item?item.data.fuelMod():0;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("item.hbm.dfc.catalyst.spice"));lines.add(Component.translatable("item.hbm.dfc.catalyst.colors"));}
}
