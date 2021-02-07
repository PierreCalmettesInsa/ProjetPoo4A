package jar;

import java.awt.*;


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

        /*
        String pathOfProject = System.getProperty("user.dir");
        String path = pathOfProject + "\\" + "chat\\src\\main\\resources\\" + "iconSend.jpg" ;
        */




        //Use it to get the image
        Image img = Toolkit.getDefaultToolkit().getImage("/iconSend.jpg");

        trayIcon = new TrayIcon(img, "Application Name", popup);

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
