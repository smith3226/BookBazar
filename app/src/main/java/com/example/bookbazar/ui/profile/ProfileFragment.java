package com.example.bookbazar.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookbazar.LoginActivity;
import com.example.bookbazar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView userName, userEmail;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ProfileFragment", "Fragment is created");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_profile_screen, container, false);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Reference UI elements
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        profileImage = view.findViewById(R.id.profileImage);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Get current user info
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());

            // Load profile picture (if available)
            if (user.getPhotoUrl() != null) {
                ImageLoader imageLoader = new ImageLoader.Builder(requireContext()).build();
                ImageRequest request = new ImageRequest.Builder(requireContext())
                        .data(user.getPhotoUrl().toString())
                        .target(profileImage)
                        .build();
                imageLoader.enqueue(request);
            }
        }

        // Logout function
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();

            // Navigate back to LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish(); // Close the parent activity
        });

        Log.d("ProfileFragment", "onViewCreated: View is created");
        return view;
    }
}
