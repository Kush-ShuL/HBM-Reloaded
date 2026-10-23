package com.hbm.machine.pa.parts;
import com.hbm.item.structureloot.StructureFoodItem;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Genuine4PAcoil stackvariants and the source chicken-collision food output; no machine placeholders. */
public final class PaPartsContent{
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<PaCoilItem> COIL=ITEMS.register("pa_coil",PaCoilItem::new);
    public static final DeferredItem<StructureFoodItem> NUGGET=ITEMS.register("nugget",()->new StructureFoodItem("nugget",200,1F));
    public static void register(IEventBus bus){ITEMS.register(bus);}public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "control"->COIL.get().creativeStacks();case "consumable"->List.of(new ItemStack(NUGGET.get()));default->List.of();};}
    private PaPartsContent(){}
}
