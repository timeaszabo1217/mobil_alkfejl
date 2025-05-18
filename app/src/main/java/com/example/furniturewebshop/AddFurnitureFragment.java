package com.example.furniturewebshop;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.furniturewebshop.models.FurnitureItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddFurnitureFragment extends Fragment {

    private EditText nameEditText, priceEditText, categoryEditText;
    private Button saveButton, selectImageButton;
    private ImageView selectedImageView;

    private Uri selectedImageUri;

    private FirebaseFirestore db;
    private StorageReference storageRef;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_furniture, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        categoryEditText = view.findViewById(R.id.categoryEditText);
        saveButton = view.findViewById(R.id.saveButton);
        selectImageButton = view.findViewById(R.id.selectImageButton);
        selectedImageView = view.findViewById(R.id.selectedImageView);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("furniture_images");

        setupActivityResultLaunchers();

        selectImageButton.setOnClickListener(v -> {
            if (hasImagePermission()) {
                openFileChooser();
            } else {
                requestImagePermission();
            }
        });

        saveButton.setOnClickListener(v -> saveFurniture());

        return view;
    }

    private void setupActivityResultLaunchers() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openFileChooser();
                    } else {
                        Toast.makeText(getContext(), "Engedély szükséges a képek kiválasztásához", Toast.LENGTH_SHORT).show();
                    }
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Picasso.get().load(selectedImageUri).into(selectedImageView);
                        }
                    }
                });
    }

    private boolean hasImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void saveFurniture() {
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

        if (selectedImageUri != null) {
            String imageFileName = UUID.randomUUID().toString();
            StorageReference fileRef = storageRef.child(imageFileName);

            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                saveItemToDatabase(name, price, category, imageUrl);
                            }).addOnFailureListener(uriEx -> {
                                Toast.makeText(getContext(), "Nem sikerült lekérni a kép URL-jét, 'default' képet használunk", Toast.LENGTH_SHORT).show();
                                saveItemToDatabase(name, price, category, "default");
                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Hiba képfeltöltéskor, 'default' képet használunk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        saveItemToDatabase(name, price, category, "default");
                    });
        } else {
            saveItemToDatabase(name, price, category, "default");
        }
    }

    private void saveItemToDatabase(String name, int price, String category, String imageUrl) {
        FurnitureItem item = new FurnitureItem(name, price, category, imageUrl, new com.google.firebase.Timestamp(new java.util.Date()));

        db.collection("furniture_items")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Sikeres mentés!", Toast.LENGTH_SHORT).show();
                    Bundle result = new Bundle();
                    result.putBoolean("refresh", true);
                    getParentFragmentManager().setFragmentResult("refreshRequest", result);
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Hiba adatbázis mentéskor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
