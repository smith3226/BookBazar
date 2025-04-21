package com.example.bookbazar;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bookbazar.ui.savedListings.SavedListings;
import com.example.bookbazar.ui.home.HomeFragment;
import com.example.bookbazar.ui.profile.ProfileFragment;
import com.example.bookbazar.ui.savedListings.SavedListings;
import com.example.bookbazar.ui.search.Search;
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
            } else if (itemId == R.id.nav_savedListings) {
                selectedFragment = new SavedListings(); // Categories Fragment
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new Search(); // Search Fragment
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment(); // Profile Fragment
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
