package com.example.bookbazar.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookbazar.R;

public class ChatFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ChatFragment", "I am here");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_book_list, container, false);

        // Log when the view is created
        Log.d("ChatFragment", "onViewCreated: View is created");

        return view;
    }
}
