package com.hbm.client.wiki;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

/** 每个界面渲染帧导出一项；精确品红背景改为透明，Esc 可中止余下项目。 */
public final class WikiRenderScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int BACKGROUND = 0xFFFF00FF;
    private final List<ItemStack> preview;
    private final String prefix;
    private final String directory;
    private final int scale;
    private final Function<ItemStack, String> stackName;
    private int index;

    public WikiRenderScreen(List<ItemStack> stacks, String prefix, String directory, int scale) {
        this(stacks, prefix, directory, scale, stack -> stack.getHoverName().getString());
    }

    public WikiRenderScreen(List<ItemStack> stacks, String prefix, String directory, int scale,
            Function<ItemStack, String> stackName) {
        super(Component.translatable("screen.hbm.wiki_render"));
        this.preview = stacks.stream().map(ItemStack::copy).toList();
        this.prefix = prefix;
        this.directory = directory;
        this.scale = scale;
        this.stackName = stackName;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        graphics.fill(0, 0, width, height, BACKGROUND);
        graphics.flush();
        if (minecraft.player == null || minecraft.level == null || index >= preview.size()) {
            onClose();
            return;
        }

        ItemStack stack = preview.get(index);
        double guiScale = minecraft.getWindow().getGuiScale();
        graphics.pose().pushPose();
        try {
            // 原窗口的非取整 GUI 高度，保证物品下边距与截图的左下角坐标一致。
            // 抵消原生 GUI 物品中心150的放大偏移，同时保留三轴等比缩放与正常光照。
            graphics.pose().translate(9F * scale,
                    minecraft.getWindow().getHeight() / guiScale - 9D * scale, 150F * (1F - scale));
            graphics.pose().scale(scale, scale, scale);
            Lighting.setupFor3DItems();
            graphics.renderItem(stack, -8, -8);
            graphics.renderItemDecorations(font, stack, -8, -8);
            graphics.flush();
        } finally {
            graphics.pose().popPose();
            Lighting.setupFor3DItems();
        }

        try {
            // 保留源命名规则：移除颜色与非 ASCII 文件名字符，不添加前缀分隔符或时间戳。
            String name = stackName.apply(stack).replaceAll("§.", "").replaceAll("[^\\w ().-]+", "");
            if (!name.endsWith(".name")) {
                int zoom = (int) (scale * guiScale);
                saveScreenshot(minecraft.getMainRenderTarget(),
                        minecraft.gameDirectory.toPath().resolve(directory).resolve(prefix + name + ".png"),
                        zoom, zoom, zoom * 16, zoom * 16);
            }
        } catch (Exception ignored) {
            // 名称获取失败时跳过本项；保存异常由保存函数写入日志。
        }
        index++;
    }

    private static void saveScreenshot(RenderTarget target, Path file, int x, int y, int width, int height) {
        try (NativeImage frame = new NativeImage(target.width, target.height, false);
                NativeImage image = new NativeImage(width, height, true)) {
            // 复用原生 Screenshot 的纹理读取与翻转；保留源 framebuffer alpha，不强制改成255。
            RenderSystem.bindTexture(target.getColorTextureId());
            frame.downloadTexture(0, false);
            frame.flipY();
            // 原截图以 GL 左下角为原点；当前 NativeImage 已翻转为左上角。
            int top = frame.getHeight() - y - height;
            int leftInset = Math.max(0, -x);
            int topInset = Math.max(0, -top);
            int right = Math.min(width, frame.getWidth() - x);
            int bottom = Math.min(height, frame.getHeight() - top);
            if (right > leftInset && bottom > topInset) {
                frame.copyRect(image, x + leftInset, top + topInset, leftInset, topInset,
                        right - leftInset, bottom - topInset, false, false);
            }
            // 超出实际窗口的部分保持透明，画布与物品均不缩小；只去掉精确品红像素。
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    if (image.getPixelRGBA(column, row) == BACKGROUND) image.setPixelRGBA(column, row, 0);
                }
            }
            Files.createDirectories(file.getParent());
            image.writeToFile(file);
        } catch (IOException | RuntimeException exception) {
            LOGGER.warn("Failed to save NTM screenshot", exception);
        }
    }

    @Override
    public void onClose() {
        if (minecraft.player != null) minecraft.player.closeContainer();
        else minecraft.setScreen(null);
    }
}
