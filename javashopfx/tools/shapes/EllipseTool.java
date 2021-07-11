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
package javashopfx.tools.shapes;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.JavaShop;
import javashopfx.ToolGraphics;
import javashopfx.drawcommands.DrawCommand;
import javashopfx.drawcommands.EllipseCommand;
import javashopfx.drawcommands.RectangleCommand;
import javashopfx.shader.ToolPixelShaderBrush;
import javashopfx.tools.Tool;

/**
 *
 * @author Anton Budnikov
 */
public class EllipseTool extends Tool {
    {
        imageURL = "/resources/ellipse.png";
        toolName = "Овал";
        settingsWindowPath = "";
    }
        
    EllipseCommand cmd = null;
    
    @Override
    protected void setWindowVars() {}

    @Override
    public DrawCommand initialize(int x, int y) {
        cmd = new EllipseCommand();
        
        cmd.x0 = x;
        cmd.y0 = y;
        
        cmd.x1 = x;
        cmd.y1 = y;
        
        cmd.outlineColor = JavaShop.windowController.getPrimaryColor();
        cmd.fillColor = JavaShop.windowController.getSecondaryColor();
        
        return cmd;
    }

    @Override
    public void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom) {
        zoom = 1.0;
        ToolGraphics.fillRect(reader, writer, x, y, (int)(x+zoom)-1, (int)(y+zoom)-1, new ToolPixelShaderBrush());
    }

    @Override
    public void drawFirst(int x, int y) {
        cmd.x0 = x;
        cmd.y0 = y;
    }

    @Override
    public void draw(int x, int y, int lastx, int lasty) {
        cmd.x1 = x;
        cmd.y1 = y;
    }

    @Override
    public void drawLast(int x, int y) {
        cmd.x1 = x;
        cmd.y1 = y;
        
        JavaShop.imageController.submitCommand();
        cmd = null;
    }
}
