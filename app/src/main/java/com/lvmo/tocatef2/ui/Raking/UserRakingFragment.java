package com.lvmo.tocatef2.ui.Raking;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lvmo.tocatef2.model.User;
import com.lvmo.tocatef2.R;

import java.util.ArrayList;
import java.util.List;


public class UserRakingFragment extends Fragment {

    private int mColumnCount = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";

    private List<User> userList;
    private  MyUserRecyclerViewAdapter adapter;
    private  FirebaseFirestore db;
    private  RecyclerView recyclerView;


    public static UserRakingFragment newInstance(int columnCount) {
        UserRakingFragment fragment = new UserRakingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public UserRakingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_raking_list, container, false);

      db = FirebaseFirestore.getInstance();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            db.collection("users")
                    .orderBy("points", Query.Direction.DESCENDING)
                    .limit(100)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            userList =new ArrayList<>();
                            for(DocumentSnapshot document: task.getResult()){
                                User userItem = document.toObject(User.class);
                                userList.add(userItem);

                                adapter= new MyUserRecyclerViewAdapter(userList);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });
        }
        return view;
    }
}
