package com.hbm.client.wiaj;

import com.google.gson.JsonParser;
import com.hbm.client.wiaj.cannery.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

/** Original ten tutorial triggers, strictly bound to actual item and actor prerequisites. */
public final class WiajTutorials {
    private record Trigger(String item,int metadata,String article,Supplier<CanneryBase> factory){}
    private static final List<Trigger> TRIGGERS=List.of(new Trigger("heater_firebox",0,"CanneryFirebox",CanneryFirebox::new),new Trigger("machine_stirling",0,"CanneryStirling",CanneryStirling::new),new Trigger("machine_stirling_steel",0,"CanneryStirling",CanneryStirling::new),new Trigger("machine_gascent",0,"CanneryCentrifuge",CanneryCentrifuge::new),new Trigger("machine_fel",0,"CannerySILEX",CannerySILEX::new),new Trigger("machine_silex",0,"CannerySILEX",CannerySILEX::new),new Trigger("foundry_channel",0,"CanneryFoundryChannel",CanneryFoundryChannel::new),new Trigger("machine_crucible",0,"CanneryCrucible",CanneryCrucible::new),new Trigger("plant_item_mustardwillow",0,"CanneryWillow",CanneryWillow::new),new Trigger("plant_flower",4,"CanneryWillow",CanneryWillow::new));
    private static final Set<String> READY=ready();
    private static Set<String> ready(){try(var input=WiajTutorials.class.getResourceAsStream("/hbm/wiaj-source-dependencies.json")){if(input==null)throw new IllegalStateException("Run port_wiaj.py");var result=new HashSet<String>();for(var entry:JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject().entrySet())if(entry.getValue().getAsJsonObject().get("ready").getAsBoolean())result.add(entry.getKey());return Set.copyOf(result);}catch(IOException error){throw new IllegalStateException(error);}}
    private static Trigger trigger(ItemStack stack){if(stack.isEmpty())return null;for(var entry:TRIGGERS)if(READY.contains(entry.article)){var expected=JarSymbols.item(entry.item,1,entry.metadata);if(stack.getItem()==expected.getItem()&&com.hbm.recipe.generic.Blueprints.metadata(stack)==com.hbm.recipe.generic.Blueprints.metadata(expected))return entry;}return null;}
    public static boolean has(ItemStack stack){return trigger(stack)!=null;}
    public static boolean open(ItemStack stack,Screen previous){var entry=trigger(stack);if(entry==null)return false;Minecraft.getInstance().setScreen(new GuiWorldInAJar(entry.factory.get(),previous));return true;}
    private WiajTutorials(){}
}
