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
import javashopfx.JavaShop;

/**
 *
 * @author Anton Budnikov
 */
public class CanvasResizeCommand extends DrawCommand {
    public int width = 0;
    public int height = 0;
    
    @Override
    public void apply(PixelReader reader, PixelReader cache, PixelWriter writer, boolean copy) {
        if(!copy){
            WritableImage img = new WritableImage(width, height); 
            PixelReader creader = JavaShop.imageController.cachedImage.getPixelReader();
            PixelWriter cwriter = img.getPixelWriter();
            int w = (int) Math.min(JavaShop.imageController.cachedImage.getWidth(), img.getWidth());
            int h = (int) Math.min(JavaShop.imageController.cachedImage.getHeight(), img.getHeight());
            
            for(int y = 0; y < h; y++){
                for(int x = 0; x < w; x++){
                    cwriter.setArgb(x, y, creader.getArgb(x, y));
                }
            }
                    
            JavaShop.imageController.cachedImage = img;
            JavaShop.imageController.commandsImage = new WritableImage(img.getPixelReader(), (int)img.getWidth(), (int)img.getHeight());
        }
    }
    
}
