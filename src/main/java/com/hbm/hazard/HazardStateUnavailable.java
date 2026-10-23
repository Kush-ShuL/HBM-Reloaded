package com.hbm.hazard;

/** An unsupported state is retained as unresolved rather than fabricated as zero risk. */
public final class HazardStateUnavailable extends RuntimeException {
    public HazardStateUnavailable(String reason){super(reason);}
}
