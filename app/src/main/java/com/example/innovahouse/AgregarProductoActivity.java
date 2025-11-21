package com.example.innovahouse;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
    Button btnSeleccionarImagen, btnGuardar, btnCancelar;

    Uri imagenUri = null;
    DBHelper dbHelper;

    ActivityResultLauncher<Intent> seleccionarImagenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        // Verificar rol
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String rol = prefs.getString("rol", "usuario");

        if (!rol.equals("admin")) {
            Toast.makeText(this, "Solo administradores pueden agregar productos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        imgPreview = findViewById(R.id.imagePreview);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

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
        btnCancelar.setOnClickListener(v -> finish());
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

        // Validaciones
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("El nombre es requerido");
            etNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(precioTxt)) {
            etPrecio.setError("El precio es requerido");
            etPrecio.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(descripcion)) {
            etDescripcion.setError("La descripción es requerida");
            etDescripcion.requestFocus();
            return;
        }

        if (imagenUri == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTxt);
            if (precio <= 0) {
                etPrecio.setError("El precio debe ser mayor a 0");
                etPrecio.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrecio.setError("Precio inválido");
            etPrecio.requestFocus();
            return;
        }

        // Guardar producto
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("precio", precio);
        values.put("descripcion", descripcion);
        values.put("imagen", imagenUri.toString());

        long id = db.insert("productos", null, values);
        db.close();

        if (id != -1) {
            Toast.makeText(this, "¡Producto agregado exitosamente!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar producto", Toast.LENGTH_SHORT).show();
        }
    }
}
