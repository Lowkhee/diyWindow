package audioplayerlight;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import windowinterface.WindowController;

public class TopMenuBar extends MenuBar 
{
	Media media;
	MediaPlayer mediaPlayer;
	File selectedDirectory = null;
	Menu menuOptions;
	Menu menuFile;

	BooleanProperty toShuffle = new SimpleBooleanProperty(false);
	BooleanProperty toRepeat = new SimpleBooleanProperty(false);
	BooleanProperty toAutoPlay = new SimpleBooleanProperty(false);
	
	public TopMenuBar(AudioPlayer application)
	{
		MenuItem menuItemOpenFiles = new MenuItem("Open File(s)");
		MenuItem menuItemOpenFolder = new MenuItem("Open Folder");
		CheckMenuItem menuItemShuffleOption = new CheckMenuItem("Shuffle");
		CheckMenuItem menuItemRepeatOption = new CheckMenuItem("Repeat");
		CheckMenuItem menuItemAutoPlayOption = new CheckMenuItem("Auto Play");
		menuOptions = new Menu("Options");
		menuFile = new Menu("File");
		
		menuFile.getItems().addAll(menuItemOpenFiles, menuItemOpenFolder);
		menuOptions.getItems().addAll(menuItemShuffleOption, menuItemRepeatOption, menuItemAutoPlayOption);
		getMenus().addAll(menuFile, menuOptions);
		
		BooleanPropertyBase.booleanProperty(toShuffle).bind(menuItemShuffleOption.selectedProperty());
		BooleanPropertyBase.booleanProperty(toRepeat).bind(menuItemRepeatOption.selectedProperty());
		BooleanPropertyBase.booleanProperty(toAutoPlay).bind(menuItemAutoPlayOption.selectedProperty());
		
		menuItemOpenFiles.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				if(isPlaying())
					mediaPlayer.pause();
				application.fileIterator = null;
				application.selectedFiles = getAdudioFiles();
				if (application.selectedFiles != null && !application.selectedFiles.isEmpty()) 
					application.playAudioThread();
			}
		});
		
		menuItemOpenFolder.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent e)
			{
				if(isPlaying())
					mediaPlayer.pause();
				application.fileIterator = null;
				selectedDirectory = getAdudioFolder();
				if (selectedDirectory != null) 
				{
					FileFilter audioFilefilter = new FileFilter()
					{
				         public boolean accept(File file) 
				         {
				            String lowercaseName = file.getName().toLowerCase();
				            if (lowercaseName.endsWith(".mp3") || lowercaseName.endsWith(".wav"))
				               return true;
				            else 
				               return false;
				            
				         }
					};
					File[] allFiles = selectedDirectory.listFiles(audioFilefilter);
					if (allFiles.length > 0) 
					{
						application.selectedFiles = new ArrayList<File>();
						for(File file : allFiles)
							application.selectedFiles.add(file);
						application.playAudioThread();
					}
				}
			}
		});
	}
	
	public static List<File> getAdudioFiles()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Audio Folder/File Selection");
		
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
		 fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		 List<File> selected; 
		 selected = fileChooser.showOpenMultipleDialog(WindowController.stage);
		 if(selected == null)
			 return null;
		 return new ArrayList<File>(fileChooser.showOpenMultipleDialog(WindowController.stage));
	}
	
	public static File getAdudioFolder()
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = directoryChooser.showDialog(WindowController.stage);
        return selectedDirectory;
	}
	
	
	
	public static boolean isSupported(MediaPlayer player)
    {
    	if(player.getStatus() == Status.UNKNOWN)
    		return false;
    	return true;
    }
	
	public boolean isPlaying()
 	{
 		if(media != null)
 			if(mediaPlayer != null)
 				if(mediaPlayer.getStatus() == Status.PLAYING)
 					return true;
 		return false;
 	}
}
