package com.hbm.bomb.nuclear.biome;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;

/** Immutable, bit-packed original three-dimensional biome samples; untouched columns retain cave biomes. */
public final class BiomePalette {
    private final int minQuartY,verticalQuarts;
    private final List<ResourceLocation> palette;
    private final SimpleBitStorage values;
    private volatile Holder<Biome>[] resolved;
    public BiomePalette(int minQuartY,int verticalQuarts,List<ResourceLocation> palette,int bits,long[] raw){
        if(verticalQuarts<1||verticalQuarts>4096||palette.isEmpty()||palette.size()>65536||bits<1||bits>16)throw new IllegalArgumentException("Invalid column biome palette");
        this.minQuartY=minQuartY;this.verticalQuarts=verticalQuarts;this.palette=List.copyOf(palette);this.values=new SimpleBitStorage(bits,Math.multiplyExact(16,verticalQuarts),raw.clone());
    }
    public static BiomePalette capture(LevelChunk chunk){
        int min=chunk.getMinBuildHeight()>>2,height=chunk.getHeight()>>2;var keys=new LinkedHashMap<ResourceLocation,Integer>();int[] samples=new int[height*16];int cx=chunk.getPos().x<<2,cz=chunk.getPos().z<<2;
        for(int y=0;y<height;y++)for(int z=0;z<4;z++)for(int x=0;x<4;x++){var biome=chunk.getNoiseBiome(cx+x,min+y,cz+z);var key=biome.unwrapKey().orElseThrow().location();samples[y*16+z*4+x]=keys.computeIfAbsent(key,ignored->keys.size());}
        int bits=Math.max(1,32-Integer.numberOfLeadingZeros(keys.size()-1));var packed=new SimpleBitStorage(bits,samples.length,samples);return new BiomePalette(min,height,List.copyOf(keys.keySet()),bits,packed.getRaw());
    }
    @SuppressWarnings("unchecked") private Holder<Biome>[] resolve(Registry<Biome> registry){
        var result=resolved;if(result!=null)return result;synchronized(this){result=resolved;if(result==null){result=(Holder<Biome>[])new Holder<?>[palette.size()];for(int i=0;i<result.length;i++)result[i]=registry.getHolderOrThrow(ResourceKey.create(Registries.BIOME,palette.get(i)));resolved=result;}}return result;
    }
    public Holder<Biome> sample(Registry<Biome> registry,int qx,int qy,int qz){int y=Mth.clamp(qy-minQuartY,0,verticalQuarts-1);return resolve(registry)[values.get(y*16+(qz&3)*4+(qx&3))];}
    public CompoundTag save(){var tag=new CompoundTag();tag.putInt("minQuartY",minQuartY);tag.putInt("verticalQuarts",verticalQuarts);tag.putInt("bits",values.getBits());tag.putLongArray("values",values.getRaw());var keys=new ListTag();for(var key:palette)keys.add(StringTag.valueOf(key.toString()));tag.put("palette",keys);return tag;}
    public static BiomePalette load(CompoundTag tag){var keys=new ArrayList<ResourceLocation>();var list=tag.getList("palette",Tag.TAG_STRING);for(int i=0;i<list.size();i++)keys.add(ResourceLocation.parse(list.getString(i)));return new BiomePalette(tag.getInt("minQuartY"),tag.getInt("verticalQuarts"),keys,tag.getInt("bits"),tag.getLongArray("values"));}
    public void encode(FriendlyByteBuf buffer){buffer.writeVarInt(minQuartY);buffer.writeVarInt(verticalQuarts);buffer.writeCollection(palette,FriendlyByteBuf::writeResourceLocation);buffer.writeVarInt(values.getBits());buffer.writeLongArray(values.getRaw());}
    public static BiomePalette decode(FriendlyByteBuf buffer){int min=buffer.readVarInt(),height=buffer.readVarInt();var keys=buffer.readList(FriendlyByteBuf::readResourceLocation);int bits=buffer.readVarInt();long[] data=buffer.readLongArray(null,65536);return new BiomePalette(min,height,keys,bits,data);}
}
