package com.example.innovahouse;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AgregarProductoActivity extends AppCompatActivity {


    EditText etNombre, etPrecio, etDescripcion, etImagen;
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_producto);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        etImagen = findViewById(R.id.etImagen);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> guardarProducto());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void guardarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String precioTxt = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String imagen = etImagen.getText().toString().trim();

        if (nombre.isEmpty() || precioTxt.isEmpty() || descripcion.isEmpty() || imagen.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTxt);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("precio", precio);
        values.put("descripcion", descripcion);
        values.put("imagen", imagen);

        long resultado = db.insert("productos", null, values);
        db.close();

        if (resultado != -1) {
            Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
            finish(); // cerrar y volver al dashboard
        } else {
            Toast.makeText(this, "Error al agregar producto", Toast.LENGTH_SHORT).show();
        }
    }
}