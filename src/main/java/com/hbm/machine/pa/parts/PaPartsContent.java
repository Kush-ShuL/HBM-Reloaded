package com.hbm.machine.pa.parts;
import com.hbm.item.structureloot.StructureFoodItem;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Four fixed source PA coil items and the source chicken-collision food output; no machine placeholders. */
public final class PaPartsContent{
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final List<DeferredItem<PaCoilItem>> COILS=registerCoils();
    private static List<DeferredItem<PaCoilItem>> registerCoils(){var coils=new ArrayList<DeferredItem<PaCoilItem>>();for(var type:PaCoilItem.Type.values())coils.add(ITEMS.register("pa_coil_"+type.name().toLowerCase(Locale.ROOT),()->new PaCoilItem(type)));return List.copyOf(coils);}
    public static List<ItemStack> coilStacks(){return COILS.stream().map(item->new ItemStack(item.get())).toList();}
    public static final DeferredItem<StructureFoodItem> NUGGET=ITEMS.register("nugget",()->new StructureFoodItem("nugget",200,1F));
    public static void register(IEventBus bus){ITEMS.register(bus);}public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "control"->coilStacks();case "consumable"->List.of(new ItemStack(NUGGET.get()));default->List.of();};}
    private PaPartsContent(){}
}
