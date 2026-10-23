package com.hbm.explosion;

/** The original GSP points in progressively decorrelated latitude-block order.
 * Immutable source-recurrence anchors recover every selected longitude exactly. */
final class OriginalSpiralOrder {
    private static final int BLOCK_SIZE=256,ANCHOR_TARGET=1<<19;
    private final int points,blocks,bits,anchorStride;
    private final double[] longitudes;
    private int preparedPoint=1,blockOrdinal,point,roundOffset,delivered;
    private final int blockSlots;
    private double preparedLongitude,longitude;
    private boolean prepared;

    OriginalSpiralOrder(int points) {
        if(points<1)throw new IllegalArgumentException("A spiral needs at least one point");
        this.points=points;blocks=(points-1)/BLOCK_SIZE+1;
        bits=32-Integer.numberOfLeadingZeros(blocks-1);
        int stride=16;
        while((points-1L)/stride+1>ANCHOR_TARGET&&stride<256)stride<<=1;
        anchorStride=stride;
        longitudes=new double[(points-1)/anchorStride+1];blockSlots=1<<bits;
    }

    boolean prepare(long deadline) {
        if(prepared)return true;
        int operations=0;
        while(preparedPoint<points) {
            if((operations++&255)==0&&System.nanoTime()>=deadline)return false;
            preparedPoint++;
            preparedLongitude=nextLongitude(preparedPoint,preparedLongitude);
            if((preparedPoint-1)%anchorStride==0)longitudes[(preparedPoint-1)/anchorStride]=preparedLongitude;
        }
        prepared=true;nextPoint();return true;
    }

    int sourcePoint(){return point;}
    double latitude(){return point==1?Math.PI:Math.acos(-1.0+2.0*(point-1.0)/(points-1.0));}
    double longitude(){return longitude;}

    void advance() {
        if(++delivered<points)nextPoint();
    }

    private void nextPoint() {
        // Each block's XOR is a permutation of all 256 offsets. Bit-reversed
        // rounds spread successive samples through that block instead of adding
        // near-identical neighboring directions to an already visited strand.
        while(true) {
            if(blockOrdinal>=blockSlots){blockOrdinal=0;roundOffset++;}
            int block=bits==0?blockOrdinal++:Integer.reverse(blockOrdinal++)>>>(32-bits);
            if(block>=blocks)continue;
            int local=(Integer.reverse(roundOffset)>>>24)^(mix32(block)&255);
            long selected=(long)block*BLOCK_SIZE+local+1;
            if(selected>points)continue;
            point=(int)selected;
            int anchor=(point-1)/anchorStride;
            longitude=longitudes[anchor];
            for(int k=anchor*anchorStride+1;k<point;)longitude=nextLongitude(++k,longitude);
            return;
        }
    }

    private static int mix32(int value) {
        value^=value>>>16;value*=0x7feb352d;
        value^=value>>>15;value*=0x846ca68b;
        return value^(value>>>16);
    }

    private double nextLongitude(int k,double previous) {
        double hk=-1.0+2.0*(k-1.0)/(points-1.0);
        // Preserve the existing canonical north-pole endpoint. Its source
        // recurrence is singular, and longitude does not change that direction.
        return hk>=1.0?0.0:(previous+3.6/Math.sqrt(points)/Math.sqrt(1.0-hk*hk))%(Math.PI*2);
    }
}
