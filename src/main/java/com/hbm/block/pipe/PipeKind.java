package com.hbm.block.pipe;

/** Original concrete block classes; exhaust carries three independent native networks. */
public enum PipeKind {
    STANDARD, VALVE, BOX, EXHAUST, PAINTABLE, PAINTABLE_EXHAUST, GAUGE, COUNTER, ANCHOR, EXHAUST_ANCHOR;
    public boolean exhaust() { return this == EXHAUST || this == PAINTABLE_EXHAUST || this == EXHAUST_ANCHOR; }
    public boolean anchor() { return this == ANCHOR || this == EXHAUST_ANCHOR; }
    public boolean box() { return this == BOX || this == EXHAUST; }
    public boolean paintable() { return this == PAINTABLE || this == PAINTABLE_EXHAUST; }
    public boolean instrument() { return this == GAUGE || this == COUNTER; }
}
