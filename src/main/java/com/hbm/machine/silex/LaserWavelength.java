package com.hbm.machine.silex;
import net.minecraft.ChatFormatting;

/** ItemFELCrystal.EnumWavelengths, source order is part of laser strength/consumption arithmetic. */
public enum LaserWavelength {
    NULL("la creatura","6 dollar",0x010101,0x010101,ChatFormatting.WHITE),
    IR("wavelengths.name.ir","wavelengths.waveRange.ir",0xBB1010,0xCC4040,ChatFormatting.RED),
    VISIBLE("wavelengths.name.visible","wavelengths.waveRange.visible",0,0,ChatFormatting.GREEN),
    UV("wavelengths.name.uv","wavelengths.waveRange.uv",0x0A1FC4,0x00EFFF,ChatFormatting.AQUA),
    GAMMA("wavelengths.name.gamma","wavelengths.waveRange.gamma",0x150560,0xEF00FF,ChatFormatting.LIGHT_PURPLE),
    DRX("wavelengths.name.drx","wavelengths.waveRange.drx",0xFF0000,0xFF0000,ChatFormatting.DARK_RED);
    public final String nameKey,rangeKey;
    public final int renderedBeamColor,guiColor;
    public final ChatFormatting textColor;
    LaserWavelength(String name,String range,int beamColor,int guiColor,ChatFormatting textColor){nameKey=name;rangeKey=range;renderedBeamColor=beamColor;this.guiColor=guiColor;this.textColor=textColor;}
}
