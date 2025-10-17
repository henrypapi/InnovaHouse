package com.example.innovahouse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProductoDetalleActivity extends AppCompatActivity {
    TextView txtNombre, txtPrecio, txtDescripcion;
    ImageView imgProducto;
    Button btnEditar, btnEliminar;
    int productoId;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producto_detalle);

        txtNombre = findViewById(R.id.txtNombreDetalle);
        txtPrecio = findViewById(R.id.txtPrecioDetalle);
        txtDescripcion = findViewById(R.id.txtDescripcionDetalle);
        imgProducto = findViewById(R.id.imgProductoDetalle);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        dbHelper = new DBHelper(this);

        // Obtener ID del producto desde el intent
        productoId = getIntent().getIntExtra("productoId", -1);

        if (productoId != -1) {
            cargarDetalleProducto(productoId);
        }

        btnEditar.setOnClickListener(v -> mostrarDialogoEditar());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());
    }

    private void cargarDetalleProducto(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(1);
            double precio = cursor.getDouble(2);
            String descripcion = cursor.getString(3);
            String imagen = cursor.getString(4);

            txtNombre.setText(nombre);
            txtPrecio.setText("S/. " + precio);
            txtDescripcion.setText(descripcion);

            int imgId = getResources().getIdentifier(imagen, "drawable", getPackageName());
            if (imgId != 0) imgProducto.setImageResource(imgId);
        }

        cursor.close();
        db.close();
    }

    private void mostrarDialogoEditar() {
        // Crear un AlertDialog con campos para editar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar producto");

        // Inflar un layout simple con campos de texto
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_producto, null);
        builder.setView(dialogView);

        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecio);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        EditText etImagen = dialogView.findViewById(R.id.etImagen);

        // Cargar datos actuales
        etNombre.setText(txtNombre.getText().toString());
        etPrecio.setText(txtPrecio.getText().toString().replace("S/. ", ""));
        etDescripcion.setText(txtDescripcion.getText().toString());

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString().trim();
            String precioTxt = etPrecio.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String imagen = etImagen.getText().toString().trim();

            if (nombre.isEmpty() || precioTxt.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio = Double.parseDouble(precioTxt);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("precio", precio);
            values.put("descripcion", descripcion);
            values.put("imagen", imagen);

            int filas = db.update("productos", values, "id = ?", new String[]{String.valueOf(productoId)});
            db.close();

            if (filas > 0) {
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                cargarDetalleProducto(productoId);
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void confirmarEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de eliminar este producto?")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int filas = db.delete("productos", "id = ?", new String[]{String.valueOf(productoId)});
                    db.close();

                    if (filas > 0) {
                        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                        finish(); // Regresar al dashboard
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
