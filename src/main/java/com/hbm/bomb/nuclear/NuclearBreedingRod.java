package com.hbm.bomb.nuclear;
import com.hbm.item.FlatSourceItem;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
/** ItemBreedingRod's material is a physical identity; breeding flux/progress remains machine state. */
public final class NuclearBreedingRod extends Item implements FlatSourceItem {
 public static final List<String> VARIANTS=List.of("lithium","tritium","co","co60","th232","thf","u235","np237","u238","pu238","pu239","rgp","waste","lead","uranium","ra226","ac227");
 private final String family;private final int sourceOrdinal;
 public NuclearBreedingRod(String family,int sourceOrdinal){super(new Properties().stacksTo(64));if(!List.of("rod","rod_dual","rod_quad").contains(family)||sourceOrdinal<0||sourceOrdinal>=VARIANTS.size())throw new IllegalArgumentException("Unknown breeding rod material "+family+":"+sourceOrdinal);this.family=family;this.sourceOrdinal=sourceOrdinal;}
 @Override public String getDescriptionId(){return "item.hbm."+family+"_"+VARIANTS.get(sourceOrdinal);}
 @Override public boolean hasCraftingRemainingItem(ItemStack stack){return true;}
 @Override public ItemStack getCraftingRemainingItem(ItemStack stack){return new ItemStack(HbmContent.item(family+"_empty"));}
}
