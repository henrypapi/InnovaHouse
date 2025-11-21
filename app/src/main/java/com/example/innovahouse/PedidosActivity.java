package com.example.innovahouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PedidosActivity extends AppCompatActivity {

    RecyclerView recyclerPedidos;
    ImageView btnHome, btnIconos, btnPerfil, btnCarrito;
    DBHelper dbHelper;
    TextView tvNoPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        dbHelper = new DBHelper(this);

        btnHome = findViewById(R.id.btnmenu);
        btnIconos = findViewById(R.id.btniconos);
        btnPerfil = findViewById(R.id.btnperfil);
        btnCarrito = findViewById(R.id.btncarrito);
        recyclerPedidos = findViewById(R.id.recyclerPedidos);
        tvNoPedidos = findViewById(R.id.tvNoPedidos);

        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));

        // Verificar rol
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String rol = prefs.getString("rol", "usuario");

        if (!rol.equals("admin")) {
            Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar botones
        configurarBarraSuperior();

        // Cargar pedidos
        cargarPedidos();
    }

    private void cargarPedidos() {
        ArrayList<Pedido> listaPedidos = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT p.id, u.nombre, u.apellido, p.fecha_pedido, p.total, p.estado " +
                "FROM pedidos p JOIN usuarios u ON p.usuario_id = u.id ORDER BY p.fecha_pedido DESC",
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            String apellido = cursor.getString(2);
            String fecha = cursor.getString(3);
            double total = cursor.getDouble(4);
            String estado = cursor.getString(5);

            listaPedidos.add(new Pedido(id, nombre + " " + apellido, fecha, total, estado));
        }

        cursor.close();
        db.close();

        if (listaPedidos.isEmpty()) {
            tvNoPedidos.setVisibility(TextView.VISIBLE);
            recyclerPedidos.setVisibility(RecyclerView.GONE);
        } else {
            tvNoPedidos.setVisibility(TextView.GONE);
            recyclerPedidos.setVisibility(RecyclerView.VISIBLE);
            PedidosAdapter adapter = new PedidosAdapter(listaPedidos);
            recyclerPedidos.setAdapter(adapter);
        }
    }

    private void configurarBarraSuperior() {
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnIconos.setOnClickListener(v ->
                Toast.makeText(this, "Ya estás en Pedidos", Toast.LENGTH_SHORT).show()
        );

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class))
        );

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, AgregarProductoActivity.class))
        );
    }
}
