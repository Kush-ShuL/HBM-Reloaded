package com.hbm.machine.turret.jeremy;
import com.hbm.item.FlatSourceItem;
import java.util.List;
import net.minecraft.world.item.Item;
/** Five fixed ItemAmmo(Ammo240Shell) identities; original empty trait sets, names/icons and stack64. */
public final class CannonAmmoItem extends Item implements FlatSourceItem {
 public static final List<String> NAMES=List.of("ammo_shell","ammo_shell_explosive","ammo_shell_apfsds_t","ammo_shell_apfsds_du","ammo_shell_w9");
 private final int sourceOrdinal;
 public CannonAmmoItem(int sourceOrdinal){super(new Properties().stacksTo(64));if(sourceOrdinal<0||sourceOrdinal>=NAMES.size())throw new IllegalArgumentException("Unknown240mm source round "+sourceOrdinal);this.sourceOrdinal=sourceOrdinal;}
 public int sourceOrdinal(){return sourceOrdinal;}
 @Override public String getDescriptionId(){return "item.hbm."+NAMES.get(sourceOrdinal);}
}
