package com.hbm.client.conveyor;

import com.hbm.conveyor.belt.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ConveyorModels {
 static final List<String> TEXTURES;
 static{var names=new ArrayList<String>(List.of("conveyor_side","concrete_smooth","steel_grate","minecraft:iron_block"));for(String base:List.of("conveyor","conveyor_express","conveyor_double","conveyor_triple")){names.add(base);names.add(base+"_curve_left");names.add(base+"_curve_right");}TEXTURES=List.copyOf(names);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ConveyorWandItem.LEFT_SHIFT=()->GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(),GLFW.GLFW_KEY_LEFT_SHIFT)==GLFW.GLFW_PRESS;ConveyorWandItem.CLIENT_TICK=ConveyorPreview::tick;ConveyorWandItem.CLEAR_PREVIEW=ConveyorPreview::clear;});}
 @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){var blocks=new HashMap<String,ConveyorBakedModel>();event.getModels().replaceAll((location,model)->{if(!location.id().getNamespace().equals("hbm"))return model;String path=location.id().getPath();for(var type:ConveyorWandItem.Type.values())if(location.id().equals(ConveyorContent.wandId(type)))return new ConveyorBakedModel(model,ConveyorBeltBlock.Kind.valueOf(type.name()),event.getTextureGetter());for(var kind:ConveyorBeltBlock.Kind.values()){String id=switch(kind){case REGULAR->"conveyor";default->"conveyor_"+kind.name().toLowerCase(Locale.US);};if(path.equals(id)){if(kind==ConveyorBeltBlock.Kind.LIFT&&location.getVariant().equals("inventory"))return model;return blocks.computeIfAbsent(id+":"+location.getVariant().equals("inventory"),k->new ConveyorBakedModel(model,kind,event.getTextureGetter()));}}return model;});}
 private ConveyorModels(){}
}
