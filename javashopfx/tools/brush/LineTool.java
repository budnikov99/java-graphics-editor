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
package javashopfx.tools.brush;

import java.awt.Point;
import javashopfx.JavaShop;
import javashopfx.drawcommands.BrushCommand;
import javashopfx.drawcommands.DrawCommand;

/**
 *
 * @author Anton Budnikov
 */
public class LineTool extends BrushTool {
    {
        imageURL = "/resources/line.png";
        toolName = "Линия";
        settingsWindowPath = "/javashopfx/tools/brush/BrushSettings.fxml";
    }
    
    int sx = 0;
    int sy = 0;
    
    public DrawCommand initialize(int x, int y){
        drawCommand = new BrushCommand();
        drawCommand.color = JavaShop.windowController.getPrimaryColor();
        drawCommand.width = width;
        return drawCommand;
    }
    
    @Override
    public void drawFirst(int x, int y) {
        drawCommand.addPoint(x, y);
        sx = x;
        sy = y;
    }

    @Override
    public void draw(int x, int y, int lastx, int lasty) {
        drawCommand.clearPoints();
        drawCommand.addPoint(sx,sy);
        drawCommand.addPointNear(x, y);
    }

    @Override
    public void drawLast(int x, int y) {
        drawCommand.clearPoints();
        drawCommand.addPoint(sx,sy);
        drawCommand.addPointNear(x, y);
        JavaShop.toolController.apply();
    }
}
