package com.example.innovahouse;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final List<Producto> listaProductos;
    private Context context;

    public ProductoAdapter(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        holder.txtNombre.setText(producto.getNombre());
        holder.txtPrecio.setText("S/. " + String.format("%.2f", producto.getPrecio()));
        holder.txtDescripcion.setText(producto.getDescripcion());

        String img = producto.getImagen();

        if (img != null && img.startsWith("content://")) {
            holder.imgProducto.setImageURI(Uri.parse(img));
        } else {
            int resId = holder.itemView.getContext().getResources()
                    .getIdentifier(img, "drawable",
                            holder.itemView.getContext().getPackageName());

            if (resId != 0) {
                holder.imgProducto.setImageResource(resId);
            } else {
                holder.imgProducto.setImageResource(R.drawable.noimage);
            }
        }

        // Obtener rol y usuarioId
        SharedPreferences prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        String rol = prefs.getString("rol", "usuario");
        int usuarioId = prefs.getInt("usuarioId", -1);

        // Botón Ver Detalles - para todos (incluyendo admin)
        holder.btnVerDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductoDetalleActivity.class);
            intent.putExtra("productoId", producto.getId());
            context.startActivity(intent);
        });

        // Botón Agregar al Carrito (icono pequeño) - solo para clientes
        if (rol.equals("admin")) {
            holder.btnAgregarCarrito.setVisibility(ImageView.GONE);
        } else {
            holder.btnAgregarCarrito.setVisibility(ImageView.VISIBLE);
            holder.btnAgregarCarrito.setOnClickListener(v -> {
                agregarAlCarrito(producto, usuarioId);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    private void agregarAlCarrito(Producto producto, int usuarioId) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("usuario_id", usuarioId);
        values.put("producto_id", producto.getId());
        values.put("cantidad", 1);

        long result = db.insert("carrito", null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error al agregar al carrito", Toast.LENGTH_SHORT).show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        ImageView btnAgregarCarrito;
        TextView txtNombre, txtPrecio, txtDescripcion;
        Button btnVerDetalles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
        }
    }
}
