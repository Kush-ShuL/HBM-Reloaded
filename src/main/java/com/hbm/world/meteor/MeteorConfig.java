package com.hbm.world.meteor;

import com.hbm.config.WorldgenControls;
import java.util.*;
import net.minecraft.util.RandomSource;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Real source meteor settings plus explicit mineral/shape controls; all defaults preserve the source generator's random calls. */
public final class MeteorConfig {
 public static final ModConfigSpec SPEC;
 public static final ModConfigSpec.BooleanValue ENABLED,SHOWERS,TAILS,SPECIAL;
 public static final ModConfigSpec.BooleanValue WORLDGEN_ENABLED,WORLDGEN_OTHER_DIMENSIONS;
 public static final ModConfigSpec.IntValue WORLDGEN_CHANCE;
 public static final ModConfigSpec.IntValue STRIKE_CHANCE,SHOWER_CHANCE,SHOWER_DURATION,SPECIAL_CHANCE,SPAWN_HEIGHT_OFFSET,SPAWN_RADIUS;
 private static final List<ModConfigSpec.IntValue> ORES=new ArrayList<>(),SHAPES=new ArrayList<>();
 private static final Map<Integer,ModConfigSpec.BooleanValue> CASES=new HashMap<>();
 static {
  var b=new ModConfigSpec.Builder();
  ENABLED=b.define(java.util.List.of("naturalStrikes","enabled"),true);SHOWERS=b.define(java.util.List.of("showers","enabled"),true);TAILS=b.define(java.util.List.of("display","tailsEnabled"),true);
  STRIKE_CHANCE=b.comment("自然陨石事件的随机间隔参数，单位：游戏刻，默认相当于 20 TPS 下的 5 小时；0 关闭自然坠落。").defineInRange(java.util.List.of("naturalStrikes","chanceDenominator"),20*60*60*5,0,10_000_000);
  SHOWER_CHANCE=b.defineInRange(java.util.List.of("showers","chanceDenominator"),20*60*15,0,10_000_000);SHOWER_DURATION=b.defineInRange(java.util.List.of("showers","durationTicks"),20*60*30,0,10_000_000);
  SPECIAL=b.define(java.util.List.of("specialMeteors","enabled"),true);SPECIAL_CHANCE=b.comment("特殊陨石类型的随机选择分母，默认 300；其中 12 个结果为特殊类型，其余为普通分层陨石。0 禁用特殊类型。").defineInRange(java.util.List.of("specialMeteors","chanceDenominator"),300,0,10_000_000);
  SPAWN_HEIGHT_OFFSET=b.comment("陨石生成位置高于维度建造上限的距离，单位：方块，默认 128。").defineInRange(java.util.List.of("naturalStrikes","heightAboveWorld"),128,4,4096);
  SPAWN_RADIUS=b.comment("陨石生成点相对所选玩家的水平随机偏移范围，包含端点，单位：方块。").defineInRange(java.util.List.of("naturalStrikes","horizontalRadius"),100,0,4096);
  b.push("specialCases");for(int i:new int[]{0,1,2,3,4,5,6,7,8,9,10,12})CASES.put(i,b.define("case"+i,true));b.pop().push("oreWeights");
  for(String name:List.of("IRON","COPPER","ALUMINIUM","RAREEARTH","COBALT"))ORES.add(b.defineInRange(name.equals("RAREEARTH")?"rareEarth":name.toLowerCase(Locale.ROOT),1,0,1024));b.pop().push("shapeWeights");
  for(String name:List.of("large","medium","small"))SHAPES.add(b.defineInRange(name,1,0,1024));b.pop();
  b.push("worldGeneration");
  WORLDGEN_ENABLED=b.comment("在新生成区块中生成原版已落地、部分埋藏的陨石；与天降陨石独立。").define("enabled",true);
  WORLDGEN_CHANCE=b.comment("每个新区块生成概率的分母。原版实际配置默认 200，即 1/200；0 关闭。").defineInRange("chanceDenominator",200,0,10_000_000);
  WORLDGEN_OTHER_DIMENSIONS=b.comment("沿用原版 enableOresInModdedDimensions 默认 true：也允许主世界之外的普通维度，始终排除下界和末地。").define("allowOtherDimensions",true);
  b.pop();SPEC=b.build();
 }
 public static boolean enabled(){return WorldgenControls.ores()&&ENABLED.get();}
 public static boolean special(int value){var flag=CASES.get(value);return flag!=null&&flag.get();}
 public static int oreWeight(int metadata){return ORES.get(metadata).get();}
 public static int shape(RandomSource random){int total=SHAPES.stream().mapToInt(value->value.get()).sum();if(total==0)return-1;int roll=random.nextInt(total);for(int i=0;i<SHAPES.size();i++)if((roll-=SHAPES.get(i).get())<0)return i;throw new IllegalStateException("Invalid meteor shape weights");}
 public static void register(ModContainer container){container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-meteors-common.toml");}
 private MeteorConfig(){}
}
