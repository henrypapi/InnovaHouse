package com.example.innovahouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "innova.db";
    // IMPORTANTE: Cambiamos a versión 3 para que se ejecute el onUpgrade y cree la nueva tabla
    public static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabla Usuarios
        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "apellido TEXT, " +
                "nombre TEXT, " +
                "email TEXT, " +
                "usuario TEXT UNIQUE, " +
                "clave TEXT, " +
                "rol TEXT DEFAULT 'usuario'" +
                ")");

        // Tabla Productos
        db.execSQL("CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "precio REAL, " +
                "descripcion TEXT, " +
                "imagen TEXT" +
                ")");

        // NUEVA TABLA: Carrito
        // Creamos una tabla separada para guardar lo que el usuario quiere comprar
        db.execSQL("CREATE TABLE IF NOT EXISTS carrito (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "precio REAL, " +
                "imagen TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la versión anterior era menor a 2, agregamos columna rol (tu código anterior)
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN rol TEXT DEFAULT 'usuario'");
            } catch (Exception ignored) { }
        }

        // Si la versión anterior es menor a 3 (la actual), creamos la tabla carrito
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS carrito (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, " +
                    "precio REAL, " +
                    "imagen TEXT" +
                    ")");
        }
    }

    // ---------------------------------------------
    // MÉTODOS PARA EL CARRITO (NUEVOS)
    // ---------------------------------------------

    // 1. Agregar un producto al carrito
    public void agregarAlCarrito(String nombre, double precio, String imagen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("precio", precio);
        values.put("imagen", imagen);

        db.insert("carrito", null, values);
        db.close();
    }

    // 2. Obtener la lista de productos en el carrito
    public ArrayList<Producto> obtenerCarrito() {
        ArrayList<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM carrito", null);
        while (cursor.moveToNext()) {
            // Creamos el objeto Producto con los datos de la tabla carrito
            // Nota: Usamos una descripción vacía "" porque la tabla carrito no la tiene
            lista.add(new Producto(
                    cursor.getInt(0),   // id
                    cursor.getString(1), // nombre
                    cursor.getDouble(2), // precio
                    "",                  // descripcion (no necesaria en carrito)
                    cursor.getString(3)  // imagen
            ));
        }
        cursor.close();
        db.close();
        return lista;
    }

    // 3. Vaciar carrito (para cuando pagues)
    public void vaciarCarrito() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM carrito");
        db.close();
    }
}