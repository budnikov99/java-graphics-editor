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
package javashopfx.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javashopfx.Config;
import javashopfx.Controller;
import javashopfx.JavaShop;
import javashopfx.ToolGraphics;
import javashopfx.shader.ToolPixelShaderBlend;
import javashopfx.drawcommands.DrawCommand;
import javashopfx.tools.ToolController;
import javax.imageio.ImageIO;

/**
 *
 * @author Anton Budnikov
 */
public class ImageController implements Controller {
    protected WritableImage baseImage;
    public volatile WritableImage commandsImage;
    public volatile WritableImage cachedImage;
    public volatile WritableImage activeImage;
    public volatile WritableImage activeCache;
    protected DrawCommand currentCommand;
        
    protected ArrayList<DrawCommand> history = new ArrayList<>();
    protected int historyIndex = 0;
    protected int baseHistoryIndex = 0;
    
    public int width = 0;
    public int height = 0;
       
    protected volatile boolean requireUpdate = true;
    public void redrawImage(){
        requireUpdate = true;
    }
    
    protected void actualRedrawImage(){
        int W = (int) baseImage.getWidth();
        int H = (int) baseImage.getHeight();
        
        commandsImage = new WritableImage(W, H);
        cachedImage = new WritableImage(W, H);
        
        PixelWriter cachewriter = cachedImage.getPixelWriter();
        PixelWriter cmdwriter = commandsImage.getPixelWriter();
        PixelReader cachereader = cachedImage.getPixelReader();
        PixelReader cmdreader = commandsImage.getPixelReader();
        PixelReader basereader = baseImage.getPixelReader();
        
        for(int y = 0; y < H; y++){
            for(int x = 0; x < W; x++){
                cachewriter.setArgb(x, y, basereader.getArgb(x, y));
                cmdwriter.setArgb(x, y, basereader.getArgb(x, y));
            }
        }
        
        for(int i = history.size()-1; i >= historyIndex; i--){
    
            cachewriter = cachedImage.getPixelWriter();
            cmdwriter = commandsImage.getPixelWriter();
            cachereader = cachedImage.getPixelReader();
            cmdreader = commandsImage.getPixelReader();
            
            history.get(i).apply(cachereader, cmdreader, cachewriter);
            history.get(i).apply(cmdreader, cachereader, cmdwriter, true);
        }

    }
        
    public void updateImage(){  
        updateImage(false);
    }
    
    public void updateImage(boolean view){ 
        if(requireUpdate){
            actualRedrawImage();

            requireUpdate = false;
        }
        
        int W = (int) cachedImage.getWidth();
        int H = (int) cachedImage.getHeight();
        
        int X0,Y0,X1,Y1;
        
        if(!view){
            X0 = 0; 
            X1 = (int)cachedImage.getWidth()-1;
            Y0 = 0; 
            Y1 = (int)cachedImage.getHeight()-1;
        }else{
            if(W<JavaShop.windowController.getViewportW()){
                X0 = 0; 
                X1 = (int)cachedImage.getWidth()-1;
            }else{
                X0 = (int)(((double)(W-JavaShop.windowController.getViewportW()))*JavaShop.windowController.getHorizontalScroll());
                X1 = X0+(int)JavaShop.windowController.getViewportW()-1;
            }
            
            if(H<JavaShop.windowController.getViewportH()){
                Y0 = 0; 
                Y1 = (int)cachedImage.getHeight()-1;
            }else{
                Y0 = (int)(((double)(H-JavaShop.windowController.getViewportH()))*JavaShop.windowController.getVerticalScroll());
                Y1 = Y0+(int)JavaShop.windowController.getViewportH()-1;
            }
        }
        
        PixelReader reader = cachedImage.getPixelReader();
        
        if(cachedImage.getWidth() != activeImage.getWidth() || cachedImage.getHeight() != activeImage.getHeight()){
            activeImage = new WritableImage(reader, (int)cachedImage.getWidth(), (int)cachedImage.getHeight());
            activeCache = new WritableImage(reader, (int)cachedImage.getWidth(), (int)cachedImage.getHeight());
        }
        
        PixelWriter writer = activeImage.getPixelWriter();
        PixelReader activereader = activeImage.getPixelReader();
        
        PixelWriter acachewriter = activeCache.getPixelWriter();
        PixelReader acachereader = activeCache.getPixelReader();
        
        for(int y = Y0; y <= Y1; y++){
            for(int x = X0; x <= X1; x++){
                int argb = reader.getArgb(x, y);
                writer.setArgb(x, y, argb);
                acachewriter.setArgb(x, y, argb);
            }
        }
                    
        if(currentCommand != null){
            currentCommand.apply(activereader, acachereader, writer);
        }
            
        reader = cachedImage.getPixelReader();
        writer = activeImage.getPixelWriter();
        activereader = activeImage.getPixelReader();
        
        JavaShop.toolController.drawOverlay(activereader, writer, 
                JavaShop.windowController.overlayX, JavaShop.windowController.overlayY, JavaShop.windowController.renderZoom);
        
        JavaShop.windowController.setImage(activeImage); 
    }
    
