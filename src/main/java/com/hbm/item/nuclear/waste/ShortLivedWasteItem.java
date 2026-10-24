package com.hbm.item.nuclear.waste;

import com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Fixed source form/isotope; inherited dropped-entity lifetime remains unchanged. */
public final class ShortLivedWasteItem extends Item {
    private final SourceWasteIsotopes.Form form;
    private final SourceWasteIsotopes.ShortLived isotope;
    public ShortLivedWasteItem(SourceWasteIsotopes.Form form,SourceWasteIsotopes.ShortLived isotope){super(new Properties());this.form=Objects.requireNonNull(form);this.isotope=Objects.requireNonNull(isotope);if(form.longLived()!=false)throw new IllegalArgumentException("Wrong waste family");}
    public SourceWasteIsotopes.Form form(){return form;}
    public SourceWasteIsotopes.ShortLived isotope(){return isotope;}
    @Override public String getDescriptionId(){return "item.hbm."+form.symbol()+"_"+isotope.name().toLowerCase(java.util.Locale.ROOT);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(SourceWasteIsotopes.tooltip(isotope.translation));super.appendHoverText(stack,context,lines,flag);}
}
