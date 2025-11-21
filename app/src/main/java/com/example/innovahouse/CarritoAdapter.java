package com.example.innovahouse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    ArrayList<Producto> lista;
    Context context;
    OnCarritoChangedListener listener;
    int usuarioId;

    public interface OnCarritoChangedListener {
        void onCarritoChanged();
    }

    public CarritoAdapter(ArrayList<Producto> lista, Context context, OnCarritoChangedListener listener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
        
        // Obtener usuarioId desde SharedPreferences
        android.content.SharedPreferences prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        this.usuarioId = prefs.getInt("usuarioId", -1);
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
        h.txtPrecio.setText("S/. " + String.format("%.2f", p.getPrecio()));

        String img = p.getImagen();

        if (img != null && img.startsWith("content://")) {
            h.imgProducto.setImageURI(Uri.parse(img));
        } else {
            int resId = context.getResources().getIdentifier(img, "drawable", context.getPackageName());
            if (resId != 0) {
                h.imgProducto.setImageResource(resId);
            } else {
                h.imgProducto.setImageResource(R.drawable.noimage);
            }
        }

        // Botón eliminar del carrito
        h.btnEliminar.setOnClickListener(v -> {
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("carrito", "usuario_id=? AND producto_id=?", 
                    new String[]{String.valueOf(usuarioId), String.valueOf(p.getId())});
            db.close();

            lista.remove(position);
            notifyItemRemoved(position);
            if (listener != null) {
                listener.onCarritoChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtPrecio;
        ImageView imgProducto;
        Button btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreCarrito);
            txtPrecio = itemView.findViewById(R.id.txtPrecioCarrito);
            imgProducto = itemView.findViewById(R.id.imgCarrito);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCarrito);
        }
    }
}
