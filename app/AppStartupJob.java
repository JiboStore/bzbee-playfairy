import com.playfairy.datasources.MongoDB;

import play.Application;
import play.GlobalSettings;
import play.Logger;

public class AppStartupJob extends GlobalSettings {
	
    public void onStart(Application app) {
        Logger.info("Application started!");

        MongoDB.connect();

        Logger.info("Connected to Database!");
    }
}