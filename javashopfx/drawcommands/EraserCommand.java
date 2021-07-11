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

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.Config;
import javashopfx.ToolGraphics;
import javashopfx.shader.ToolPixelShader;
import javashopfx.shader.ToolPixelShaderBlend;
import javashopfx.shader.ToolPixelShaderColor;
import javashopfx.shader.ToolPixelShaderCopy;

/**
 *
 * @author Anton Budnikov
 */
public class EraserCommand extends DrawCommand {
    protected CopyOnWriteArrayList<Point> points = new CopyOnWriteArrayList<>();
    public int width = 1;

    public void addPoint(int x, int y){
        Point p = new Point(x,y);
        points.add(p);
    }
    
    public void addPointNear(int x, int y){
        Point lp = points.get(points.size()-1);
        
        Point p = new Point(x,y);
        
        double d = lp.distance(p);
        
        if(d > width/Config.brushCircleDistanceDivisor)
            points.add(p);
        
        if(d > (width/Config.brushCircleDistanceDivisor)*2){
            int pts = (int)Math.ceil(d/((width/Config.brushCircleDistanceDivisor)*2));
            double dx = (double)(p.x-lp.x)/(double)pts;
            double dy = (double)(p.y-lp.y)/(double)pts;
            for(double i = 1.0; i <= pts; i+=1.0){
                points.add(new Point((int)(lp.x+dx*i),(int)(lp.y+dy*i)));     
            }
        }
    }
    
    @Override
    public void apply(PixelReader reader, PixelReader cache, PixelWriter writer, boolean copy) {
        ToolPixelShader shader = new ToolPixelShaderColor(0x00000000);

        int cwidth = (int)Math.floor((double)width/2.0);

        for(Point p : points){
            ToolGraphics.fillCircle(reader, writer, p.x, p.y, cwidth, shader);
        }
    }
}
