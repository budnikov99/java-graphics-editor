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
import javashopfx.QuickFill;
import javashopfx.ToolGraphics;

/**
 *
 * @author Anton Budnikov
 */
public class FillCommand extends DrawCommand {
    public int width = 0;
    public int height = 0;
    public int baseColor = 0;
    public int newColor = 0;
    
    public int x = 0;
    public int y = 0;
    
    @Override
    public void apply(PixelReader reader, PixelReader cache, PixelWriter writer, boolean copy) {
        QuickFill qf = new QuickFill(reader, writer, width, height, ToolGraphics.safeRead(reader, x, y), newColor);
        qf.fill(x, y);
    }
    
}
