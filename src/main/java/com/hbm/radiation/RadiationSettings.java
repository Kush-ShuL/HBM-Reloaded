package com.hbm.radiation;

import java.util.Objects;

/** Parameters of the default X5846 Simple backend. Values are RAD, not HE. */
public record RadiationSettings(boolean enabled, int updateIntervalTicks, float maximumRadiation,
                                Diffusion diffusion, Fog fog, WorldEffects worldEffects) {
    public static final RadiationSettings DEFAULT = new RadiationSettings(
            true, 20, 100_000F,
            new Diffusion(0.6F, 0.075F, 0.025F, 0.99F, 0.05F),
            new Fog(100F, 20), new WorldEffects(true, 5, 10, 10F));

    public RadiationSettings {
        if (updateIntervalTicks <= 0 || !Float.isFinite(maximumRadiation) || maximumRadiation <= 0) {
            throw new IllegalArgumentException("Invalid Simple radiation interval or maximum");
        }
        Objects.requireNonNull(diffusion);
        Objects.requireNonNull(fog);
        Objects.requireNonNull(worldEffects);
    }

    public record Diffusion(float centerWeight, float sideWeight, float cornerWeight,
                            float decayMultiplier, float decaySubtraction) {
        public Diffusion {
            for (float value : new float[]{centerWeight, sideWeight, cornerWeight, decayMultiplier, decaySubtraction}) {
                if (!Float.isFinite(value) || value < 0) throw new IllegalArgumentException("Invalid radiation diffusion parameter");
            }
        }

        public float weight(int offsetX, int offsetZ) {
            return switch (Math.abs(offsetX) + Math.abs(offsetZ)) {
                case 0 -> centerWeight;
                case 1 -> sideWeight;
                default -> cornerWeight;
            };
        }
    }

    public record Fog(float minimumRadiation, int chanceDenominator) {
        public Fog {
            if (!Float.isFinite(minimumRadiation) || minimumRadiation < 0 || chanceDenominator <= 0) {
                throw new IllegalArgumentException("Invalid radiation fog parameter");
            }
        }
    }

    /** These are Simple's actual hardcoded defaults, independent of other backends. */
    public record WorldEffects(boolean enabled, int sampledChunks, int attemptsPerChunk, float minimumRadiation) {
        public WorldEffects {
            if (sampledChunks < 0 || attemptsPerChunk < 0 || !Float.isFinite(minimumRadiation) || minimumRadiation < 0) {
                throw new IllegalArgumentException("Invalid radiation world effect parameter");
            }
        }
    }
}
