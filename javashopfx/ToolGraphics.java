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
package javashopfx;

import javashopfx.shader.ToolPixelShader;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

/**
 *
 * @author Anton Budnikov
 */
public class ToolGraphics {
    public static int clampVal(int v, int max, int min){
        return Math.max(Math.min(v , max), min);
    }
    
    public static void safeWrite(PixelWriter writer, int x, int y, int color){
        try{
            writer.setArgb(x, y, color);
        }catch(Exception ex){}
    }
    
    public static int safeRead(PixelReader reader, int x, int y){
        try{
            return reader.getArgb(x, y);
        }catch(Exception ex){
            return 0;
        }
    }
    
    public static void drawPoint(int x, int y, int color, PixelReader reader, PixelWriter writer){
        safeWrite(writer, x, y, color);
    }
    
    public static void drawPointShader(int x, int y, ToolPixelShader color, PixelReader reader, PixelWriter writer){
        drawPoint(x,y,color.getColor(x, y, reader, writer), reader, writer);
    }
    
    public static void drawPointAliased(int x, int y, int color, int alpha, PixelReader reader, PixelWriter writer){
        int a = (color >> 24) & 0xFF;
        a = JavaShop.byte_mult_table[a][255-alpha];
        color = (color & 0x00FFFFFF) | ((a << 24) & 0xFF000000);
        safeWrite(writer, x, y, color);
    }
    
    public static void drawLine(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, ToolPixelShader color){
        int dx = Math.abs(x1-x0);
        int dy = -Math.abs(y1-y0);
        int sx = x0<x1? 1: -1;
        int sy = y0<y1? 1: -1;
        int err = dx+dy;
        int e2;
        while(true){
            drawPoint(x0,y0,color.getColor(x0, y0, reader, writer), reader, writer);
            if(x0 == x1 && y0 == y1) return;
            e2 = 2*err;
            
            if(e2 >= dy){ 
                err += dy; 
                x0 += sx; 
            }
            if(e2 <= dx){
                err += dx; 
                y0 += sy; 
            }
        }
    }
    
    public static void drawLineDotted(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, int dl, ToolPixelShader color){
        int dx = Math.abs(x1-x0);
        int dy = -Math.abs(y1-y0);
        int sx = x0<x1? 1: -1;
        int sy = y0<y1? 1: -1;
        int err = dx+dy;
        int e2;
        
        boolean draw = true;
        int cnt = 1;
        
        while(true){
            if(draw)
                drawPoint(x0,y0,color.getColor(x0, y0, reader, writer), reader, writer);
            
            cnt++;
            if(cnt%dl == 0) draw = !draw;
            
            if(x0 == x1 && y0 == y1) return;
            e2 = 2*err;
            
            if(e2 >= dy){ 
                err += dy; 
                x0 += sx; 
            }
            if(e2 <= dx){
                err += dx; 
                y0 += sy; 
            }
        }
    }
    
    public static void drawLineSmooth(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, ToolPixelShader color){
        int dx = Math.abs(x1-x0);
        int dy = Math.abs(y1-y0);
        int sx = x0<x1? 1: -1;
        int sy = y0<y1? 1: -1;
        int err = dx-dy;
        int e2;
        int x2;
        
        int ed = dx+dy == 0 ? 1 : (int)Math.sqrt((dx*dx)+(dy*dy));
        
        while(true){
            drawPointAliased(x0,y0,color.getColor(x0, y0, reader, writer), 255*Math.abs(err-dx+dy)/ed, reader, writer);
            e2 = err; x2 = x0;
            if(2*e2 >= -dx) {                                    
                if (x0 == x1) break;
                if (e2+dy < ed) drawPointAliased(x0,y0+sy,color.getColor(x0,y0+sy, reader, writer), 255*(e2+dy)/ed, reader, writer);
                err -= dy; x0 += sx; 
            } 
            if(2*e2 <= dy) {                                     
                if (y0 == y1) break;
                if (dx-e2 < ed) drawPointAliased(x2+sx,y0,color.getColor(x2+sx,y0, reader, writer), 255*(dx-e2)/ed, reader, writer);
                err += dx; y0 += sy; 
           }
        }
    }
    
    public static void drawLineThick(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, int wd, ToolPixelShader color){
        int dx = Math.abs(x1-x0);
        int dy = Math.abs(y1-y0);
        
        if(dx < dy){ 
            wd = (int)Math.floor((double)(wd)/2.0);
            for(int x = -wd; x <= wd; x++){
                drawLine(reader, writer, x0+x, y0, x1+x, y1, color);
            }
        }else{
            wd = (int)Math.floor((double)(wd)/2.0);
            for(int y = -wd; y <= wd; y++){
                drawLine(reader, writer, x0, y0+y, x1, y1+y, color);
            }
        }
    }
    
