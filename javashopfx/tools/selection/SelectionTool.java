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
package javashopfx.tools.selection;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javashopfx.JavaShop;
import javashopfx.ToolGraphics;
import javashopfx.drawcommands.DrawCommand;
import javashopfx.drawcommands.SelectionCommand;
import javashopfx.shader.ToolPixelShaderBrush;
import javashopfx.tools.Tool;

/**
 *
 * @author Anton Budnikov
 */
public class SelectionTool extends Tool {
    {
        imageURL = "/resources/selection.png";
        toolName = "Перемещение";
        settingsWindowPath = "/javashopfx/tools/selection/SelectionSettings.fxml";
    }
    
    public SelectionCommand command = null;
    
    public boolean eraseOld;
    
    public int startX = 0;
    public int startY = 0;
    public int startW = 0;
    public int startH = 0;
    public boolean hasSelection = false;
    
    public int endX = 0;
    public int endY = 0;
    public int endW = 0;
    public int endH = 0;
    
    
    @Override
    protected void setWindowVars() {
        ((SelectionSettingsController)windowController).tool = this;
    }

    @Override
    public DrawCommand initialize(int x, int y) {
        return command;
    }

    protected boolean isInRect(int x, int y, int x0, int y0, int x1, int y1){
        return (x > x0 && x < x1) && (y > y0 && y < y1);
    }
    
    int rw;
    @Override
    public void drawOverlay(PixelReader reader, PixelWriter writer, int x, int y, double zoom) {
        ToolPixelShaderBrush shader = new ToolPixelShaderBrush();
        
        rw = Math.max(1,(int)(10.0/zoom));
        
        if(hasSelection){
            ToolGraphics.drawRectDotted(reader, writer, startX, startY, startX+startW, startY+startH, rw/3, shader);
            
            ToolGraphics.drawRectDotted(reader, writer, endX, endY, endX+endW, endY+endH, rw/3+1, shader);
            
            ToolGraphics.drawRect(reader, writer, endX+endW-rw/2, endY+endH-rw/2, endX+endW+rw/2, endY+endH+rw/2, shader);
            ToolGraphics.drawRect(reader, writer, endX-rw/2, endY-rw/2, endX+rw/2, endY+rw/2, shader);
            
        }else{
            int tx1, tx2, ty1, ty2;
            
            tx1 = tsx;
            tx2 = tex;
            ty1 = tsy;
            ty2 = tey;
            
            if(tex < tsx){
                tx1 = tex;
                tx2 = tsx;
            }
            
            if(tey < tsy){
                ty1 = tey;
                ty2 = tsy;
            }
            
            ToolGraphics.drawRectDotted(reader, writer, tx1, ty1, tx2, ty2, rw, shader);
        }
    }
    
    public void applyTransform(){
        if(command != null && hasSelection && (startX!=endX || startY!=endY || startW!=endW || startH!=endH)){
            JavaShop.imageController.submitCommand();
        }
        reset();
    }
    
    public void reset(){
        command = null;
        hasSelection = false;
        
        startX = 0;
        startY = 0;
        startW = 0;
        startH = 0;
        
        JavaShop.imageController.setCurrentCommand(command);
    }
        
    public void setSelection(int x, int y, int w, int h){
        startX = x;
        startY = y;
        startW = w;
        startH = h;
        
        endX = x;
        endY = y;
        endW = w;
        endH = h;
        
        hasSelection = true;
        command = new SelectionCommand();
        
        command.eraseOld = eraseOld;
        
        command.startX = startX;
        command.startY = startY;
        command.startW = startW;
        command.startH = startH;
        
        command.endX = endX;
        command.endY = endY;
        command.endW = endW;
        command.endH = endH;
        
        JavaShop.imageController.setCurrentCommand(command);
    }
    
    public void updateEndSize(){
        if(command == null){
            return;
        }
        command.endX = endX;
        command.endY = endY;
        command.endW = endW;
        command.endH = endH;
        command.eraseColor = JavaShop.windowController.getSecondaryColor();
    }
    
    public void selectAll(){
        setSelection(0, 0, 
                (int)JavaShop.imageController.cachedImage.getWidth()-1, (int)JavaShop.imageController.cachedImage.getHeight()-1);
    }
    
    public void updateMode(boolean nmode){
        eraseOld = nmode;
        if(command != null){
            command.eraseOld = eraseOld;
            command.eraseColor = JavaShop.windowController.getSecondaryColor();
        }
    }
   
    int tsx = -1, tsy = -1;
    
    @Override
    public void drawFirst(int x, int y) {
        tsx = x;
        tsy = y;

        tex = x;
        tey = y;
        
        if(hasSelection){            
            if(isInRect(x, y, endX+endW-rw/2, endY+endH-rw/2, endX+endW+rw/2, endY+endH+rw/2)){
                //BOTTOM RIGHT
                br = true;
            }
            
            if(isInRect(x, y, endX-rw/2, endY-rw/2, endX+rw/2, endY+rw/2)){
                //TOP LEFT
                tl = true;
            }
        }
    }

    
    boolean tl = false,br = false;
    int tex = -1, tey = -1;
    @Override
    public void draw(int x, int y, int lastx, int lasty) {
        tex = x;
        tey = y;
        
        if(hasSelection){
            if(br && tex>endX && tey>endY){
                endW = tex-endX;
                endH = tey-endY;
            }
            
            if(tl){
                endX = x;
                endY = y;
            }
            //System.out.println(""+tl+" "+br);
            updateEndSize();
        }
    }

    @Override
    public void drawLast(int x, int y) {
        if(!hasSelection && tsx != -1 && tsy != -1){
            tex = x;
            tey = y;
            
            if(tex < tsx){
                int tx = tex;
                tex = tsx;
                tsx = tx;
                //System.out.println("X INV");
            }
            
            if(tey < tsy){
                int ty = tey;
                tey = tsy;
                tsy = ty;
                //System.out.println("Y INV");
            }
            
            //System.out.println(""+tsx+" "+tsy+" - "+tex+" "+tey+"; "+(tex-tsx)+" "+(tey-tsy));
            if(tsx < tex && tsy < tey){
                //System.out.println("SET");
                setSelection(tsx, tsy, tex-tsx, tey-tsy);
            }
        }
        tsx = -1;
        tsy = -1;

        tex = -1;
        tey = -1;
        
        tl = false;
        br = false;
    }
    
    @Override
    public void onSelected(){
        reset();
    }
    
    @Override
    public void onDeselected(){
        reset();
    }
    
}
