package com.hbm.item.bobmazondeps;

/** Exact original enum order/labels/inscriptions; NONE stays a valid stored type. */
public final class TrinketTypes {
 public enum Snowglobe {
  NONE("NONE",null),RIVETCITY("Rivet City","Welcome to Rivet City. Please wait while the bridge extends."),TENPENNYTOWER("Tenpenny Tower","Tenpenny Tower is the brainchild of Allistair Tenpenny, a British refugee who came to the Capital Wasteland seeking his fortune."),LUCKY38("Lucky 38","My guess? Leads to a big cashout at some casino - and if the \"38\" on it is any indication... well... Lucky 38 it is."),SIERRAMADRE("Sierra Madre","It's the moment you've been waiting for, the reason we're all here - the Gala Event, the Grand Opening of the Sierra Madre Casino."),PRYDWEN("The Prydwen","People of the Commonwealth. Do not interfere. Our intentions are peaceful. We are the Brotherhood of Steel.");
  public final String label,inscription;Snowglobe(String label,String inscription){this.label=label;this.inscription=inscription;}
  public static Snowglobe metadata(int meta){return values()[Math.abs(meta)%values().length];}
  public String key(String field){return "gui.hbm.snowglobe."+name().toLowerCase(java.util.Locale.ROOT)+"."+field;}
 }
 public enum Plushie {
  NONE("NONE",null),YOMI("Yomi","Hi! Can I be your rabbit friend?"),NUMBERNINE("Number Nine","None of y'all deserve coal."),HUNDUN("Hundun","混沌"),DERG("Dragon","Squeeze him.");
  public final String label,inscription;Plushie(String label,String inscription){this.label=label;this.inscription=inscription;}
  public static Plushie metadata(int meta){return values()[Math.abs(meta)%values().length];}
  public String key(String field){return "tooltip.hbm.plushie."+name().toLowerCase(java.util.Locale.ROOT)+"."+field;}
 }
 private TrinketTypes(){}
}
