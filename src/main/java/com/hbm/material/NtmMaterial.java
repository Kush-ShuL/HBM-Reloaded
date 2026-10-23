package com.hbm.material;

import com.hbm.hazard.HbmHazards;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class NtmMaterial {
    public enum SmeltingBehavior { NOT_SMELTABLE, VAPORIZES, BREAKS, SMELTABLE, ADDITIVE }
    public enum Trait { METAL, NONMETAL }
    private final String symbol;
    private final int id;
    private final List<String> names;
    private final Set<MaterialShape> autogen=new HashSet<>();
    private final Set<Trait> traits=new HashSet<>();
    private final SmeltingBehavior smeltingBehavior;
    private final int light,dark,molten;
    private NtmMaterial smeltsInto;
    private int convIn=1,convOut=1;
    NtmMaterial(String symbol,int id,List<String> names,SmeltingBehavior behavior,int light,int dark,int molten) {
        this.symbol=symbol; this.id=id; this.names=List.copyOf(names); this.smeltingBehavior=behavior; this.light=light; this.dark=dark; this.molten=molten; this.smeltsInto=this;
    }
    public String symbol() { return symbol; }
    public int id() { return id; }
    public List<String> names() { return names; }
    public Set<MaterialShape> autogen() { return Set.copyOf(autogen); }
    public Set<Trait> traits() { return Set.copyOf(traits); }
    public NtmMaterial autogen(MaterialShape shape) { if(shape!=null) autogen.add(shape); return this; }
    public NtmMaterial trait(Trait trait) { traits.add(trait); return this; }
    public SmeltingBehavior smeltingBehavior() { return smeltingBehavior; }
    public int solidColorLight() { return light; }
    public int solidColorDark() { return dark; }
    public int moltenColor() { return molten; }
    public NtmMaterial smeltsInto() { return smeltsInto; }
    public int convIn() { return convIn; }
    public int convOut() { return convOut; }
    public NtmMaterial setConversion(NtmMaterial target,int required,int produced) { smeltsInto=target; convIn=required; convOut=produced; return this; }
    public String descriptionId() { return "hbmmat."+names.getFirst().toLowerCase(Locale.US); }
    public Component displayName() { return Component.translatable(descriptionId()); }
    public ItemStack make(Item item) { return make(item,1); }
    public ItemStack make(Item item,int count) { ItemStack stack=new ItemStack(item,count); stack.set(HbmHazards.LEGACY_METADATA.get(),id); return stack; }
}
