package com.example.innovahouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProductoDetalleActivity extends AppCompatActivity {

    TextView txtNombre, txtPrecio, txtDescripcion;
    ImageView imgProducto;

    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;

    Button btnEditar, btnEliminar;

    DBHelper dbHelper;

    int productoId;
    String imagenActual = "";
    Button btnAnadirCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producto_detalle);

        dbHelper = new DBHelper(this);

        txtNombre = findViewById(R.id.txtNombreDetalle);
        txtPrecio = findViewById(R.id.txtPrecioDetalle);
        txtDescripcion = findViewById(R.id.txtDescripcionDetalle);
        imgProducto = findViewById(R.id.imgProductoDetalle);

        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);
        btnAnadirCarrito = findViewById(R.id.btnCarrito);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String rol = prefs.getString("rol", "usuario");

        if (!rol.equals("admin")) {
            btnEditar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        }

        productoId = getIntent().getIntExtra("productoId", -1);

        if (productoId != -1) cargarDetalleProducto(productoId);

        btnEditar.setOnClickListener(v -> mostrarDialogoEditar());
        btnEliminar.setOnClickListener(v -> confirmarEliminar());

        btnHome.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        });

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, CarritoActivity.class)));

        btnIconos.setOnClickListener(v ->
                Toast.makeText(this, "Función no implementada", Toast.LENGTH_SHORT).show());

        btnAnadirCarrito.setOnClickListener(v -> {
            // 1. Obtener el precio limpio (quitando el "S/. ")
            String precioTexto = txtPrecio.getText().toString().replace("S/. ", "");
            double precioFinal = 0.0;
            try {
                precioFinal = Double.parseDouble(precioTexto);
            } catch (NumberFormatException e) {
                precioFinal = 0.0;
            }

            // 2. Insertar en la Base de Datos usando el método que creamos en DBHelper
            String nombreFinal = txtNombre.getText().toString();

            // Llamamos al método agregarAlCarrito del DBHelper
            dbHelper.agregarAlCarrito(nombreFinal, precioFinal, imagenActual);

            Toast.makeText(this, "Producto añadido al carrito", Toast.LENGTH_SHORT).show();

            // Opcional: Si quieres ir al carrito inmediatamente, descomenta esto:
            // startActivity(new Intent(this, CarritoActivity.class));
        });

    }

    private void cargarDetalleProducto(int id) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos WHERE id=?",
                new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {

            txtNombre.setText(cursor.getString(1));
            txtPrecio.setText("S/. " + cursor.getDouble(2));
            txtDescripcion.setText(cursor.getString(3));
            imagenActual = cursor.getString(4);

            if (imagenActual != null && imagenActual.startsWith("content://")) {
                imgProducto.setImageURI(Uri.parse(imagenActual));

            } else {
                int resId = getResources().getIdentifier(imagenActual, "drawable", getPackageName());

                if (resId != 0) imgProducto.setImageResource(resId);
                else imgProducto.setImageResource(R.drawable.noimage);
            }
        }

        cursor.close();
        db.close();
    }

    private void mostrarDialogoEditar() {

        EditarProductoDialog dialog = new EditarProductoDialog(
                productoId,
                txtNombre.getText().toString(),
                Double.parseDouble(txtPrecio.getText().toString().replace("S/. ", "")),
                txtDescripcion.getText().toString(),
                imagenActual
        );

        dialog.show(getSupportFragmentManager(), "editarProducto");
    }

    private void confirmarEliminar() {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro que deseas eliminarlo?")
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    int filas = db.delete("productos", "id=?",
                            new String[]{String.valueOf(productoId)});
                    db.close();

                    if (filas > 0) {
                        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void refrescarProducto() {
        cargarDetalleProducto(productoId);
    }
}
