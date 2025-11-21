package com.example.innovahouse;

import android.content.ContentValues;
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

    TextView txtNombre, txtPrecio, txtDescripcion, txtEspecificaciones;
    ImageView imgProducto;
    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    Button btnEditar, btnEliminar, btnComprar, btnAgregarCarrito, btnSalir;

    DBHelper dbHelper;
    int productoId;
    String imagenActual = "";
    double precioProducto = 0;
    String rol = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producto_detalle);

        dbHelper = new DBHelper(this);

        txtNombre = findViewById(R.id.txtNombreDetalle);
        txtPrecio = findViewById(R.id.txtPrecioDetalle);
        txtDescripcion = findViewById(R.id.txtDescripcionDetalle);
        txtEspecificaciones = findViewById(R.id.txtEspecificacionesDetalle);
        imgProducto = findViewById(R.id.imgProductoDetalle);

        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnComprar = findViewById(R.id.btnComprar);
        btnAgregarCarrito = findViewById(R.id.btnAgregarCarritoDetalle);
        btnSalir = findViewById(R.id.btnSalir);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarritoNav);

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        rol = prefs.getString("rol", "usuario");
        int usuarioId = prefs.getInt("usuarioId", -1);

        productoId = getIntent().getIntExtra("productoId", -1);

        // Mostrar/ocultar botones según rol
        if (!rol.equals("admin")) {
            btnEditar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        }

        if (productoId != -1) cargarDetalleProducto(productoId);

        // Evento Editar
        btnEditar.setOnClickListener(v -> mostrarDialogoEditar());

        // Evento Eliminar
        btnEliminar.setOnClickListener(v -> confirmarEliminar());

        // Evento Comprar (para clientes)
        btnComprar.setOnClickListener(v -> {
            if (rol.equals("cliente") || rol.equals("usuario")) {
                agregarAlCarritoYIrAPago(productoId, usuarioId);
            } else {
                Toast.makeText(this, "Solo los clientes pueden comprar", Toast.LENGTH_SHORT).show();
            }
        });

        // Evento Agregar al Carrito
        btnAgregarCarrito.setOnClickListener(v -> {
            agregarAlCarrito(productoId, usuarioId);
        });

        // Evento Salir
        btnSalir.setOnClickListener(v -> finish());

        // Navegación superior
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
                Toast.makeText(this, "Volviendo a inicio", Toast.LENGTH_SHORT).show());
    }

    public void cargarDetalleProducto(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos WHERE id=?",
                new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            txtNombre.setText(cursor.getString(1));
            precioProducto = cursor.getDouble(2);
            txtPrecio.setText("S/. " + String.format("%.2f", precioProducto));
            txtDescripcion.setText(cursor.getString(3));
            imagenActual = cursor.getString(4);

            // Cargar especificaciones según el producto
            cargarEspecificaciones(cursor.getString(1));

            if (imagenActual != null && imagenActual.startsWith("content://")) {
                imgProducto.setImageURI(Uri.parse(imagenActual));
            } else {
                int resId = getResources().getIdentifier(imagenActual, "drawable", getPackageName());
                if (resId != 0) {
                    imgProducto.setImageResource(resId);
                } else {
                    imgProducto.setImageResource(R.drawable.noimage);
                }
            }
        }

        cursor.close();
        db.close();
    }

    private void cargarEspecificaciones(String nombreProducto) {
        String especificaciones = "";

        switch (nombreProducto.toLowerCase()) {
            case "refrigeradora":
                especificaciones = "• Capacidad: 400L\n" +
                        "• Tipo: No Frost\n" +
                        "• Dimensiones: 70x180x75 cm\n" +
                        "• Consumo: 50 kWh/mes\n" +
                        "• Garantía: 2 años";
                break;
            case "lavadora":
                especificaciones = "• Capacidad: 12 kg\n" +
                        "• Velocidad: 1200 RPM\n" +
                        "• Programas: 15 ciclos\n" +
                        "• Dimensiones: 60x85x65 cm\n" +
                        "• Garantía: 2 años";
                break;
            case "microondas":
                especificaciones = "• Potencia: 900W\n" +
                        "• Capacidad: 30L\n" +
                        "• Controles: Digitales\n" +
                        "• Dimensiones: 51x29x45 cm\n" +
                        "• Garantía: 1 año";
                break;
            case "cocina":
                especificaciones = "• Quemadores: 6 (4 gas + 2 eléctricos)\n" +
                        "• Horno: Convencional\n" +
                        "• Dimensiones: 90x85x60 cm\n" +
                        "• Material: Acero inoxidable\n" +
                        "• Garantía: 2 años";
                break;
            case "cafetera":
                especificaciones = "• Capacidad: 1.8L\n" +
                        "• Potencia: 1000W\n" +
                        "• Tazas: Hasta 12\n" +
                        "• Dimensiones: 24x30x20 cm\n" +
                        "• Garantía: 1 año";
                break;
            case "licuadora":
                especificaciones = "• Potencia: 500W\n" +
                        "• Velocidades: 6\n" +
                        "• Jarra: 1.5L de vidrio\n" +
                        "• Dimensiones: 15x15x40 cm\n" +
                        "• Garantía: 1 año";
                break;
            default:
                especificaciones = "Especificaciones no disponibles";
        }

        txtEspecificaciones.setText(especificaciones);
    }

    private void agregarAlCarrito(int productoId, int usuarioId) {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("usuario_id", usuarioId);
        values.put("producto_id", productoId);
        values.put("cantidad", 1);

        long result = db.insert("carrito", null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al agregar al carrito", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarAlCarritoYIrAPago(int productoId, int usuarioId) {
        agregarAlCarrito(productoId, usuarioId);
        // Ir al carrito
        startActivity(new Intent(this, CarritoActivity.class));
    }

    private void mostrarDialogoEditar() {
        EditarProductoDialog dialog = new EditarProductoDialog(
                productoId,
                txtNombre.getText().toString(),
                precioProducto,
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
}
