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
package javashopfx.tools;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.JavaShop;
import javashopfx.ToolGraphics;
import javashopfx.drawcommands.DrawCommand;

/**
 *
 * @author Anton Budnikov
 */
public class PickerTool extends Tool{
    {
        imageURL = "/resources/picker.png";
        toolName = "Пипетка";
        settingsWindowPath = "";
    }

    @Override
    protected void setWindowVars() {}

    @Override
    public DrawCommand initialize(int x, int y) {
        return null;
    }
    
    public void pickColor(int x, int y){
        int argb = ToolGraphics.safeRead(JavaShop.imageController.cachedImage.getPixelReader(), x, y);
        JavaShop.windowController.setPrimaryColor(JavaShop.getARGBColor(argb));
    }
    
    @Override
    public void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom) {
        
    }

    @Override
    public void drawFirst(int x, int y) {
        pickColor(x,y);
    }

    @Override
    public void draw(int x, int y, int lastx, int lasty) {
        pickColor(x,y);
    }

    @Override
    public void drawLast(int x, int y) {
        pickColor(x,y);
    }
    
}
