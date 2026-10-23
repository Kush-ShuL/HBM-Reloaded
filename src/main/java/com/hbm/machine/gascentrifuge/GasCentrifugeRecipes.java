package com.hbm.machine.gascentrifuge;
import com.hbm.fluid.*;import java.util.*;import net.minecraft.core.registries.BuiltInRegistries;import net.minecraft.network.chat.Component;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;
/** Original pseudo-fluids are internal stages, deliberately not world fluids or interchangeable containers. */
public final class GasCentrifugeRecipes {
 public enum PseudoFluidType {
  NONE(0,0,null,false),HEUF6(300,0,"NONE",true,"nugget_u238",2,"nugget_u235",1,"fluorite",1),MEUF6(200,100,"HEUF6",false,"nugget_u238",1),LEUF6(300,200,"MEUF6",false,"nugget_u238",1,"fluorite",1),NUF6(400,300,"LEUF6",false,"nugget_u238",1),PF6(300,0,"NONE",false,"nugget_pu238",1,"nugget_pu_mix",2,"fluorite",1),MUD_HEAVY(500,0,"NONE",false,"powder_iron",1,"dust",1,"nuclear_waste_tiny",1),MUD(1000,500,"MUD_HEAVY",false,"powder_lead",1,"dust",1);
  public final int fluidConsumed,fluidProduced;public final boolean highSpeed;private final String next;private final Object[] products;
  PseudoFluidType(int consumed,int produced,String next,boolean fast,Object... products){fluidConsumed=consumed;fluidProduced=produced;this.next=next;highSpeed=fast;this.products=products;}
  public PseudoFluidType outputType(){return next==null?NONE:valueOf(next);}public Component displayName(){return Component.translatable("hbmpseudofluid."+name().toLowerCase(Locale.US));}public String nameKey(){return "hbmpseudofluid."+name().toLowerCase(Locale.US);}
  public List<ItemStack> outputs(){var out=new ArrayList<ItemStack>();for(int i=0;i<products.length;i+=2)out.add(item((String)products[i],(int)products[i+1]));return out;}
  public static PseudoFluidType byName(String name){try{return valueOf(name);}catch(IllegalArgumentException ex){return NONE;}}
 }
 public static PseudoFluidType conversion(HbmFluidType type){return type==HbmFluids.UF6?PseudoFluidType.NUF6:type==HbmFluids.PUF6?PseudoFluidType.PF6:type==HbmFluids.WATZ?PseudoFluidType.MUD:null;}
 public static boolean convertible(PseudoFluidType type){return type==PseudoFluidType.NUF6||type==PseudoFluidType.PF6||type==PseudoFluidType.MUD;}
 public static ItemStack item(String name,int count){var id=ResourceLocation.fromNamespaceAndPath("hbm",name);if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Missing real gas centrifuge output "+id);return new ItemStack(BuiltInRegistries.ITEM.get(id),count);}
 public static List<GasCentrifugeRecipe> recipes(net.minecraft.world.item.crafting.RecipeManager manager){return manager.getAllRecipesFor(GasCentrifugeContent.TYPE.get()).stream().map(h->h.value()).toList();}
 private GasCentrifugeRecipes(){}
}
