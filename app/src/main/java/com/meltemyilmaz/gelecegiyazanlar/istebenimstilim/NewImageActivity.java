package com.meltemyilmaz.gelecegiyazanlar.istebenimstilim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class NewImageActivity extends AppCompatActivity {

    private ImageView imageViewProductPhoto;
    private EditText editTextInformation;
    private Button buttonAddPhoto, buttonAddProduct;

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    private Uri downloadUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image);

        imageViewProductPhoto = (ImageView) findViewById(R.id.imageViewProductPhoto);
        editTextInformation = (EditText) findViewById(R.id.editTextInformation);
        buttonAddPhoto = (Button) findViewById(R.id.buttonAddPhoto);
        buttonAddProduct = (Button) findViewById(R.id.buttonAddProduct);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Toast.makeText(getApplicationContext(), "onAuthStateChanged:signed_in:" + user.getUid(), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "onAuthStateChanged:signed_out", Toast.LENGTH_SHORT).show();
                }
            }
        };


        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (downloadUrl != null) {
                    addProduct(editTextInformation.getText().toString(),"User Information", downloadUrl.toString(),0);

                } else {
                    addProduct(editTextInformation.getText().toString(),"User Information","",0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            Uri targetUri = data.getData();

            if (targetUri != null) {
                addPhoto(targetUri);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void addPhoto(Uri targetUri) {

        Bitmap bitmap;

        try
        {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            imageViewProductPhoto.setImageBitmap(bitmap);
            imageViewProductPhoto.setDrawingCacheEnabled(true);
            imageViewProductPhoto.buildDrawingCache();
            bitmap = imageViewProductPhoto.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);

            String uid = "";
            if (user != null) {
                uid = user.getUid().toString();
            }

            StorageReference imagesRef = mStorageRef.child("images/"+ uid + "/" + System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = imagesRef.putBytes(baos.toByteArray());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Fotoğraf Yüklenemedi.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "Fotoğraf Yüklendi.", Toast.LENGTH_SHORT).show();
                    downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(String information, String userInformation, String imageURL, int favoriteCount) {

        String key = mDatabase.child("posts").push().getKey();
        String uid = "";

        BaseImage product = new BaseImage(information, userInformation, imageURL, favoriteCount);
        Map<String, Object> productValues = product.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (user != null) {
            uid = user.getUid().toString();
        }

        childUpdates.put("/posts/" + key, productValues);
        childUpdates.put("/user-posts/" + uid + "/" + key, productValues);

        mDatabase.updateChildren(childUpdates);
        editTextInformation.setText("");
    }
}
