package com.hbm.bomb.nuclear.programmable;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** Real loading requirements/contributions, separate from actual AssemblerRecipe manufacturing. No executable recipe is invented. */
public final class NuclearPayloadDisplays {
 public record Display(CustomNuclearIngredients.Entry entry) {
  public boolean timed(){return entry==null;}
  public List<List<ItemStack>> inputs(){return timed()?List.of(List.of(new ItemStack(HbmContent.item("egg_balefire"))),List.of(new ItemStack(HbmContent.item("battery_spark")),new ItemStack(HbmContent.item("battery_trixite")))):List.of(List.of(entry.stack()));}
  public ItemStack bomb(){return new ItemStack((timed()?ProgrammableNuclearContent.TIMED_ITEM:ProgrammableNuclearContent.CUSTOM_ITEM).get());}
  public List<Component> lines(){if(timed())return List.of(Component.translatable("gui.hbm.fstbmb.payload"),Component.translatable("gui.hbm.fstbmb.payload_note"));return List.of(Component.translatable("gui.hbm.custom_nuke.stage."+entry.stage().name().toLowerCase(Locale.ROOT)),Component.translatable("gui.hbm.custom_nuke.operation."+entry.operation().name().toLowerCase(Locale.ROOT),entry.value()),Component.translatable("gui.hbm.custom_nuke.gating_note"));}
 }
 public static List<Display> all(){var displays=new ArrayList<Display>();displays.add(new Display(null));CustomNuclearIngredients.entries().forEach(entry->displays.add(new Display(entry)));return List.copyOf(displays);}
 private NuclearPayloadDisplays(){}
}
