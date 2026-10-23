package com.hbm.world.structure.generated;
import com.hbm.HbmReloaded;
import com.hbm.world.structure.generated.algorithm.*;
import com.hbm.world.structure.generated.compat.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Called by the original single weighted scheduler after its choice, with its remaining random stream. */
public final class GeneratedStructures {
    private static final DeferredRegister<StructurePieceType> PIECES=DeferredRegister.create(Registries.STRUCTURE_PIECE,"hbm");
    static final DeferredHolder<StructurePieceType,StructurePieceType> PIECE=PIECES.register("generated_source_piece",()->GeneratedPiece::new);
    static final DeferredHolder<StructurePieceType,StructurePieceType> PLAN_PIECE=PIECES.register("generated_source_plan_piece",()->GeneratedPlanPiece::new);
    private static final Set<String> UNREADY=ConcurrentHashMap.newKeySet();
    public static void register(IEventBus bus){PIECES.register(bus);}
    public static Optional<Structure.GenerationStub> generate(String sourceName,Structure.GenerationContext context,RandomSource sourceRandom){if(!sourceName.equals("features")&&!sourceName.equals("bunker"))return Optional.empty();var random=new SourceRandom(sourceRandom);var world=new World(context,random);int chunkX=context.chunkPos().x,chunkZ=context.chunkPos().z;StructureStart start;
        try{if(sourceName.equals("bunker"))start=new BunkerComponents.BunkerStart(world,random,chunkX,chunkZ);else{start=new StructureStart(chunkX,chunkZ);int x=(chunkX<<4)+8,z=(chunkZ<<4)+8;var biome=world.getBiomeGenForCoords(x,z);StructureComponent component;
            if(biome.heightVariation()<=.25F&&random.nextInt(10)==0)component=new SiloComponent(random,x,z);
            else if(biome.temperature()>=1&&biome.rainfall()==0&&!biome.mesa())component=random.nextBoolean()?new CivilianFeatures.NTMHouse1(random,x,z):new CivilianFeatures.NTMHouse2(random,x,z);
            else component=switch(random.nextInt(6)){case 0->new CivilianFeatures.NTMLab2(random,x,z);case 1->new CivilianFeatures.NTMLab1(random,x,z);case 2->new OfficeFeatures.LargeOffice(random,x,z);case 3->new OfficeFeatures.LargeOfficeCorner(random,x,z);default->new CivilianFeatures.RuralHouse1(random,x,z);};
            start.components.add(component);start.updateBoundingBox();}
            for(var component:start.components)if(component instanceof SiloComponent silo)silo.prepareGroundHeight(world);start.updateBoundingBox();
            var dependencies=GeneratedDependencies.forComponents(start.components);dependencies.requireRegistryAndHooks();var anchor=new BlockPos((chunkX<<4)+8,64,(chunkZ<<4)+8);return Optional.of(new Structure.GenerationStub(anchor,builder->{for(var component:start.components)builder.addPiece(new GeneratedPiece(component,dependencies,context.heightAccessor()));}));
        }catch(RuntimeException error){if(UNREADY.add(sourceName+":"+error.getMessage()))HbmReloaded.LOGGER.error("Original procedural structure {} requires its real source dependencies",sourceName,error);return Optional.empty();}
    }
    static StructureComponent component(String sourceType){return switch(sourceType){case "SiloComponent"->new SiloComponent();case "NTMHouse1"->new CivilianFeatures.NTMHouse1();case "NTMHouse2"->new CivilianFeatures.NTMHouse2();case "NTMLab1"->new CivilianFeatures.NTMLab1();case "NTMLab2"->new CivilianFeatures.NTMLab2();case "RuralHouse1"->new CivilianFeatures.RuralHouse1();case "LargeOffice"->new OfficeFeatures.LargeOffice();case "LargeOfficeCorner"->new OfficeFeatures.LargeOfficeCorner();case "StartingHub"->new BunkerComponents.StartingHub();case "Corridor"->new BunkerComponents.Corridor();case "BedroomL"->new BunkerComponents.BedroomL();case "FunJunction"->new BunkerComponents.FunJunction();case "BathroomL"->new BunkerComponents.BathroomL();case "Laboratory"->new BunkerComponents.Laboratory();case "PowerRoom"->new BunkerComponents.PowerRoom();default->throw new IllegalStateException("Unknown original component "+sourceType);};}
    private GeneratedStructures(){}
}
