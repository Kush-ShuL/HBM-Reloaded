package com.hbm.client.loadingtips;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/** The original public tip feed, fetched once without joining the render or world-loading thread. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class LoadingTipSource {
    private static final URI TIP_FEED=URI.create("https://gist.githubusercontent.com/HbmMods/a03c66ba160184e12f43de826b30c096/raw/tip_of_the_day");
    private static final AtomicBoolean REQUESTED=new AtomicBoolean();
    private static volatile List<String> tips=List.of();
    // Selection belongs to the installed renderer, not to individual native screens or loading stages.
    private static boolean selected;
    private static String selectedTip;

    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        if(LoadingTipsConfig.remoteTipsEnabled()&&REQUESTED.compareAndSet(false,true)) {
            Util.ioPool().execute(LoadingTipSource::readTips);
        }
    }

    private static void readTips() {
        HttpURLConnection connection=null;
        try {
            connection=(HttpURLConnection)TIP_FEED.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            var lines=new ArrayList<String>();
            try(var reader=new BufferedReader(new InputStreamReader(connection.getInputStream(),StandardCharsets.UTF_8))) {
                String line;
                while((line=reader.readLine())!=null)lines.add(line);
            }
            tips=List.copyOf(lines);
        } catch(IOException|RuntimeException exception) {
            LogUtils.getLogger().debug("Could not read loading tips; using the built-in tip",exception);
        } finally {
            if(connection!=null)connection.disconnect();
        }
    }

    /** Called only by the native render thread; a late response never rerolls an already selected tip. */
    public static String text() {
        if(!selected) {
            var available=tips;
            if(!available.isEmpty())selectedTip=available.get(new Random().nextInt(available.size()));
            selected=true;
        }
        var tip=selectedTip==null?Component.translatable("gui.hbm.loading_screen.fallback"):Component.literal(selectedTip);
        return Component.translatable("gui.hbm.loading_screen.tip",tip).getString();
    }

    private LoadingTipSource(){}
}
