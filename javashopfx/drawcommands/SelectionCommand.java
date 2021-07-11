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
import javafx.scene.image.WritableImage;
import javashopfx.ToolGraphics;
import javashopfx.shader.ToolPixelShaderCopy;

/**
 *
 * @author Anton Budnikov
 */
public class SelectionCommand extends DrawCommand {
    public boolean eraseOld;
    
    public int startX = 0;
    public int startY = 0;
    public int startW = 0;
    public int startH = 0;
    
    public int endX = 0;
    public int endY = 0;
    public int endW = 0;
    public int endH = 0;
    
    public int eraseColor = 0;
    
    @Override
    public void apply(PixelReader reader, PixelReader cache, PixelWriter writer, boolean copy) {

        if(copy){
            ToolPixelShaderCopy shader = new ToolPixelShaderCopy();
            if(eraseOld){
                ToolGraphics.fillRect(cache, writer, startX, startY, startX+startW, startY+startH, shader);
            }
            ToolGraphics.fillRect(cache, writer, endX, endY, endX+endW, endY+endH, shader);
            return;
        }
        
        double xz = (double)startW/(double)endW;
        double yz = (double)startH/(double)endH;
                
        for(int y = 0; y <= endH; y++){
            for(int x = 0; x <= endW; x++){
                int argb = ToolGraphics.safeRead(cache, (int)(x*xz)+startX, (int)(y*yz)+startY);
                ToolGraphics.safeWrite(writer, endX+x, endY+y, argb);
            }
        }
        
        
        if(eraseOld){
            for(int y = 0; y <= startH; y++){
                for(int x = 0; x <= startW; x++){
                    if((y+startY > endY+endH || y+startY < endY) || (x+startX > endX+endW || x+startX < endX)){
                        ToolGraphics.safeWrite(writer, startX+x, startY+y, eraseColor);
                    }
                }
            }
        }

    }
}
