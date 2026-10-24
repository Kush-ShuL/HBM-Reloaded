package com.hbm.machine.converter;

import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Actual converter ratios and input-buffer decay with the existing mandatory lossy cycle. */
public final class ConverterConfig {
    public record Settings(long heInput,long rfOutput,double heInputDecay,long rfInput,long heOutput,double rfInputDecay) {
        public Settings {
            if(heInput<=0||rfOutput<=0||rfInput<=0||heOutput<=0)throw new IllegalArgumentException("Converter input/output ratios must be positive");
            if(!Double.isFinite(heInputDecay)||heInputDecay<0||heInputDecay>1||!Double.isFinite(rfInputDecay)||rfInputDecay<0||rfInputDecay>1)throw new IllegalArgumentException("Converter input decay rates must be within0..1");
            if(BigInteger.valueOf(rfOutput).multiply(BigInteger.valueOf(heOutput)).compareTo(BigInteger.valueOf(heInput).multiply(BigInteger.valueOf(rfInput)))>=0)throw new IllegalArgumentException("HE/RF converters must lose energy over a round trip");
        }
    }
    public static final Settings SOURCE_DEFAULTS=new Settings(5,1,0,2,5,0);
    private static volatile Settings settings=SOURCE_DEFAULTS;
    private static ModConfigSpec.LongValue heInputOption,rfOutputOption,rfInputOption,heOutputOption;
    private static ModConfigSpec.DoubleValue heDecayOption,rfDecayOption;
    public static Settings settings(){return settings;}
    public static void configure(Settings values){settings=java.util.Objects.requireNonNull(values);}

    public static void define(ModConfigSpec.Builder builder){
        builder.comment("两种HE/RF转换器的转换比例和转换后输入余量衰减。RF在现有转换器FE桥接中按1RF=1FE处理。两方向组成的完整往返必须损失能量：rfOutput×heOutput严格小于heInput×rfInput；无损或增益组合整组六项使用默认5HE→1RF、2RF→5HE、衰减均0，配置文件不自动改写。").push("energyConverters");
        builder.comment("原生HE输入、RF/FE输出；每heInput份HE对应rfOutput份RF，保留现有整数分组与扣款向上取整。").push("heToRf");
        heInputOption=builder.comment("一组转换所需HE；单位：HE/组；范围：1～9223372036854775807；与同组rfOutput共同定义比例。").defineInRange("heInput",5L,1L,Long.MAX_VALUE);
        rfOutputOption=builder.comment("一组转换产出的RF/FE；单位：RF/组；范围：1～9223372036854775807；与同组heInput共同定义比例。").defineInRange("rfOutput",1L,1L,Long.MAX_VALUE);
        heDecayOption=builder.comment("转换后剩余HE输入缓存每刻损失的比例，保留乘剩余比例后向零截断；单位：比例；范围：0～1的有限数；0无额外衰减，1清空剩余输入，不影响已产RF。").defineInRange("residualInputDecay",0D,0D,1D);
        builder.pop();
        builder.comment("RF/FE输入、原生HE输出；每rfInput份RF对应heOutput份HE，保留现有整数分组。").push("rfToHe");
        rfInputOption=builder.comment("一组转换所需RF/FE；单位：RF/组；范围：1～9223372036854775807；与同组heOutput共同定义比例。").defineInRange("rfInput",2L,1L,Long.MAX_VALUE);
        heOutputOption=builder.comment("一组转换产出的HE；单位：HE/组；范围：1～9223372036854775807；与同组rfInput共同定义比例。").defineInRange("heOutput",5L,1L,Long.MAX_VALUE);
        rfDecayOption=builder.comment("转换后剩余RF输入缓存每刻移除的比例，移除量向上取整；单位：比例；范围：0～1的有限数；0无额外衰减，1清空剩余输入，不影响已产HE。").defineInRange("residualInputDecay",0D,0D,1D);
        builder.pop();builder.pop();
    }

    public static void apply(){
        try{configure(new Settings(heInputOption.get(),rfOutputOption.get(),heDecayOption.get(),rfInputOption.get(),heOutputOption.get(),rfDecayOption.get()));}
        catch(IllegalArgumentException error){settings=SOURCE_DEFAULTS;LogUtils.getLogger().error("Invalid HE/RF conversion combination in hbm-machine-settings.toml; keeping source defaults5HE:1RF /2RF:5HE with no residual decay",error);}
    }
    private ConverterConfig(){}
}
