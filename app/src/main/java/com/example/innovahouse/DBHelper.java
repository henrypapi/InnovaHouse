package com.example.innovahouse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "innova.db";
    public static final int DB_VERSION = 2;  // Incrementado para añadir 'rol'

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "apellido TEXT, " +
                "nombre TEXT, " +
                "email TEXT, " +
                "usuario TEXT UNIQUE, " +
                "clave TEXT, " +
                "rol TEXT DEFAULT 'usuario'" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "precio REAL, " +
                "descripcion TEXT, " +
                "imagen TEXT" +   // guardamos URI o nombre; ahora usaremos URI
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Agregar la columna 'rol' sin perder datos si venimos de versión 1
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN rol TEXT DEFAULT 'usuario'");
            } catch (Exception ignored) { }
        }
        // No borramos tablas para no perder datos
    }
}
