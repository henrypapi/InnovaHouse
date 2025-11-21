package com.example.innovahouse;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarritoActivity extends AppCompatActivity {

    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    RecyclerView recyclerCarrito;
    CarritoAdapter adapter;

    TextView tvTotal, btnRealizarPedido;

    ArrayList<Producto> listaCarrito = new ArrayList<>();
    DBHelper dbHelper;
    int usuarioId;

    ActivityResultLauncher<Intent> paymentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        dbHelper = new DBHelper(this);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);

        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));

        tvTotal = findViewById(R.id.tvTotal);
        btnRealizarPedido = findViewById(R.id.btnPagar);

        // Obtener usuario actual
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        usuarioId = prefs.getInt("usuarioId", -1);
        String rol = prefs.getString("rol", "usuario");

        if (rol.equals("admin")) {
            Toast.makeText(this, "El carrito es solo para clientes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar adapter
        adapter = new CarritoAdapter(listaCarrito, this, () -> actualizarTotal());
        recyclerCarrito.setAdapter(adapter);

        actualizarTotal();

        // Launcher para payment
        paymentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String metodo = result.getData().getStringExtra("metodoPago");
                        double total = result.getData().getDoubleExtra("total", 0.0);
                        finalizarPedido(metodo, total);
                    }
                }
        );

        // Botón realizar pedido
        btnRealizarPedido.setOnClickListener(v -> {
            if (listaCarrito.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            double total = calcularTotal();
            Intent intent = new Intent(CarritoActivity.this, PaymentActivity.class);
            intent.putExtra("total", total);
            paymentLauncher.launch(intent);
        });

        // Configurar botones superiores
        configurarBarraSuperior();

        // Cargar carrito
        cargarCarrito();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCarrito();
    }

    private void cargarCarrito() {
        listaCarrito.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT p.id, p.nombre, p.precio, p.descripcion, p.imagen, c.cantidad " +
                "FROM carrito c JOIN productos p ON c.producto_id = p.id " +
                "WHERE c.usuario_id=?",
                new String[]{String.valueOf(usuarioId)}
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            double precio = cursor.getDouble(2);
            String descripcion = cursor.getString(3);
            String imagen = cursor.getString(4);

            listaCarrito.add(new Producto(id, nombre, precio, descripcion, imagen));
        }

        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = calcularTotal();
        tvTotal.setText("Total: S/. " + String.format("%.2f", total));
    }

    private double calcularTotal() {
        double total = 0;
        for (Producto p : listaCarrito) {
            total += p.getPrecio();
        }
        return total;
    }

    private void finalizarPedido(String metodo, double total) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Crear pedido
            ContentValues valuesPedido = new ContentValues();
            valuesPedido.put("usuario_id", usuarioId);
            valuesPedido.put("total", total);
            valuesPedido.put("estado", "pendiente");
            valuesPedido.put("metodo_pago", metodo);

            long pedidoId = db.insert("pedidos", null, valuesPedido);

            if (pedidoId != -1) {
                // Agregar detalles del pedido
                for (Producto p : listaCarrito) {
                    ContentValues valuesDetalle = new ContentValues();
                    valuesDetalle.put("pedido_id", pedidoId);
                    valuesDetalle.put("producto_id", p.getId());
                    valuesDetalle.put("cantidad", 1);
                    valuesDetalle.put("precio_unitario", p.getPrecio());
                    db.insert("detalles_pedido", null, valuesDetalle);
                }

                // Limpiar carrito
                db.delete("carrito", "usuario_id=?", new String[]{String.valueOf(usuarioId)});

                Toast.makeText(this, "¡Pedido realizado exitosamente!", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Método de pago: " + metodo, Toast.LENGTH_SHORT).show();

                // Volver a inicio
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Error al crear el pedido", Toast.LENGTH_SHORT).show();
            }
        } finally {
            db.close();
        }
    }

    private void configurarBarraSuperior() {
        btnHome.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class))
        );

        btnIconos.setOnClickListener(v ->
                startActivity(new Intent(this, HistorialActivity.class))
        );

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );

        btnCarrito.setOnClickListener(v -> {}); // ya estás en carrito
    }
}
