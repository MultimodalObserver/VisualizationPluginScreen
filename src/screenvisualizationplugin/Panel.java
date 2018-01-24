/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screenvisualizationplugin;

import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javax.swing.JPanel;

/**
 *
 * @author Khrikhri
 */
public class Panel extends JFXPanel{
    
    private File MEDIA_URL;
    private Media media;
    private MediaPlayer mediaPlayer;
    MediaView mediaView;
    public long end;
    private boolean isPlaying=false;
    private boolean isSync=false;
    double velocidad = 1;
    long seekSlider=0;
    int cambio = 0;
    double deltaT;
         
    public Panel(File file){
        
        MEDIA_URL = file;
        setLayout(new BorderLayout());
        Group root = new Group();
         
        try {
            // create media player
            media = new Media(MEDIA_URL.toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        }
        mediaPlayer = new MediaPlayer(media);   
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                end=(long)media.getDuration().toMillis();
            }
        });
               // create mediaView and add media player to the viewer
        mediaView = new MediaView();
        mediaView.setMediaPlayer(mediaPlayer);
                DoubleProperty width = mediaView.fitWidthProperty();
                DoubleProperty height = mediaView.fitHeightProperty();
                width.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
                height.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));
        Scene scene = new Scene(root, mediaView.getFitWidth(), mediaView.getFitHeight());
        ((Group)scene.getRoot()).getChildren().add(mediaView);
         
        setScene(scene);
    }
 
    public JFXPanel getPanel() {
        return this;
    }
    
     public void play(){
        mediaPlayer.play();
        isPlaying=true;      
    }
    
    public void play2(long frame){
        if(mediaPlayer.getCurrentTime().toMillis()>=frame && isPlaying){
            mediaPlayer.pause();
            isPlaying=false;
        }
    }
    public void stop(){
        mediaPlayer.stop();
        isPlaying=false;
    }
    public void pause(){
        mediaPlayer.pause();
        isPlaying=false;
    }
    
    public void sync(boolean sync){
        isSync=sync;
    }
    
    public void current(long sw){
        new Thread(new Runnable() {

            @Override
            public void run() {
                    mediaPlayer.seek(Duration.millis(sw)); 
            }
        }).start();
    }
    public Status getStatus(){
       return mediaPlayer.getStatus();
    }
}
