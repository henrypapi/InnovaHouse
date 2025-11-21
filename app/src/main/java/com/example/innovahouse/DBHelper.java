package com.example.innovahouse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "innova.db";
    public static final int DB_VERSION = 3;  // Incrementado para agregar teléfono y tabla pedidos

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "apellido TEXT, " +
                "nombre TEXT, " +
                "email TEXT UNIQUE, " +
                "usuario TEXT UNIQUE, " +
                "clave TEXT, " +
                "rol TEXT DEFAULT 'usuario', " +
                "telefono TEXT" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "precio REAL, " +
                "descripcion TEXT, " +
                "imagen TEXT" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS carrito (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER, " +
                "producto_id INTEGER, " +
                "cantidad INTEGER, " +
                "fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(usuario_id) REFERENCES usuarios(id), " +
                "FOREIGN KEY(producto_id) REFERENCES productos(id)" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER, " +
                "fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "total REAL, " +
                "estado TEXT DEFAULT 'pendiente', " +
                "metodo_pago TEXT, " +
                "FOREIGN KEY(usuario_id) REFERENCES usuarios(id)" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS detalles_pedido (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pedido_id INTEGER, " +
                "producto_id INTEGER, " +
                "cantidad INTEGER, " +
                "precio_unitario REAL, " +
                "FOREIGN KEY(pedido_id) REFERENCES pedidos(id), " +
                "FOREIGN KEY(producto_id) REFERENCES productos(id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Agregar columna 'rol' si venimos de versión 1
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN rol TEXT DEFAULT 'usuario'");
            } catch (Exception ignored) { }
        }
        
        // Agregar columna 'telefono' si venimos de versión < 3
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN telefono TEXT");
            } catch (Exception ignored) { }
            
            // Crear tablas de carrito y pedidos
            try {
                db.execSQL("CREATE TABLE IF NOT EXISTS carrito (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario_id INTEGER, " +
                        "producto_id INTEGER, " +
                        "cantidad INTEGER, " +
                        "fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(usuario_id) REFERENCES usuarios(id), " +
                        "FOREIGN KEY(producto_id) REFERENCES productos(id)" +
                        ")");

                db.execSQL("CREATE TABLE IF NOT EXISTS pedidos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario_id INTEGER, " +
                        "fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "total REAL, " +
                        "estado TEXT DEFAULT 'pendiente', " +
                        "metodo_pago TEXT, " +
                        "FOREIGN KEY(usuario_id) REFERENCES usuarios(id)" +
                        ")");

                db.execSQL("CREATE TABLE IF NOT EXISTS detalles_pedido (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "pedido_id INTEGER, " +
                        "producto_id INTEGER, " +
                        "cantidad INTEGER, " +
                        "precio_unitario REAL, " +
                        "FOREIGN KEY(pedido_id) REFERENCES pedidos(id), " +
                        "FOREIGN KEY(producto_id) REFERENCES productos(id)" +
                        ")");
            } catch (Exception ignored) { }
        }
    }
}
