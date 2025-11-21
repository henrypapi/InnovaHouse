package com.example.innovahouse;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    ArrayList<Producto> lista;
    Context context;

    public CarritoAdapter(ArrayList<Producto> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Aquí enlazamos con tu diseño item_carrito.xml
        View v = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Producto p = lista.get(position);

        // Asignamos los textos
        h.txtNombre.setText(p.getNombre());
        // Formato a 2 decimales para que se vea profesional (ej. 1500.00)
        h.txtPrecio.setText(String.format("S/. %.2f", p.getPrecio()));

        // Lógica para la Imagen (Maneja tanto recursos drawable como URIs de galería)
        String path = p.getImagen();

        boolean imagenCargada = false;

        // 1. Intentar cargar como URI (si viene de la galería)
        if (path != null && !path.isEmpty()) {
            try {
                if (path.startsWith("content://") || path.startsWith("file://")) {
                    h.imgProducto.setImageURI(Uri.parse(path));
                    imagenCargada = true;
                }
                // 2. Intentar cargar como archivo local
                else {
                    File f = new File(path);
                    if (f.exists()) {
                        h.imgProducto.setImageURI(Uri.fromFile(f));
                        imagenCargada = true;
                    }
                }
            } catch (Exception e) {
                imagenCargada = false;
            }
        }

        // 3. Si no es URI, intentar cargar como recurso Drawable (ej. "lavadora")
        if (!imagenCargada && path != null) {
            String key = path.toLowerCase().replace(" ", ""); // limpieza básica
            int resId = context.getResources().getIdentifier(key, "drawable", context.getPackageName());
            if (resId != 0) {
                h.imgProducto.setImageResource(resId);
                imagenCargada = true;
            }
        }

        // 4. Si todo falla, imagen por defecto
        if (!imagenCargada) {
            h.imgProducto.setImageResource(R.drawable.noimage);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Referencias a los elementos dentro de item_carrito.xml
        TextView txtNombre, txtPrecio;
        ImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCarrito);
            txtPrecio = itemView.findViewById(R.id.txtPrecioCarrito);
            imgProducto = itemView.findViewById(R.id.imgCarrito);
        }
    }
}