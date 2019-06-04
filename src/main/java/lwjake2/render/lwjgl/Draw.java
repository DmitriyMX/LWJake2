/*
 * Copyright (C) 1997-2001 Id Software, Inc.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package lwjake2.render.lwjgl;

import lombok.extern.slf4j.Slf4j;
import lwjake2.Defines;
import lwjake2.ErrorCode;
import lwjake2.client.VID;
import lwjake2.qcommon.Com;
import dmx.lwjake2.render.Q2Image;
import lwjake2.util.Lib;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import static dmx.lwjake2.render.ImageType.PICTURE;

/**
 * Draw
 * (gl_draw.c)
 * 
 * @author cwei
 */
@Slf4j
public abstract class Draw extends Image {

    /*
    ===============
    Draw_InitLocal
    ===============
    */
    void Draw_InitLocal() {
        // load console characters (don't bilerp characters)
        draw_chars = GL_FindImage("pics/conchars.pcx", PICTURE);
        GL_Bind(draw_chars.getTexNum());
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    /*
    ================
    Draw_Char

    Draws one 8*8 graphics character with 0 being transparent.
    It can be clipped to the top of the screen to allow the console to be
    smoothly scrolled off.
    ================
    */
    protected void Draw_Char(int x, int y, int num) {

        num &= 255;
    
        if ( (num&127) == 32 ) return; // space

        if (y <= -8) return; // totally off screen

        int row = num>>4;
        int col = num&15;

        float frow = row*0.0625f;
        float fcol = col*0.0625f;
        float size = 0.0625f;

        GL_Bind(draw_chars.getTexNum());

        GL11.glBegin (GL11.GL_QUADS);
        GL11.glTexCoord2f (fcol, frow);
        GL11.glVertex2f (x, y);
        GL11.glTexCoord2f (fcol + size, frow);
        GL11.glVertex2f (x+8, y);
        GL11.glTexCoord2f (fcol + size, frow + size);
        GL11.glVertex2f (x+8, y+8);
        GL11.glTexCoord2f (fcol, frow + size);
        GL11.glVertex2f (x, y+8);
        GL11.glEnd ();
    }


    /*
    =============
    Draw_FindPic
    =============
    */
    protected Q2Image Draw_FindPic(String name) {
        Q2Image image = null;
        String fullname;

        if (!name.startsWith("/") && !name.startsWith("\\"))
        {
            fullname = "pics/" + name + ".pcx";
            image = GL_FindImage(fullname, PICTURE);
        } else {
            image = GL_FindImage(name.substring(1), PICTURE);
        }
        return image;
    }


    /*
    =============
    Draw_GetPicSize
    =============
    */
    protected void Draw_GetPicSize(Dimension dim, String pic)    {

        Q2Image image = Draw_FindPic(pic);
        dim.width = (image != null) ? image.getWidth() : -1;
        dim.height = (image != null) ? image.getHeight() : -1;
    }

    /*
    =============
    Draw_StretchPic
    =============
    */
    protected void Draw_StretchPic (int x, int y, int w, int h, String pic) {
        
        Q2Image image;

        image = Draw_FindPic(pic);
        if (image == null)
        {
            log.warn("Can't find pic: {}", pic);
            return;
        }

        if (scrap_dirty)
            Scrap_Upload();

        if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0) ) && !image.isAlpha())
            GL11.glDisable(GL11.GL_ALPHA_TEST);

        GL_Bind(image.getTexNum());
        GL11.glBegin (GL11.GL_QUADS);
        GL11.glTexCoord2f (image.getSl(), image.getTl());
        GL11.glVertex2f (x, y);
        GL11.glTexCoord2f (image.getSh(), image.getTl());
        GL11.glVertex2f (x+w, y);
        GL11.glTexCoord2f (image.getSh(), image.getTh());
        GL11.glVertex2f (x+w, y+h);
        GL11.glTexCoord2f (image.getSl(), image.getTh());
        GL11.glVertex2f (x, y+h);
        GL11.glEnd ();

        if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) !=0 ) ) && !image.isAlpha())
            GL11.glEnable(GL11.GL_ALPHA_TEST);
    }


    /*
    =============
    Draw_Pic
    =============
    */
    protected void Draw_Pic(int x, int y, String pic)
    {
        Q2Image image;

        image = Draw_FindPic(pic);
        if (image == null)
        {
            log.warn("Can't find pic: {}", pic);
            return;
        }
        if (scrap_dirty)
            Scrap_Upload();

        if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) && !image.isAlpha())
            GL11.glDisable (GL11.GL_ALPHA_TEST);

        GL_Bind(image.getTexNum());

        GL11.glBegin (GL11.GL_QUADS);
        GL11.glTexCoord2f (image.getSl(), image.getTl());
        GL11.glVertex2f (x, y);
        GL11.glTexCoord2f (image.getSh(), image.getTl());
        GL11.glVertex2f (x+image.getWidth(), y);
        GL11.glTexCoord2f (image.getSh(), image.getTh());
        GL11.glVertex2f (x+image.getWidth(), y+ image.getHeight());
        GL11.glTexCoord2f (image.getSl(), image.getTh());
        GL11.glVertex2f (x, y+image.getHeight());
        GL11.glEnd ();

        if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.isAlpha())
            GL11.glEnable (GL11.GL_ALPHA_TEST);
    }

    /*
    =============
    Draw_TileClear

    This repeats a 64*64 tile graphic to fill the screen around a sized down
    refresh window.
    =============
    */
    protected void Draw_TileClear(int x, int y, int w, int h, String pic) {
        Q2Image image;

        image = Draw_FindPic(pic);
        if (image == null)
        {
            log.warn("Can't find pic: {}", pic);
            return;
        }

        if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.isAlpha())
            GL11.glDisable(GL11.GL_ALPHA_TEST);

        GL_Bind(image.getTexNum());
        GL11.glBegin (GL11.GL_QUADS);
        GL11.glTexCoord2f(x/64.0f, y/64.0f);
        GL11.glVertex2f (x, y);
        GL11.glTexCoord2f( (x+w)/64.0f, y/64.0f);
        GL11.glVertex2f(x+w, y);
        GL11.glTexCoord2f( (x+w)/64.0f, (y+h)/64.0f);
        GL11.glVertex2f(x+w, y+h);
        GL11.glTexCoord2f( x/64.0f, (y+h)/64.0f );
        GL11.glVertex2f (x, y+h);
        GL11.glEnd ();

        if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.isAlpha())
            GL11.glEnable(GL11.GL_ALPHA_TEST);
    }


    /*
    =============
    Draw_Fill

    Fills a box of pixels with a single color
    =============
    */
    protected void Draw_Fill(int x, int y, int w, int h, int colorIndex)    {

        if ( colorIndex > 255)
            Com.Error(ErrorCode.ERR_FATAL, "Draw_Fill: bad color");

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        int color = d_8to24table[colorIndex]; 

        GL11.glColor3ub(
            (byte)((color >> 0) & 0xff), // r
            (byte)((color >> 8) & 0xff), // g
            (byte)((color >> 16) & 0xff) // b
        );

        GL11.glBegin (GL11.GL_QUADS);

        GL11.glVertex2f(x,y);
        GL11.glVertex2f(x+w, y);
        GL11.glVertex2f(x+w, y+h);
        GL11.glVertex2f(x, y+h);

        GL11.glEnd();
        GL11.glColor3f(1,1,1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    //=============================================================================

    /*
    ================
    Draw_FadeScreen
    ================
    */
    protected void Draw_FadeScreen()    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0, 0, 0, 0.8f);
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex2f(0,0);
        GL11.glVertex2f(vid.width, 0);
        GL11.glVertex2f(vid.width, vid.height);
        GL11.glVertex2f(0, vid.height);

        GL11.glEnd();
        GL11.glColor4f(1,1,1,1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

// ====================================================================

    // allocate a 256 * 256 texture buffer
    private ByteBuffer image8 = Lib.newByteBuffer(256 * 256 * Defines.SIZE_OF_INT);
    // share the buffer
    private IntBuffer image32 = image8.asIntBuffer();

    /*
    =============
    Draw_StretchRaw
    =============
    */
    protected void Draw_StretchRaw (int x, int y, int w, int h, int cols, int rows, byte[] data)
    {
        int i, j, trows;
        int sourceIndex;
        int frac, fracstep;
        float hscale;
        int row;
        float t;

        GL_Bind(0);

        if (rows<=256)
        {
            hscale = 1;
            trows = rows;
        }
        else
        {
            hscale = rows/256.0f;
            trows = 256;
        }
        t = rows*hscale / 256;

        if ( !qglColorTableEXT )
        {
            //int[] image32 = new int[256*256];
            image32.clear();
            int destIndex = 0;

            for (i=0 ; i<trows ; i++)
            {
                row = (int)(i*hscale);
                if (row > rows)
                    break;
                sourceIndex = cols*row;
                destIndex = i*256;
                fracstep = cols*0x10000/256;
                frac = fracstep >> 1;
                for (j=0 ; j<256 ; j++)
                {
                    image32.put(destIndex + j, r_rawpalette[data[sourceIndex + (frac>>16)] & 0xff]);
                    frac += fracstep;
                }
            }
            GL11.glTexImage2D (GL11.GL_TEXTURE_2D, 0, gl_tex_solid_format, 256, 256, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image32);
        }
        else
        {
            //byte[] image8 = new byte[256*256];
            image8.clear();
            int destIndex = 0;;

            for (i=0 ; i<trows ; i++)
            {
                row = (int)(i*hscale);
                if (row > rows)
                    break;
                sourceIndex = cols*row;
                destIndex = i*256;
                fracstep = cols*0x10000/256;
                frac = fracstep >> 1;
                for (j=0 ; j<256 ; j++)
                {
                    image8.put(destIndex  + j, data[sourceIndex + (frac>>16)]);
                    frac += fracstep;
                }
            }

            GL11.glTexImage2D( GL11.GL_TEXTURE_2D, 
                           0, 
                           GL_COLOR_INDEX8_EXT, 
                           256, 256, 
                           0, 
                           GL11.GL_COLOR_INDEX, 
                           GL11.GL_UNSIGNED_BYTE, 
                           image8 );
        }
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        if ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) 
            GL11.glDisable (GL11.GL_ALPHA_TEST);

        GL11.glBegin (GL11.GL_QUADS);
        GL11.glTexCoord2f (0, 0);
        GL11.glVertex2f (x, y);
        GL11.glTexCoord2f (1, 0);
        GL11.glVertex2f (x+w, y);
        GL11.glTexCoord2f (1, t);
        GL11.glVertex2f (x+w, y+h);
        GL11.glTexCoord2f (0, t);
        GL11.glVertex2f (x, y+h);
        GL11.glEnd ();

        if ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) 
            GL11.glEnable (GL11.GL_ALPHA_TEST);
    }

}
