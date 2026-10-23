package com.hbm.machine.missileassembly;

import com.hbm.missile.custom.CustomMissileParts;
import net.minecraft.world.Container;
import static com.hbm.missile.custom.CustomMissileParts.PartType.*;

/** Exact original state checks; absent fins=-1 is allowed, invalid present fins=0 blocks construction. */
public final class MissileAssemblyRules {
    public static int fuselage(Container slots) { var d = CustomMissileParts.part(slots.getItem(2)); return d != null && d.type() == FUSELAGE ? 1 : 0; }
    public static int chip(Container slots) { var d = CustomMissileParts.part(slots.getItem(0)); return d != null && d.type() == CHIP ? 1 : 0; }
    public static int warhead(Container slots) {
        var w = CustomMissileParts.part(slots.getItem(1)); var f = CustomMissileParts.part(slots.getItem(2)); var t = CustomMissileParts.part(slots.getItem(4));
        return w != null && f != null && t != null && w.type() == WARHEAD && f.type() == FUSELAGE && t.type() == THRUSTER
                && w.bottom() == f.top() && w.number(2) <= t.number(2) ? 1 : 0;
    }
    public static int stability(Container slots) {
        if (slots.getItem(3).isEmpty()) return -1;
        var s = CustomMissileParts.part(slots.getItem(3)); var f = CustomMissileParts.part(slots.getItem(2));
        return s != null && f != null && s.top() == f.bottom() && s.type() == FINS ? 1 : 0;
    }
    public static int thruster(Container slots) {
        var t = CustomMissileParts.part(slots.getItem(4)); var f = CustomMissileParts.part(slots.getItem(2));
        return t != null && f != null && t.type() == THRUSTER && f.type() == FUSELAGE
                && t.top() == f.bottom() && t.enumName(0).equals(f.enumName(0)) ? 1 : 0;
    }
    public static boolean canBuild(Container slots) {
        return slots.getItem(5).isEmpty() && chip(slots) == 1 && warhead(slots) == 1
                && fuselage(slots) == 1 && thruster(slots) == 1 && stability(slots) != 0;
    }
    private MissileAssemblyRules() {}
}
