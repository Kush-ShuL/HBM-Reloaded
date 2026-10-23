package com.hbm.material;
/** Exactly one source material with a mutable quantum count, not an item stack count. */
public final class MaterialStack {
    public final NtmMaterial material;
    public int amount;
    public MaterialStack(NtmMaterial material,int amount) { this.material=material; this.amount=amount; }
    public MaterialStack copy() { return new MaterialStack(material,amount); }
}
