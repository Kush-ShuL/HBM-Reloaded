package com.hbm.machine.radar;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual normal/large radar scanning and map-loading parameters in the shared machine spec. */
public final class RadarConfig {
    private static ModConfigSpec.IntValue capacityOption,consumptionOption,rangeOption,largeRangeOption,bufferOption,altitudeOption,chunkCapOption;
    private static ModConfigSpec.BooleanValue generationOption;
    public static volatile int maxPower=100000,consumption=500,range=1000,buffer=30,altitude=55,chunkLoadCap=10,largeRange=3000;
    public static volatile boolean generateChunks=false;

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("普通与大型雷达共享的储电、目标扫描和地图设置。").push("radar");
        capacityOption=builder.comment("两种雷达的最大储电量；单位：HE；范围：0～2147483647；储能仍按既有电池与电网流程处理。").defineInRange("energyCapacity",100000,0,Integer.MAX_VALUE);
        consumptionOption=builder.comment("达到最低高度后每次目标扫描的耗电，正常每刻扫描一次；单位：HE/次扫描；范围：0～2147483647；0免扫描耗电，地图读取仍按独立规则运行。").defineInRange("energyPerScan",500,0,Integer.MAX_VALUE);
        altitudeOption=builder.comment("目标扫描所需的雷达核心最低Y坐标，核心Y大于或等于此值才扫描；单位：方块高度；范围：-2147483648～2147483647，允许负Y。").defineInRange("minimumHeight",55,Integer.MIN_VALUE,Integer.MAX_VALUE);
        bufferOption=builder.comment("目标Y减核心Y必须严格大于此值才进入扫描列表；单位：方块；范围：-2147483648～2147483647；这是相对高度差，负值可包含核心下方目标。").defineInRange("targetHeightOffset",30,Integer.MIN_VALUE,Integer.MAX_VALUE);
        builder.comment("普通雷达范围。").push("normal");
        rangeOption=builder.comment("核心中心沿X、Z各方向的扫描距离，形成正方形水平扫描区，同时用于地图坐标和距离红石计算；单位：方块；范围：1～2147483647。").defineInRange("scanRange",1000,1,Integer.MAX_VALUE);
        builder.pop();
        builder.comment("大型雷达范围，其余运行参数与普通雷达共享。").push("large");
        largeRangeOption=builder.comment("核心中心沿X、Z各方向的扫描距离，形成正方形水平扫描区，同时用于地图坐标和距离红石计算；单位：方块；范围：1～2147483647。").defineInRange("scanRange",3000,1,Integer.MAX_VALUE);
        builder.pop();
        builder.comment("仅在雷达地图开启时使用；每刻仍绘制100像素，完整40000像素地图仍为400刻一轮。").push("map");
        chunkCapOption=builder.comment("每刻地图分支对未载入区块的成功加载次数上限，也限制同时未完成的已保存区块读取；单位：次/刻；范围：0～2147483647；0不发起新加载，已载入区块仍正常绘图。").defineInRange("chunkLoadsPerTick",10,0,Integer.MAX_VALUE);
        generationOption=builder.comment("地图是否允许生成尚不存在的区块；默认false先异步探查区块存档，未就绪则留待后续，确认数据存在后才走已有加载路径；true沿既有同步区块请求路径并允许生成缺失区块；不改变目标扫描或已保存地图。").define("allowChunkGeneration",false);
        builder.pop();builder.pop();
    }

    public static void apply(){
        maxPower=capacityOption.get();consumption=consumptionOption.get();altitude=altitudeOption.get();buffer=bufferOption.get();
        range=rangeOption.get();largeRange=largeRangeOption.get();chunkLoadCap=chunkCapOption.get();generateChunks=generationOption.get();
    }
    private RadarConfig(){}
}
