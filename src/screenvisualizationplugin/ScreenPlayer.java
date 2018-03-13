    package screenvisualizationplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.visualization.Playable;

public class ScreenPlayer implements Playable{

    private long start;
    private long end;
    private boolean isPlaying=false;
    private boolean isSync=false;
    private static Panel wcpanel;
    private String path;
    private ArrayList<Long> frames = new ArrayList<Long>();
    private int cont=0;
    private String id;
    
    private MediaPlayer mediaPlayer;
    

    private static final Logger logger = Logger.getLogger(ScreenPlayer.class.getName());

    public ScreenPlayer(File file, String id) {
            wcpanel = new Panel(file);
            mediaPlayer = wcpanel.getMP();
            path = file.getAbsolutePath();
            String path2 =  path.substring(0,path.lastIndexOf(".")) + "-temp.txt";
            String path3 =  path.substring(0,path.lastIndexOf(".")) + "-frames.txt";
            String cadena;
            FileReader f;
            FileReader f2;
            try {
                f = new FileReader(path2);
                BufferedReader b = new BufferedReader(f);
                try {
                    if((cadena=b.readLine())!=null){
                        start = Long.parseLong(cadena);
                    }if((cadena=b.readLine())!=null){
                        end = Long.parseLong(cadena);
                    }  
                    b.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            try {
                f2 = new FileReader(path3);
                BufferedReader b = new BufferedReader(f2);
                try {
                    while((cadena=b.readLine())!=null){
                        frames.add(Long.parseLong(cadena));
                    }
                    b.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            
                    DockableElement e = new DockableElement(id);
                    e.add(wcpanel);
                    DockablesRegistry.getInstance().addAppWideDockable(e);
                
    }
    
    @Override
    public void pause() {
        if(isPlaying){
            mediaPlayer.pause();    
            isPlaying=false;
        }
    }

    @Override
    public void seek(long desiredMillis) {
        if(desiredMillis>=start && desiredMillis<=end){      
            new Thread(new Runnable() {

            @Override
            public void run() {
                    mediaPlayer.seek(Duration.millis(desiredMillis-start));  
            }
        }).start();
        }
        else if(desiredMillis<start){
            new Thread(new Runnable() {

            @Override
            public void run() {
                    mediaPlayer.seek(Duration.millis(0));  
            }
        }).start();
        }
        cont=0;
        while(frames.get(cont)<desiredMillis){
            cont++;
        }
    }
    
    @Override
    public long getStart() {        
        return start;
    }

    @Override
    public long getEnd() {
        return end;
    } 

    @Override
    public void play(long millis) {
        if(millis>=start && millis<=end){
            if(isSync){
                if(frames.size()>cont){
                    if(frames.get(cont)==millis){
                        if(cont != 0){
                            isPlaying=true;
                            mediaPlayer.play();
                            cont++; 
                        }
                        if(cont==0){
                            isPlaying=true;
                            mediaPlayer.play();
                            cont++;                            
                        }
                    }
                    else if(frames.get(cont)>millis && isPlaying){
                        mediaPlayer.pause();
                        isPlaying=false;
                    }
                }
            }
            else{
                if(!isPlaying){                    
                    isPlaying=true;
                    mediaPlayer.play();
                }
            }
        }
        else{    
            mediaPlayer.stop();
        }
    }

    @Override
    public void stop() {
        if(isPlaying){
            cont=0; 
            mediaPlayer.stop();
            isPlaying=false;
        }
    }    

    @Override
    public void sync(boolean bln) {
        isSync=bln;
    }

}
