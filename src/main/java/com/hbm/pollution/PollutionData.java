package com.hbm.pollution;

import java.util.HashMap;
import java.util.Map;

/** The original 64-by-64 block cell simulation, independent of world loading. */
public final class PollutionData {
    private final float[] channels = new float[PollutionType.values().length];

    public record Cell(int x, int z) {
        public static Cell at(int blockX, int blockZ) {
            return new Cell(blockX >> 6, blockZ >> 6);
        }
    }

    public float get(PollutionType type) {
        return channels[type.ordinal()];
    }

    public void set(PollutionType type, float value) {
        channels[type.ordinal()] = value;
    }

    private boolean positive() {
        for (float value : channels) if (value > 0) return true;
        return false;
    }

    /** One X5846 simulation step, called every 60 server ticks. */
    public static Map<Cell, PollutionData> advance(Map<Cell, PollutionData> previous) {
        Map<Cell, PollutionData> next = new HashMap<>();
        int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        previous.forEach((cell, data) -> {
            float[] local = data.channels.clone();
            float[] neighbors = new float[local.length];
            int soot = PollutionType.SOOT.ordinal(), poison = PollutionType.POISON.ordinal(), metal = PollutionType.HEAVYMETAL.ordinal();
            if (local[soot] > 10) {
                neighbors[soot] = local[soot] * 0.05F;
                local[soot] *= 0.8F;
            }
            local[soot] *= 0.99F;
            local[metal] *= 0.9995F;
            if (local[poison] > 10) {
                neighbors[poison] = local[poison] * 0.025F;
                local[poison] *= 0.9F;
            } else {
                local[poison] *= 0.995F;
            }
            add(next, cell, local);
            for (int[] offset : offsets) add(next, new Cell(cell.x() + offset[0], cell.z() + offset[1]), neighbors);
        });
        return next;
    }

    private static void add(Map<Cell, PollutionData> target, Cell cell, float[] channels) {
        PollutionData result = target.get(cell);
        if (result == null) result = new PollutionData();
        for (int index = 0; index < channels.length; index++) result.channels[index] += channels[index];
        if (result.positive()) target.put(cell, result);
    }
}
