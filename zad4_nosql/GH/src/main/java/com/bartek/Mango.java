package com.bartek;

import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class Mango {
    private final Datastore datastore;
    private static Mango mangoIns = new Mango();

    public Mango() {
        MongoClient client = new MongoClient("localhost", 8004);
        Morphia morphia = new Morphia();
        morphia.mapPackage("models");

        this.datastore = morphia.createDatastore(client, "DATABASE");
        this.datastore.ensureIndexes();
        this.datastore.enableDocumentValidation();
//        this.fillDummy(this);
    }

    public static Mango getMangoIns() {
        return mangoIns;
    }

    public static void setMangoIns(Mango mangoIns) {
        Mango.mangoIns = mangoIns;
    }
}
