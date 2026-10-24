package com.hbm.item.autogen;

import com.hbm.material.MaterialShape;
import com.hbm.material.NtmMaterial;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Original rendering and names, with the material fixed by the physical item identity. */
public class AutogenItem extends Item implements com.hbm.item.FlatSourceItem {
    private final AutogenContent.Spec spec;
    private final MaterialShape shape;
    private final NtmMaterial material;
    public AutogenItem(AutogenContent.Spec spec,MaterialShape shape,NtmMaterial material) { super(new Properties().stacksTo(64)); this.spec=spec; this.shape=shape; this.material=material; }
    public AutogenContent.Spec spec() { return spec; }
    public MaterialShape shape() { return shape; }
    public NtmMaterial material() { return material; }
    public int materialId(ItemStack stack) { return material.id(); }
    public boolean remaps(NtmMaterial material) { return !spec.textureOverrides().containsKey(Integer.toString(material.id())) && material.solidColorLight()!=material.solidColorDark() && (shape==null || material.autogen().contains(shape)); }
    public boolean hasCustomIcon(NtmMaterial material) { return spec.textureOverrides().containsKey(Integer.toString(material.id())) || remaps(material); }
    public int color(ItemStack stack) { NtmMaterial material=this.material; return 0xff000000 | (material==null || hasCustomIcon(material)?0xffffff:material.moltenColor()); }
    @Override public String getDescriptionId(ItemStack stack) { return spec.descriptionId().substring(0,spec.descriptionId().length()-5); }
    @Override public Component getName(ItemStack stack) { NtmMaterial material=this.material; return material==null?Component.literal("UNDEFINED"):Component.translatable(spec.descriptionId(),material.displayName()); }
    public List<ItemStack> creativeStacks() { return material.autogen().contains(shape)?List.of(new ItemStack(this)):List.of(); }
}
