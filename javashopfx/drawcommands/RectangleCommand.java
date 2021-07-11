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
package javashopfx.drawcommands;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.ToolGraphics;
import javashopfx.shader.ToolPixelShaderBlend;

/**
 *
 * @author Anton Budnikov
 */

public class RectangleCommand extends DrawCommand {
    public int x0 = 0;
    public int y0 = 0;
    public int x1 = 0;
    public int y1 = 0;
    
    public int outlineColor = 0;
    public int fillColor = 0;
    
    @Override
    public void apply(PixelReader reader, PixelReader cache, PixelWriter writer, boolean copy) {
        int tx0 = x0;
        int tx1 = x1;
        
        if(x1 < x0){
            tx0 = x1;
            tx1 = x0;
        }
        
        int ty0 = y0;
        int ty1 = y1;
        
        if(y1 < y0){
            ty0 = y1;
            ty1 = y0;
        }
        
        ToolGraphics.drawRect(reader, writer, tx0, ty0, tx1, ty1, new ToolPixelShaderBlend(outlineColor));
        ToolGraphics.fillRect(reader, writer, tx0+1, ty0+1, tx1-1, ty1-1, new ToolPixelShaderBlend(fillColor));
    }
    
}
