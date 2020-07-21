package com.lvmo.tocatef2.ui.Raking;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.R;
import com.squareup.picasso.Picasso;


import java.util.List;

public class MyUserRecyclerViewAdapter extends RecyclerView.Adapter<MyUserRecyclerViewAdapter.ViewHolder> {
    private StorageReference mStorageRef;
    private final List<User> mValues;

    public MyUserRecyclerViewAdapter(List<User> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_raking, parent, false);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        int pos = position +1;
        holder.etPosicion.setText(pos +"°");
        holder.etAciertos.setText(String.valueOf(mValues.get(position).getPoints()));
        holder.etnombre.setText(mValues.get(position).getName());

        //Create an instance of StorageReference first (here in this code snippet, it is storageRef)
        String foto=mValues.get(position).getFoto();
        StorageReference filepath = mStorageRef.child("images/"+foto+".jpg");

        //If file exist in storage this works.
        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().toString();
                    Picasso.get()
                            .load(downloadUrl)
                            .resize(100, 100)
                            .error(R.drawable.tocate)
                            .into(holder.imFoto);
                }

            }
        });
       holder.imFoto.setImageResource(R.drawable.bistec);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imFoto;
        public final TextView etnombre;
        public final TextView etAciertos;
        public final TextView etPosicion;

        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imFoto = view.findViewById(R.id.imageView1_FUR);
            etnombre = view.findViewById(R.id.textView1_FUR);
            etAciertos = view.findViewById(R.id.textView3_FUR);
            etPosicion = view.findViewById(R.id.textView2_FUR);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + etnombre.getText() + "'";
        }
    }
}
