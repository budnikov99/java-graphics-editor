/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javashopfx.window;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.lang.Double;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javashopfx.Config;
import javashopfx.JavaShop;
import javashopfx.ToolGraphics;
import javashopfx.tools.MenuTool;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Anton Budnikov
 */
public class WindowController implements Initializable {
    protected Stage windowStage;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ChangeListener<Number> paneSizeListener = new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateCanvas();
            }
        };

        resizeSlider.setMax(Config.availableZooms.length-1);
        resizeSlider.setValue(zoomToSlider(1.0));

        imagePane.widthProperty().addListener(paneSizeListener);
        imagePane.heightProperty().addListener(paneSizeListener); 
        horizontalImageScrollBar.valueProperty().addListener(paneSizeListener);
        verticalImageScrollBar.valueProperty().addListener(paneSizeListener);

        setPrimaryColor(new Color(0,0,0,1));
        setSecondaryColor(new Color(1,1,1,1));
                
    }    
    
    public void stageInit(Stage stage){
        windowStage = stage;
        
        windowStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(!saveBeforeExit()){
                    event.consume();
                }
            }
        });
        
        screenUpdater.setRestartOnFailure(true);
        screenUpdater.start();
        
        updateTitle();
    }
    
    public void stop(){
        screenUpdater.cancel();
        if(creationStage != null){
            creationStage.close();
        }
    }
      
    protected volatile boolean needsUpdate = false;
    
    protected ScheduledService screenUpdater = new ScheduledService() {
        @Override
        protected Task createTask() {
            return new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    if(needsUpdate){
                        needsUpdate = false;
                        
                        try{
                         
                        JavaShop.imageController.updateImage(true);
                        
                        WritableImage screen = new WritableImage((int)(renderX1-renderX0), (int)(renderY1-renderY0));
                        
                        resampleCanvas(screen, renderZoom, renderX0, renderY0, renderX1, renderY1);
                        
                        imageCanvas.setImage(screen); 
                        }catch(Exception ex){
                            //System.out.println("NEW "+(renderX1)+"-"+(renderX0)+" "+(renderY1)+"-"+(renderY0));
                            System.out.println("EXCEPTION AT WINDOW UPDATE THREAD\n   "+ex);
                        }
                    }
                    return null;
                }
            };
        }
    };
    
    public volatile double renderX0 = 0;
    public volatile double renderY0 = 0;
    public volatile double renderX1 = 0;
    public volatile double renderY1 = 0;
    public volatile double renderZoom = 0;
    
    protected volatile int relativeX = 0; 
    protected volatile int relativeY = 0;
    
    protected volatile int offsetX = 0;
    protected volatile int offsetY = 0;
    
    protected volatile int relativeW = 0; 
    protected volatile int relativeH = 0;
       
    protected void requireUpdate(){
        renderX0 = horizontalImageScrollBar.getValue();
        renderY0 = verticalImageScrollBar.getValue();
        
        renderX1 = imagePane.getWidth()+renderX0;
        renderY1 = imagePane.getHeight()+renderY0;
        
        //System.out.println("NEW "+(renderX1)+"-"+(renderX0)+" "+(renderY1)+"-"+(renderY0));

        renderZoom = imageZoom; 
        
        needsUpdate = true;
    }
    
    ////////////////////////////
    //MENU HANDLERS
    ////////////////////////////
    
    @FXML protected Menu lastOpenedMenu;
    @FXML protected Menu editMenu;
    
    public void addMenuTool(MenuItem t){
        editMenu.getItems().add(t);
    }
    
    public void removeMenuTool(MenuItem t){
        editMenu.getItems().remove(t);
    }
    
    public void updateTitle(){
        if(saveFileName.equals("")){
            windowStage.setTitle(Config.programName+" *");
        }else{
            windowStage.setTitle(Config.programName+" ["+saveFileName+"]");
        }
    }
    
    public Stage creationStage;
    @FXML protected void createMenuAction(ActionEvent event){
        try {
            if(creationStage != null && creationStage.isShowing()){
                return;
            }
            Parent p = FXMLLoader.load(getClass().getResource("/javashopfx/window/CreateWindow.fxml"));
            creationStage = new Stage(StageStyle.UTILITY);
            creationStage.setScene(new Scene(p));
            creationStage.setResizable(false);
            creationStage.setTitle("Новое изображение");
            creationStage.showAndWait();
            creationStage.close();
            creationStage = null;
        } catch (IOException ex) {}
        
        saveFileName = "";
        updateTitle();
    }
    
    protected File showFileDialog(String name, boolean save){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(name);
        
        ObservableList<ExtensionFilter> filters = fileChooser.getExtensionFilters();
        filters.add(new ExtensionFilter("PNG","*.png"));
        filters.add(new ExtensionFilter("JPG","*.jpg","*.jpeg"));
        filters.add(new ExtensionFilter("BMP","*.bmp"));
        filters.add(new ExtensionFilter("WBMP","*.wbmp"));
        filters.add(new ExtensionFilter("GIF","*.gif"));
        filters.add(new ExtensionFilter("Все файлы","*.*"));
        
        if(lastDirectory != null){
            fileChooser.setInitialDirectory(new File(lastDirectory));
        }
        
        File ret = save ? fileChooser.showSaveDialog(windowStage) : fileChooser.showOpenDialog(windowStage);
        if(ret != null){
            lastDirectory = ret.getParent();
        }
        return ret;
    }
    
    @FXML protected void openMenuAction(ActionEvent event){
        File f = showFileDialog("Открыть", false);
        if(f == null){
            return;
        }
        open(f.getAbsolutePath());
        updateTitle();
    }
    
    protected boolean saveByDialog(boolean error){
        File f = showFileDialog("Сохранить", true);
        if(f == null){
            return false;
        }
        if(!saveAs(f.getAbsolutePath())){
            if(error){
                JavaShop.errorMessage("Не удалось сохранить файл.", "Ошибка");
            }
            return false;
        }
        return true;
    }
    
    @FXML protected void saveMenuAction(ActionEvent event){
        if(saveFileName.equals("")){
            saveByDialog(true);
            updateTitle();
            return;
        }
        save();
        updateTitle();
    }
    
    @FXML protected void saveAsMenuAction(ActionEvent event){
        saveByDialog(true);
        updateTitle();
    }
    
    public boolean saveBeforeExit(){
        int conf = JavaShop.confirmYNCDialog("Сохранить изменения?", "Сохранение");
        if(conf == JOptionPane.CANCEL_OPTION){
            return false;
        }
        
        if(conf == JOptionPane.YES_OPTION){
            save();
        }
        
        return true;
    }
    
    @FXML protected void quitMenuAction(ActionEvent event){
        if(saveBeforeExit()){
            Platform.exit();
        }
    }
    
    @FXML protected void undoMenuAction(ActionEvent event){
        JavaShop.imageController.undo();
    }
    
    @FXML protected void redoMenuAction(ActionEvent event){
        JavaShop.imageController.redo();
    }
    
    @FXML protected void clearHistoryMenuAction(ActionEvent event){
        boolean ok = JavaShop.confirmDialog("Очистка истории повысит производительность, но отмена операций станет недоступна.\n"+
                "Продолжить?", "Очистка истории");
        if(ok){
            JavaShop.imageController.mergeHistoryEnd(0);
        }
    }
    
    protected String saveFileName = "";
    protected String saveFormat = "png";
    protected String lastDirectory = null;
    
    public boolean save(){
        if(saveFileName.equals("")){
            return saveByDialog(true);
        }
        
        return JavaShop.imageController.saveToFile(saveFileName, saveFormat);
    }
    
    public boolean saveAs(String fname){
        saveFileName = fname;
        saveFormat = "png";
        
        int pind = fname.lastIndexOf(".");
        if(pind == -1){
            return false;
        }
        String ftmp = fname.substring(pind);

        for(String s : ImageIO.getWriterFormatNames()){
            //System.out.println("FORMAT CHECK"+s);
            if(s.equals(ftmp)){
                saveFormat = ftmp;
                break;
            }
        }

        return save();
    }

    public boolean open(String fname){
        if(JavaShop.imageController.loadFromFile(fname)){
            saveFileName = fname;
            saveFormat = "png";
            
            int pind = fname.lastIndexOf(".");
            if(pind == -1){
                return false;
            }
            String ftmp = fname.substring(pind);

            for(String s : ImageIO.getWriterFormatNames()){
                //System.out.println("FORMAT CHECK"+s);
                if(s.equals(ftmp)){
                    saveFormat = ftmp;
                    break;
                }
            }

            return true;
        }
        return false;
    }
    
    ////////////////////////////
    //CONTROLS HANDLERS
    ////////////////////////////
    
    @FXML protected Rectangle primaryColorRect;
    @FXML protected Rectangle secondaryColorRect;
    
    @FXML protected ColorPicker primaryColorPicker;
    @FXML protected ColorPicker secondaryColorPicker;
    
    @FXML protected Slider resizeSlider;
    @FXML protected Label zoomLabel;
    
    @FXML protected ScrollPane toolSettingsPane;
    @FXML protected AnchorPane imagePane;
    @FXML protected ScrollBar horizontalImageScrollBar;
    @FXML protected ScrollBar verticalImageScrollBar;
    
    @FXML protected ToolBar toolBar;
    
    @FXML protected Label toolNameLabel;
    
    public void setToolName(String s){
        toolNameLabel.setText(s);
    }
      
    public void addToolButton(Button b){
        toolBar.getItems().add(b);
    }
    
    public void removeToolButton(Button b){
        toolBar.getItems().remove(b);
    }
    
    public void setSettingsPane(Pane p){
        toolSettingsPane.setContent(p);
    }
    
    public double getHorizontalScroll(){
        return horizontalImageScrollBar.getValue()/horizontalImageScrollBar.getMax();
    }
    
    public double getVerticalScroll(){
        return verticalImageScrollBar.getValue()/verticalImageScrollBar.getMax();
    }
    
    public int getViewportW(){
        return (int)(imageCanvas.getFitWidth()/imageZoom);
    }
    
    public int getViewportH(){
        return (int)(imageCanvas.getFitHeight()/imageZoom);
    }
    
    @FXML protected void swapColorsButtonAction(ActionEvent event){
        Color prim = primaryColorPicker.getValue();
        setPrimaryColor(secondaryColorPicker.getValue());
        setSecondaryColor(prim);
    }
    
    @FXML protected void setDefaultColorsButtonAction(ActionEvent event){
        setPrimaryColor(new Color(0,0,0,1));
        setSecondaryColor(new Color(1,1,1,1));
    }
    
    @FXML protected void changePrimaryColor(ActionEvent event){
        setPrimaryColor(primaryColorPicker.getValue());
    }
    
    @FXML protected void changeSecondaryColor(ActionEvent event){
        setSecondaryColor(secondaryColorPicker.getValue());
    }
    
    public void setPrimaryColor(Color col){
        primaryColorRect.setFill(col);
        primaryColorPicker.setValue(col);
    }
    
    public void setSecondaryColor(Color col){
        secondaryColorRect.setFill(col);
        secondaryColorPicker.setValue(col);
    }
    
    public int getPrimaryColor(){
        return JavaShop.getColorARGB(primaryColorPicker.getValue());
    }
    
    public int getSecondaryColor(){
        return JavaShop.getColorARGB(secondaryColorPicker.getValue());
    }
    
    @FXML protected void resizeSliderDrag(MouseEvent event){
        updateCanvas();  
    }
        
    @FXML protected void resizeSliderMouseReleased(MouseEvent event){
        updateCanvas();
    }
    
    protected double sliderToZoom(double slider){
        return Config.availableZooms[(Config.availableZooms.length-1)-(int)slider];
        //return ((100.0-slider)/100.0)*(Config.maxZoom-Config.minZoom)+Config.minZoom;
    }   
    protected double zoomToSlider(double zoom){
        for(int i = 0; i < Config.availableZooms.length; i++){
            if(Config.availableZooms[i] == zoom){
                return (Config.availableZooms.length-1)-i;
            }
        }
        return 0;
        //return 100.0-(zoom-Config.minZoom)/(Config.maxZoom-Config.minZoom)*100.0;
    }
         
    public void updateCanvas(){
        setZoom(sliderToZoom(resizeSlider.getValue()));
        adjustCanvas();
    }
            
    protected double imageZoom = 1.0;
    protected void setZoom(double zoom){
        imageZoom = zoom;
        if(Math.abs(zoom-1.0) < 0.20){
            zoom = 1.0;
        }

        zoomLabel.setText(Long.toString(Math.round(zoom*100.0))+"%");
    }
    
    protected void adjustCanvas(){
        imageCanvas.setFitWidth(imagePane.getWidth());
        imageCanvas.setFitHeight(imagePane.getHeight());
        
        if(cachedImage != null){
            double hscroll = horizontalImageScrollBar.getValue()/horizontalImageScrollBar.getMax();
            double vscroll = verticalImageScrollBar.getValue()/verticalImageScrollBar.getMax();

            horizontalImageScrollBar.setMax(cachedImage.getWidth()*imageZoom - imagePane.getWidth());
            verticalImageScrollBar.setMax(cachedImage.getHeight()*imageZoom - imagePane.getHeight());
            
            if(horizontalImageScrollBar.getMax() == 0){
                horizontalImageScrollBar.setMax(-1.0);
            }
            if(verticalImageScrollBar.getMax() == 0){
                verticalImageScrollBar.setMax(-1.0);
            }

            horizontalImageScrollBar.setValue(hscroll*horizontalImageScrollBar.getMax());
            verticalImageScrollBar.setValue(vscroll*verticalImageScrollBar.getMax());

            requireUpdate();           
        }
    }
        
    private void resampleCanvas(WritableImage output, double vz, double vx1, double vy1, double vx2, double vy2) {
        double S = vz;
        
        Image input = cachedImage;
        
        int X = (int) Math.min(vx1, input.getWidth()*S);
        int Y = (int) Math.min(vy1, input.getHeight()*S);
        int W = (int) Math.min(vx2, input.getWidth()*S);
        int H = (int) Math.min(vy2, input.getHeight()*S);
        int SQS = Config.backgroundSquaresSize;
        int XO = 0;
        int YO = 0;
           
        relativeX = X;
        relativeY = Y;
        
        offsetX = 0;
        offsetY = 0;
        
        if(Math.floor(input.getWidth()*S) <= W-X){
            X = 0;
            W = (int) (output.getWidth());
            XO = (int) ((input.getWidth()*S-output.getWidth())/2.0);
            relativeX = XO;
            offsetX = XO;
        }
        if(Math.floor(input.getHeight()*S) <= H-Y){
            Y = 0;
            H = (int) (output.getHeight());
            YO = (int) ((input.getHeight()*S-output.getHeight())/2.0);
            relativeY = YO;
            offsetY = YO;
        }
        
        relativeW = W-X;
        relativeH = H-Y;
        
        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();
        
        for (int y = Y; y < H; y++) {
          for (int x = X; x < W; x++) {
            int ix = (int)Math.floor((x+XO)/S);
            int iy = (int)Math.floor((y+YO)/S);
            
            int argb = 0x00000000;
            if(ix >= 0 && ix < input.getWidth() && iy >= 0 && iy < input.getHeight()){
                argb = reader.getArgb(ix, iy);
                
                if(((argb >> 24) & 0xff) < 255){
                    if(((YO+(int)y)/SQS)%2 == ((XO+(int)x)/SQS)%2){
                        argb = JavaShop.overlayARGBonRGB(argb, 0xFFFFFF);
                    }else{
                        argb = JavaShop.overlayARGBonRGB(argb, 0xC0C0C0);
                    }
                }
            }
            
            int ox = x-X;
            int oy = y-Y;
            if(ox >= 0 && ox < output.getWidth() && oy >= 0 && oy < output.getHeight()){
                writer.setArgb(ox, oy, argb);
            }
          }
        }
        
        //JavaShop.toolController.drawOverlay(output.getPixelReader(), writer, (int)((overlayX*S)-X-XO), (int)((overlayY*S)-Y-YO), S);
        
        //System.out.println(""+X+" "+Y+" "+W+" "+H+" - "+(W-X)+" "+(H-Y)+" - "+vz);
    }
       
    ////////////////////////////
    //IMAGE HANDLERS
    ////////////////////////////
    
    @FXML protected ImageView imageCanvas;
    
    protected WritableImage cachedImage;
    protected WritableImage drawingImage;
    
    public void setImage(WritableImage img){
        cachedImage = img;

        updateCanvas();
    }
      
    protected int transformX(int x){
        return (int)((x+relativeX)/imageZoom);
    }
    protected int transformY(int y){
        return (int)((y+relativeY)/imageZoom);
    }
    
    @FXML protected void canvasMousePressed(MouseEvent event){
        JavaShop.toolController.processMousePress(transformX((int)event.getX()), transformY((int)event.getY()));
        overlayX = transformX((int)event.getX());
        overlayY = transformY((int)event.getY());
    }
    @FXML protected void canvasMouseReleased(MouseEvent event){
        JavaShop.toolController.processMouseRelease(transformX((int)event.getX()), transformY((int)event.getY()));
        overlayX = transformX((int)event.getX());
        overlayY = transformY((int)event.getY());
    }
    @FXML protected void canvasMouseDragged(MouseEvent event){
        JavaShop.toolController.processMouseDrag(transformX((int)event.getX()), transformY((int)event.getY()));
        overlayX = transformX((int)event.getX());
        overlayY = transformY((int)event.getY());
    }
    
    public int overlayX = 0;
    public int overlayY = 0;
    @FXML protected void canvasMouseMoved(MouseEvent event){
        overlayX = transformX((int)event.getX());
        overlayY = transformY((int)event.getY());
    }
    
}
