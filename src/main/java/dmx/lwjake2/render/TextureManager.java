package dmx.lwjake2.render;

public class TextureManager {

    private static int[] colorTable = new int[256];

    public static void setColorPalette(int[] palette) {
        for (int i = 0; i < 256; i++) {
            colorTable[i] = palette[i] & 0x00FFFFFF;
        }
    }

    public static int getColorPalette(int index) {
        return colorTable[index];
    }
}
