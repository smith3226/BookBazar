package com.example.bookbazar;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.category.CategoriesFragment;
import com.example.bookbazar.ui.chat.ChatFragment;
import com.example.bookbazar.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Main layout with BottomNavigationView

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment()); // Load HomeFragment initially
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment(); // Home Fragment
            } else if (itemId == R.id.nav_categories) {
                selectedFragment = new CategoriesFragment(); // Categories Fragment
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatFragment(); // Chat Fragment
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    // Method to open Fragments inside FrameLayout
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // Replace with your FrameLayout ID
                .commit();
    }
}
