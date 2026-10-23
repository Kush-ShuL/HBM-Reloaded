package com.hbm.exposure;

public record ExposureSettings(boolean contamination, boolean asbestos, boolean coalDust, float netherRadiation) {
    public static final ExposureSettings DEFAULT = new ExposureSettings(true,true,true,.1F);
    public ExposureSettings {
        if (!Float.isFinite(netherRadiation) || netherRadiation<0) throw new IllegalArgumentException("Invalid ambient radiation");
    }
}
