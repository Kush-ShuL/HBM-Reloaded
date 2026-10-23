package com.hbm.entity.mob;
/** X5846's actual two-state flying contract; no independent flying AI/backend. */
public interface IFlyingCreature {
    int STATE_WALKING=0,STATE_FLYING=1;
    int getFlyingState();
    void setFlyingState(int state);
}
