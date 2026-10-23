package com.hbm.machine.reactor.zirnox;
/** Source foundation policy now shares the original single GeneralConfig with other machines. */
public final class ZirnoxConfig {
    public static boolean gravity(){return com.hbm.config.GeneralModes.machineGravity();}
    private ZirnoxConfig(){}
}
