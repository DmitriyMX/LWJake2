package dmx.lwjake2.render;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@EqualsAndHashCode
public class PcxTexture {

    private static final int $PALETTE_SIZE = 48;
    private static final int $FILLER_SIZE = 58;

    private byte manufacturer;
    private byte version;
    private byte encoding;
    private byte bitsPerPixel;

    private int xMin;
    private int yMin;
    private int xMax;
    private int yMax;
    private int hRes;
    private int vRes;

    private byte[] palette = new byte[$PALETTE_SIZE];

    private byte reserved;
    private byte colorPlanes;
    private int bytesPerLine;
    private int paletteType;

    private byte[] filler = new byte[$FILLER_SIZE];

    private ByteBuffer data;

    private int height;
    private int width;

    public PcxTexture(ByteBuffer byteBuffer) {
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        header(byteBuffer);

        data = byteBuffer.slice();

        width = xMax - xMin + 1;
        height = yMax - yMin + 1;
    }

    public PcxTexture(byte[] bytes) {
        this(ByteBuffer.wrap(bytes));
    }

    public byte[] decode() {
        byte[] result = new byte[width * height];

        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; ) {
                byte dataByte = data.get();

                if ((dataByte & 0xC0) == 0xC0) {
                    int runLength = dataByte & 0x3F;
                    dataByte = data.get();
                    while (runLength-- > 0) {
                        result[count++] = dataByte;
                        x++;
                    }
                } else {
                    result[count++] = dataByte;
                    x++;
                }
            }
        }

        return result;
    }

    private int getUnsignedShort(ByteBuffer byteBuffer) {
        return byteBuffer.getShort() & 0xffff;
    }

    private void header(ByteBuffer byteBuffer) {
        manufacturer = byteBuffer.get();
        version = byteBuffer.get();
        encoding = byteBuffer.get();
        bitsPerPixel = byteBuffer.get();
        xMin = getUnsignedShort(byteBuffer);
        yMin = getUnsignedShort(byteBuffer);
        xMax = getUnsignedShort(byteBuffer);
        yMax = getUnsignedShort(byteBuffer);
        hRes = getUnsignedShort(byteBuffer);
        vRes = getUnsignedShort(byteBuffer);
        byteBuffer.get(palette);
        reserved = byteBuffer.get();
        colorPlanes = byteBuffer.get();
        bytesPerLine = getUnsignedShort(byteBuffer);
        paletteType = getUnsignedShort(byteBuffer);
        byteBuffer.get(filler);
    }

    public static boolean isValid(PcxTexture pcx) {
        return pcx.manufacturer != 0x0A
                || pcx.version != 5
                || pcx.encoding != 1
                || pcx.bitsPerPixel != 8
                || pcx.xMax >= 640
                || pcx.yMax >= 480;
    }
}
