package com.example.a3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword,
            edtConfirmPassword, edtFirstName, edtLastName, edtEmail;
    private Button btnSignup;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String username, password, confirmPassword,firstName, lastName, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // get the instance of the Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance();
        // get the reference to the JSON tree
        databaseReference = firebaseDatabase.getReference();

        edtUsername = findViewById(R.id.editTextUsername);
        edtPassword = findViewById(R.id.editTextPassword);
        edtConfirmPassword = findViewById(R.id.ediTextConfirmPassword);
        edtFirstName = findViewById(R.id.editFirstName);
        edtLastName = findViewById(R.id.editTextLastName);
        edtEmail = findViewById(R.id.editTextEmail);

        btnSignup = findViewById(R.id.btnCreateAccount);
        btnSignup.setOnClickListener(view -> {
            if (checkFields()){
                addUser();
            }
        });

    }

    private boolean checkFields() {
        username = edtUsername.getText().toString().trim();
        password = edtPassword.getText().toString().trim();
        confirmPassword = edtConfirmPassword.getText().toString().trim();
        firstName = edtFirstName.getText().toString().trim();
        lastName = edtLastName.getText().toString().trim();
        email = edtEmail.getText().toString().trim();

        if(TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Please enter a first name",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Please enter a last name", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter a email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this, "password did not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void addUser() {
        // use push method to generate a unique key for a new child node
        String id = databaseReference.push().getKey();
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        // create a task to set the value of the node as the new user
        Task setValueTask = databaseReference.child("Users").child(id).setValue(user);

        // add a success listener to the task
        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(SignupActivity.this, "Account created",
                        Toast.LENGTH_LONG).show();
                Intent homeIntent = new Intent(SignupActivity.this,
                        HomeActivity.class);
                startActivity(homeIntent);
            }
        });

        // add a failure listener to the task
        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignupActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}