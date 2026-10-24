package com.hbm.network.dispatch;

/** A connection's source work becomes obsolete when its protocol or world context changes. */
public interface DispatchConnection {
    long hbm$dispatchEpoch();
    void hbm$invalidateDispatch();
}
