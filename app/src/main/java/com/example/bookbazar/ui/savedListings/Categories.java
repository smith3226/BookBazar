package com.example.bookbazar.ui.savedListings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookbazar.R;

public class Categories extends Fragment {
    private static final String TAG = "CategoriesFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Categories Fragment is created");
        return inflater.inflate(R.layout.activity_saved_list, container, false);
    }
}
