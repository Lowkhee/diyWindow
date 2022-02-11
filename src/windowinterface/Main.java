package windowinterface;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import audioplayerlight.AudioPlayer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application 
{
	public static WindowController windowController;
	public static String[] styleSheetArray = {"",""}; 
    
	@Override
    public void start(Stage stage) throws Exception
    {
    	WindowController.stage = stage;
    	windowController = new WindowController();
    	
    	
        /*EDIT: careful editting the fxml, "BorderPane.center.windowAppPane" is the intended location for additions
         * protected Pane windowAppPane is added to BorderPane.center in WindowController.java, not fxml */ 
    	String path = System.getProperty("user.home") + "\\Documents\\JavaProjects\\CustomWindowInterface\\src\\resources\\WindowController.fxml"; //path within the "bin", not "src"
        FXMLLoader loader = new FXMLLoader((new File(path)).toURI().toURL());
        loader.setRoot(windowController.windowRootStackPane);
        loader.setController(windowController);

        try 
        {
        	loader.load();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        stage.initStyle(StageStyle.TRANSPARENT);
        
        /*EDIT: Window/App task bar highlight or process name */
        stage.setTitle("Audio Player Light");
        
        Scene scene = new Scene(windowController.windowRootStackPane, WindowController.DEFAULT_WIDTH, WindowController.DEFAULT_HEIGHT);
        
        path = System.getProperty("user.home") + "\\Documents\\JavaProjects\\CustomWindowInterface\\src\\resources\\WindowController.css";
        scene.getStylesheets().add((new File(path)).toURI().toString());
        /*EDIT: AudioPlayer will use this specific stylesheet... ie, any defaults */
        path = System.getProperty("user.home") + "\\Documents\\JavaProjects\\CustomWindowInterface\\src\\resources\\audioplayergui.css";
        scene.getStylesheets().add((new File(path)).toURI().toString());
        
        styleSheetArray[0] = scene.getStylesheets().get(0);
        windowController.windowRootStackPane.getStylesheets().add(Main.styleSheetArray[0]);
        /*Style sheet for audioplayer*/
        styleSheetArray[1] = scene.getStylesheets().get(1);
        
        scene.fillProperty().set(Color.TRANSPARENT);
        
        stage.setScene(scene);
        stage.setResizable(true);
        
        /*EDIT: App added to windowAppPane*/
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.initialise();
        
        stage.show();
        
        /*EDIT: used to center window/app on screen, unnecessary */
        windowController.resizeDefaultWindow();
    }


    public static void main(String[] args) 
    {
        launch(args);
    }
    
    /**
     * Load an image resource 
     * @param fileName
     * @return Image
     * @default null
     */
    public static Image loadImage(String fileName)
    {
    	 String path = System.getProperty("user.home") + "\\Documents\\JavaProjects\\CustomWindowInterface\\src\\resources\\" + fileName;
         File file = new File(path);
         Image image = null;
         try 
         {
         	image = new Image(file.toURI().toURL().openStream());
 		} 
         catch (MalformedURLException e) 
         {
 			e.printStackTrace();
 		} 
         catch (IOException e) 
         {
 			e.printStackTrace();
 		}
         return image;
    }
}