    public static void drawLineThickSmooth(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, float wd, ToolPixelShader color){
        int dx = Math.abs(x1-x0);
        int dy = Math.abs(y1-y0);
        int sx = x0<x1? 1: -1;
        int sy = y0<y1? 1: -1;
        int err = dx-dy;
        int e2;
        int x2;
        int y2;
        
        double ed = dx+dy == 0 ? 1 : Math.sqrt((float)(dx*dx)+(float)(dy*dy));
        
        wd = (wd+1)/2;
        
        int xr = sx*(int)wd;
        int yr = sy*(int)wd;
        
        while(true){
            drawPointAliased(x0,y0,color.getColor(x0, y0, reader, writer), (int) Math.max(0,255*(Math.abs(err-dx+dy)/ed-wd+1)), reader, writer);
            e2 = err; x2 = x0-xr;
            if(2*e2 >= -dx) {                                    
                for (e2 += dy, y2 = y0+yr; e2 < ed*wd && (y1 != y2 || dx > dy); e2 += dx){
                    drawPointAliased(x0, y2 += sy, color.getColor(x0, y2, reader, writer), 
                            (int) Math.max(0,255*(Math.abs(e2)/ed-wd+1)), reader, writer);
                }
                if (x0 == x1) break;
                e2 = err;
                err -= dy; 
                x0 += sx;
                if(Math.abs(xr) > 0) xr += sx;
            }
            if(2*e2 <= dy) {
                for (e2 = dx-e2; e2 < ed*wd && (x1 != x2 || dx < dy); e2 += dy){
                    drawPointAliased(x2 += sx  , y0, color.getColor(x2, y0, reader, writer), 
                            (int) Math.max(0,255*(Math.abs(e2)/ed-wd+1)), reader, writer);
                }
                if (y0 == y1) break;
                err += dx; 
                y0 += sy; 
                if(Math.abs(yr) < 0) yr += sy;
           }
        }
    }
    
    ///////////////
    
    public static void drawCircle(PixelReader reader, PixelWriter writer, int x0, int y0, int r, ToolPixelShader color){
        int x = -r, y = 0, err = 2-2*r;
        do {
            drawPoint4x(x,y,x0,y0, color, reader, writer);
            r = err;
            if (r <= y) err += ++y*2+1;           
            if (r > x || err > y) err += ++x*2+1; 
        } while (x < 0);
    }
    
    public static void drawPoint4x(int x, int y, int x0, int y0, ToolPixelShader color, PixelReader reader, PixelWriter writer){        
        drawPoint(x0-x, y0+y, color.getColor(x0-x, y0+y, reader, writer), reader, writer);
        drawPoint(x0-y, y0-x, color.getColor(x0-y, y0-x, reader, writer), reader, writer); 
        drawPoint(x0+x, y0-y, color.getColor(x0+x, y0-y, reader, writer), reader, writer);
        drawPoint(x0+y, y0+x, color.getColor(x0+y, y0+x, reader, writer), reader, writer);
    }
    
    public static void fillCircle(PixelReader reader, PixelWriter writer, int x0, int y0, int r, ToolPixelShader color){
        
        int x = -r, y = 0, err = 2-2*r; 
        boolean line = true;
        
        drawPoint(x0, y0, color.getColor(x0, y0, reader, writer), reader, writer);
        do {
            if(line){
                for(int lx = x; lx < 0; lx++){
                    drawPoint4x(lx,y,x0,y0, color, reader, writer);
                }
                line = false;
            }
            r = err;
            if (r <= y) {
                err += ++y*2+1; 
                line = true;
            }          
            if (r > x || err > y) err += ++x*2+1; 
        } while (x < 0);
    }
    
    public static void drawRect(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, ToolPixelShader color){
        if(x0 == x1 && y0 == y1){
            return;
        }
        
        if(x0 > x1){
            int tx = x0;
            x0 = x1;
            x1 = tx;
        }
        
        if(y0 > y1){
            int ty = y0;
            y0 = y1;
            y1 = ty;
        }
        
        for(int x = x0; x <= x1; x++){
            drawPoint(x, y0, color.getColor(x, y0, reader, writer), reader, writer);
            drawPoint(x, y1, color.getColor(x, y1, reader, writer), reader, writer);
        }
        for(int y = y0+1; y < y1; y++){
            drawPoint(x0, y, color.getColor(x0, y, reader, writer), reader, writer);
            drawPoint(x1, y, color.getColor(x1, y, reader, writer), reader, writer);
        }
    }
    
