package com.example.furniturewebshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniturewebshop.models.FurnitureItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private FurnitureAdapter adapter;
    private ArrayList<FurnitureItem> itemList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button addButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_admin, container, false);

        recyclerView = view.findViewById(R.id.adminRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FurnitureAdapter(itemList, true);
        recyclerView.setAdapter(adapter);

        addButton = view.findViewById(R.id.addButton);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null || !AdminManager.isAdmin(user.getUid())) {
            Toast.makeText(getContext(), "Nincs jogosultság", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
            return view;
        }

        addButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AddFurnitureFragment())
                    .addToBackStack(null)
                    .commit();
        });

        adapter.setOnItemEditClickListener((item, position) -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);

            EditFurnitureFragment editFragment = new EditFurnitureFragment();
            editFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, editFragment)
                    .addToBackStack(null)
                    .commit();
        });

        adapter.setOnItemDeleteClickListener((item, position) -> {
            db.collection("furniture_items")
                    .document(item.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        itemList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), "Törölve!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Törlés sikertelen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        loadItems();

        return view;
    }

    private void loadItems() {
        db.collection("furniture_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        FurnitureItem item = doc.toObject(FurnitureItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            itemList.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("refreshRequest", this, (requestKey, bundle) -> {
            boolean refresh = bundle.getBoolean("refresh", false);
            if (refresh) {
                loadItems();
            }
        });
    }
}