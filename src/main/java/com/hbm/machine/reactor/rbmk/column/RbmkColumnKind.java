package com.hbm.machine.reactor.rbmk.column;
import com.hbm.machine.reactor.rbmk.core.RbmkFluxColumn;
/** Genuine first passive source types; later functional modules extend the common column base. */
public enum RbmkColumnKind {
    BLANK("rbmk_blank", RbmkFluxColumn.Type.OTHER, "BLANK"),
    MODERATOR("rbmk_moderator", RbmkFluxColumn.Type.MODERATOR, "MODERATOR"),
    ABSORBER("rbmk_absorber", RbmkFluxColumn.Type.ABSORBER, "ABSORBER"),
    REFLECTOR("rbmk_reflector", RbmkFluxColumn.Type.REFLECTOR, "REFLECTOR");
    public final String id, consoleType;
    public final RbmkFluxColumn.Type neutronType;
    RbmkColumnKind(String id, RbmkFluxColumn.Type neutronType, String consoleType) { this.id = id; this.neutronType = neutronType; this.consoleType = consoleType; }
}
