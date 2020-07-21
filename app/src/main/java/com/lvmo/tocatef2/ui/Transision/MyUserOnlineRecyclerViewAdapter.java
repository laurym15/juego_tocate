package com.lvmo.tocatef2.ui.Transision;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.aplicacion.Constantes;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.ui.FindGameActivity;

import java.util.List;


public class MyUserOnlineRecyclerViewAdapter extends RecyclerView.Adapter<MyUserOnlineRecyclerViewAdapter.ViewHolder> {

    private final List<User> mValues;
    private Context context;

    public MyUserOnlineRecyclerViewAdapter(List<User> items, Context conText) {
        mValues = items;
        this.context = this.context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_online, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.tvnombre.setText(mValues.get(position).getName());
        holder.tvpuntos.setText(String.valueOf(mValues.get(position).getPoints()));
        holder.BtInvitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FindGameActivity.class);
                i.putExtra(Constantes.EXTRA_TIPO_PARTIDA, "Invita?" + mValues.get(position).getFoto());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvnombre;
        public final TextView tvpuntos;
        public final ImageView indicadorOn;
        public final ImageButton BtInvitar;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvnombre = view.findViewById(R.id.item_nombre);
            tvpuntos = view.findViewById(R.id.item_puntos);
            indicadorOn = view.findViewById(R.id.imageView1);
            BtInvitar = view.findViewById(R.id.imageButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvnombre.getText() + "'";
        }
    }
}