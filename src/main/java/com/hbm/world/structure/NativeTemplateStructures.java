package com.hbm.world.structure;

import com.google.gson.Gson;
import com.hbm.world.structure.templates.TemplateSourceStateProcessor;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source weighted scheduling, actual modern templates and persistent clipped vanilla pieces. */
public final class NativeTemplateStructures {
    public record Definition(String name,String template,boolean available,List<String> pending){}
    private record Definitions(List<Definition> spawns){}
    private static final DeferredRegister<StructurePieceType> PIECES=DeferredRegister.create(Registries.STRUCTURE_PIECE,"hbm");
    private static final DeferredRegister<StructureProcessorType<?>> PROCESSORS=DeferredRegister.create(Registries.STRUCTURE_PROCESSOR,"hbm");
    static final DeferredHolder<StructurePieceType,StructurePieceType> PIECE=PIECES.register("native_template_piece",()->NativeTemplatePiece::new);
    static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<NativeTemplateHeightProcessor>> HEIGHT=PROCESSORS.register("legacy_template_height",()->()->NativeTemplateHeightProcessor.CODEC);
    private static final List<StructureProcessor> ADDITIONAL=new ArrayList<>();
    private static List<Definition> definitions;

    public static void addProcessor(StructureProcessor processor){ADDITIONAL.add(java.util.Objects.requireNonNull(processor));}
    static void sourceProcessors(net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings settings){
        settings.addProcessor(TemplateSourceStateProcessor.INSTANCE);
        ADDITIONAL.forEach(settings::addProcessor);
    }
    private static List<Definition> definitions(){
        if(definitions==null)try(var input=NativeTemplateStructures.class.getResourceAsStream("/hbm/native-template-index.json")){
            if(input==null)throw new IllegalStateException("Missing complete native structure template index");
            definitions=List.copyOf(new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),Definitions.class).spawns());
        }catch(java.io.IOException error){throw new IllegalStateException(error);}
        return definitions;
    }
    public static void register(IEventBus bus){
        PIECES.register(bus);PROCESSORS.register(bus);
        for(var definition:definitions())if(definition.available())NaturalStructures.registerGenerator(definition.name(),NativeTemplateStructures::generate);
    }
    static Definition definition(String name){return definitions().stream().filter(row->row.name().equals(name)).findFirst().orElseThrow(()->new IllegalStateException("Unknown native source structure "+name));}
    static StructureTemplate template(StructureTemplateManager manager,ResourceLocation location){
        var template=manager.get(location).orElseThrow(()->new IllegalStateException("Missing real structure template "+location));
        if(template.getSize().getX()<=0||template.getSize().getY()<=0||template.getSize().getZ()<=0)throw new IllegalStateException("Empty real structure template "+location);
        return template;
    }
    public static Optional<Structure.GenerationStub> generate(String name,Structure.GenerationContext context,RandomSource random){
        var definition=definition(name);if(!definition.available())return Optional.empty();
        var spawn=NaturalStructures.spawn(name);var location=ResourceLocation.parse(definition.template());
        var template=template(context.structureTemplateManager(),location);
        int turns=random.nextInt(4),x=context.chunkPos().getMinBlockX(),z=context.chunkPos().getMinBlockZ();
        int y=spawn.conform()?context.chunkGenerator().getFirstFreeHeight(x+8,z+8,Heightmap.Types.WORLD_SURFACE_WG,context.heightAccessor(),context.randomState()):NativeTemplatePiece.anchoredHeight(spawn,context.chunkGenerator(),context.randomState(),context.heightAccessor(),x,z);
        var position=new BlockPos(x,y,z);
        return Optional.of(new Structure.GenerationStub(position,builder->builder.addPiece(new NativeTemplatePiece(spawn,location,template,turns,context.heightAccessor(),position))));
    }
    private NativeTemplateStructures(){}
}
