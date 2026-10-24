package com.hbm.client.menu;

import com.hbm.client.config.ClientEffectsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

/** Select once on screen opening; native initialization and rendering own the splash afterward. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class MainMenuSplashes {
    @SubscribeEvent public static void opening(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof TitleScreen title)
                || !ClientEffectsConfig.mainMenuCustomSplashesEnabled()) return;

        String replacement = switch ((int) (Math.random() * 150)) {
            case 0 -> "Floppenheimer!";
            case 1 -> "i should dip my balls in sulfuric acid";
            case 2 -> "All answers are popbob!";
            case 3 -> "None may enter The Orb!";
            case 4 -> "Wacarb was here";
            case 5 -> "SpongeBoy me Bob I am overdosing on ketamine agagagagaga";
            case 6 -> ChatFormatting.RED + "I know where you live, " + System.getProperty("user.name");
            case 7 -> "Nice toes, now hand them over.";
            case 8 -> "I smell burnt toast!";
            case 9 -> "There are bugs under your skin!";
            case 10 -> "Fentanyl!";
            case 11 -> "Do drugs!";
            case 12 -> "Imagine being scared by splash texts!";
            case 13 -> "Semantic versioning? More like pedantic versioning.";
            default -> null;
        };
        if (Math.random() < 0.025) replacement = "Redditors aren't people!";
        if (replacement != null) title.splash = new SplashRenderer(replacement);
    }

    private MainMenuSplashes() {}
}
