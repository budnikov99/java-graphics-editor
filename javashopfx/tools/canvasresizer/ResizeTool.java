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
package javashopfx.tools.canvasresizer;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.JavaShop;
import javashopfx.drawcommands.BrushCommand;
import javashopfx.drawcommands.CanvasResizeCommand;
import javashopfx.drawcommands.DrawCommand;
import javashopfx.tools.MenuTool;
import javashopfx.tools.Tool;

/**
 *
 * @author Anton Budnikov
 */
public class ResizeTool extends MenuTool{
    public int width = 0;
    public int height = 0;
    
    {
        imageURL = "/resources/canvasresizer.png";
        toolName = "Изменить размер холста";
        settingsWindowPath = "/javashopfx/tools/canvasresizer/ResizeTool.fxml";
    }
    
    @Override
    protected void setWindowVars() {
        ((ResizeToolController) windowController).tool = this;
    }

    @Override
    public DrawCommand initialize(int x, int y) {
        
        return null;
    }

    @Override
    public void onSelected(){
        ((ResizeToolController)windowController).initValues();
    }
    
    @Override
    public void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom) {
        
    }

    @Override
    public void drawFirst(int x, int y) {
        CanvasResizeCommand dc = new CanvasResizeCommand();
        dc.width = width;
        dc.height = height;
        JavaShop.imageController.draw(dc);
    }

    @Override
    public void draw(int x, int y, int lastx, int lasty) {
        
    }

    @Override
    public void drawLast(int x, int y) {
        
    }
    
}
