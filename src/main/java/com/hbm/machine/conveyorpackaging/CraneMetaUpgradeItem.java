package com.hbm.machine.conveyorpackaging;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** 两族各三个固定等级；等级由注册物品决定，不是耐久或可切换模式。 */
public final class CraneMetaUpgradeItem extends Item {
    public enum Kind { STACK("upgrade_stack"), EJECTOR("upgrade_ejector");
        private final String id;Kind(String id){this.id=id;}public String id(){return id;}
    }
    private final Kind kind;
    private final int level;
    public CraneMetaUpgradeItem(Kind kind,int level){super(new Properties().stacksTo(1));if(level<0||level>=3)throw new IllegalArgumentException("Unknown crane upgrade level "+level);this.kind=java.util.Objects.requireNonNull(kind);this.level=level;}
    public static boolean isStack(ItemStack stack){return stack.getItem() instanceof CraneMetaUpgradeItem item&&item.kind==Kind.STACK;}
    public static boolean isEjector(ItemStack stack){return stack.getItem() instanceof CraneMetaUpgradeItem item&&item.kind==Kind.EJECTOR;}
    public static int level(ItemStack stack){if(!(stack.getItem() instanceof CraneMetaUpgradeItem item))throw new IllegalArgumentException("Not a crane upgrade");return item.level;}
    @Override public String getDescriptionId(){return "item.hbm."+kind.id()+"_"+(level+1);}
}
