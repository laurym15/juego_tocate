package com.lvmo.tocatef2.ui.Transision;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lvmo.tocatef2.R;
import com.lvmo.tocatef2.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserOnlineFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    List<User> userList;
    MyUserOnlineRecyclerViewAdapter adapter;
    FirebaseFirestore db;
    private FirebaseAuth fireBaseAuth;

    RecyclerView recyclerView;

    public UserOnlineFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserOnlineFragment newInstance(int columnCount) {
        UserOnlineFragment fragment = new UserOnlineFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        fireBaseAuth = fireBaseAuth.getInstance();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_online_list, container, false);

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
                    .whereEqualTo("online", "on")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(getContext(), getString(R.string.TlistenFaild), Toast.LENGTH_LONG).show();
                                return;
                            }

                            userList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                if (!doc.get("foto").equals(fireBaseAuth.getCurrentUser().getUid())) {
                                    User userItem = doc.toObject(User.class);
                                    userList.add(userItem);
                                }
                                ;
                                adapter = new MyUserOnlineRecyclerViewAdapter(userList, getContext());
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });

        }
        return view;
    }
}