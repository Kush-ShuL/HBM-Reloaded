package com.hbm.machine.anvil;

import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialShape;
import com.hbm.material.NtmMaterial;
import com.hbm.registry.HbmItemTags;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** One immutable source mold definition per registered physical item. */
public final class MoldItem extends Item {
    public record Mold(ResourceLocation itemId,int size,String name,String shape,int amount,int cost,Map<String,ItemStack> fixed,boolean blockOverride) {
        public ItemStack output(NtmMaterial material) {
            if(!fixed.isEmpty()) { ItemStack value=fixed.get(material.symbol());return value==null?ItemStack.EMPTY:value.copy(); }
            if(blockOverride&&(material.symbol().equals("MAT_STONE")||material.symbol().equals("MAT_OBSIDIAN"))) return new ItemStack(material.symbol().equals("MAT_STONE")?net.minecraft.world.level.block.Blocks.STONE:net.minecraft.world.level.block.Blocks.OBSIDIAN);
            MaterialShape actual=HbmMaterials.shape(shape);if(actual==null)return ItemStack.EMPTY;
            List<ItemStack> matches=HbmItemTags.items(HbmItemTags.material(actual.symbol(),material.names().getFirst())).toList();
            if(matches.isEmpty())return ItemStack.EMPTY;
            ItemStack preferred=matches.stream().filter(stack->{var id=BuiltInRegistries.ITEM.getKey(stack.getItem());return id.getNamespace().equals("hbm")&&!id.toString().contains("fragment");}).findFirst().orElse(matches.getFirst());return preferred.copyWithCount(amount);
        }
        public int requiredUnits() { return shape.isEmpty()?cost:HbmMaterials.shape(shape).q(amount); }
        public Component title() {return Component.translatable("shape."+(shape.isEmpty()?name:HbmMaterials.shape(shape).name())).append(" x"+amount);}
    }
    private final MoldContent.Definition definition;
    private Mold resolved;
    public MoldItem(MoldContent.Definition definition){super(new Properties());this.definition=Objects.requireNonNull(definition);}
    /** Source comparison token only; it is never read from a saved stack or used to mutate this item. */
    public int sourceOrdinal(){return definition.sourceOrdinal();}
    public synchronized Mold mold(){if(resolved==null)resolved=definition.resolve();return resolved;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.mold.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){var mold=mold();tooltip.add(mold.title().copy().withStyle(ChatFormatting.YELLOW));tooltip.add(Component.translatable(mold.size()==0?"tile.foundry_mold.name":"tile.foundry_basin.name").withStyle(mold.size()==0?ChatFormatting.GOLD:ChatFormatting.RED));}
}
