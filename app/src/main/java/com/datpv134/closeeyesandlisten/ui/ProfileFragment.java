package com.datpv134.closeeyesandlisten.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.FragmentProfileBinding;
import com.datpv134.closeeyesandlisten.model.MyUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    FirebaseUser user;
    DatabaseReference reference;
    MyUser myUser = new MyUser();
    String userId;

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setUpUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            binding.btnSignUp.setVisibility(View.GONE);
            binding.btnSignIn.setVisibility(View.GONE);
            binding.tvChangeInfo.setVisibility(View.VISIBLE);
            binding.tvChangePass.setVisibility(View.VISIBLE);
            binding.btnSignOut.setVisibility(View.VISIBLE);
            binding.tvHelloUser.setVisibility(View.VISIBLE);
            reference = FirebaseDatabase.getInstance().getReference("Users");
            userId = user.getUid();

            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myUser = snapshot.getValue(MyUser.class);

                    if (myUser != null || getContext() != null) {
//                        Glide.with(getContext()).load(myUser.getProfileImg()).fitCenter().into(binding.imgProfile);
                        binding.tvHelloUser.setText("Xin chào, " + myUser.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        setUpUser();

        binding.tvChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChangeInfoActivity.class));
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RegisterActivity.class));
            }
        });

        binding.tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChangePassActivity.class));
            }
        });

        binding.vAddNewPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    startActivity(new Intent(getContext(), InUpdatingActivity.class));
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle("Thông báo")
                            .setMessage("Bạn cần đăng nhập để có thể sử dụng chức năng này")
                            .setPositiveButton("Tắt thông báo", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }
        });

        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                binding.btnSignUp.setVisibility(View.VISIBLE);
                binding.btnSignIn.setVisibility(View.VISIBLE);
                binding.tvChangeInfo.setVisibility(View.GONE);
                binding.tvChangePass.setVisibility(View.GONE);
                binding.btnSignOut.setVisibility(View.GONE);
                binding.tvHelloUser.setVisibility(View.GONE);
                binding.imgProfile.setImageResource(R.drawable.ic_launcher_foreground);
                user = null;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUser();
    }
}
