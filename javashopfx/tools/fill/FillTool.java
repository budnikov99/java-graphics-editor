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
package javashopfx.tools.fill;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.JavaShop;
import javashopfx.ToolGraphics;
import javashopfx.drawcommands.DrawCommand;
import javashopfx.drawcommands.FillCommand;
import javashopfx.shader.ToolPixelShaderBrush;
import javashopfx.tools.Tool;

/**
 *
 * @author Anton Budnikov
 */
public class FillTool extends Tool {
    {
        imageURL = "/resources/fill.png";
        toolName = "Заливка";
        settingsWindowPath = "";
    }
        
    
    @Override
    protected void setWindowVars() {}

    @Override
    public DrawCommand initialize(int x, int y) {
        return null;
    }

    @Override
    public void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom) {
        zoom = 1.0;
        ToolGraphics.fillRect(reader, writer, x, y, (int)(x+zoom)-1, (int)(y+zoom)-1, new ToolPixelShaderBrush());
    }

    @Override
    public void drawFirst(int x, int y) {
        FillCommand cmd = new FillCommand();
        cmd.x = x;
        cmd.y = y;
        
        cmd.newColor = JavaShop.windowController.getPrimaryColor();
        cmd.width = (int)JavaShop.imageController.cachedImage.getWidth();
        cmd.height = (int)JavaShop.imageController.cachedImage.getHeight();
        
        JavaShop.imageController.draw(cmd);
    }

    @Override
    public void draw(int x, int y, int lastx, int lasty) {}

    @Override
    public void drawLast(int x, int y) {}
    
}
