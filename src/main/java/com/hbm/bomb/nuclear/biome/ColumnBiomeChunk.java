package com.hbm.bomb.nuclear.biome;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.core.Holder;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;

/** One exact target per physical X/Z column; the native quart updates are batched until sync. */
public final class ColumnBiomeChunk {
    public final BiomePalette original;
    private final AtomicReferenceArray<ResourceLocation> columns=new AtomicReferenceArray<>(256);
    private final Map<Integer,Holder<Biome>> pendingQuart=new HashMap<>();
    private volatile int quartMask;
    public ColumnBiomeChunk(BiomePalette original){this.original=original;}
    public ResourceLocation at(int x,int z){return columns.get((x&15)|((z&15)<<4));}
    public boolean touchesQuart(int qx,int qz){return (quartMask&(1<<((qx&3)|((qz&3)<<2))))!=0;}
    public boolean set(int x,int z,Holder<Biome> biome){var key=biome.unwrapKey().orElseThrow().location();int index=(x&15)|((z&15)<<4),quart=((x&15)>>2)|(((z&15)>>2)<<2);pendingQuart.put(quart,biome);quartMask|=1<<quart;return !Objects.equals(columns.getAndSet(index,key),key);}
    private void restore(int index,ResourceLocation key){columns.set(index,key);int x=index&15,z=index>>4;quartMask|=1<<((x>>2)|((z>>2)<<2));}
    public void applyNative(LevelChunk chunk){
        if(pendingQuart.isEmpty())return;int cx=chunk.getPos().x<<2,cz=chunk.getPos().z<<2;
        for(int sectionIndex=0;sectionIndex<chunk.getSections().length;sectionIndex++){var section=chunk.getSections()[sectionIndex];int bottom=chunk.getSectionYFromSectionIndex(sectionIndex)<<2;section.fillBiomesFromNoise((qx,qy,qz,sampler)->{var value=pendingQuart.get((qx&3)|((qz&3)<<2));return value==null?section.getNoiseBiome(qx&3,qy&3,qz&3):value;},null,cx,bottom,cz);}
        pendingQuart.clear();chunk.setUnsaved(true);
    }
    public record Targets(List<ResourceLocation> palette,int bits,long[] data){
        public Targets{palette=List.copyOf(palette);data=data.clone();}
        public SimpleBitStorage storage(){return new SimpleBitStorage(bits,256,data.clone());}
    }
    public Targets targets(){var palette=new LinkedHashMap<ResourceLocation,Integer>();int[] indices=new int[256];for(int i=0;i<256;i++){var key=columns.get(i);if(key!=null)indices[i]=palette.computeIfAbsent(key,ignored->palette.size()+1);}int bits=Math.max(1,32-Integer.numberOfLeadingZeros(palette.size()));var storage=new SimpleBitStorage(bits,256,indices);return new Targets(List.copyOf(palette.keySet()),bits,storage.getRaw());}
    public static ColumnBiomeChunk from(BiomePalette original,Targets targets){var result=new ColumnBiomeChunk(original);var storage=targets.storage();for(int i=0;i<256;i++){int key=storage.get(i);if(key>0)result.restore(i,targets.palette().get(key-1));}return result;}
    public CompoundTag save(){var tag=new CompoundTag();tag.put("original",original.save());var targets=targets();var palette=new ListTag();for(var key:targets.palette())palette.add(StringTag.valueOf(key.toString()));tag.put("columnPalette",palette);tag.putInt("columnBits",targets.bits());tag.putLongArray("columns",targets.data());return tag;}
    public static ColumnBiomeChunk load(CompoundTag tag){var list=tag.getList("columnPalette",Tag.TAG_STRING);var palette=new ArrayList<ResourceLocation>();for(int i=0;i<list.size();i++)palette.add(ResourceLocation.parse(list.getString(i)));return from(BiomePalette.load(tag.getCompound("original")),new Targets(palette,tag.getInt("columnBits"),tag.getLongArray("columns")));}
}
