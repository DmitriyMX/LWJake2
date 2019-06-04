package dmx.lwjake2.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lwjake2.render.msurface_t;

import static dmx.lwjake2.render.ImageType.SKIN;

@RequiredArgsConstructor
@Getter
@Setter
public class Q2Image {

    public static final int FREE_SEQUENCE = 0;

    /**
     * used to get the pos in array
     */
    private final int id;

    /**
     * game path, including extension
     */
    private String name;

    private ImageType type;

    // source image
    private int width;
    private int height;

    // after power of two and picmip
    private int uploadWidth;
    private int uploadHeight;

    private int registrationSequence;

    /**
     * for sort-by-texture world drawing
     */
    private msurface_t textureChain;

    /**
     * gl texture binding
     */
    private int texNum;

    // 0,0 - 1,1 unless part of the scrap
    private float sl;
    private float tl;
    private float sh;
    private float th;
    private boolean scrap;

    private boolean alpha;
    private boolean paletted;

    public void clear() {
        setName("");
        setType(SKIN);
        setWidth(0);
        setHeight(0);
        setUploadWidth(0);
        setUploadHeight(0);
        setRegistrationSequence(FREE_SEQUENCE);
        setTextureChain(null);
        setTexNum(0);
        setSl(0);
        setSh(0);
        setTl(0);
        setTh(0);
        setScrap(false);
        setAlpha(false);
        setPaletted(false);
    }
}
