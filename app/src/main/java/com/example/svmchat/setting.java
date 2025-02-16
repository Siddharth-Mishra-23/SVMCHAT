package com.example.svmchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class setting extends AppCompatActivity {
    ImageView setprofile;
    EditText setname, setstatus;
    Button donebut;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String email, password;
    Uri setImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initializing Firebase services
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initializing views
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        donebut = findViewById(R.id.donebut);

        // Firebase references
        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        // Retrieving existing user data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("mail").getValue() != null) {
                        email = snapshot.child("mail").getValue().toString();
                    } else {
                        email = "No email";
                    }

                    if (snapshot.child("password").getValue() != null) {
                        password = snapshot.child("password").getValue().toString();
                    } else {
                        password = "No password";
                    }

                    if (snapshot.child("userName").getValue() != null) {
                        String name = snapshot.child("userName").getValue().toString();
                        setname.setText(name);
                    } else {
                        setname.setText("No name");
                    }

                    if (snapshot.child("profilepic").getValue() != null) {
                        String profile = snapshot.child("profilepic").getValue().toString();
                        Picasso.get().load(profile).into(setprofile);
                    } else {
                        setprofile.setImageResource(R.drawable.man); // Default image
                    }

                    if (snapshot.child("status").getValue() != null) {
                        String status = snapshot.child("status").getValue().toString();
                        setstatus.setText(status);
                    } else {
                        setstatus.setText("No status");
                    }
                } else {
                    Log.e("FirebaseData", "DataSnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });

        // Image selection
        setprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose a Profile Picture"), 10);
            }
        });

        // Save button functionality
        donebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = setname.getText().toString();
                String status = setstatus.getText().toString();

                if (setImageUri != null) {
                    // If a new image was selected, upload it
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String finalImageUri = uri.toString();
                                        Users users = new Users(auth.getUid(), name, email, password, finalImageUri, status);
                                        saveUserData(reference, users);
                                    }
                                });
                            } else {
                                Toast.makeText(setting.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // No new image selected, use existing profile picture
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String existingProfilePic = snapshot.child("profilepic").getValue(String.class);
                            Users users = new Users(auth.getUid(), name, email, password, existingProfilePic, status);
                            saveUserData(reference, users);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(setting.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    // Save user data method
    private void saveUserData(DatabaseReference reference, Users users) {
        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(setting.this, "HO GYA..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(setting.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(setting.this, "NHI HUA..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Handling the image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            setImageUri = data.getData();
            setprofile.setImageURI(setImageUri);
        }
    }
}
