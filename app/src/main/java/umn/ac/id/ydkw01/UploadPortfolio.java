package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadPortfolio extends AppCompatActivity {
    private static final int READ_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST_CODE = 101;

    ImageView postpreview;
    ImageButton btnpick, btnback, btnuploadport;
    ProgressBar loadingpost;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fstorage;
    FirebaseUser user;
    DocumentReference reference;
    String userID;
    Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_portfolio);
        getSupportActionBar().hide();

        postpreview = findViewById(R.id.postpreview);
        btnpick = findViewById(R.id.btnpick);
        btnback = findViewById(R.id.btndiscardport);
        btnuploadport = findViewById(R.id.btnuploadport);
        loadingpost = findViewById(R.id.loadingpost);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fstorage = FirebaseStorage.getInstance();
        user = fAuth.getCurrentUser();
        userID = user.getUid();
        reference = fStore.collection("users").document(userID);

        btnpick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissionAndPickImage();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UploadPortfolio.this, PortfolioActivity.class);
                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        btnuploadport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imguri != null){
                    uploadPortfolio(imguri);
                }else {
                    Toast.makeText(UploadPortfolio.this, "No Image Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imguri = data.getData();
            Glide.with(this).load(imguri).into(postpreview);
//            Picasso.get().load(imguri).into(postpreview);
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mt = MimeTypeMap.getSingleton();
        return mt.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadPortfolio(Uri uri) {
        StorageReference storageReference = fstorage.getReference("users/" + user.getUid() + "/" + "portfolios/").child(System.currentTimeMillis() + "." + getFileExt(uri));

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", task.getException().toString());
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            Map<String,Object> imageUrl = new HashMap<>();
                            imageUrl.put("PortfolioUrl", downloadUri.toString());
                            reference.set(imageUrl, SetOptions.merge());
                            Toast.makeText(UploadPortfolio.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                            loadingpost.setVisibility(View.INVISIBLE);

                            Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(UploadPortfolio.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                loadingpost.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingpost.setVisibility(View.INVISIBLE);
                Toast.makeText(UploadPortfolio.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


}