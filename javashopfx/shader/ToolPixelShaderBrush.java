/*
 * Copyright (C) 2019 Anton Budnikov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package javashopfx.shader;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.ToolGraphics;

/**
 *
 * @author Anton Budnikov
 */
public class ToolPixelShaderBrush implements ToolPixelShader {
  
    @Override
    public int getColor(int x, int y, PixelReader reader, PixelWriter writer) {
        int color = ToolGraphics.safeRead(reader, x, y);
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        
        if(a <= 32){
            r = 255;
            g = 255;
            b = 255;
        }
        
        return (255 << 24) | ((255-r) << 16) | ((255-g) << 8) | (255-b); 
    }
    
}
