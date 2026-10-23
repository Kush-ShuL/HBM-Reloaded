package com.hbm.client.animation.hmf;

/** Original shared HMF scrolling controls, including TomPronter's temporary overrides. */
public final class HmfController {
    public static double modoloMod = 100000D;
    public static double quotientMod = 5000D;
    private HmfController() { }
    public static void setMod(double modulo, double quotient) { modoloMod = modulo; quotientMod = quotient; }
    public static void resetMod() { modoloMod = 100000D; quotientMod = 5000D; }
}