    public static void drawRectDotted(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, int dl, ToolPixelShader color){
        if(x0 == x1 && y0 == y1){
            return;
        }
        
        if(x0 > x1){
            int tx = x0;
            x0 = x1;
            x1 = tx;
        }
        
        if(y0 > y1){
            int ty = y0;
            y0 = y1;
            y1 = ty;
        }
        
        boolean draw = true;
        for(int x = x0; x <= x1; x++){
            if(draw){
                drawPoint(x, y0, color.getColor(x, y0, reader, writer), reader, writer);
                drawPoint(x, y1, color.getColor(x, y1, reader, writer), reader, writer);
            }
            if((x-x0)%dl == 0){
                draw = !draw;
            }
        }
        draw = false;
        for(int y = y0+1; y < y1; y++){
            if(draw){
                drawPoint(x0, y, color.getColor(x0, y, reader, writer), reader, writer);
                drawPoint(x1, y, color.getColor(x1, y, reader, writer), reader, writer);
            }
            if((y-y0)%dl == 0){
                draw = !draw;
            }
        }
    }
    
    public static void fillRect(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, ToolPixelShader color){
        if(x0 == x1 && y0 == y1){
            return;
        }
        
        if(x0 > x1){
            int tx = x0;
            x0 = x1;
            x1 = tx;
        }
        
        if(y0 > y1){
            int ty = y0;
            y0 = y1;
            y1 = ty;
        }
        
        for(int x = x0; x <= x1; x++){
            for(int y = y0; y <= y1; y++){
                drawPoint(x, y, color.getColor(x, y, reader, writer), reader, writer);
            }
        }
    }
    
    public static void drawEllipse(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, ToolPixelShader color)
    {
       int a = Math.abs(x1-x0), b = Math.abs(y1-y0), b1 = b&1; 
       long dx = 4*(1-a)*b*b, dy = 4*(b1+1)*a*a; 
       long err = dx+dy+b1*a*a, e2; 

       if (x0 > x1) { x0 = x1; x1 += a; } 
       if (y0 > y1) y0 = y1; 
       y0 += (b+1)/2; y1 = y0-b1;   
       a *= 8*a; b1 = 8*b*b;

       do {
           drawPointShader(x1, y0, color, reader, writer); 
           drawPointShader(x0, y0, color, reader, writer); 
           drawPointShader(x0, y1, color, reader, writer); 
           drawPointShader(x1, y1, color, reader, writer);
           
           e2 = 2*err;
           if (e2 <= dy) { y0++; y1--; err += dy += a; }  
           if (e2 >= dx || 2*err > dy) { x0++; x1--; err += dx += b1; }
       } while (x0 <= x1);

       while (y0-y1 < b) {
           drawPointShader(x0-1, y0, color, reader, writer);
           drawPointShader(x1+1, y0++, color, reader, writer); 
           drawPointShader(x0-1, y1, color, reader, writer);
           drawPointShader(x1+1, y1--, color, reader, writer); 
       }
    }
    
    public static void drawXLine(PixelReader reader, PixelWriter writer, int x0, int x1, int y, ToolPixelShader color){
        for(int x = Math.min(x0,x1); x <= Math.max(x0,x1); x++){
            drawPointShader(x, y, color, reader, writer); 
        }
    }
    
    public static void fillEllipse(PixelReader reader, PixelWriter writer, int x0, int y0, int x1, int y1, ToolPixelShader color, ToolPixelShader fcolor)
    {
        int a = Math.abs(x1-x0), b = Math.abs(y1-y0), b1 = b&1; 
        long dx = 4*(1-a)*b*b, dy = 4*(b1+1)*a*a; 
        long err = dx+dy+b1*a*a, e2; 
        
        int cx = Math.min(x0,x1) + Math.abs(x0-x1)/2;
        
        if (x0 > x1) { x0 = x1; x1 += a; } 
        if (y0 > y1) y0 = y1;
        y0 += (b+1)/2; y1 = y0-b1; 
        a *= 8*a; b1 = 8*b*b;

        do {
            drawXLine(reader, writer, cx+1, x1-1, y0, fcolor);
            drawXLine(reader, writer, x0+1, cx, y0, fcolor);
            drawXLine(reader, writer, cx+1, x1-1, y1, fcolor);
            drawXLine(reader, writer, x0+1, cx, y1, fcolor);
            
            drawPointShader(x1, y0, color, reader, writer); 
            drawPointShader(x0, y0, color, reader, writer); 
            drawPointShader(x0, y1, color, reader, writer); 
            drawPointShader(x1, y1, color, reader, writer);
            
            e2 = 2*err;
            if (e2 <= dy) { y0++; y1--; err += dy += a; }  
            if (e2 >= dx || 2*err > dy) { x0++; x1--; err += dx += b1; }
        } while (x0 <= x1);

        while (y0-y1 < b) {
            drawPointShader(x0-1, y0, color, reader, writer);
            drawPointShader(x1+1, y0++, color, reader, writer); 
            drawPointShader(x0-1, y1, color, reader, writer);
            drawPointShader(x1+1, y1--, color, reader, writer); 
        }
    }
}
