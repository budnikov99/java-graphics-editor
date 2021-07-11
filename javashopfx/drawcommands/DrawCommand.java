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

/**
 *
 * @author Anton Budnikov
 */
public abstract class DrawCommand {
    public boolean active = true;
    
    public void apply(PixelReader reader, PixelReader cache, PixelWriter writer){
        apply(reader, cache, writer, false);
    }
    
    public abstract void apply(PixelReader reader, PixelReader cache, PixelWriter writer, boolean copy);
    
    public void onDraw(){}
    public void onReset(){}
}
