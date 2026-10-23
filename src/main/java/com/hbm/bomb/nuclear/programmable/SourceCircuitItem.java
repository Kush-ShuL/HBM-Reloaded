package com.hbm.bomb.nuclear.programmable;
import com.hbm.item.FlatSourceItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
/** The21 real static ItemCircuit materials. The X5846 tooltip body is commented out and stays inactive. */
public final class SourceCircuitItem extends Item implements FlatSourceItem {
 public static final List<String> NAMES=List.of("vacuum_tube","capacitor","capacitor_tantalium","pcb","silicon","chip","chip_bismoid","analog","basic","advanced","capacitor_board","bismoid","controller_chassis","controller","controller_advanced","quantum","chip_quantum","controller_quantum","atomic_clock","numitron","crystal");
 public static final List<Integer> ORDER=List.of(0,19,1,2,18,3,4,5,6,16,7,8,9,10,11,15,20,12,13,14,17);
 public final String sourceVariant;public final int sourceOrdinal;
 private SourceCircuitItem(String variant,int ordinal){super(new Properties().stacksTo(64));sourceVariant=variant;sourceOrdinal=ordinal;}
 public static List<DeferredItem<SourceCircuitItem>> register(DeferredRegister.Items items){var result=new ArrayList<DeferredItem<SourceCircuitItem>>();for(int index=0;index<NAMES.size();index++){int ordinal=index;String name=NAMES.get(index);result.add(items.register("circuit_"+name,()->new SourceCircuitItem(name,ordinal)));}return List.copyOf(result);}
 public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(ProgrammableNuclearContent.CIRCUITS.get(sourceOrdinal).get(),count);}
 public static List<ItemStack> creativeStacks(){return ORDER.stream().map(index->stack(index,1)).toList();}
}
