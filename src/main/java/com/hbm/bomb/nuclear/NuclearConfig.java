package com.hbm.bomb.nuclear;

import java.util.LinkedHashMap;
import java.util.Map;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Source defaults and configurable modern TOM scheduling, with the new schema only. */
public final class NuclearConfig {
 public static final String FILE_NAME = "hbm-nuclear-common.toml";
 public static final ModConfigSpec SPEC;
 public static final Map<String,ModConfigSpec.IntValue> RADII=new LinkedHashMap<>();
 public static final ModConfigSpec.IntValue TICK_BUDGET,FALLOUT_DELAY,FALLOUT_RANGE,BLAST_SPEED,EXPLOSION_LIFESPAN,TOM_MIN_COLUMNS,TOM_ACCELERATION,TOM_MAX_COLUMNS,TOM_TICK_BUDGET,TOM_RESIDENT_CHUNKS;
 public static final ModConfigSpec.BooleanValue CHUNKLOADING,CRATER_BIOMES,MYCELIUM,CLEANUP_DEAD_DIRT,EXTENDED_LOGGING,DROP_CELL,DROP_SINGULARITY,DROP_CRYSTAL;
 public static final ModConfigSpec.DoubleValue CRATER_RADIATION,CRATER_INNER_RADIATION,CRATER_OUTER_RADIATION,CRATER_WATER_MULTIPLIER;
 static {
  var config=new ModConfigSpec.Builder();
  config.comment("Gadget 实验核装置：原半径参数，单位为方块。默认 150。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("gadget",config.defineInRange("warheads.gadget.radiusBlocks",150,0,Integer.MAX_VALUE/2));
  config.comment("小男孩核弹：原半径参数，单位为方块。默认 120。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("boy",config.defineInRange("warheads.littleBoy.radiusBlocks",120,0,Integer.MAX_VALUE/2));
  config.comment("胖子核弹（不是 M42 发射器）：原半径参数，单位为方块。默认 175。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("man",config.defineInRange("warheads.fatMan.radiusBlocks",175,0,Integer.MAX_VALUE/2));
  config.comment("常春藤迈克氢弹：原半径参数，单位为方块。默认 250。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("mike",config.defineInRange("warheads.ivyMike.radiusBlocks",250,0,Integer.MAX_VALUE/2));
  config.comment("沙皇氢弹：原半径参数，单位为方块。默认 500。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("tsar",config.defineInRange("warheads.tsarBomba.radiusBlocks",500,0,Integer.MAX_VALUE/2));
  config.comment("原型弹：原半径参数，单位为方块。默认 150。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("prototype",config.defineInRange("warheads.prototype.radiusBlocks",150,0,Integer.MAX_VALUE/2));
  config.comment("F.L.E.I.J.A. 弹：原半径参数，单位为方块。默认 50。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("fleija",config.defineInRange("warheads.fleija.radiusBlocks",50,0,Integer.MAX_VALUE/2));
  config.comment("蓝色洗礼弹：原半径参数，单位为方块。默认 150。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("solinium",config.defineInRange("warheads.blueRinse.radiusBlocks",150,0,Integer.MAX_VALUE/2));
  config.comment("N² 弹：原半径参数，单位为方块。默认 200。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("n2",config.defineInRange("warheads.n2.radiusBlocks",200,0,Integer.MAX_VALUE/2));
  config.comment("普通核导弹：原半径参数，单位为方块。默认 100。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("missile",config.defineInRange("warheads.nuclearMissile.radiusBlocks",100,0,Integer.MAX_VALUE/2));
  config.comment("分导式核弹头（每个子弹头）：原半径参数，单位为方块。默认 100。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("mirv",config.defineInRange("warheads.mirvWarhead.radiusBlocks",100,0,Integer.MAX_VALUE/2));
  config.comment("高爆迷你核弹的半径参数，单位：方块，默认 35。","Sedna M42 各弹种保持原固定参数；此值不会改变 Sedna 普通弹的 VNT 爆炸。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("fatman",config.defineInRange("warheads.m42MiniNuke.radiusBlocks",35,0,Integer.MAX_VALUE/2));
  config.comment("核子手榴弹：原半径参数，单位为方块。默认 25。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("nuka",config.defineInRange("warheads.nukaGrenade.radiusBlocks",25,0,Integer.MAX_VALUE/2));
  config.comment("落地反Sa326燃料／物品爆炸：原半径参数，单位为方块。默认 20。","设为 0 的效果取决于弹种，不作为通用禁用开关。");
  RADII.put("aSchrab",config.defineInRange("warheads.antiSchrabidiumDrop.radiusBlocks",20,0,Integer.MAX_VALUE/2));
  config.comment("单位：毫秒／世界／游戏刻。MK5 地形与沉降工作器共享的总 CPU 时间预算。","多个活跃工作器公平分配此总额；不是每枚核弹各占此时间。默认 50 毫秒。","不含实体伤害、声音等预算外工作；增大可能提高处理速度，也可能降低 TPS。");
  TICK_BUDGET=config.defineInRange("processing.mk5.terrainBudgetMilliseconds",50,1,1000);
  config.comment("是否允许核爆持有原中心区块票据并按实际地形处理需求加载／生成区块（MK3／MK5 共用）。","默认 true。关闭不改变爆炸半径或辐射剂量。");
  CHUNKLOADING=config.define("processing.chunkLoading",true);
  config.comment("单位：方块／游戏刻。原 MK3、旧式及施拉布相关爆炸的每刻处理上限。","默认 1024。此值不是 MK5 的时间预算，也不改变武器威力。");
  BLAST_SPEED=config.defineInRange("processing.mk3.blocksPerTick",1024,1,2147483647);
  config.comment("单位：现实秒。原 MK3 爆炸重新读档时，距保存的现实时间超过此值就终止。","默认 0：不启用此超时。不是爆炸动画寿命，也不是游戏刻数。");
  EXPLOSION_LIFESPAN=config.defineInRange("processing.mk3.maximumUnloadedSeconds",0,0,2147483647);
  config.comment("是否记录原核爆扩展诊断日志。默认 false，不改变地形或伤害。");
  EXTENDED_LOGGING=config.define("processing.extendedLogging",false);
  config.comment("单位：游戏刻（正常 20 TPS 时每刻约 0.05 秒）。沉降相邻计算批次的等待参数。","默认 4；0 为不增加等待。沉降仍在对应主爆炸地形处理完成后启动。");
  FALLOUT_DELAY=config.defineInRange("fallout.chunkDelayTicks",4,0,10000);
  config.comment("单位：百分比。乘在原始沉降范围公式上，100 保持原范围。","这是沉降的范围倍率，不是主爆炸半径或辐射剂量倍率；默认 100。");
  FALLOUT_RANGE=config.defineInRange("fallout.radiusPercent",100,0,10000);
  config.comment("核爆是否按原规则改变弹坑群系。默认 true；按方块边界记录，不改采样算法。");
  CRATER_BIOMES=config.define("terrain.craterBiomes",true);
  config.comment("是否启用原发光菌丝／废土菌丝的扩散规则。默认 false。");
  MYCELIUM=config.define("terrain.glowingMyceliumSpread",false);
  config.comment("死亡草地／菌丝是否按原随机刻逻辑退化为泥土，便于之后恢复植被。","默认 false；关闭时仍保留原有低光照退化条件。");
  CLEANUP_DEAD_DIRT=config.define("terrain.decayDeadGrassToDirt",false);
  config.comment("单位：RAD／秒。普通弹坑群系的环境辐射。默认 5.0；保留原可配置数值范围。");
  CRATER_RADIATION=config.defineInRange("craterRadiation.craterRadsPerSecond",5.0D,-Double.MAX_VALUE,Double.MAX_VALUE);
  config.comment("单位：RAD／秒。内层弹坑群系的环境辐射。默认 25.0。");
  CRATER_INNER_RADIATION=config.defineInRange("craterRadiation.innerRadsPerSecond",25.0D,-Double.MAX_VALUE,Double.MAX_VALUE);
  config.comment("单位：RAD／秒。外层弹坑群系的环境辐射。默认 0.5。");
  CRATER_OUTER_RADIATION=config.defineInRange("craterRadiation.outerRadsPerSecond",0.5D,-Double.MAX_VALUE,Double.MAX_VALUE);
  config.comment("无单位倍率。处于水、雨或气泡柱中时，以上弹坑群系环境辐射乘此值。","默认 5.0；不缩放其它辐射来源。");
  CRATER_WATER_MULTIPLIER=config.defineInRange("craterRadiation.waterMultiplier",5.0D,-Double.MAX_VALUE,Double.MAX_VALUE);
  config.comment("反物质燃料／反物质团及反Sa326燃料落地时，是否触发原爆炸效果。","默认 true。关闭只禁止爆炸；原物品落地后仍会被消耗，不会变成可安全丢弃物。");
  DROP_CELL=config.define("droppedItems.antimatterCellsExplode",true);
  config.comment("奇点、黑洞及对应精炼阶段物品落地时，是否产生其原黑洞／涡旋效果。","默认 true。关闭后物品落地仍按原规则被消耗。");
  DROP_SINGULARITY=config.define("droppedItems.singularitiesActivate",true);
  config.comment("异世界水晶落地时，是否触发原浮空与移动方块效果。默认 true。","关闭后物品落地仍按原规则被消耗。");
  DROP_CRYSTAL=config.define("droppedItems.xenCrystalsMoveBlocks",true);
  config.comment("每批起始至少处理的螺旋列数；只提高调度吞吐，不改变范围、坑形或随机采样。","默认512；实际工作仍受本世界共享时间预算和全部邻区FULL就绪限制。");
  TOM_MIN_COLUMNS=config.defineInRange("processing.tom.minimumColumnsPerBatch",512,1,1048576);
  config.comment("每完成一批后下一批增加的列数。默认128。","等待区块或耗尽时间片不会重复加速；部分列继续保存原进度。");
  TOM_ACCELERATION=config.defineInRange("processing.tom.accelerationColumnsPerBatch",128,1,1048576);
  config.comment("单批最多处理的列数。默认4096；上限优先于minimumColumnsPerBatch。","一次批次可以分摊到多个游戏刻，不会为凑满批次阻塞单个游戏刻。");
  TOM_MAX_COLUMNS=config.defineInRange("processing.tom.maximumColumnsPerBatch",4096,1,1048576);
  config.comment("单位：毫秒／游戏刻。TOM建筑机器人地形工作的自身CPU上限。默认35。","同时参与原共享核爆预算，二者取较小时间片；在逐Y层边界让出，不等待整列结束。");
  TOM_TICK_BUDGET=config.defineInRange("processing.tom.terrainBudgetMilliseconds",35,1,1000);
  config.comment("TOM每个建筑机器人保留的工作区块上限，默认1024。覆盖半径600外围螺旋的工作集，避免64区块缓存反复保存重读。","仍按实际列请求并检查全部邻区FULL就绪；不扩大爆破范围、不增加并发生成，不启动远程随机刻。");
  TOM_RESIDENT_CHUNKS=config.defineInRange("processing.tom.residentChunks",1024,64,4096);
  SPEC=config.build();
 }

 public static void register(ModContainer container) {
  container.registerConfig(ModConfig.Type.COMMON,SPEC,FILE_NAME);
 }

 public static int radius(String kind){return RADII.get(kind).get();}
 public static int schrabOreChance(){return com.hbm.config.GeneralModes.lbsm()?com.hbm.config.GeneralModes.schrabRate():250;}
 private NuclearConfig(){}
}
