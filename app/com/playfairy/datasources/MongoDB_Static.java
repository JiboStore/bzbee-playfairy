package com.playfairy.datasources;

/**
 * Created by ntenisOT on 16/10/14.
 */

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.playfairy.models.UploadIpaEntry;

import models.*;
import org.mongodb.morphia.Morphia;
import play.Logger;
import play.Play;

import java.net.UnknownHostException;

public final class MongoDB_Static {

    /**
     * Connects to MongoDB based on the configuration settings.
     * <p/>
     * If the database is not reachable, an error message is written and the
     * application exits.
     */
    public static boolean connect() {
        String _mongoURI = Play.application().configuration().getString("mongodb.uri");

        MongoClientURI mongoURI = new MongoClientURI(_mongoURI);

        MorphiaObject_Static.mongo = null;

        try {
            MorphiaObject_Static.mongo = new MongoClient(mongoURI);
        }
        catch(UnknownHostException e) {
            Logger.info("Unknown Host");
        }

        if (MorphiaObject_Static.mongo != null) {
            MorphiaObject_Static.morphia = new Morphia();
            MorphiaObject_Static.datastore = MorphiaObject_Static.morphia.createDatastore(MorphiaObject_Static.mongo, mongoURI.getDatabase());

            //Map classes
            MorphiaObject_Static.morphia.map(UploadIpaEntry.class);
//            MorphiaObject.morphia.map(User.class);
//            MorphiaObject.morphia.map(LinkedAccount.class);
//            MorphiaObject.morphia.map(SecurityRole.class);
//            MorphiaObject.morphia.map(TokenAction.class);
//            MorphiaObject.morphia.map(UserPermission.class);

            MorphiaObject_Static.datastore.ensureIndexes();
            MorphiaObject_Static.datastore.ensureCaps();
        }

        Logger.debug("** Morphia datastore: " + MorphiaObject_Static.datastore.getDB());

        return true;
    }


    /**
     * Disconnect from MongoDB.
     */
    public static boolean disconnect() {
        if (MorphiaObject_Static.mongo == null) {
            return false;
        }

        MorphiaObject_Static.morphia = null;
        MorphiaObject_Static.datastore = null;
        MorphiaObject_Static.mongo.close();
        return true;
    }
}

