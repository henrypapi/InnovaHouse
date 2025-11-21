package com.example.innovahouse;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AgregarProductoActivity extends AppCompatActivity {

    EditText etNombre, etPrecio, etDescripcion;
    ImageView imgPreview;
    Button btnSeleccionarImagen, btnGuardar;

    Uri imagenUri = null;
    DBHelper dbHelper;

    ActivityResultLauncher<Intent> seleccionarImagenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        imgPreview = findViewById(R.id.imagePreview);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = findViewById(R.id.btnGuardar);

        dbHelper = new DBHelper(this);

        // Registrar lanzador para seleccionar imagen
        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imagenUri = data.getData();
                            imgPreview.setImageURI(imagenUri);
                        }
                    }
                }
        );

        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());
        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        seleccionarImagenLauncher.launch(intent);
    }

    private void guardarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String precioTxt = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty() || precioTxt.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenUri == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTxt);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("precio", precio);
        values.put("descripcion", descripcion);
        values.put("imagen", imagenUri.toString()); // guardamos URI persistente

        long id = db.insert("productos", null, values);
        db.close();

        if (id != -1) {
            Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar producto", Toast.LENGTH_SHORT).show();
        }
    }
}
