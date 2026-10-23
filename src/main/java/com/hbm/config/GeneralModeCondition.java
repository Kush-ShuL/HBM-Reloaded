package com.hbm.config;
import com.mojang.serialization.*;
import net.neoforged.neoforge.common.conditions.ICondition;
/** Whitelisted original GeneralConfig boolean grammar; recipe data cannot execute Java. */
public record GeneralModeCondition(String expression) implements ICondition {
    public static final MapCodec<GeneralModeCondition> CODEC=Codec.STRING.fieldOf("expression").xmap(GeneralModeCondition::new,GeneralModeCondition::expression);
    @Override public boolean test(IContext context){return evaluate(expression);}
    @Override public MapCodec<? extends ICondition> codec(){return CODEC;}
    public static boolean evaluate(String expression){var parser=new Parser(expression);boolean result=parser.or();parser.space();if(parser.at!=parser.text.length())throw new IllegalArgumentException("Unsupported source condition "+expression);return result;}
    private static boolean value(String token){if(token.startsWith("GeneralConfig."))token=token.substring(14);return switch(token){
        case "true"->true;case "false"->false;
        case "enable528","enable528Mode"->GeneralModes.mode528();case "enableLBSM","enableLessBullshitMode"->GeneralModes.lbsm();
        case "enableExpensiveMode"->GeneralModes.expensive();
        case "enableMekanismChanges"->GeneralModes.flag("mekanismRecipeChangesEnabled");
        case "enable528ReasimBoilers"->GeneralModes.forceReasimBoilers();
        case "enable528ColtanDeposit"->GeneralModes.coltanDeposit();case "enable528ColtanSpawn"->GeneralModes.coltanRandom();
        case "enable528BosniaSimulator"->GeneralModes.bosnia();case "enable528NetherBurn"->GeneralModes.netherBurn();case "enable528PressurizedRecipes"->GeneralModes.pressurizedRecipes();case "enable528ExplosiveEnergistics"->GeneralModes.explosiveEnergistics();
        case "enable528MachineGravity"->GeneralModes.mode528()&&GeneralModes.flag("X528_enable528MachineGravity");
        case "enableMachineGravity"->GeneralModes.flag("1.44_enableMachineGravity");
        case "enableLBSMShorterDecay"->GeneralModes.flag("LBSM_shortDecay");case "enableLBSMSimpleArmorRecipes"->GeneralModes.flag("LBSM_recipeSimpleArmor");case "enableLBSMSimpleToolRecipes"->GeneralModes.flag("LBSM_recipeSimpleTool");case "enableLBSMSimpleAlloy"->GeneralModes.flag("LBSM_recipeSimpleAlloy");case "enableLBSMSimpleChemsitry"->GeneralModes.flag("LBSM_recipeSimpleChemistry");case "enableLBSMSimpleCentrifuge"->GeneralModes.flag("LBSM_recipeSimpleCentrifuge");case "enableLBSMUnlockAnvil"->GeneralModes.flag("LBSM_recipeUnlockAnvil");case "enableLBSMSimpleCrafting"->GeneralModes.flag("LBSM_recipeSimpleCrafting");case "enableLBSMSimpleMedicineRecipes"->GeneralModes.flag("LBSM_recipeSimpleMedicine");case "enableLBSMSafeCrates"->GeneralModes.flag("LBSM_safeCrates");case "enableLBSMSafeMEDrives"->GeneralModes.flag("LBSM_safeMEDrives");default->throw new IllegalArgumentException("Unsupported GeneralConfig boolean "+token);
    };}
    private static final class Parser {
        final String text;int at;Parser(String text){this.text=text;}
        void space(){while(at<text.length()&&Character.isWhitespace(text.charAt(at)))at++;}
        boolean take(String token){space();if(text.startsWith(token,at)){at+=token.length();return true;}return false;}
        boolean or(){boolean result=and();while(take("||")){boolean next=and();result=result||next;}return result;}
        boolean and(){boolean result=atom();while(take("&&")){boolean next=atom();result=result&&next;}return result;}
        boolean atom(){if(take("!"))return !atom();if(take("(")){boolean result=or();if(!take(")"))throw new IllegalArgumentException("Unclosed GeneralConfig condition");return result;}space();int start=at;while(at<text.length()&&(Character.isJavaIdentifierPart(text.charAt(at))||text.charAt(at)=='.'))at++;if(start==at)throw new IllegalArgumentException("Invalid GeneralConfig boolean at "+at);return value(text.substring(start,at));}
    }
}
