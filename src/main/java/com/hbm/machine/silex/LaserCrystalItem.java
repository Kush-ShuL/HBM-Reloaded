package com.hbm.machine.silex;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Actual source FEL crystal, stack1, explicit wavelength and original description/range tooltip. */
public final class LaserCrystalItem extends Item {
    public final LaserWavelength wavelength;
    private final String legacyName;
    public LaserCrystalItem(String legacyName,LaserWavelength wavelength){super(new Properties().stacksTo(1));this.legacyName=legacyName;this.wavelength=wavelength;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        lines.add(wavelength==LaserWavelength.DRX?Component.literal("THERADIANCEOFATHOUSANDSUNS").withStyle(ChatFormatting.OBFUSCATED):Component.translatable("item."+legacyName+".desc"));
        lines.add(Component.translatable(wavelength.nameKey).withStyle(wavelength.textColor).append(" - ").append(Component.translatable(wavelength.rangeKey).withStyle(wavelength.textColor)));
    }
}
