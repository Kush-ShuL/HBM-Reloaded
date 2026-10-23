package com.hbm.item.autogen;

import com.hbm.hazard.HbmHazards;
import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialShape;
import com.hbm.material.NtmMaterial;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Original ItemAutogen: the subtype is the stable material ID, never an ordinal in a filtered list. */
public class AutogenItem extends Item {
    private final AutogenContent.Spec spec;
    private final MaterialShape shape;
    public AutogenItem(AutogenContent.Spec spec,MaterialShape shape) { super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0)); this.spec=spec; this.shape=shape; }
    public AutogenContent.Spec spec() { return spec; }
    public MaterialShape shape() { return shape; }
    public int materialId(ItemStack stack) { return HbmMaterials.metadata(stack); }
    public boolean remaps(NtmMaterial material) { return !spec.textureOverrides().containsKey(Integer.toString(material.id())) && material.solidColorLight()!=material.solidColorDark() && (shape==null || material.autogen().contains(shape)); }
    public boolean hasCustomIcon(NtmMaterial material) { return spec.textureOverrides().containsKey(Integer.toString(material.id())) || remaps(material); }
    public int color(ItemStack stack) { NtmMaterial material=HbmMaterials.byId(materialId(stack)); return 0xff000000 | (material==null || hasCustomIcon(material)?0xffffff:material.moltenColor()); }
    @Override public String getDescriptionId(ItemStack stack) { return spec.descriptionId().substring(0,spec.descriptionId().length()-5); }
    @Override public Component getName(ItemStack stack) { NtmMaterial material=HbmMaterials.byId(materialId(stack)); return material==null?Component.literal("UNDEFINED"):Component.translatable(spec.descriptionId(),material.displayName()); }
    public List<ItemStack> creativeStacks() { return HbmMaterials.ordered().stream().filter(material->material.autogen().contains(shape)).map(material->material.make(this)).toList(); }
}
