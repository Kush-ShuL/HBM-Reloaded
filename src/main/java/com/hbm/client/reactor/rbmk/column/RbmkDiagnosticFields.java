package com.hbm.client.reactor.rbmk.column;

import com.hbm.api.machine.MachineView.ProgressView;
import com.hbm.machine.reactor.rbmk.boiler.RbmkBoilerEntity;
import com.hbm.machine.reactor.rbmk.channel.RbmkFuelChannelEntity;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnEntity;
import com.hbm.machine.reactor.rbmk.control.RbmkControlEntity;
import com.hbm.machine.reactor.rbmk.irradiation.RbmkOutgasserEntity;
import com.hbm.machine.reactor.rbmk.thermal.RbmkCoolerEntity;
import com.hbm.machine.reactor.rbmk.thermal.RbmkHeaterEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.network.chat.Component;

/** Read-only fields from the source RBMK getDiagData contract, after its existing progress bars. */
public final class RbmkDiagnosticFields {
    private static final String LABEL_PREFIX = "tile.rbmk.dodd.";

    public static List<Component> lines(RbmkColumnEntity column, List<ProgressView> bars) {
        var keys = new TreeSet<String>();
        keys.addAll(List.of("heat", "reasimWater", "reasimSteam"));
        if (column instanceof RbmkControlEntity control) {
            keys.addAll(List.of("level", "targetLevel"));
            keys.addAll(control.automatic()
                    ? List.of("levelLower", "levelUpper", "heatLower", "heatUpper", "function")
                    : List.of("startingLevel", "mult", "color"));
        } else if (column instanceof RbmkFuelChannelEntity) {
            keys.addAll(List.of("fluxSlow", "fluxFast", "hasRod", "f_yield", "f_xenon", "f_heat"));
        } else if (column instanceof RbmkBoilerEntity || column instanceof RbmkHeaterEntity) {
            tank(keys, "feed");
            tank(keys, "steam");
        } else if (column instanceof RbmkCoolerEntity) {
            tank(keys, "t0");
            tank(keys, "t1");
        } else if (column instanceof RbmkOutgasserEntity) {
            keys.add("progress");
            tank(keys, "gas");
        }
        for (var bar : bars) {
            if (bar.name().startsWith(LABEL_PREFIX)) keys.remove(bar.name().substring(LABEL_PREFIX.length()));
            if (column instanceof RbmkOutgasserEntity && bar.name().equals("jade.hbm.rbmk.irradiation.flux"))
                keys.remove("progress");
        }
        var data = column.diagnosticData();
        var lines = new ArrayList<Component>();
        for (var key : keys) {
            var value = data.get(key);
            if (value == null) continue;
            String text = value.toString();
            if (!text.isEmpty()) {
                char suffix = text.charAt(text.length() - 1);
                if (suffix == 'd' || suffix == 's' || suffix == 'b') text = text.substring(0, text.length() - 1);
            }
            lines.add(Component.translatable(LABEL_PREFIX + key).append(": " + text));
        }
        return List.copyOf(lines);
    }

    private static void tank(TreeSet<String> keys, String name) {
        keys.addAll(List.of(name, name + "_max", name + "_type", name + "_p"));
    }

    private RbmkDiagnosticFields() {}
}
