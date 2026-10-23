package com.hbm.world.structure.loot;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
/** Original LootGenerator layouts and HbmChestContents office/lab lore selection. */
public final class StructureLootPresets {
    private static final Map<String,List<String>> POOLS=Map.ofEntries(
        Map.entry("LOOT_MEDICINE",List.of("POOL_PILE_MED_SYRINGE","POOL_PILE_MED_PILLS")),Map.entry("LOOT_CAPSTASH",List.of("POOL_PILE_CAPS")),Map.entry("LOOT_MAKESHIFT_GUN",List.of("POOL_PILE_MAKESHIFT_GUN","POOL_PILE_MAKESHIFT_WRENCH","POOL_PILE_MAKESHIFT_PLATES","POOL_PILE_MAKESHIFT_WIRE")),Map.entry("LOOT_NUKE_STORAGE",List.of("POOL_PILE_NUKE_STORAGE")),Map.entry("LOOT_BONES",List.of("POOL_PILE_BONES")),Map.entry("LOOT_GLYPHID_HIVE",List.of("POOL_PILE_HIVE")),Map.entry("LOOT_SHIT",List.of("POOL_PILE_OF_GARBAGE")),Map.entry("LOOT_MECHANICAL",List.of("POOL_PILE_MECHANICAL")),Map.entry("LOOT_GEAR",List.of("POOL_PILE_GEAR")),Map.entry("LOOT_SUPPLIES",List.of("POOL_PILE_SUPPLIES")));
    /** Exact original LootGenerator.getLootNames order; this is a name view, not another layout table. */
    public static List<String> editorNames(){return List.of("LOOT_BOOKLET","LOOT_CAPNUKE","LOOT_MEDICINE","LOOT_CAPSTASH","LOOT_MAKESHIFT_GUN","LOOT_NUKE_STORAGE","LOOT_BONES","LOOT_GLYPHID_HIVE","LOOT_METEOR","LOOT_FLAREGUN","LOOT_MECHANICAL","LOOT_SHIT","LOOT_GEAR","LOOT_SUPPLIES");}
    private static String canonical(String name){return switch(name){case "medicine","lootMedicine"->"LOOT_MEDICINE";case "capStash","lootCapStash"->"LOOT_CAPSTASH";case "makeshiftGun","lootMakeshiftGun"->"LOOT_MAKESHIFT_GUN";default->name;};}
    public static boolean ready(ServerLevel level,String name){name=canonical(name);if(name.equals("LOOT_METEOR"))return MkuStructureLoot.ready();if(name.equals("LOOT_FLAREGUN")&&!hasItem("gun_flaregun"))return false;for(String pool:POOLS.getOrDefault(name,List.of()))if(!LegacyStructureLoot.ready(level,pool))return false;return true;}
    public static boolean ready(net.minecraft.core.HolderLookup.Provider lookup,String name){name=canonical(name);if(name.equals("LOOT_METEOR"))return MkuStructureLoot.ready();if(name.equals("LOOT_FLAREGUN")&&!hasItem("gun_flaregun"))return false;for(String pool:POOLS.getOrDefault(name,List.of()))if(!LegacyStructureLoot.ready(lookup,pool))return false;return true;}
    private static boolean hasItem(String name){return BuiltInRegistries.ITEM.containsKey(ResourceLocation.fromNamespaceAndPath("hbm",name));}
    private static ItemStack item(String name,int count){var id=ResourceLocation.fromNamespaceAndPath("hbm",name);if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Missing true source loot item "+id);return new ItemStack(BuiltInRegistries.ITEM.get(id),count);}
    private static ItemStack ammo(int metadata){return com.hbm.weapon.sedna.ammo.FlatAmmoContent.standardStack(metadata,1);}
    private static LootPileBlockEntity pile(net.minecraft.world.level.WorldGenLevel world,BlockPos pos){if(!(world.getBlockEntity(pos) instanceof LootPileBlockEntity loot))throw new IllegalStateException("Source loot pile host is missing at "+pos);return loot;}
    private static void varied(LootPileBlockEntity loot,RandomSource random,ItemStack stack,double x,double y,double z){loot.add(stack,x+random.nextGaussian()*.02,y,z+random.nextGaussian()*.02);}
    private static void varied(LootPileBlockEntity loot,RandomSource random,LegacyStructureLoot.Draw stack,double x,double y,double z){loot.add(stack,x+random.nextGaussian()*.02,y,z+random.nextGaussian()*.02);}
    private static LegacyStructureLoot.Draw draw(ServerLevel level,BlockPos pos,String pool,RandomSource random){return LegacyStructureLoot.drawPlaced(level,Vec3.atCenterOf(pos),pool,random);}
    /** Original simple pile layout shared by live placement and pre-chunk source planning. */
    private static void simplePile(LootPileBlockEntity loot,int limit,RandomSource random,java.util.function.Supplier<LegacyStructureLoot.Draw> draw){for(int i=0;i<limit;i++){var stack=draw.get();double x=random.nextDouble()-.5,z=random.nextDouble()-.5;varied(loot,random,stack,x,i*.03125,z);}}
    /** Only the two actual GlyphidHive presets are valid here; no deferred placement RNG or duplicate loot definitions. */
    public static void planSimple(net.minecraft.core.HolderLookup.Provider lookup,LootPileBlockEntity loot,String name,RandomSource random){
        if(!name.equals("LOOT_BONES")&&!name.equals("LOOT_GLYPHID_HIVE"))throw new IllegalArgumentException("Not an original simple hive pile: "+name);
        if(!ready(lookup,name))throw new IllegalStateException("Incomplete real planned source pile dependencies for "+name);
        if(!loot.items().isEmpty())return;String pool=name.equals("LOOT_BONES")?"POOL_PILE_BONES":"POOL_PILE_HIVE";
        simplePile(loot,random.nextInt(3)+3,random,()->LegacyStructureLoot.planDraw(lookup,pool,random));loot.sync();
    }
    public static ItemStack book(String kind,RandomSource random){boolean office=kind.equals("office")||kind.equals("generateOfficeBook");String[] keys=office?new String[]{"resignation_note","memo_stocks","memo_schrab_gsa","memo_schrab_rd","memo_schrab_nuke"}:new String[]{"bf_bomb_1","bf_bomb_2","bf_bomb_3","bf_bomb_4","bf_bomb_5"};int[] pages=office?new int[]{3,1,2,4,3}:new int[]{4,6,6,5,9};int which=random.nextInt(5);return LoreBookItem.create(keys[which],pages[which],office?0x6bc8ff:0x1e1e1e,office?0x0a0a0a:0x46ea44);}
    public static void lore(net.minecraft.world.level.WorldGenLevel world,BlockPos pos,ItemStack book,RandomSource random){var level=world.getLevel();var loot=pile(world,pos);if(!loot.items().isEmpty())return;varied(loot,random,book,0,0,-.25);int count=random.nextInt(3)+2;for(int k=0;k<count;k++)varied(loot,random,new ItemStack(net.minecraft.world.item.Items.BOOK),-.25,k*.03125,.25);count=random.nextInt(2)+1;for(int k=0;k<count;k++)varied(loot,random,new ItemStack(net.minecraft.world.item.Items.PAPER),.25,k*.03125,.125);loot.sync();}
    public static void preset(net.minecraft.world.level.WorldGenLevel world,BlockPos pos,String sourceName,RandomSource random){populate(world.getLevel(),pile(world,pos),sourceName,random);}
    public static void populate(ServerLevel level,LootPileBlockEntity loot,String sourceName,RandomSource random){var pos=loot.getBlockPos();String name=canonical(sourceName);if(!ready(level,name))throw new IllegalStateException("Incomplete real source pile dependencies for "+name);if(!loot.items().isEmpty())return;
        switch(name){
            case "LOOT_BOOKLET"->loot.add(LoreBookItem.create("beacon",12,0x404040,0xd637b3),0,0,0);
            case "LOOT_CAPNUKE"->{boolean nuke=random.nextInt(5)==0;loot.add(ammo(nuke?73:59),-.25,0,nuke?-.125:-.25);for(int i=0;i<4;i++)varied(loot,random,item("cap_nuka",2),.125,i*.03125,.25);for(int i=0;i<2;i++)varied(loot,random,item("syringe_metal_stimpak",1),-.25,i*.03125,.25);for(int i=0;i<6;i++)varied(loot,random,item("cap_nuka",2),.125,i*.03125,-.25);}
            case "LOOT_MEDICINE"->{for(int i=0;i<4;i++)varied(loot,random,draw(level,pos,"POOL_PILE_MED_SYRINGE",random),.125,i*.03125,.25);varied(loot,random,draw(level,pos,"POOL_PILE_MED_PILLS",random),-.25,0,-.125);}
            case "LOOT_CAPSTASH"->{for(int i=-1;i<=1;i++)for(int j=-1;j<=1;j++){int count=random.nextInt(5)+3;for(int k=0;k<count;k++)varied(loot,random,draw(level,pos,"POOL_PILE_CAPS",random),i*.3125,k*.03125,j*.3125);}}
            case "LOOT_MAKESHIFT_GUN"->{boolean r=random.nextBoolean();if(r)varied(loot,random,draw(level,pos,"POOL_PILE_MAKESHIFT_GUN",random),.125,.025,.25);if(!r||random.nextBoolean())varied(loot,random,draw(level,pos,"POOL_PILE_MAKESHIFT_WRENCH",random),-.25,0,-.28125);int count=random.nextInt(2)+1;for(int i=0;i<count;i++)varied(loot,random,draw(level,pos,"POOL_PILE_MAKESHIFT_PLATES",random),-.25,i*.03125,.3125);count=random.nextInt(2)+2;for(int i=0;i<count;i++)varied(loot,random,draw(level,pos,"POOL_PILE_MAKESHIFT_WIRE",random),.25,i*.03125,.1875);}
            case "LOOT_NUKE_STORAGE"->{for(int i=0;i<4;i++)for(int j=0;j<4;j++)if(random.nextBoolean())loot.add(draw(level,pos,"POOL_PILE_NUKE_STORAGE",random),-.375+i*.25,0,-.375+j*.25);}
            case "LOOT_METEOR"->MkuStructureLoot.populate(level,loot,random);
            case "LOOT_FLAREGUN"->{varied(loot,random,item("gun_flaregun",1),0,0,-.25);int count=random.nextInt(3)+2;for(int k=0;k<count;k++)varied(loot,random,ammo(50),-.25,k*.03125,.25);count=random.nextInt(1)+1;for(int k=0;k<count;k++)varied(loot,random,ammo(random.nextBoolean()?51:52),.25,k*.03125,.125);}
            default->{String pool;int limit;switch(name){case "LOOT_GLYPHID_HIVE"->{pool="POOL_PILE_HIVE";limit=random.nextInt(3)+3;}case "LOOT_SHIT"->{pool="POOL_PILE_OF_GARBAGE";limit=random.nextInt(3)+3;}case "LOOT_MECHANICAL"->{pool="POOL_PILE_MECHANICAL";limit=random.nextInt(6)+1;}case "LOOT_GEAR"->{pool="POOL_PILE_GEAR";limit=random.nextInt(6)+1;}case "LOOT_SUPPLIES"->{pool="POOL_PILE_SUPPLIES";limit=random.nextInt(3)+4;}default->{pool="POOL_PILE_BONES";limit=random.nextInt(3)+3;}}simplePile(loot,limit,random,()->draw(level,pos,pool,random));}
        }
        // applyLoot has source fall-through. If all16 nuclear grid booleans are false, its following bones branch actually fills the empty pile.
        if(name.equals("LOOT_NUKE_STORAGE")&&loot.items().isEmpty())populate(level,loot,"LOOT_BONES",random);
        loot.sync();
    }
    private StructureLootPresets(){}
}
