package com.hbm.client.oilflare;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.oilflare.FlareEntity;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
/** Live original fluid traits, display-only burning/venting operations; never synthetic crafting or fluid recipes. */
final class FlareViews {
    record Operation(HbmFluidType type,boolean burn,int amount,BigInteger power){ItemStack icon(){return FluidIconItem.make(type,amount);}}
    static List<Operation> operations(){var result=new ArrayList<Operation>();for(var type:HbmFluids.getAll()){if(type.hasTrait(FluidTraits.Flammable.class))result.add(new Operation(type,true,10,FlareEntity.production(type,10,0)));if(FlareEntity.gaseous(type))result.add(new Operation(type,false,50,BigInteger.ZERO));}return result;}
    static Component mode(Operation operation){return Component.translatable(operation.burn?"gui.hbm.flare.burning":"gui.hbm.flare.venting");}
    static Component efficiency(Operation operation){return Component.translatable("gui.hbm.flare.efficiency",operation.burn?(FlareEntity.gaseous(operation.type)?20:10):0);}
    static Component output(Operation operation){return Component.translatable("gui.hbm.flare.power",operation.power);}
    static Component rate(Operation operation){return Component.translatable("gui.hbm.flare.rate",operation.amount);}
}
