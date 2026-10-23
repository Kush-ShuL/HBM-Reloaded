package com.hbm.machine.pa.physics;
/** Original TileEntityPASource.PAState order and display colors are protocol/NBT stable. */
public enum PaState{
    IDLE(0x8080ff),RUNNING(0xffff00),SUCCESS(0x00ff00),PAUSE_UNLOADED(0x808080),CRASH_DEFOCUS(0xff0000),CRASH_DERAIL(0xff0000),CRASH_CANNOT_ENTER(0xff0000),CRASH_NOCOOL(0xff0000),CRASH_NOPOWER(0xff0000),CRASH_NOCOIL(0xff0000),CRASH_OVERSPEED(0xff0000),CRASH_UNDERSPEED(0xff0000),CRASH_NORECIPE(0xff0000);
    public final int color;PaState(int color){this.color=color;}
}
