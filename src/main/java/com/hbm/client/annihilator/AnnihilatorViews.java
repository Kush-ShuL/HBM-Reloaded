package com.hbm.client.annihilator;
import com.hbm.machine.annihilator.AnnihilatorRecipes;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.world.item.ItemStack;
/** Source NEI one display per milestone, exact BigInteger threshold; missing real external items stay unavailable. */
final class AnnihilatorViews {
 record View(List<ItemStack> inputs,BigInteger amount,ItemStack output){}
 static List<View> recipes(){var rows=new ArrayList<View>();for(var entry:AnnihilatorRecipes.recipes().entrySet()){var inputs=AnnihilatorRecipes.examples(entry.getKey());if(inputs.isEmpty())continue;for(var step:entry.getValue())rows.add(new View(inputs,step.amount(),step.payout().copy()));}return rows;}
}
