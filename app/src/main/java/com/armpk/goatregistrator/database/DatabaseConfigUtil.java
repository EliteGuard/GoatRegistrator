package com.armpk.goatregistrator.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    /*private static final Class<?>[] classes = new Class[] {
            Address.class,
            Breed.class,
            City.class,
            Farm.class,
            Goat.class,
            GoatStatus.class,
            Herd.class,
            Phone.class,
            User.class,
            UserRole.class,
    };*/

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt");
    }
}
