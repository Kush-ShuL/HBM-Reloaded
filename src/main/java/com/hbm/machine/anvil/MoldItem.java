package com.hbm.machine.anvil;
import com.google.gson.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.*;
import com.hbm.registry.HbmItemTags;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Actual source stable-ID cast molds, including material-config custom molds and casting output/cost APIs. */
public final class MoldItem extends Item {
    public record Mold(int id,int size,String name,String shape,int amount,int cost,Map<String,ItemStack> fixed,boolean blockOverride) {
        public ItemStack output(NtmMaterial material) {
            if(!fixed.isEmpty()) { ItemStack value=fixed.get(material.symbol());return value==null?ItemStack.EMPTY:value.copy(); }
            if(blockOverride&&(material.symbol().equals("MAT_STONE")||material.symbol().equals("MAT_OBSIDIAN"))) return new ItemStack(material.symbol().equals("MAT_STONE")?net.minecraft.world.level.block.Blocks.STONE:net.minecraft.world.level.block.Blocks.OBSIDIAN);
            MaterialShape actual=HbmMaterials.shape(shape);if(actual==null)return ItemStack.EMPTY;
            List<ItemStack> matches=HbmItemTags.items(HbmItemTags.material(actual.symbol(),material.names().getFirst())).toList();
            if(matches.isEmpty())return ItemStack.EMPTY;
            ItemStack preferred=matches.stream().filter(stack->{var id=BuiltInRegistries.ITEM.getKey(stack.getItem());return id.getNamespace().equals("hbm")&&!id.toString().contains("fragment");}).findFirst().orElse(matches.getFirst());return preferred.copyWithCount(amount);
        }
        public int requiredUnits() { return shape.isEmpty()?cost:HbmMaterials.shape(shape).q(amount); }
        public Component title() {return Component.translatable("shape."+(shape.isEmpty()?name:HbmMaterials.shape(shape).name())).append(" x"+amount);}
    }
    private List<Mold> molds;private Map<Integer,Mold> byId;
    public MoldItem(){super(new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    public synchronized List<Mold> molds(){if(molds==null)load();return molds;}
    private void load(){var values=new ArrayList<Mold>();try(var stream=MoldItem.class.getResourceAsStream("/hbm/anvil-molds.json")){if(stream==null)throw new IllegalStateException("Missing anvil-molds.json");for(var value:JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonArray()){var row=value.getAsJsonObject();Map<String,ItemStack> fixed=new LinkedHashMap<>();if(row.has("fixed"))row.getAsJsonObject("fixed").entrySet().forEach(entry->{var data=entry.getValue().getAsJsonObject();var id=ResourceLocation.parse(data.get("id").getAsString());if(BuiltInRegistries.ITEM.containsKey(id)){ItemStack stack=new ItemStack(BuiltInRegistries.ITEM.get(id),data.has("count")?data.get("count").getAsInt():1);if(data.has("components"))for(var component:data.getAsJsonObject("components").entrySet())if(component.getKey().equals("hbm:legacy_metadata"))stack.set(HbmHazards.LEGACY_METADATA.get(),component.getValue().getAsInt());fixed.put(entry.getKey(),stack);}});values.add(new Mold(row.get("id").getAsInt(),row.get("size").getAsInt(),row.get("name").getAsString(),row.has("shape")?row.get("shape").getAsString():"",row.has("amount")?row.get("amount").getAsInt():1,row.has("cost")?row.get("cost").getAsInt():0,fixed,row.has("blockOverride")&&row.get("blockOverride").getAsBoolean()));}}catch(java.io.IOException error){throw new IllegalStateException(error);}int small=100,large=200;for(var mold:HbmMaterials.molds()){int id=mold.large()?large++:small++;values.add(new Mold(id,mold.large()?1:0,mold.legacyName(),mold.shape().symbol(),mold.castAmount(),0,Map.of(),false));}molds=List.copyOf(values);byId=new LinkedHashMap<>();for(var mold:molds)byId.put(mold.id(),mold);}
    public Mold mold(ItemStack stack){molds();return byId.getOrDefault(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()),molds.getFirst());}
    public List<ItemStack> creativeStacks(){return molds().stream().map(value->{ItemStack stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),value.id());return stack;}).toList();}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.mold.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){var mold=mold(stack);tooltip.add(mold.title().copy().withStyle(ChatFormatting.YELLOW));tooltip.add(Component.translatable(mold.size()==0?"tile.foundry_mold.name":"tile.foundry_basin.name").withStyle(mold.size()==0?ChatFormatting.GOLD:ChatFormatting.RED));}
}