    public void resetImage(Image img){
        historyIndex = 0;
        history.clear();
        baseImage = new WritableImage(img.getPixelReader(), (int)img.getWidth(), (int)img.getHeight());
        width = (int)img.getWidth();
        height = (int)img.getHeight();
        commandsImage = new WritableImage(width, height);
        cachedImage = new WritableImage(width, height);
        activeImage = new WritableImage(width, height);
        activeCache = new WritableImage(width, height);
        redrawImage();
        updateImage();
    }
    
    public void resizeImage(int w, int h){
        redrawImage();
        resizeImage(cachedImage, w, h);
    }
    
    public void resizeImage(WritableImage base, int w, int h){
        baseImage = JavaShop.resizeImage(base, w, h, true, true);
        cutHistoryFront(history.size());
    }
    
    public void undo(){
        if(historyIndex <= history.size()-1){
            historyIndex++;
            redrawImage();
        }
    }
    
    public void redo(){
        if(historyIndex > 0){
            historyIndex--;
            redrawImage();
            history.get(historyIndex).onDraw();
        }
    }
    
    public void setCurrentCommand(DrawCommand c){
        currentCommand = c;
        //updateImage();
    }
    
    public void submitCommand(){
        draw(currentCommand);
        currentCommand = null;
    }
    
    public void draw(DrawCommand c){
        if(c == null){
            return;
        }
        
        if(historyIndex > 0){
            cutHistoryFront(historyIndex);
            historyIndex = 0;
        }
        
        c.active = false;
        history.add(0, c);
        
        c.onDraw();
        
        cutHistoryOverDepth();
        
        redrawImage();
    }
 
    
    protected void cutHistoryOverDepth(){
        mergeHistoryEnd(Config.historyDepth+1);
    }
    
    public void mergeHistoryEnd(int threshold){
        if(history.size() <= threshold){
            return;
        }
        int old = historyIndex; 
        historyIndex = threshold;
        actualRedrawImage();
        baseImage = cachedImage;
        cachedImage = new WritableImage((int)baseImage.getWidth(), (int)baseImage.getHeight());
        
        historyIndex = Math.min(old, threshold);
        for(int i = threshold; i <= history.size()-1; ){
            history.remove(history.size()-1);
        }
        
        redrawImage();
    }
    
    protected void cutHistoryFront(int end){
        for(int i = 0; i < end; i++){
            history.remove(0);
        }
    }
    
    public boolean loadFromFile(String name){
        BufferedImage img;
        try{
            img = ImageIO.read(new File(name));
        }catch(IOException ex){
            return false;
        }
        
        WritableImage limg = SwingFXUtils.toFXImage(img, null);
        
        resetImage(limg);
        
        return true;
    }
    
    public boolean saveToFile(String name, String format){
        actualRedrawImage();
        BufferedImage img = SwingFXUtils.fromFXImage(cachedImage, null);
        
        try{
            return ImageIO.write(img, format, new File(name));
        }catch(IOException ex){
            return false;
        }
    }
}
