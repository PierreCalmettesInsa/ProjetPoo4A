package jar;

import java.awt.*;
import java.net.URL;


public class Minimizer {

    protected MenuItem displayItem ;
    protected MenuItem exitItem ;
    protected SystemTray tray ;
    protected TrayIcon trayIcon ;
    

    public Minimizer(){
           //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();

        final URL url = Thread.currentThread().getContextClassLoader().getResource("icon.png");
        Image img = Toolkit.getDefaultToolkit().getImage(url);
        int trayIconWidth = new TrayIcon(img).getSize().width;
      
        trayIcon = new TrayIcon(img.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), "Application Name", popup);

        tray = SystemTray.getSystemTray();
    
        // Create a pop-up menu components
        displayItem = new MenuItem("Display");
        exitItem = new MenuItem("Exit");

    
        //Add components to pop-up menu
        popup.add(displayItem);
        popup.addSeparator();
        popup.add(exitItem);
    
        trayIcon.setPopupMenu(popup);
    
        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            System.out.println("TrayIcon could not be added.");
        }
    }



    public MenuItem getDisplayMin(){
        return displayItem ;
    }

    public MenuItem getExitMin(){
        return exitItem ;
    }

    public SystemTray getTray(){
        return tray ;
    }

    public TrayIcon getTrayIcon(){
        return trayIcon ;
    }





}
