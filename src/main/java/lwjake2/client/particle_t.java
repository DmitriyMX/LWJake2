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

package lwjake2.client;

import dmx.lwjake2.render.TextureManager;
import lwjake2.Defines;
import lwjake2.util.Lib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class particle_t {
    
    // lwjgl renderer needs a ByteBuffer
    private static ByteBuffer colorByteArray = Lib.newByteBuffer(Defines.MAX_PARTICLES * Lib.SIZEOF_INT, ByteOrder.LITTLE_ENDIAN);

    public static FloatBuffer vertexArray = Lib.newFloatBuffer(Defines.MAX_PARTICLES * 3);
    public static IntBuffer colorArray = colorByteArray.asIntBuffer();
    
    @Deprecated
    public static void setColorPalette(int[] palette) {
        TextureManager.setColorPalette(palette);
    }
    
    public static ByteBuffer getColorAsByteBuffer() {
        return colorByteArray;
    }
}
