package com.datpv134.closeeyesandlisten.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityChangePassBinding;
import com.datpv134.closeeyesandlisten.model.MyUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassActivity extends AppCompatActivity {
    ActivityChangePassBinding binding;
    FirebaseUser user;
    FirebaseDatabase database;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pass);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        binding.btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
            }
        });
    }

    private void changePass() {
        String oldPass = binding.etOldPass.getText().toString();
        String newPass = binding.etNewPass.getText().toString();
        String reNewPass = binding.etReNewPass.getText().toString();

        if (TextUtils.isEmpty(oldPass)) {
            unChange();
            return;
        }

        if (newPass.length() < 6) {
            binding.etNewPass.setError("Mật khẩu mới không được ít hơn 6 ký tự");
            return;
        }

        if (!newPass.equals(reNewPass)) {
            binding.etReNewPass.setError("Mật khẩu không khớp");
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MyUser myUser = snapshot.getValue(MyUser.class);

                if (myUser.getPassword().equals(oldPass)) {
                    updatePass(newPass);
                    finish();
                } else {
                    unChange();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updatePass(String s) {
        user.updatePassword(s)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            database.getReference().child("Users").child(userID)
                                    .child("password").setValue(binding.etNewPass.getText().toString().trim());
                            Log.e("changePass", "success");
                            finish();
                        }
                    }
                });
    }

    private void unChange() {
        binding.etOldPass.setError("Mật khẩu cũ sai");
    }
}