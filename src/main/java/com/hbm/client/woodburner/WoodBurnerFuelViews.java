package com.hbm.client.woodburner;
import com.hbm.fluid.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.fluid.*;
import com.hbm.machine.woodburner.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Both viewers use the actual machine fuel evaluator and live fluid traits, including configured fluids. */
final class WoodBurnerFuelViews {
    record Fuel(ItemStack input,ItemStack ash,int burnTicks,int generatedPerTick,HbmFluidType fluid) {
        boolean liquid() { return fluid!=null; }
        List<Component> ashInfo() { return liquid()?List.of():List.of(Component.literal("Adds "+burnTicks+" / 2000 to this ash accumulator"),Component.literal("1 ash per 2000 units, retained until the output has room")); }
    }
    static List<Fuel> recipes(Collection<ItemStack> variants) {
        var result=new ArrayList<Fuel>(); var seen=new HashSet<String>(); var all=new ArrayList<ItemStack>();
        for(Item item:BuiltInRegistries.ITEM) all.add(new ItemStack(item)); all.addAll(variants);
        for(ItemStack stack:all) {
            if(stack.isEmpty()) continue; int metadata=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()); String key=BuiltInRegistries.ITEM.getKey(stack.getItem())+"/"+metadata;
            if(!seen.add(key)) continue; int burn=WoodBurnerFuel.burnTime(stack); if(burn<=0) continue;
            ItemStack ash=com.hbm.item.ash.FlatAshContent.stack(WoodBurnerFuel.ashType(stack),1); result.add(new Fuel(stack.copyWithCount(1),ash,burn,100,null));
        }
        for(HbmFluidType fluid:HbmFluids.getInNiceOrder()) {
            var trait=fluid.getTrait(FluidTraits.Flammable.class); if(trait==null) continue;
            ItemStack icon=FluidIconItem.make(fluid,2);
            result.add(new Fuel(icon,ItemStack.EMPTY,1,(int)(trait.getHeatEnergy()*2/2000L),fluid));
        }
        return result;
    }
    static void draw(GuiGraphics graphics,Fuel fuel,int x,int y) {
        var texture=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/generators/gui_wood_burner_alt.png"); graphics.blit(texture,x,y,5,11,166,78,256,256);
        if(fuel.liquid()) { graphics.blit(texture,x+11,y+6,176,52,60,54,256,256); graphics.blit(texture,x+74,y+6,176,106,36,54,256,256); }
        graphics.blit(texture,x+48,y+6,196,0,16,15,256,256);
        var font=com.hbm.client.gui.font.LegacyFonts.get(); graphics.drawString(font,fuel.generatedPerTick()+" HE/t",x+3,y+65,0x404040,false);
        String description=fuel.liquid()?"2mB/t":fuel.burnTicks()+"t / "+(long)fuel.burnTicks()*100+"HE"; graphics.drawString(font,description,x+68,y+65,0x404040,false);
    }
    private WoodBurnerFuelViews() {}
}
