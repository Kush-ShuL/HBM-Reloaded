package com.hbm.machine.reactor.rbmk.control;
public enum RbmkControlKind {
    MANUAL("rbmk_control",false,false,false), MODERATED("rbmk_control_mod",false,true,false), AUTO("rbmk_control_auto",true,false,false),
    REASIM("rbmk_control_reasim",false,false,true), REASIM_AUTO("rbmk_control_reasim_auto",true,false,true);
    public final String id; public final boolean automatic, moderated, powered;
    RbmkControlKind(String id,boolean automatic,boolean moderated,boolean powered){this.id=id;this.automatic=automatic;this.moderated=moderated;this.powered=powered;}
}
