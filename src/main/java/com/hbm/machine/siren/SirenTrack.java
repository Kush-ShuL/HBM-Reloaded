package com.hbm.machine.siren;

import java.util.Locale;
import net.minecraft.network.chat.Component;

/** Exact X5846 ItemCassette enum order, original spelling, ranges and RGB palette. */
public enum SirenTrack {
    NULL(" ",null,SoundType.SOUND,0,0),
    HATCH("Hatch Siren","alarm.hatch",SoundType.LOOP,3358839,250),
    ATUOPILOT("Autopilot Disconnected","alarm.autopilot",SoundType.LOOP,11908533,50),
    AMS_SIREN("AMS Siren","alarm.amsSiren",SoundType.LOOP,15055698,50),
    BLAST_DOOR("Blast Door Alarm","alarm.blastDoorAlarm",SoundType.LOOP,11665408,50),
    APC_LOOP("APC Siren","alarm.apcLoop",SoundType.LOOP,3565216,50),
    KLAXON("Klaxon","alarm.klaxon",SoundType.LOOP,8421504,50),
    KLAXON_A("Vault Door Alarm","alarm.foKlaxonA",SoundType.LOOP,0x8c810b,50),
    KLAXON_B("Security Alert","alarm.foKlaxonB",SoundType.LOOP,0x76818e,50),
    SIREN("Standard Siren","alarm.regularSiren",SoundType.LOOP,6684672,100),
    CLASSIC("Classic Siren","alarm.classic",SoundType.LOOP,0xc0cfe8,100),
    BANK_ALARM("Bank Alarm","alarm.bankAlarm",SoundType.LOOP,3572962,100),
    BEEP_SIREN("Beep Siren","alarm.beepSiren",SoundType.LOOP,13882323,100),
    CONTAINER_ALARM("Container Alarm","alarm.containerAlarm",SoundType.LOOP,14727839,100),
    SWEEP_SIREN("Sweep Siren","alarm.sweepSiren",SoundType.LOOP,15592026,500),
    STRIDER_SIREN("Missile Silo Siren","alarm.striderSiren",SoundType.LOOP,11250586,500),
    AIR_RAID("Air Raid Siren","alarm.airRaid",SoundType.LOOP,0xdf3795,500),
    NOSTROMO_SIREN("Nostromo Self Destruct","alarm.nostromoSiren",SoundType.LOOP,0x5dd800,100),
    EAS_ALARM("EAS Alarm Screech","alarm.easAlarm",SoundType.LOOP,0xb3a8c1,50),
    APC_PASS("APC Pass","alarm.apcPass",SoundType.PASS,3422163,50),
    RAZORTRAIN("Razortrain Horn","alarm.razortrainHorn",SoundType.SOUND,7819501,250);

    public enum SoundType { LOOP,PASS,SOUND }
    public final String title,sourceSound;
    public final SoundType type;
    public final int color,volume;
    SirenTrack(String title,String sourceSound,SoundType type,int color,int volume){this.title=title;this.sourceSound=sourceSound;this.type=type;this.color=color;this.volume=volume;}
    // Source checks the upper bound only. Do not wrap a malformed value into a different audible cassette.
    public static SirenTrack getEnum(int metadata){return metadata<values().length?values()[metadata]:NULL;}
    public Component title(){return Component.translatableWithFallback("siren.hbm.track."+name().toLowerCase(Locale.ROOT),title);}
    public Component typeName(){return Component.translatableWithFallback("siren.hbm.type."+type.name().toLowerCase(Locale.ROOT),type.name());}
}
