package com.example.furniturewebshop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furniturewebshop.models.FurnitureItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class FurnitureFragment extends Fragment {

    private RecyclerView recyclerView;
    private FurnitureAdapter adapter;
    private ArrayList<FurnitureItem> itemList = new ArrayList<>();
    private Spinner sortSpinner;

    private FirebaseFirestore db;
    private DocumentSnapshot lastVisible = null;
    private boolean isLoading = false;

    private final int PAGE_SIZE = 10;
    private String currentSortField = "createdAt";
    private Query.Direction currentSortDirection = Query.Direction.DESCENDING;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_furniture, container, false);

        recyclerView = view.findViewById(R.id.furnitureRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FurnitureAdapter(itemList, false);
        recyclerView.setAdapter(adapter);

        setupAdapterListeners();

        sortSpinner = view.findViewById(R.id.sortSpinner);
        setupSortSpinner();

        db = FirebaseFirestore.getInstance();

        loadFurnitureItems();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            loadFurnitureItems();
                        }
                    }
                }
            }
        });

        return view;
    }

    private void setupAdapterListeners() {
        adapter.setOnItemEditClickListener((item, position) -> {
            Toast.makeText(getContext(), "Szerkesztés: " + item.getName(), Toast.LENGTH_SHORT).show();
            // Például: startActivity(EditFurnitureActivity.newIntent(...));
        });

        adapter.setOnItemDeleteClickListener((item, position) -> {
            db.collection("furniture_items")
                    .document(item.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Törölve: " + item.getName(), Toast.LENGTH_SHORT).show();
                        itemList.remove(position);
                        adapter.notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Hiba törléskor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapterSpinner);

        sortSpinner.setSelection(0, false);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lastVisible = null;
                itemList.clear();
                FurnitureFragment.this.adapter.notifyDataSetChanged();

                switch (position) {
                    case 0:
                        currentSortField = "createdAt";
                        currentSortDirection = Query.Direction.DESCENDING;
                        break;
                    case 1:
                        currentSortField = "price";
                        currentSortDirection = Query.Direction.ASCENDING;
                        break;
                }
                loadFurnitureItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nem csinál semmit
            }
        });
    }

    private void loadFurnitureItems() {
        isLoading = true;

        Query query = db.collection("furniture_items")
                .whereNotIn("category", Arrays.asList("Dekoráció", "Kert"))
                .orderBy(currentSortField, currentSortDirection)
                .limit(PAGE_SIZE);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            FurnitureItem item = doc.toObject(FurnitureItem.class);
                            item.setId(doc.getId());
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    isLoading = false;
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Hiba a lekérdezésnél: ", e);
                    isLoading = false;
                });
    }
}