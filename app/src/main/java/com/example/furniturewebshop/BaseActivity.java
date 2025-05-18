package com.example.furniturewebshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageView menuIcon, cartIcon, profileIcon, searchIcon, arrowIcon, logoImage;
    private TextView couponCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_bar);

        mAuth = FirebaseAuth.getInstance();

        menuIcon = findViewById(R.id.menu_icon);
        profileIcon = findViewById(R.id.profile_icon);
        cartIcon = findViewById(R.id.cart_icon);
        searchIcon = findViewById(R.id.search_icon);
        logoImage = findViewById(R.id.logoImage);

        couponCodeText = findViewById(R.id.coupon_code);
        arrowIcon = findViewById(R.id.arrow_icon);

        menuIcon.setOnClickListener(this::showHamburgerMenu);
        profileIcon.setOnClickListener(this::showProfileMenu);
        cartIcon.setOnClickListener(v -> openCart());
        couponCodeText.setOnClickListener(v -> copyToClipboard());
        arrowIcon.setOnClickListener(v -> openGardenPage());
        logoImage.setOnClickListener(v -> openMainActivity());

        hideSystemUI();
    }

    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(
                    android.view.WindowInsets.Type.statusBars()
                            | android.view.WindowInsets.Type.navigationBars()
            );
        } else {
            getWindow().setFlags(
                    android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
    }

    private void showHamburgerMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(BaseActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_furniture) {
                startActivity(new Intent(this, FurnitureActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.menu_home_decor) {
                startActivity(new Intent(this, HomeDecorActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.menu_garden) {
                startActivity(new Intent(this, GardenActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showProfileMenu(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        PopupMenu popupMenu = new PopupMenu(BaseActivity.this, view);

        if (user != null) {
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu_user, popupMenu.getMenu());

            if (!AdminManager.isAdmin(user.getUid())) {
                popupMenu.getMenu().removeItem(R.id.menu_admin);
            }
        } else {
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu_guest, popupMenu.getMenu());
        }


        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_login) {
                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.menu_register) {
                startActivity(new Intent(this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.menu_logout) {
                mAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.menu_admin) {
                startActivity(new Intent(this, AdminActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void openCart() {
        startActivity(new Intent(this, CartActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openGardenPage() {
        startActivity(new Intent(this, GardenActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void copyToClipboard() {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("couponCode", couponCodeText.getText().toString());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Kuponkód másolva a vágólapra!", Toast.LENGTH_SHORT).show();
    }
}