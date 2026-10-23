package com.hbm.item.meteorgavel;
/** One original ItemRenderGavel renderer for wood/lead/diamond/Mese; no second renderer or item ID. */
public interface SourceGavelAppearance {
    String sourceGavelTexture();
    default boolean sourceLargeGavel(){return false;}
}
