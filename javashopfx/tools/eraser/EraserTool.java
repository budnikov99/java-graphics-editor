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
package javashopfx.tools.eraser;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.JavaShop;
import javashopfx.ToolGraphics;
import javashopfx.drawcommands.BrushCommand;
import javashopfx.drawcommands.DrawCommand;
import javashopfx.drawcommands.EraserCommand;
import javashopfx.shader.ToolPixelShaderBlend;
import javashopfx.shader.ToolPixelShaderBrush;
import javashopfx.tools.Tool;

/**
 *
 * @author Anton Budnikov
 */
public class EraserTool extends Tool {
    protected EraserCommand drawCommand;
    public int width = 1;
    public boolean smoothing = false;
    {
        imageURL = "/resources/eraser.png";
        toolName = "Ластик";
        settingsWindowPath = "/javashopfx/tools/eraser/EraserSettings.fxml";
    }
    
    @Override
    protected void setWindowVars() {
        EraserSettingsController ctrl = (EraserSettingsController) windowController;
        ctrl.holder = this;
    }

    @Override
    public void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom) {
        zoom = 1.0;
        if(width <= 1){
            ToolGraphics.fillRect(reader, writer, x, y, (int)(x+zoom)-1, (int)(y+zoom)-1, new ToolPixelShaderBrush());
        }else
            ToolGraphics.fillCircle(reader, writer, x, y, (int)((width)/2 * zoom), new ToolPixelShaderBrush());
    }
    
    public DrawCommand initialize(int x, int y){
        drawCommand = new EraserCommand();
        drawCommand.width = width;
        
        return drawCommand;
    }
    
    @Override
    public void drawFirst(int x, int y) {
        drawCommand.addPoint(x, y);
    }

    @Override
    public void draw(int x, int y, int lastx, int lasty) {
         drawCommand.addPointNear(x, y);
    }

    @Override
    public void drawLast(int x, int y) {
        drawCommand.addPoint(x, y);
        JavaShop.toolController.apply();
    }
}
