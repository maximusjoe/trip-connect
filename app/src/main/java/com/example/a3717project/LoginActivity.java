package com.example.a3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnSignup, btnLogin, btnDelete;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String username, password;
    static LoginActivity LOGIN_INSTANCE;
    private User currentUser;

    public static LoginActivity getLoginActivity() {
        return LOGIN_INSTANCE;
    }
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        LOGIN_INSTANCE = this;
        // get the instance of the Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        // get the reference to the JSON tree
        databaseReference = firebaseDatabase.getReference();

        edtUsername = findViewById(R.id.editTextUsername);
        edtPassword = findViewById(R.id.editTextPassword);

        btnSignup = findViewById(R.id.textViewNoAccount);
        btnSignup.setOnClickListener(view -> {
            Intent homeIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(homeIntent);
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            if(checkFields()) {
                login();
            }
        });

    }

    private boolean checkFields() {
        username = edtUsername.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        if(TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void login() {
        // add a value event listener to the Users node
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            // called to read a static snapshot of the contents at a given path
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean match = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if(username.equals(user.getUsername()) && password.equals(user.getPassword())){
                        match = true;
                        Toast.makeText(LoginActivity.this, "Access granted",
                                Toast.LENGTH_LONG).show();
                        currentUser = user;
                    }
                }

                if(!match) {
                    Toast.makeText(LoginActivity.this, "no user found",
                            Toast.LENGTH_LONG).show();
                } else {
                    //Goes to HomeActivity if login success.
                    Log.d("login", "about to log him in");
                    Intent homeIntent = new Intent(LoginActivity.this,
                            HomeActivity.class);
                    homeIntent.putExtra("username", username);
                    startActivity(homeIntent);
                }
            }

            // called when the client doesn't have permission to access the data
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}