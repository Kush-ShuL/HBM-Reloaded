package com.hbm.client.weapon.rifle;
import com.hbm.weapon.sedna.gun.rifle.RifleBallistics;
import com.hbm.client.weapon.pepperbox.SourceBulletStyles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RifleBallisticsClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{RifleBallistics.initialize();
 RifleBallistics.r556_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.r556_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.r556_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.r556_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
 RifleBallistics.r762_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.r762_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.r762_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.r762_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
 RifleBallistics.r762_du.setRenderer(SourceBulletStyles.RENDER_DU_BULLET);
 RifleBallistics.r762_he.setRenderer(SourceBulletStyles.RENDER_HE_BULLET);
 RifleBallistics.bmg50_sp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.bmg50_fmj.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.bmg50_jhp.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
 RifleBallistics.bmg50_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
 RifleBallistics.bmg50_du.setRenderer(SourceBulletStyles.RENDER_DU_BULLET);
 RifleBallistics.bmg50_he.setRenderer(SourceBulletStyles.RENDER_HE_BULLET);
 RifleBallistics.bmg50_sm.setRenderer(SourceBulletStyles.RENDER_SM_BULLET);
 RifleBallistics.r556_inc_sp.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
 RifleBallistics.r556_inc_fmj.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
 RifleBallistics.r556_inc_jhp.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
 RifleBallistics.r556_inc_ap.setRenderer(SourceBulletStyles.RENDER_AP_BULLET); });}
}
