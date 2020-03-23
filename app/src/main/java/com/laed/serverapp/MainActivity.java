package com.laed.serverapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.uylab.serverapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnCompleteListener<UploadTask.TaskSnapshot>, MultiplePermissionsListener {

    private final int PICK_IMAGE = 71;
    private static final int CAMERA_REQUEST_CODE=1;
    ArrayList<Uri> imageList=new ArrayList<Uri>();
    private Uri ImageUri;
    private TextView alerttext;
    private Button chosse;
    private Button upload;
    private int uploadCount=0;
    ProgressDialog progressDialog;
    public static final String Storage_Path = "ImageFolder";
    String current_photo_path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alerttext=findViewById(R.id.alerttext);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Image is uploading...");
        chosse=findViewById(R.id.chooseimage);
        upload=findViewById(R.id.uploadimage);
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(this).check();


    }

    public void ChooseImageToUpload(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent,PICK_IMAGE);

    }

    public void UploadImage(View view) {
        progressDialog.show();
        StorageReference imagefolder = FirebaseStorage.getInstance().getReference().child(Storage_Path);
        for (uploadCount=0;uploadCount<imageList.size();uploadCount++)
        {
            Uri individualImage=imageList.get(uploadCount);
            Log.d("individualImage","individualImage"+individualImage);
            StorageReference imageName=imagefolder.child("Image"+individualImage.getLastPathSegment());
            Log.d("imageName","imageName"+individualImage);
            imageName.putFile(individualImage).addOnCompleteListener(this);


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {
            StorageReference firebaseStorage=FirebaseStorage.getInstance().getReference().child(Storage_Path);
            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri uri= getImageUri(getApplicationContext(), photo);
            Log.d("capture", "image: "+uri);
            StorageReference filepath=firebaseStorage.child("Image"+uri.getLastPathSegment());
            filepath.putFile(uri).addOnCompleteListener(this);

        }
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getClipData() != null )
        {
            int countClipData=data.getClipData().getItemCount();
            int currentImageSelect=0;
            upload.setVisibility(View.VISIBLE);
            while(currentImageSelect < countClipData)
            {
                ImageUri=data.getClipData().getItemAt(currentImageSelect).getUri();
                imageList.add(ImageUri);
                currentImageSelect++;
            }
            alerttext.setText("You have selected "+imageList.size()+" Images!");

        }

        else {
            Toast.makeText(this,"Please Select Multiple Image",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
        if(task.isSuccessful())
        {
            Log.d("chk","success:"+task.getResult().getUploadSessionUri().getPath());
            progressDialog.dismiss();
            upload.setVisibility(View.INVISIBLE);
            alerttext.setText("Image Uploaded Successfully!");
           // Toast.makeText(MainActivity.this,"Image Uploaded Successfully!",Toast.LENGTH_SHORT).show();
            imageList.clear();
        }
        else {
            Log.d("chk","failed:"+task.getException().getMessage());
        }
    }

    public void ShowAllImages(View view) {
        Intent intent = new Intent(MainActivity.this, DisplayImageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {

    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

    }

    //Upload Image from Camera Directly
    public void TakeImageToUpload(View view) {

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_REQUEST_CODE);
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }
}
