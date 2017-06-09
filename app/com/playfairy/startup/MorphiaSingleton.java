package com.playfairy.startup;

import java.net.UnknownHostException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.playfairy.models.UploadIpaEntry;

import play.Configuration;
import play.Logger;

@Singleton
public class MorphiaSingleton {
	
	private MongoClientURI mongoUri;
	private MongoClient mongoClient;
	private Morphia morphiaObject;
	private Datastore morphiaStore;
	
	private static MorphiaSingleton instance;
	
	@Inject
	public MorphiaSingleton(final Configuration configuration) {
		instance = this;
		connect(configuration);
	}
	
	public static MorphiaSingleton getInstance() {
		return instance;
	}
	
	public Datastore getDatastore() {
		Logger.debug("MorphiaSingleton.getDatastore: " + morphiaStore.toString());
		return morphiaStore;
	}
	
    /**
     * Connects to MongoDB based on the configuration settings.
     * <p/>
     * If the database is not reachable, an error message is written and the
     * application exits.
     */
    public boolean connect(final Configuration configuration) {
        String _mongoURI = configuration.getString("mongodb.uri");
        Logger.debug("MorphiaSingleton connect: " + _mongoURI);

        mongoUri = new MongoClientURI(_mongoURI);

        mongoClient = null;

        try {
        	mongoClient = new MongoClient(mongoUri);
        }
        catch(UnknownHostException e) {
            Logger.info("Unknown Host");
        }

        if (mongoClient != null) {
        	morphiaObject = new Morphia();
            morphiaStore = morphiaObject.createDatastore(mongoClient, mongoUri.getDatabase());

            //Map classes
            morphiaObject.map(UploadIpaEntry.class);
            
            morphiaStore.ensureIndexes();
            morphiaStore.ensureCaps();
        }

        Logger.debug("** MorphiaSingleton initialized datastore: " + morphiaStore.getDB());

        return true;
    }
}
