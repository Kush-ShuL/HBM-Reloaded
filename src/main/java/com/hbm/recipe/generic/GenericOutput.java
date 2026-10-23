package com.hbm.recipe.generic;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
/** Original independent per-item chance and WeightedRandom selection, retaining source float comparisons. */
public record GenericOutput(List<Chance> pool,boolean weighted) {
    public record Chance(ItemStack stack,float chance,int weight){
        public static final Codec<Chance> CODEC=RecordCodecBuilder.create(i->i.group(ItemStack.CODEC.fieldOf("stack").forGetter(Chance::stack),Codec.FLOAT.optionalFieldOf("chance",1F).forGetter(Chance::chance),Codec.INT.optionalFieldOf("weight",0).forGetter(Chance::weight)).apply(i,Chance::new));
        public ItemStack collapse(RandomSource random){if(chance>=1F)return stack.copy();int size=0;for(int i=0;i<stack.getCount();i++)if(random.nextFloat()<=chance)size++;return size==0?ItemStack.EMPTY:stack.copyWithCount(size);}
    }
    public static final Codec<GenericOutput> CODEC=RecordCodecBuilder.create(i->i.group(Chance.CODEC.listOf().fieldOf("pool").forGetter(GenericOutput::pool),Codec.BOOL.optionalFieldOf("weighted",false).forGetter(GenericOutput::weighted)).apply(i,GenericOutput::new));
    public GenericOutput{pool=List.copyOf(pool);if(pool.isEmpty())throw new IllegalArgumentException("Original output pool is empty");}
    public GenericOutput(ItemStack stack){this(List.of(new Chance(stack,1F,0)),false);}
    public boolean possibleMultiOutput(){return weighted&&pool.size()>1;}
    public ItemStack single(){return possibleMultiOutput()?ItemStack.EMPTY:pool.getFirst().stack().copy();}
    public List<Chance> possibilities(){return pool;}
    public float selectionProbability(Chance chance){if(!weighted)return 1F;int total=0;for(var c:pool)total+=c.weight();return (float)chance.weight()/total;}
    public ItemStack collapse(RandomSource random){if(!weighted)return pool.getFirst().collapse(random);int total=0;for(var c:pool)total+=c.weight();if(total<=0)throw new IllegalArgumentException("Original weighted total must be positive");int chosen=random.nextInt(total);for(var c:pool){chosen-=c.weight();if(chosen<0)return c.collapse(random);}return ItemStack.EMPTY;}
    public void encode(RegistryFriendlyByteBuf b){b.writeBoolean(weighted);b.writeVarInt(pool.size());for(var c:pool){ItemStack.STREAM_CODEC.encode(b,c.stack());b.writeFloat(c.chance());b.writeInt(c.weight());}}
    public static GenericOutput decode(RegistryFriendlyByteBuf b){boolean weighted=b.readBoolean();var pool=new ArrayList<Chance>();int count=b.readVarInt();for(int i=0;i<count;i++)pool.add(new Chance(ItemStack.STREAM_CODEC.decode(b),b.readFloat(),b.readInt()));return new GenericOutput(pool,weighted);}
}
