package com.hbm.explosion;

/** One section's original YZX voxel indices, ordered as nested three-dimensional cubes. */
final class MortonSectionOrder {
    private static final int[] SPREAD = new int[16];
    private static final short[] TO_MORTON = new short[4096], TO_LINEAR = new short[4096];
    static {
        for (int value = 0; value < 16; value++) {
            SPREAD[value] = (value & 1) | ((value & 2) << 2) | ((value & 4) << 4) | ((value & 8) << 6);
        }
        for (int linear = 0; linear < 4096; linear++) {
            int morton = SPREAD[linear & 15] | (SPREAD[(linear >>> 8) & 15] << 1)
                    | (SPREAD[(linear >>> 4) & 15] << 2);
            TO_MORTON[linear] = (short) morton;
            TO_LINEAR[morton] = (short) linear;
        }
    }

    static int encode(int linear, int linearFlip) { return TO_MORTON[linear ^ linearFlip]; }
    static int linear(int morton, int linearFlip) { return TO_LINEAR[morton] ^ linearFlip; }

    /** The nearest section corner becomes Morton zero, including negative sections. */
    static int linearFlip(int originX, int originY, int originZ, int sectionX, int sectionY, int sectionZ) {
        return (originX >= ((long) sectionX << 4) + 8 ? 0x00F : 0)
                | (originY >= ((long) sectionY << 4) + 8 ? 0xF00 : 0)
                | (originZ >= ((long) sectionZ << 4) + 8 ? 0x0F0 : 0);
    }

    private MortonSectionOrder() { }
}
