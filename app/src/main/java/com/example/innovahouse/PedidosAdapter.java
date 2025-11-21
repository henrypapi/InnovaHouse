package com.example.innovahouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {

    private final List<Pedido> listaPedidos;

    public PedidosAdapter(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        holder.tvCliente.setText(pedido.getCliente());
        holder.tvFecha.setText(pedido.getFecha());
        holder.tvTotal.setText("S/. " + pedido.getTotal());
        holder.tvEstado.setText("Estado: " + pedido.getEstado());

        // Color según estado
        if ("completado".equals(pedido.getEstado())) {
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if ("cancelado".equals(pedido.getEstado())) {
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvFecha, tvTotal, tvEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
