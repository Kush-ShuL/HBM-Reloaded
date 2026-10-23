package com.hbm.world.structure.loot.mixin;
import com.hbm.world.structure.loot.LegacyCraftingOrigin;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(CraftingInput.class)
public abstract class CraftingOriginMixin implements LegacyCraftingOrigin {
    @Unique private int hbm$width,hbm$height;
    @Unique private List<ItemStack> hbm$items=List.of();
    @Override public void hbm$setOrigin(int width,int height,List<ItemStack> items){hbm$width=width;hbm$height=height;hbm$items=List.copyOf(items);}
    @Override public int hbm$originalWidth(){return hbm$width;}
    @Override public int hbm$originalHeight(){return hbm$height;}
    @Override public List<ItemStack> hbm$originalItems(){return hbm$items;}
    @Inject(method="ofPositioned",at=@At("RETURN"))
    private static void hbm$keepAbsoluteGrid(int width,int height,List<ItemStack> items,CallbackInfoReturnable<CraftingInput.Positioned> callback){var input=callback.getReturnValue().input();if(!input.isEmpty())((LegacyCraftingOrigin)input).hbm$setOrigin(width,height,items);}
}
