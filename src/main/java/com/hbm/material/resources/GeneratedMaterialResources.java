package com.hbm.material.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.resource.EmptyPackResources;

/** Generated resource bytes enter the ordinary resource/data-pack loaders before their first reload. */
public final class GeneratedMaterialResources {
    private static final Map<PackType,List<Supplier<Map<ResourceLocation,byte[]>>>> SOURCES=new EnumMap<>(PackType.class);
    private static boolean registered;
    private GeneratedMaterialResources() {}
    public static void contribute(PackType type,Supplier<Map<ResourceLocation,byte[]>> resources) {
        SOURCES.computeIfAbsent(type,ignored->new ArrayList<>()).add(resources);
    }
    public static void register(IEventBus bus) {
        if(registered) return; registered=true; bus.addListener(GeneratedMaterialResources::packs);
    }
    private static void packs(AddPackFindersEvent event) {
        PackType type=event.getPackType(); var resources=new LinkedHashMap<ResourceLocation,byte[]>();
        for(var source:SOURCES.getOrDefault(type,List.of())) source.get().forEach((id,bytes)->{
            if(resources.putIfAbsent(id,bytes.clone())!=null) throw new IllegalStateException("Duplicate generated material resource "+id);
        });
        if(resources.isEmpty()) return;
        Map<ResourceLocation,byte[]> snapshot=Map.copyOf(resources);
        var title=Component.literal("HBM materials");
        var meta=new PackMetadataSection(title,SharedConstants.getCurrentVersion().getPackVersion(type));
        var supplier=new Pack.ResourcesSupplier() {
            @Override public PackResources openPrimary(PackLocationInfo info) { return new MemoryPack(info,meta,type,snapshot); }
            @Override public PackResources openFull(PackLocationInfo info,Pack.Metadata metadata) { return openPrimary(info); }
        };
        var pack=Pack.readMetaAndCreate(new PackLocationInfo("hbm/generated_materials",title,PackSource.BUILT_IN,Optional.empty()),supplier,type,new PackSelectionConfig(true,Pack.Position.BOTTOM,false));
        if(pack==null) throw new IllegalStateException("Could not create generated material resources");
        event.addRepositorySource(consumer->consumer.accept(pack.hidden()));
    }
    private static final class MemoryPack extends EmptyPackResources {
        private final PackType type;
        private final Map<ResourceLocation,byte[]> resources;
        private MemoryPack(PackLocationInfo info,PackMetadataSection meta,PackType type,Map<ResourceLocation,byte[]> resources) { super(info,meta); this.type=type; this.resources=resources; }
        @Override public Set<String> getNamespaces(PackType requested) { return requested==type?resources.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toUnmodifiableSet()):Set.of(); }
        @Override public IoSupplier<InputStream> getResource(PackType requested,ResourceLocation id) { byte[] bytes=requested==type?resources.get(id):null; return bytes==null?null:()->new ByteArrayInputStream(bytes); }
        @Override public void listResources(PackType requested,String namespace,String path,ResourceOutput output) {
            if(requested!=type) return;
            String prefix=path.isEmpty()?"":path+"/";
            resources.forEach((id,bytes)->{if(id.getNamespace().equals(namespace)&&id.getPath().startsWith(prefix)) output.accept(id,()->new ByteArrayInputStream(bytes));});
        }
    }
}
