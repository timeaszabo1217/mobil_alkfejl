package com.example.furniturewebshop;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.furniturewebshop.models.FurnitureItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EditFurnitureFragment extends Fragment {

    private EditText nameEditText, priceEditText, categoryEditText;
    private Button updateButton;
    private ImageView selectedImageView;

    private FurnitureItem furnitureItem;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_furniture, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        categoryEditText = view.findViewById(R.id.categoryEditText);
        updateButton = view.findViewById(R.id.updateButton);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            furnitureItem = (FurnitureItem) getArguments().getSerializable("item");

            if (furnitureItem != null) {
                nameEditText.setText(furnitureItem.getName());
                priceEditText.setText(String.valueOf(furnitureItem.getPrice()));
                categoryEditText.setText(furnitureItem.getCategory());

                if (!"default".equals(furnitureItem.getImageUrl())) {
                    Picasso.get()
                            .load(furnitureItem.getImageUrl())
                            .placeholder(R.drawable.image)
                            .error(R.drawable.image)
                            .into(selectedImageView);
                }
            }
        }

        updateButton.setOnClickListener(v -> updateFurniture());

        return view;
    }

    private void updateFurniture() {
        String name = nameEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(category)) {
            Toast.makeText(getContext(), "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Az ár nem érvényes szám", Toast.LENGTH_SHORT).show();
            return;
        }

        furnitureItem.setName(name);
        furnitureItem.setPrice(price);
        furnitureItem.setCategory(category);

        db.collection("furniture_items")
                .document(furnitureItem.getId())
                .set(furnitureItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Sikeres módosítás!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Hiba mentés közben: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
