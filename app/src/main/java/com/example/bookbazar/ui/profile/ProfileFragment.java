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
import com.example.bookbazar.ui.listing.AddListing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import coil.ImageLoader;
import coil.request.ImageRequest;

import com.example.bookbazar.ui.profile.MyOrdersActivity;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView userName, userEmail;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("ProfileFragment", "Fragment is created");

        View view = inflater.inflate(R.layout.activity_profile_screen, container, false);

        mAuth = FirebaseAuth.getInstance();

        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        profileImage = view.findViewById(R.id.profileImage);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        Button addListingBtn = view.findViewById(R.id.addListingBtn);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());

            if (user.getPhotoUrl() != null) {
                ImageLoader imageLoader = new ImageLoader.Builder(requireContext()).build();
                ImageRequest request = new ImageRequest.Builder(requireContext())
                        .data(user.getPhotoUrl().toString())
                        .target(profileImage)
                        .build();
                imageLoader.enqueue(request);
            }
        }

        addListingBtn.setOnClickListener(activityView ->{
                Intent intent = new Intent(getActivity(), AddListing.class);
                startActivity(intent);
        });


        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish(); // Close the parent activity
        });

        Button myOrdersBtn = view.findViewById(R.id.btnMyOrders);

        myOrdersBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
            startActivity(intent);
        });


        Log.d("ProfileFragment", "onViewCreated: View is created");
        return view;
    }
}
