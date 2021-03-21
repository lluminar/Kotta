package net.lidia.iessochoa.kotta.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Partitura.class}, version = 2)

@TypeConverters({TransformaFechaSQLite.class})
public abstract class PartituraDatabase extends RoomDatabase {

    public abstract PartituraDao partituraDao();

    private static volatile PartituraDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static PartituraDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PartituraDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            //nombre del fichero de la base de datos
                            PartituraDatabase.class, "partituras_database")
                            .addCallback(sRoomDatabaseCallback)//para ejecutar al crear o al abrir
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                PartituraDao mDao = INSTANCE.partituraDao();
                SimpleDateFormat textFormat = new SimpleDateFormat("dd-MM-yyyy");
                Partitura partitura = null;
                try {
                    partitura = new Partitura(textFormat.parse("21-2-2020"), "Sonata","Piano",
                            "Vivaldi","Clasica","fjdsfd.pdf");
                    mDao.insert(partitura);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }
    };
}

