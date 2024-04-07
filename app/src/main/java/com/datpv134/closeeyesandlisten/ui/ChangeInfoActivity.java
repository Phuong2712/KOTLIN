package com.datpv134.closeeyesandlisten.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityChangeInfoBinding;
import com.datpv134.closeeyesandlisten.model.MyUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ChangeInfoActivity extends AppCompatActivity {
    ActivityChangeInfoBinding binding;
    FirebaseUser user;
    FirebaseDatabase database;
    MyUser myUser;
    String currentName, changeName;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_info);

        user = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        if (user != null) {
            String uId = user.getUid();
            database = FirebaseDatabase.getInstance();
            database.getReference("Users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myUser = snapshot.getValue(MyUser.class);
                    currentName = myUser.getName();
                    binding.etChangeName.setText(currentName);
//                    Glide.with(getBaseContext()).load(myUser.getProfileImg()).fitCenter().into(binding.imgProfileInChange);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        binding.btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeName = binding.etChangeName.getText().toString().trim();
                if (!changeName.equals(currentName)) {
                    database.getReference("Users").child(user.getUid()).child("name").setValue(changeName);
                }
                finish();
            }
        });

        binding.imgProfileInChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openActivityForResult();
            }
        });
    }

//    public void openActivityForResult() {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        activityResultLauncher.launch(intent);
//    }
}