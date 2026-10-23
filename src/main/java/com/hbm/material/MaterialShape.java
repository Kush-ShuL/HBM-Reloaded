package com.hbm.material;

import java.util.List;

/** Source quantities are integer quanta; q(a,b) deliberately truncates after int multiplication. */
public final class MaterialShape {
    private final String symbol;
    private final int quantity;
    private final List<String> prefixes;
    private final boolean noAutogen;
    MaterialShape(String symbol,int quantity,List<String> prefixes,boolean noAutogen) { this.symbol=symbol; this.quantity=quantity; this.prefixes=List.copyOf(prefixes); this.noAutogen=noAutogen; }
    public String symbol() { return symbol; }
    public int quantity() { return quantity; }
    public List<String> prefixes() { return prefixes; }
    public boolean noAutogen() { return noAutogen; }
    public int q(int amount) { return quantity*amount; }
    public int q(int unitsUsed,int itemsProduced) { return quantity*unitsUsed/itemsProduced; }
    public String name() { return prefixes.isEmpty()?"unknown":prefixes.getFirst(); }
    public String make(NtmMaterial material) { return name()+material.names().getFirst(); }
}
