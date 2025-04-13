package com.example.furniturewebshop;

import android.os.Bundle;

public class HomeDecorActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new MainFragment())
                    .commit();
        }
    }
}