package audioplayerlight;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import windowinterface.Main;

public class AudioPlayer 
{
	
	//public static double stageWidth = 720;
	//public static double stageHeight = 535;
	GUI guiPane;
	TopMenuBar menuBar;
	//int fileActive = 0;
	List<File> selectedFiles;
	Iterator<File> fileIterator;
	static int PLAY_ERRORS = 0;
	static Pane windowAppPane;
	static String styleSheet;
	
	public void initialise()
	{
		/*ADDED*/
		windowAppPane = Main.windowController.windowAppPane;
		/*ADDED*/
		styleSheet = Main.styleSheetArray[1];
		
		guiPane = new GUI();
		menuBar = new TopMenuBar(this);
        guiPane.setTop(menuBar);
        guiPane.toBack();
        guiPane.setBackground(new Background(new BackgroundFill(Color.SLATEGREY, null, null)));
        
        //Scene scene = new Scene(guiPane, windowAppPane.getWidth(), windowAppPane.getHeight(), Color.SLATEGREY); 
        //scene.getStylesheets().add((new File("src/resources/audioplayergui.css")).toURI().toString());
        //stage.setScene(scene);
        //stage.centerOnScreen();
        //stage.setTitle("Audio Player Light");
        //stage.initStyle(StageStyle.DECORATED);
        //stage.show(); 
        /*ADDED*/
        windowAppPane.getChildren().add(guiPane);
         
	}
	
	public void playAudioThread()
	{
		if(fileIterator == null)
		{
			if(menuBar.toShuffle.getValue())
				Collections.shuffle(selectedFiles);
			fileIterator = selectedFiles.iterator();
		}
		if(fileIterator.hasNext())
			loadAudio(fileIterator.next());
	}
	
	public boolean loadAudio(File file)
    { 
    	try
    	{
    		menuBar.media = new Media(file.toURI().toURL().toExternalForm());
    	}
    	catch (MalformedURLException e1) 
		{
			e1.printStackTrace();
			return false;
		}
    	menuBar.mediaPlayer = new MediaPlayer(menuBar.media);
        guiPane.mediaView.setMediaPlayer(menuBar.mediaPlayer);
        guiPane.audioPane.loadMedia(menuBar.mediaPlayer);
        
        menuBar.mediaPlayer.setOnHalted(new Runnable() 
        {
            @Override
            public void run() 
            {
            	guiPane.labelAudioFile.setText("Audio Stopped Abruptly");
            	menuBar.mediaPlayer.dispose();
            }
        });
        
        menuBar.mediaPlayer.setOnReady(new Runnable() 
        {
            @Override
            public void run() 
            {
            	guiPane.labelAudioFile.setText(file.getName());
            	guiPane.audioPane.enableControls();
            	guiPane.audioPane.timeSlider.setValue(0);
            	guiPane.audioPane.mediaPlayer.setVolume(guiPane.audioPane.volumeSlider.getValue());
            	if(menuBar.toAutoPlay.get())
            		guiPane.audioPane.mediaPlayer.play();
            }
        });
        
        menuBar.mediaPlayer.setOnError(new Runnable() 
        {
            @Override
            public void run() 
            {
            	if(AudioPlayer.PLAY_ERRORS >= 10)
            	{
            		guiPane.labelAudioFile.setText(file.getName() + " - Unsupported Media");
            		fileIterator.remove();
            		menuBar.mediaPlayer.dispose();
            		AudioPlayer.PLAY_ERRORS = 0;
            		endOfMedia();
            	}
            	else
            	{
            		guiPane.labelAudioFile.setText(file.getName() + " - Unsupported Media... Retrying");
            		AudioPlayer.PLAY_ERRORS += 1;
            		fileIterator = null;
            		playAudioThread();            		
            	}
            }
        });
        
        menuBar.mediaPlayer.setOnEndOfMedia(new Runnable() 
        {
            @Override
            public void run() 
            {
            	menuBar.mediaPlayer.dispose();
            	endOfMedia();
            }
        });
        return true;
    }
	
	public void endOfMedia()
	{
		guiPane.audioPane.playButton.setText(">");
		if(fileIterator.hasNext())
    		loadAudio(fileIterator.next());
    	else if(!selectedFiles.isEmpty() && menuBar.toRepeat.getValue())
		{
			fileIterator = null;
			playAudioThread();
		}
    	else
    	{
    		guiPane.labelAudioFile.setText("Waiting...");
    		guiPane.audioPane.disableControls();
    		fileIterator = null;
    		selectedFiles = null;
    	}
	}
}

