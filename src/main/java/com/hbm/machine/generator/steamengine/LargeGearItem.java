package com.hbm.machine.generator.steamengine;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Complete original ItemGear: exact two creative variants and original exceptional metadata naming. */
public final class LargeGearItem extends Item {
    public LargeGearItem(){super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    @Override public String getDescriptionId(ItemStack stack){return "item.hbm.gear_large"+(metadata(stack)==1?"_steel":"");}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.gear_large"+(metadata(stack)==1?"_steel":"")+".name");}
    public List<ItemStack> creativeStacks(){var normal=new ItemStack(this);var steel=new ItemStack(this);steel.set(HbmHazards.LEGACY_METADATA.get(),1);return List.of(normal,steel);}
}
