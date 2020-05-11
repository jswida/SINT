package com.bartek.nosql;

import com.bartek.models.Course;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class Mango {
    private static final boolean debug = true;
    private static Mango instance = new Mango();
    private final Datastore datastore;

    private Mango() {
        final Morphia morphia = new Morphia();
        morphia.mapPackage("models");

        datastore = morphia.createDatastore(new MongoClient(), "sint_db_1");
        datastore.ensureIndexes();

        loadMango();
    }

    public static Mango getInstance() {
        return instance;
    }

    public void loadMango() {
        if (debug) {
            datastore.delete(datastore.createQuery(Course.class));
            datastore.delete(datastore.createQuery(Student.class));
            datastore.delete(datastore.createQuery(Grade.class));
            datastore.delete(datastore.createQuery(Seq.class));
        }


    }



}
