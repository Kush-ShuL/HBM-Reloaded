package com.hbm.world.structure;

import com.google.gson.JsonParser;
import com.hbm.bomb.conventional.LandmineBlockEntity;
import com.hbm.world.structure.generated.GeneratedAttachments;
import com.hbm.world.structure.generated.GeneratedLegacyFeatures;
import com.hbm.world.structure.generated.GeneratedLoot;
import com.hbm.world.structure.generated.GeneratedStructures;
import com.hbm.world.structure.loot.LegacyLootRandom;
import com.hbm.world.structure.loot.StructureLootPresets;
import com.hbm.world.structure.loot.StructureLootProcessor;
import com.hbm.world.structure.meteor.MeteorDungeonJigsaw;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

/** Hooks only complete original content into the native generation lifecycle. */
public final class NaturalSourceIntegration {
    public static void register(IEventBus bus,ModContainer container){
        com.hbm.world.structure.templates.TemplateProcessors.register(bus);
        com.hbm.world.structure.meteor.MeteorProcessors.register(bus);
        NativeTemplateStructures.register(bus);
        NativeTemplateStructures.addProcessor(StructureLootProcessor.INSTANCE);
        GeneratedStructures.register(bus);GeneratedLegacyFeatures.registerConfig(container);
        NaturalStructures.registerGenerator("features",GeneratedStructures::generate);
        NaturalStructures.registerGenerator("bunker",GeneratedStructures::generate);
        GeneratedLoot.installExtras(new GeneratedLoot.Extras(){
            public ItemStack book(String kind,java.util.Random random){return StructureLootPresets.book(kind,new LegacyLootRandom(random));}
            public void preset(String kind,WorldGenLevel level,BlockPos pos,java.util.Random random){StructureLootPresets.preset(level,pos,kind,new LegacyLootRandom(random));}
            public void lore(WorldGenLevel level,BlockPos pos,ItemStack stack){StructureLootPresets.lore(level,pos,stack,level.getRandom());}
            public void require(Set<String> effects){
                var lookup=RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
                for(String effect:effects){
                    if(effect.equals("lore")||effect.equals("book:office")||effect.equals("book:lab")){
                        if(!BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse("hbm:book_lore")))throw new IllegalStateException("Missing real source lore book");
                    }else if(effect.startsWith("preset:")){
                        if(!StructureLootPresets.ready(lookup,effect.substring(7)))throw new IllegalStateException("Incomplete original structure preset "+effect);
                    }else throw new IllegalStateException("Unknown original structure extra "+effect);
                }
            }
        });
        GeneratedAttachments.install(new GeneratedAttachments.Backend(){
            public boolean supports(String kind){return kind.equals("landmine")||kind.equals("bobblehead")||kind.equals("launch_pad_rusted")||kind.equals("radio_torch");}
            public void apply(String kind,WorldGenLevel level,BlockPos pos,CompoundTag values){
                if(kind.equals("landmine")&&level.getBlockEntity(pos) instanceof LandmineBlockEntity mine){mine.waitingForPlayer=values.getBoolean("waitingForPlayer");mine.setChanged();}
                else if(kind.equals("bobblehead")&&level.getBlockEntity(pos) instanceof com.hbm.block.bobble.BobbleBlockEntity bobble)bobble.setType(values.getInt("type"));
                else if(kind.equals("launch_pad_rusted")&&level.getBlockEntity(pos) instanceof com.hbm.missile.launchpad.rusted.RustedLaunchPadBlockEntity pad)pad.setMissileLoaded(values.getBoolean("missileLoaded"));
                else if(kind.equals("radio_torch")&&level.getBlockEntity(pos) instanceof com.hbm.network.rtty.RadioTorchEntity radio){radio.channel=values.getString("channel");radio.lastState=values.getInt("lastState");radio.setChanged();}
                else throw new IllegalStateException("Missing real generated structure attachment "+kind+" at "+pos);
            }
        });
        try(var input=NaturalSourceIntegration.class.getResourceAsStream("/hbm/meteor-native-status.json")){
            if(input==null)throw new IllegalStateException("Missing native Meteor completeness index");
            boolean ready=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject().get("available").getAsBoolean();
            if(ready)NaturalStructures.registerGenerator("meteor_dungeon",(name,context,random)->{
                var structure=context.registryAccess().registryOrThrow(Registries.STRUCTURE).get(ResourceLocation.parse("hbm:meteor_dungeon"));
                if(structure==null)throw new IllegalStateException("Missing genuine minecraft:jigsaw Meteor structure");
                try(var ignored=MeteorDungeonJigsaw.usingSourceRandom(random)){return structure.findValidGenerationPoint(context);}
            });
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
    }
    private NaturalSourceIntegration(){}
}
