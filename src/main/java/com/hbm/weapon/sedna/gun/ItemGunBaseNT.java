package com.hbm.weapon.sedna.gun;

import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import java.text.*;
import java.util.*;
import java.util.function.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Source ItemGunBaseNT state, key edges, equip/reset, wear and configurable callback graph. */
public class ItemGunBaseNT extends Item {
    public long[] lastShot; public double shotRand;
    public static final List<Item> secrets=new ArrayList<>();
    public final List<XWeaponModManager.ComparableStack> recognizedMods=new ArrayList<>();
    public ItemStack defaultAmmo; public boolean isDefaultExpensive;
    public static final DecimalFormat FORMAT_DMG=new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.US));
    public static float recoilVertical,recoilHorizontal,recoilDecay=.75F,recoilRebound=.25F,offsetVertical,offsetHorizontal,prevAimingProgress,aimingProgress;
    public static GunClientBridge CLIENT;
    public static void setupRecoil(float vertical,float horizontal,float decay,float rebound){recoilVertical+=vertical;recoilHorizontal+=horizontal;recoilDecay=decay;recoilRebound=rebound;}
    public static void setupRecoil(float vertical,float horizontal){setupRecoil(vertical,horizontal,.75F,.25F);}
    public static final String O_GUNCONFIG="O_GUNCONFIG_",KEY_DRAWN="drawn",KEY_AIMING="aiming",KEY_MODE="mode_",KEY_WEAR="wear_",KEY_TIMER="timer_",KEY_STATE="state_",KEY_PRIMARY="mouse1_",KEY_SECONDARY="mouse2_",KEY_TERTIARY="mouse3_",KEY_RELOAD="reload_",KEY_LASTANIM="lastanim_",KEY_ANIMTIMER="animtimer_",KEY_LOCKONTARGET="lockontarget",KEY_LOCKEDON="lockedon",KEY_CANCELRELOAD="cancel",KEY_EQUIPPED="eqipped";
    protected final GunConfig[] configs_DNA;
    public Function<ItemStack,String> LAMBDA_NAME_MUTATOR;
    public final WeaponQuality quality;
    private boolean builtInDualWield,pistolDualWield;
    public enum WeaponQuality { A_SIDE,B_SIDE,LEGENDARY,SPECIAL,UTILITY,SECRET,DEBUG }
    public enum GunState { DRAWING,IDLE,COOLDOWN,RELOADING,JAMMED }
    public ItemGunBaseNT(WeaponQuality quality,GunConfig... configs){super(new Properties().stacksTo(1));this.quality=quality;builtInDualWield=configs.length>1;configs_DNA=configs;lastShot=new long[configs.length];for(int i=0;i<configs.length;i++)configs[i].index=i;if(quality==WeaponQuality.LEGENDARY||quality==WeaponQuality.SECRET)secrets.add(this);}
    public GunConfig getConfig(ItemStack stack,int index){var config=configs_DNA[index];return stack==null?config:XWeaponModManager.eval(config,stack,O_GUNCONFIG+index,this,index);}
    public int getConfigCount(){return configs_DNA.length;}
    /** X5846's six multi-config factories are all source isAkimbo renderers; receivers/modes never imply this flag. */
    public boolean isBuiltInDualWield(){return builtInDualWield;}
    public boolean supportsPistolDualWield(){return pistolDualWield&&!builtInDualWield;}
    public ItemGunBaseNT pistolDualWield(boolean value){pistolDualWield=value;return this;}
    public ItemGunBaseNT builtInDualWield(boolean value){builtInDualWield=value;return this;}
    public ItemGunBaseNT setDefaultAmmo(GunAmmo ammo,int count){defaultAmmo=ammo.toStack();defaultAmmo.setCount(count);return this;}
    public ItemGunBaseNT setDefaultAmmoExpensive(GunAmmo ammo,int count){isDefaultExpensive=true;return setDefaultAmmo(ammo,count);}
    public ItemGunBaseNT setNameMutator(Function<ItemStack,String> mutator){LAMBDA_NAME_MUTATOR=mutator;return this;}
    @Override public Component getName(ItemStack stack){if(LAMBDA_NAME_MUTATOR!=null){String name=LAMBDA_NAME_MUTATOR.apply(stack);if(name!=null)return Component.translatable(name+".name");}return Component.translatable("item."+net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).getPath()+".name");}
    public void handleKeybind(LivingEntity entity,Container inventory,ItemStack stack,GunKey key,boolean next){
        if(!GunSettings.enabled())return;
        for(int i=0;i<configs_DNA.length;i++){
            var config=getConfig(stack,i);var context=new LambdaContext(config,entity,inventory,i);
            boolean old=switch(key){case PRIMARY->getPrimary(stack,i);case SECONDARY->getSecondary(stack,i);case TERTIARY->getTertiary(stack,i);case RELOAD->getReloadKey(stack,i);};
            if(next==old)continue;
            BiConsumer<ItemStack,LambdaContext> callback=switch(key){case PRIMARY->next?config.getPressPrimary(stack):config.getReleasePrimary(stack);case SECONDARY->next?config.getPressSecondary(stack):config.getReleaseSecondary(stack);case TERTIARY->next?config.getPressTertiary(stack):config.getReleaseTertiary(stack);case RELOAD->next?config.getPressReload(stack):config.getReleaseReload(stack);};
            if(callback!=null)try(var active=GunHands.active(stack)){callback.accept(stack,context);}
            switch(key){case PRIMARY->setPrimary(stack,i,next);case SECONDARY->setSecondary(stack,i,next);case TERTIARY->setTertiary(stack,i,next);case RELOAD->setReloadKey(stack,i,next);}
        }
    }
    public void onEquip(Player player,ItemStack stack){for(int i=0;i<configs_DNA.length;i++){if(getLastAnim(stack,i)==GunAnimation.EQUIP&&getAnimTimer(stack,i)<5)continue;playAnimation(player,stack,GunAnimation.EQUIP,i);setPrimary(stack,i,false);setSecondary(stack,i,false);setTertiary(stack,i,false);setReloadKey(stack,i,false);}}
    public static void playAnimation(Player player,ItemStack stack,GunAnimation type,int index){if(player instanceof ServerPlayer server)GunNetwork.animation(server,stack,type,0,index);setLastAnim(stack,index,type);setAnimTimer(stack,index,0);}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean held){
        if(!(entity instanceof LivingEntity living))return;Player player=entity instanceof Player p?p:null;
        if(player!=null)held=player.isAlive()&&!player.isSpectator()&&GunSettings.enabled()&&(stack==player.getMainHandItem()||stack==player.getOffhandItem()&&GunHands.operable(player,net.minecraft.world.InteractionHand.OFF_HAND));
        if(player!=null&&GunHands.dual(player)&&getIsAiming(stack))setIsAiming(stack,false);
        GunConfig[] configs=new GunConfig[configs_DNA.length];LambdaContext[] contexts=new LambdaContext[configs.length];
        for(int i=0;i<configs.length;i++){configs[i]=getConfig(stack,i);contexts[i]=new LambdaContext(configs[i],living,player==null?null:new GunInventory(player),i);}
        if(level.isClientSide){
            if(held&&CLIENT!=null&&player==CLIENT.localPlayer())try(var visual=GunVisualState.bind(stack);var active=GunHands.active(stack)){
                if(stack==GunHands.viewStack(player)){prevAimingProgress=aimingProgress;boolean aiming=getIsAiming(stack);if(aiming&&aimingProgress<1)aimingProgress+=.25F;if(!aiming&&aimingProgress>0)aimingProgress-=.25F;aimingProgress=Mth.clamp(aimingProgress,0F,1F);}
                for(int i=0;i<configs.length;i++){var smoke=configs[i].getSmokeHandler(stack);if(smoke!=null)smoke.accept(stack,contexts[i]);}
                for(int i=0;i<configs.length;i++){var orchestra=configs[i].getOrchestra(stack);if(orchestra!=null)orchestra.accept(stack,contexts[i]);}
            }return;
        }
        if(player!=null&&!getIsEquipped(stack)&&held)onEquip(player,stack);setIsEquipped(stack,held);
        if(!held){GunHands.clearControls(stack);for(int i=0;i<configs.length;i++){if(getState(stack,i)!=GunState.JAMMED){setState(stack,i,GunState.DRAWING);setTimer(stack,i,configs[i].getDrawDuration(stack));}setLastAnim(stack,i,GunAnimation.CYCLE);}setIsAiming(stack,false);setReloadCancel(stack,false);return;}
        for(int i=0;i<configs.length;i++)for(int k=0;k==0||(k<2&&GunArmorPerks.isTrenchMaster(player)&&getState(stack,i)==GunState.RELOADING);k++){
            var orchestra=configs[i].getOrchestra(stack);if(orchestra!=null)try(var active=GunHands.active(stack)){orchestra.accept(stack,contexts[i]);}setAnimTimer(stack,i,getAnimTimer(stack,i)+1);
            int timer=getTimer(stack,i);if(timer>0)setTimer(stack,i,timer-1);if(timer<=1)configs[i].getDecider(stack).accept(stack,contexts[i]);
        }
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        var player=CLIENT==null?null:CLIENT.localPlayer();
        for(int i=0;i<configs_DNA.length;i++){
            var config=getConfig(stack,i);for(var rec:config.getReceivers(stack)){
                var mag=rec.getMagazine(stack);var ammo=mag.getIconForHUD(stack,player);tooltip.add(Component.translatable("gui.weapon.ammo").append(": ").append(ammo.getHoverName()).append(" "+mag.reportAmmoStateForHUD(stack,player)));
                float damage=rec.getBaseDamage(stack);tooltip.add(Component.translatable("gui.weapon.baseDamage").append(": "+FORMAT_DMG.format(damage)));
                if(mag.getType(stack,player==null?null:new GunInventory(player)) instanceof com.hbm.weapon.sedna.BulletConfig bullet){int min=(int)(bullet.projectilesMin*rec.getSplitProjectiles(stack)),max=(int)(bullet.projectilesMax*rec.getSplitProjectiles(stack));tooltip.add(Component.translatable("gui.weapon.damageWithAmmo").append(": "+FORMAT_DMG.format(damage*bullet.damageMult)+(min>1?" x"+(min!=max?min+"-"+max:min):"")));}
            }
            float durability=config.getDurability(stack);if(durability>0)tooltip.add(Component.translatable("gui.weapon.condition").append(": "+Mth.clamp((int)((durability-getWear(stack,i))*100/durability),0,100)+"%"));
            for(var mod:XWeaponModManager.getUpgradeItems(stack,i))if(!mod.isEmpty())tooltip.add(mod.getHoverName().copy().withStyle(ChatFormatting.YELLOW));
        }
        boolean blink=System.currentTimeMillis()%1000<500;ChatFormatting color=switch(quality){case A_SIDE->ChatFormatting.YELLOW;case B_SIDE->ChatFormatting.GOLD;case LEGENDARY->ChatFormatting.RED;case SPECIAL->ChatFormatting.AQUA;case UTILITY->ChatFormatting.GREEN;case SECRET->blink?ChatFormatting.DARK_RED:ChatFormatting.RED;case DEBUG->blink?ChatFormatting.YELLOW:ChatFormatting.GOLD;};
        tooltip.add(Component.translatable("gui.weapon.quality."+switch(quality){case A_SIDE->"aside";case B_SIDE->"bside";default->quality.name().toLowerCase(Locale.ROOT);}).withStyle(color));
        if(CLIENT!=null&&CLIENT.isWeaponTable()&&!recognizedMods.isEmpty()){tooltip.add(Component.translatable("gui.weapon.accepts").append(":").withStyle(ChatFormatting.RED));for(var mod:recognizedMods)tooltip.add(Component.literal("  ").append(mod.toStack().getHoverName()).withStyle(ChatFormatting.RED));}
    }
    public static int getValueInt(ItemStack stack,String name){return GunStackData.data(stack).getInt(name);}
    public static void setValueInt(ItemStack stack,String name,int value){GunStackData.update(stack,data->data.putInt(name,value));}
    public static float getValueFloat(ItemStack stack,String name){return GunStackData.data(stack).getFloat(name);}
    public static void setValueFloat(ItemStack stack,String name,float value){GunStackData.update(stack,data->data.putFloat(name,value));}
    public static byte getValueByte(ItemStack stack,String name){return GunStackData.data(stack).getByte(name);}
    public static void setValueByte(ItemStack stack,String name,byte value){GunStackData.update(stack,data->data.putByte(name,value));}
    public static boolean getValueBool(ItemStack stack,String name){return GunStackData.data(stack).getBoolean(name);}
    public static void setValueBool(ItemStack stack,String name,boolean value){GunStackData.update(stack,data->data.putBoolean(name,value));}
    public static final class LambdaContext {
        public final GunConfig config;public final LivingEntity entity;public final Container inventory;public final int configIndex;
        public LambdaContext(GunConfig config,LivingEntity entity,Container inventory,int configIndex){this.config=config;this.entity=entity;this.inventory=inventory;this.configIndex=configIndex;}
        public Player getPlayer(){return entity instanceof Player p?p:null;}
    }
    public static final class SmokeNode {public double forward,side,lift,alpha,width=1;public SmokeNode(double alpha){this.alpha=alpha;}}
	// GUN DRAWN //
	public static boolean getIsDrawn(ItemStack stack) { return getValueBool(stack, KEY_DRAWN); }
	public static void setIsDrawn(ItemStack stack, boolean value) { setValueBool(stack, KEY_DRAWN, value); }
	// GUN STATE TIMER //
	public static int getTimer(ItemStack stack, int index) { return getValueInt(stack, KEY_TIMER + index); }
	public static void setTimer(ItemStack stack, int index, int value) { setValueInt(stack, KEY_TIMER + index, value); }
	// GUN STATE //
	public static GunState getState(ItemStack stack, int index) { return safeEnum(GunState.values(), getValueByte(stack, KEY_STATE + index)); }
	public static void setState(ItemStack stack, int index, GunState value) { setValueByte(stack, KEY_STATE + index, (byte) value.ordinal()); }
	// GUN MODE //
	public static int getMode(ItemStack stack, int index) { return getValueInt(stack, KEY_MODE + index); }
	public static void setMode(ItemStack stack, int index, int value) { setValueInt(stack, KEY_MODE + index, value); }
	// GUN AIMING //
	public static boolean getIsAiming(ItemStack stack) { return getValueBool(stack, KEY_AIMING); }
	public static void setIsAiming(ItemStack stack, boolean value) { setValueBool(stack, KEY_AIMING, value); }
	// GUN AIMING //
	public static float getWear(ItemStack stack, int index) { return getValueFloat(stack, KEY_WEAR + index); }
	public static void setWear(ItemStack stack, int index, float value) { setValueFloat(stack, KEY_WEAR + index, value); }
	// LOCKON //
	public static int getLockonTarget(ItemStack stack) { return getValueInt(stack, KEY_LOCKONTARGET); }
	public static void setLockonTarget(ItemStack stack, int value) { setValueInt(stack, KEY_LOCKONTARGET, value); }
	public static boolean getIsLockedOn(ItemStack stack) { return getValueBool(stack, KEY_LOCKEDON); }
	public static void setIsLockedOn(ItemStack stack, boolean value) { setValueBool(stack, KEY_LOCKEDON, value); }
	// ANIM TRACKING //
	public static GunAnimation getLastAnim(ItemStack stack, int index) { return safeEnum(GunAnimation.values(), getValueInt(stack, KEY_LASTANIM + index)); }
	public static void setLastAnim(ItemStack stack, int index, GunAnimation value) { setValueInt(stack, KEY_LASTANIM + index, value.ordinal()); }
	public static int getAnimTimer(ItemStack stack, int index) { return getValueInt(stack, KEY_ANIMTIMER + index); }
	public static void setAnimTimer(ItemStack stack, int index, int value) { setValueInt(stack, KEY_ANIMTIMER + index, value); }

	// BUTTON STATES //
	public static boolean getPrimary(ItemStack stack, int index) { return getValueBool(stack, KEY_PRIMARY + index); }
	public static void setPrimary(ItemStack stack, int index, boolean value) { setValueBool(stack, KEY_PRIMARY + index, value); }
	public static boolean getSecondary(ItemStack stack, int index) { return getValueBool(stack, KEY_SECONDARY + index); }
	public static void setSecondary(ItemStack stack, int index, boolean value) { setValueBool(stack, KEY_SECONDARY + index, value); }
	public static boolean getTertiary(ItemStack stack, int index) { return getValueBool(stack, KEY_TERTIARY + index); }
	public static void setTertiary(ItemStack stack, int index, boolean value) { setValueBool(stack, KEY_TERTIARY + index, value); }
	public static boolean getReloadKey(ItemStack stack, int index) { return getValueBool(stack, KEY_RELOAD + index); }
	public static void setReloadKey(ItemStack stack, int index, boolean value) { setValueBool(stack, KEY_RELOAD + index, value); }
	// RELOAD CANCEL //
	public static boolean getReloadCancel(ItemStack stack) { return getValueBool(stack, KEY_CANCELRELOAD); }
	public static void setReloadCancel(ItemStack stack, boolean value) { setValueBool(stack, KEY_CANCELRELOAD, value); }
	// EQUIPPED //
	public static boolean getIsEquipped(ItemStack stack) { return getValueBool(stack, KEY_EQUIPPED); }
	public static void setIsEquipped(ItemStack stack, boolean value) { setValueBool(stack, KEY_EQUIPPED, value); }

    private static <E> E safeEnum(E[] values,int index){return values[Math.abs(index%values.length)];}
}
