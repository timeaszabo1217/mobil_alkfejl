package com.example.furniturewebshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView nameTextView, emailTextView;
    private Button changePasswordButton, deleteAccountButton, savePasswordButton;
    private EditText currentPasswordEditText, newPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        savePasswordButton = view.findViewById(R.id.savePasswordButton);
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            nameTextView.setText(user.getDisplayName() != null ? user.getDisplayName() : "Nincs név");
            emailTextView.setText(user.getEmail() != null ? user.getEmail() : "");
        } else {
            Toast.makeText(getActivity(), "Nincs bejelentkezve felhasználó", Toast.LENGTH_SHORT).show();
        }

        currentPasswordEditText.setVisibility(View.GONE);
        newPasswordEditText.setVisibility(View.GONE);
        savePasswordButton.setVisibility(View.GONE);

        changePasswordButton.setOnClickListener(v -> {
            currentPasswordEditText.setVisibility(View.VISIBLE);
            newPasswordEditText.setVisibility(View.VISIBLE);
            savePasswordButton.setVisibility(View.VISIBLE);
            changePasswordButton.setVisibility(View.GONE);
        });

        savePasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(getActivity(), "Töltse ki az összes mezőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user1 = mAuth.getCurrentUser();
            if (user1 != null) {
                user1.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Jelszó sikeresen módosítva!", Toast.LENGTH_SHORT).show();
                        currentPasswordEditText.setVisibility(View.GONE);
                        newPasswordEditText.setVisibility(View.GONE);
                        savePasswordButton.setVisibility(View.GONE);
                        changePasswordButton.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Hiba: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        deleteAccountButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(getActivity())
                    .setMessage("Biztos vagy benne, hogy törölni szeretnéd a fiókodat?")
                    .setCancelable(false)
                    .setPositiveButton("Igen", (dialog, id) -> {
                        FirebaseUser user2 = mAuth.getCurrentUser();
                        if (user2 != null) {
                            user2.delete().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "A fiók sikeresen törölve lett!", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Hiba történt: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Nincs bejelentkezve felhasználó", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Mégse", (dialog, id) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        return view;
    }
}