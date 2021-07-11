/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javashopfx;

import java.awt.image.BufferedImage;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javashopfx.image.ImageController;
import javashopfx.tools.ToolController;
import javashopfx.window.WindowController;
import javax.swing.JOptionPane;

/**
 *
 * @author Anton Budnikov
 */
public class JavaShop extends Application {
    public static WindowController windowController;
    public static ImageController imageController;
    public static ToolController toolController;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL mainFormFXML = getClass().getResource("/javashopfx/window/Window.fxml");
        System.out.println(mainFormFXML);
        if(mainFormFXML == null){
            errorMessage("Cannot load main form","Fatal error");
            Platform.exit();
            return;
        }
        
        Parent root;
        FXMLLoader loader = new FXMLLoader(mainFormFXML);
        
        try{
            root = loader.load();
        }
        catch(Exception ex){
            errorMessage("An error occurred while loading main form","Fatal error");
            Platform.exit();
            return;
        }
        
        windowController = (WindowController)loader.getController();
        windowController.stageInit(primaryStage);
                
        primaryStage.setTitle("Javashop 1.0");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.getIcons().add(new Image("/resources/icon.png"));
        primaryStage.show();
        
        toolController = new ToolController();
        imageController = new ImageController();
        
        //imageController.resetImage(new Image("https://avatanplus.com/files/resources/original/572decbb9d1151548b64bcfb.png"));
        imageController.resetImage(new WritableImage(400,300));
    }
    
    @Override
    public void stop(){
        if(windowController != null){
            windowController.stop();
        }
    }
    
    public static void infoMessage(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void errorMessage(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void warningMessage(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.WARNING_MESSAGE);
    }
    
    public static boolean confirmDialog(String message, String title){
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    public static int confirmYNCDialog(String message, String title){
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public static int overlayARGBonARGB(int over, int base){
        int ba = ((base >> 24) & 0xFF);
        int br = ((base >> 16) & 0xFF);
        int bg = ((base >> 8) & 0xFF);
        int bb = (base & 0xFF);
                
        int oa = (over >> 24) & 0xFF;
        int or = ((over >> 16) & 0xFF);
        int og = ((over >> 8)  & 0xFF);
        int ob = (over & 0xFF);
        
        int a = byte_mult_table[ba][255-oa] + oa;
        
        int r = byte_mult_table[br][255-oa] + byte_mult_table[or][oa];
        int g = byte_mult_table[bg][255-oa] + byte_mult_table[og][oa];
        int b = byte_mult_table[bb][255-oa] + byte_mult_table[ob][oa];
        
        //int a = ba;
        
        return (a << 24) | (r << 16) + (g << 8) | b;
    } 
    
    public static int blendARGBwithARGB(int over, int base){
        int ba = ((base >> 24) & 0xFF);
        int br = ((base >> 16) & 0xFF);
        int bg = ((base >> 8) & 0xFF);
        int bb = (base & 0xFF);
                
        int oa = (over >> 24) & 0xFF;
        int or = ((over >> 16) & 0xFF);
        int og = ((over >> 8)  & 0xFF);
        int ob = (over & 0xFF);
        
        int r = byte_mult_table[br][255-oa] + byte_mult_table[or][oa];
        int g = byte_mult_table[bg][255-oa] + byte_mult_table[og][oa];
        int b = byte_mult_table[bb][255-oa] + byte_mult_table[ob][oa];
        int a = byte_mult_table[ba][255-oa] + byte_mult_table[oa][oa];
        //int a = ba;
        
        return (a << 24) | (r << 16) + (g << 8) | b;
    } 
    
    public static int getColorARGB(Color col){
        int color = (int)(col.getOpacity()*255.0) << 24;
        color += (int)(col.getRed()*255.0) << 16;
        color += (int)(col.getGreen()*255.0) << 8;
        color += (int)(col.getBlue()*255.0);
        return color;
    }
    
    public static Color getARGBColor(int argb){
        int a = ((argb >> 24) & 0xFF);
        int r = ((argb >> 16) & 0xFF);
        int g = ((argb >> 8) & 0xFF);
        int b = (argb & 0xFF);
        
        return Color.color(r/255.0, g/255.0, b/255.0, a/255.0);
    }
    
    public static int overlayARGBonRGB(int over, int base){
        return overlayARGBonARGB(over, (base & 0x00ffffff) | (255 << 24));
    } 
    
    public static int byte_mult_table[][] = new int[256][256];
    protected static void generate_byte_mult_table(){
        for(int a = 0; a<256; a++){
            for(int b = 0; b<256; b++){
                byte_mult_table[a][b] = (a*b)/255;
            }
        }
    }
    
    public static WritableImage resizeImage(WritableImage src, int targetW, int targetH, boolean preserveRatio, boolean smoothing){
 
        ImageView iv = new ImageView(src);
        iv.setPreserveRatio(preserveRatio);
        iv.setFitWidth(targetW);
        iv.setFitHeight(targetH);
        return iv.snapshot(null, null);
 
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        generate_byte_mult_table();
        launch(args);
    }
    
}
