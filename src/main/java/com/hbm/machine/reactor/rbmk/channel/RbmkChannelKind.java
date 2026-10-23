package com.hbm.machine.reactor.rbmk.channel;
public enum RbmkChannelKind {
    NORMAL("rbmk_rod", "rbmk_element", false, false), MODERATED("rbmk_rod_mod", "rbmk_element_mod", true, false),
    REASIM("rbmk_rod_reasim", "rbmk_element_reasim", false, true), REASIM_MODERATED("rbmk_rod_reasim_mod", "rbmk_element_reasim_mod", true, true);
    public final String id, texture; public final boolean moderated, reasim;
    RbmkChannelKind(String id, String texture, boolean moderated, boolean reasim) { this.id = id; this.texture = texture; this.moderated = moderated; this.reasim = reasim; }
}
