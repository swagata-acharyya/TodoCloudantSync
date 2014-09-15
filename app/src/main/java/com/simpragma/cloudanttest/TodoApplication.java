package com.simpragma.cloudanttest;

import android.app.Application;
import android.util.Log;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.indexing.IndexExistsException;
import com.cloudant.sync.indexing.IndexManager;
import com.cloudant.sync.indexing.QueryBuilder;
import com.cloudant.sync.indexing.QueryResult;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

/**
 * Created by swagata on 10/09/14.
 */
public class TodoApplication extends Application {
    public static final String DB_NAME = "trial";
    DatastoreManager manager;
    Datastore dataStore;
    IndexManager indexManager;
    @Override
    public void onCreate() {
        super.onCreate();
        File path = getApplicationContext().getFilesDir();
        manager = new DatastoreManager(path.getAbsolutePath());
        dataStore = manager.openDatastore(DB_NAME);
        try {
            URI uri = new URI("https://swagatatrial:pwd12345!@swagatatrial.cloudant.com/trial");
            Replicator replicator = ReplicatorFactory.oneway(uri, dataStore);
            replicator.start();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            indexManager = new IndexManager(dataStore);
            indexManager.ensureIndexed("default", "name");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexExistsException e) {
            e.printStackTrace();
        }
        QueryBuilder query = new QueryBuilder();
        query.index("default").equalTo("Swagata");
        QueryResult result = indexManager.query(query.build());
        Log.d("LOGDATA","result size is "+result.documentIds());
        for (DocumentRevision revision : result) {
            Log.d("LOGDATA",revision.getAttachments().keySet().toString());
        }

    }

}
