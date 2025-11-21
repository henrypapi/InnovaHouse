package com.example.innovahouse;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        // Obtenemos el producto actual (usamos 'final' para usarlo dentro del click)
        final Producto p = lista.get(position);

        h.txtNombre.setText(p.getNombre());
        h.txtPrecio.setText(String.format("S/. %.2f", p.getPrecio()));

        // --- Carga de imagen (Mismo código de antes) ---
        String path = p.getImagen();
        boolean imagenCargada = false;
        if (path != null && !path.isEmpty()) {
            try {
                if (path.startsWith("content://") || path.startsWith("file://")) {
                    h.imgProducto.setImageURI(Uri.parse(path));
                    imagenCargada = true;
                } else {
                    File f = new File(path);
                    if (f.exists()) {
                        h.imgProducto.setImageURI(Uri.fromFile(f));
                        imagenCargada = true;
                    }
                }
            } catch (Exception e) {}
        }
        if (!imagenCargada && path != null) {
            String key = path.toLowerCase().replace(" ", "");
            int resId = context.getResources().getIdentifier(key, "drawable", context.getPackageName());
            if (resId != 0) {
                h.imgProducto.setImageResource(resId);
                imagenCargada = true;
            }
        }
        if (!imagenCargada) h.imgProducto.setImageResource(R.drawable.noimage);
        // -----------------------------------------------


        // -----------------------------------------------
        // LÓGICA DEL BOTÓN ELIMINAR (NUEVO)
        // -----------------------------------------------
        h.btnEliminar.setOnClickListener(v -> {

            // 1. Eliminar de la Base de Datos
            DBHelper db = new DBHelper(context);
            db.eliminarProductoCarrito(p.getId());

            // 2. Eliminar de la lista visual (ArrayList)
            // Necesitamos obtener la posición actual del adaptador porque puede haber cambiado
            int currentPosition = h.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                lista.remove(currentPosition);

                // 3. Notificar al adaptador que se quitó un ítem
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, lista.size());

                Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();

                // 4. Actualizar el precio total en el Activity
                if (context instanceof CarritoActivity) {
                    ((CarritoActivity) context).actualizarTotal();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio;
        ImageView imgProducto, btnEliminar; // Agregamos btnEliminar

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCarrito);
            txtPrecio = itemView.findViewById(R.id.txtPrecioCarrito);
            imgProducto = itemView.findViewById(R.id.imgCarrito);

            // Referencia al nuevo botón del XML
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}