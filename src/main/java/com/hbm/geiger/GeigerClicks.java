package com.hbm.geiger;

import java.util.ArrayList;
import java.util.function.IntUnaryOperator;

/** Original overlapping sample windows, including silence entries and exact strict boundaries. */
public final class GeigerClicks {
    private GeigerClicks() {}
    public static int[] samples(float radiation) {
        var values=new ArrayList<Integer>(3);
        if(radiation<1) values.add(0);
        if(radiation<5) values.add(0);
        if(radiation<10) values.add(1);
        if(radiation>5&&radiation<15) values.add(2);
        if(radiation>10&&radiation<20) values.add(3);
        if(radiation>15&&radiation<25) values.add(4);
        if(radiation>20&&radiation<30) values.add(5);
        if(radiation>25) values.add(6);
        return values.stream().mapToInt(Integer::intValue).toArray();
    }
    public static int block(float radiation,IntUnaryOperator random) {
        if(radiation>0) { int[] samples=samples(radiation); return samples[random.applyAsInt(samples.length)]; }
        // The old block also consumes nextInt(1); the handheld item does not.
        return random.applyAsInt(50)==0?1+random.applyAsInt(1):0;
    }
    public static int handheld(float radiation,IntUnaryOperator random) {
        if(radiation>1E-5D) { int[] samples=samples(radiation); return samples[random.applyAsInt(samples.length)]; }
        return random.applyAsInt(50)==0?1:0;
    }
    public static int comparator(float radiation) { return Math.min((int)Math.ceil(radiation/5F),15); }
}
