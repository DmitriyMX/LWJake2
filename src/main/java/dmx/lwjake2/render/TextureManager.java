package dmx.lwjake2.render;

import dmx.lwjake2.UnpackLoader;
import lombok.extern.slf4j.Slf4j;

import static lwjake2.render.lwjgl.Main.d_8to24table;

@Slf4j
public class TextureManager {

    private static final String $PALETTE_PATH = "pics/colormap.pcx";
    private static int[] colorTable;

    public static void LoadPalette() {
        byte[] raw = UnpackLoader.loadFile($PALETTE_PATH);

        if (raw == null) {
            log.error("Error load palette");
            return;
        }

        byte[] pal = new byte[768];
        System.arraycopy(raw, raw.length - 768, pal, 0, 768);

        int r, g, b;
        int j = 0;
        for (int i = 0; i < 256; i++) {
            r = pal[j++] & 0xFF;
            g = pal[j++] & 0xFF;
            b = pal[j++] & 0xFF;

            d_8to24table[i] = (255 << 24) | (b << 16) | (g << 8) | (r);
        }

        d_8to24table[255] &= 0x00FFFFFF; // 255 is transparent

        setColorPalette(d_8to24table);
    }

    private static void setColorPalette(int[] palette) {
        if (colorTable != null) {
            return;
        }

        colorTable = new int[256];
        for (int i = 0; i < 256; i++) {
            colorTable[i] = palette[i] & 0x00FFFFFF;
        }
    }

    public static int getColorPalette(int index) {
        return colorTable[index];
    }
}
