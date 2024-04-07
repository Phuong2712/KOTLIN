package com.datpv134.closeeyesandlisten.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        binding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String userEmail = binding.etUserName.getText().toString();
        String userPassword = binding.etPassWord.getText().toString();

        if (!userEmail.matches(emailPattern)) {
            binding.etUserName.setError("Email không hợp lệ");
        } else if (userPassword.isEmpty() || userPassword.length() < 6) {
            binding.etPassWord.setError("Mật khẩu không hợp lệ (Từ 6 ký tự trở lên)");
        } else {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        sendUserToHomeActivity();
                        Toast.makeText(getBaseContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(getBaseContext(), "Đăng nhập không thành công", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void sendUserToHomeActivity() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
//        intent.putExtra("fromLogin", true);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}