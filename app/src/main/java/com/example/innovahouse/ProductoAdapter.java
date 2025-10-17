package com.example.innovahouse;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final List<Producto> listaProductos;

    public ProductoAdapter(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔹 Inflar el layout de cada tarjeta (item_producto.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        // 🔹 Mostrar datos
        holder.txtNombre.setText(producto.getNombre());
        holder.txtPrecio.setText("S/. " + producto.getPrecio());
        holder.txtDescripcion.setText(producto.getDescripcion());

        // 🔹 Cargar imagen por nombre desde drawable
        int resId = holder.itemView.getContext().getResources()
                .getIdentifier(producto.getImagen(), "drawable",
                        holder.itemView.getContext().getPackageName());
        if (resId != 0) {
            holder.imgProducto.setImageResource(resId);
        } else {
            holder.imgProducto.setImageResource(R.drawable.noimage); // imagen por defecto
        }

        // 🔹 Evento: abrir detalle del producto
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductoDetalleActivity.class);
            intent.putExtra("productoId", producto.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    // 🔹 ViewHolder (representa una tarjeta de producto)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView txtNombre, txtPrecio, txtDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
        }
    }
}
