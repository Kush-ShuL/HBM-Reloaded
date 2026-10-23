package com.hbm.machine.fensu;

import java.math.BigInteger;

/** Plain arbitrary-precision formatting; HUD/GUI never truncate storage into the source provision-speed view. */
public final class FensuNumbers {
    public static String format(BigInteger value){return String.format(java.util.Locale.US,"%,d",value);}
    private FensuNumbers(){}
}
