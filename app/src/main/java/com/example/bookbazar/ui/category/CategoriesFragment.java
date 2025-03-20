package com.example.bookbazar.ui.category;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bookbazar.R;

public class CategoriesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CategoriesFragment", "I am here");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_category_list, container, false);

        // Log when the view is created
        Log.d("CategoriesFragment", "onViewCreated: View is created");

        return view;
    }

}
