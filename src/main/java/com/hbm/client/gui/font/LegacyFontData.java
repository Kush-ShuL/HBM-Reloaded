package com.hbm.client.gui.font;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/** Exact 1.7.10 FontRenderer asset interpretation; drawing and measured widths are distinct. */
public final class LegacyFontData {
    public static final String ASCII = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\0\0\0\0\0\0\0 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\0ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\0";
    public static final ResourceLocation ASCII_TEXTURE = id("textures/font/legacy/ascii.png");
    private final byte[] glyphSizes;
    private final int[] asciiWidths;
    private final Map<String, Boolean> localeModes;

    private LegacyFontData(byte[] sizes, int[] widths, Map<String, Boolean> modes) {
        glyphSizes = sizes;
        asciiWidths = widths;
        localeModes = Map.copyOf(modes);
    }

    public static LegacyFontData load(ResourceManager resources) throws IOException {
        byte[] sizes;
        try (var input = resources.open(id("legacy_font/glyph_sizes.bin"))) { sizes = input.readAllBytes(); }
        if (sizes.length != 65536) throw new IOException("Original glyph_sizes.bin must contain 65536 entries");
        int[] widths = new int[256];
        try (var input = resources.open(ASCII_TEXTURE); NativeImage image = NativeImage.read(input)) {
            int cellWidth = image.getWidth() / 16, cellHeight = image.getHeight() / 16;
            float factor = 8.0F / cellWidth;
            for (int character = 0; character < 256; character++) {
                int column = character % 16, row = character / 16;
                int right;
                for (right = cellWidth - 1; right >= 0; right--) {
                    boolean transparent = true;
                    for (int y = 0; y < cellHeight && transparent; y++) {
                        if ((image.getPixelRGBA(column * cellWidth + right, row * cellHeight + y) >>> 24) != 0) transparent = false;
                    }
                    if (!transparent) break;
                }
                widths[character] = (int) (0.5 + ++right * factor) + 1;
            }
        }
        Map<String, Boolean> modes = new HashMap<>();
        try (var reader = new InputStreamReader(resources.open(id("legacy_font/unicode_locales.json")), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (var entry : json.entrySet()) modes.put(entry.getKey(), entry.getValue().getAsBoolean());
        }
        return new LegacyFontData(sizes, widths, modes);
    }

    public boolean localeUsesUnicode(String locale) { return localeModes.getOrDefault(locale, false); }
    public int asciiWidth(int index) { return asciiWidths[index]; }
    public boolean supports(int character) {
        return character >= 0 && character < 65536 && (glyphSizes[character] != 0 || ASCII.indexOf(character) >= 0 || character == ' ');
    }

    public int measuredWidth(int character, boolean unicode) {
        if (character == '§') return -1;
        if (character == ' ') return 4;
        if (character < 0 || character >= 65536) return 0;
        int ascii = ASCII.indexOf(character);
        if (character > 0 && ascii >= 0 && !unicode) return asciiWidths[ascii];
        if (glyphSizes[character] == 0) return 0;
        // Preserve the original signed-byte >>> operation and the separate wide-glyph measurement rule.
        int left = glyphSizes[character] >>> 4;
        int right = glyphSizes[character] & 15;
        if (right > 7) { right = 15; left = 0; }
        return (++right - left) / 2 + 1;
    }

    public Glyph glyph(int character, boolean unicode) {
        if (character == ' ') return new Glyph(ASCII_TEXTURE, 0, 0, 0, 0, 0, 4, true, false);
        if (character < 0 || character >= 65536) return empty();
        int ascii = ASCII.indexOf(character);
        if (ascii >= 0 && !unicode) return asciiGlyph(ascii);
        if (glyphSizes[character] == 0) return empty();
        int left = glyphSizes[character] >>> 4;
        int right = glyphSizes[character] & 15;
        float leftEdge = left, rightEdge = right + 1;
        float x = character % 16 * 16 + leftEdge;
        float y = (character & 255) / 16 * 16;
        float width = rightEdge - leftEdge - 0.02F;
        ResourceLocation texture = id(String.format(java.util.Locale.ROOT, "textures/font/legacy/unicode_page_%02x.png", character / 256));
        return new Glyph(texture, x / 256F, (x + width) / 256F, y / 256F, (y + 15.98F) / 256F,
                width / 2.0F, (rightEdge - leftEdge) / 2.0F + 1.0F, false, true);
    }

    public Glyph asciiGlyph(int index) {
        float x = index % 16 * 8, y = index / 16 * 8;
        float width = asciiWidths[index] - 0.01F - 1.0F;
        return new Glyph(ASCII_TEXTURE, x / 128F, (x + width) / 128F, y / 128F, (y + 7.99F) / 128F,
                width, asciiWidths[index], false, false);
    }

    private static Glyph empty() { return new Glyph(ASCII_TEXTURE, 0, 0, 0, 0, 0, 0, true, true); }
    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }

    public record Glyph(ResourceLocation texture, float u0, float u1, float v0, float v1,
                        float drawWidth, float rawAdvance, boolean empty, boolean unicodeGlyph) { }
}
