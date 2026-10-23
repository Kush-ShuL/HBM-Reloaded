package com.hbm.world.structure.generated.compat;
/** Source predicates over actual modern states, never material substitutions. */
public enum Material {
    air(false,false),leaves(true,false),rock(true,false),solid(true,false),plants(false,false),water(false,true),other(false,false);
    private final boolean solidMaterial,liquid;Material(boolean solid,boolean liquid){this.solidMaterial=solid;this.liquid=liquid;}public boolean isSolid(){return solidMaterial;}public boolean isLiquid(){return liquid;}public boolean isReplaceable(){return this==air||this==other;}
}
