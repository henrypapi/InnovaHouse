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
        View v = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Producto p = lista.get(position);
        h.txtNombre.setText(p.getNombre());
        h.txtPrecio.setText("S/. " + p.getPrecio());

        String path = p.getImagen();

        // mostrar interna
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                h.imgProducto.setImageURI(Uri.fromFile(f));
                return;
            }
        }

        // fallback drawable
        String key = p.getNombre().toLowerCase().replace(" ", "");
        int resId = context.getResources().getIdentifier(key, "drawable", context.getPackageName());
        if (resId != 0) {
            h.imgProducto.setImageResource(resId);
            return;
        }

        h.imgProducto.setImageResource(R.drawable.noimage);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
