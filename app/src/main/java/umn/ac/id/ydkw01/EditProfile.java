package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private static final int READ_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST_CODE = 101;
    public static final String TAG = "TAG";

    EditText edtname, edtemail, edtnis;
    Button btnsave;
    ImageButton btnback;
    CircleImageView edtprofileimg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    Uri imguri;
    DocumentReference reference;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        Intent data = getIntent();
        String fullname = data.getStringExtra("fullname");
        String email = data.getStringExtra("email");
        String nis = data.getStringExtra("nis");

        edtname = findViewById(R.id.edtname);
        edtemail = findViewById(R.id.edtemail);
        edtnis = findViewById(R.id.edtnis);
        edtprofileimg = findViewById(R.id.edtprofileimg);
        btnsave = findViewById(R.id.savebtn);
        btnback = findViewById(R.id.btnback);
        RequestManager manager = Glide.with(edtprofileimg);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        userID = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = fStore.collection("users").document(userID);

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                manager.load(snapshot.getString("Profilepict")).into(edtprofileimg);
            }
        });

        edtprofileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissionAndPickImage();
            }
        });

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtname.getText().toString().isEmpty() || edtemail.getText().toString().isEmpty() || edtnis.getText().toString().isEmpty()){
                    Toast.makeText(EditProfile.this, "Fields must not empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imguri != null){
                    uploadProfilepicture(imguri);
                }
                String email = edtemail.getText().toString();

                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("Email", email);
                        edited.put("Fullname", edtname.getText().toString());
                        edited.put("NIS", edtnis.getText().toString());
                        reference.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        });
                        Toast.makeText(EditProfile.this, "Email is Changed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        edtname.setText(fullname);
        edtemail.setText(email);
        edtnis.setText(nis);

        Log.d(TAG, "oncreate :" + fullname +" "+ email +" "+ nis);
    }

    private void verifyPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            }
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void pickImage() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imguri = data.getData();
            Glide.with(this).load(imguri).into(edtprofileimg);
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mt = MimeTypeMap.getSingleton();
        return mt.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadProfilepicture(Uri uri) {
        StorageReference fileref = storageReference.child("users/"+user.getUid()+"/profile pict" + "." + getFileExt(uri));

        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", task.getException().toString());
                        }
                        return fileref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            Map<String,Object> imageUrl = new HashMap<>();
                            imageUrl.put("Profilepict", downloadUri.toString());
                            reference.set(imageUrl, SetOptions.merge());
                            Toast.makeText(EditProfile.this, "Upload Successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(EditProfile.this, "Profile Picture Update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Profile Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}