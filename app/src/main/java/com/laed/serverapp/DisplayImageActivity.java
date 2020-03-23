package com.laed.serverapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.uylab.serverapp.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayImageActivity extends AppCompatActivity {

    // Creating DatabaseReference.
    StorageReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<StorageReference> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter=new RecycleViewAdapter(this,list);
        recyclerView.setAdapter(adapter);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);




        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DisplayImageActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseStorage.getInstance().getReference(MainActivity.Storage_Path);

        // Adding Add Value Event Listener to databaseReference.
    databaseReference.getParent().child(MainActivity.Storage_Path).listAll().addOnCompleteListener(command -> {
        if(command.isSuccessful())
        {
            ListResult listResult=command.getResult();
            for(StorageReference item: listResult.getItems())
            {
                list.add(item);
                //Log.d("chk","item:"+item.getDownloadUrl().toString());
            }
            progressDialog.dismiss();
            adapter.notifyDataSetChanged();
//            listResult.getItems().get(0).getDownloadUrl().addOnCompleteListener(command1 -> {
//                if(command.isSuccessful())
//                {
//                    Log.d("chk","url:"+command.getResult().getItems().toString());
//                }
//                else {
//                    Log.d("failed","url:"+command.getException().getMessage());
//                }
//            });
//StorageReference reference=listResult.getItems().get(0);
//            final long ONE_MEGABYTE = 1024 * 1024;
//reference.getBytes(ONE_MEGABYTE).addOnCompleteListener(command1 -> {
//    byte[] bytes=command1.getResult();
//    Glide.with(DisplayImageActivity.this).load(bytes).into(imageView);
//});
        //  FirebaseIm
          // Glide.with(DisplayImageActivity.this).load(listResult.getItems().get(0)).into(imageView);

        }
        else {
            Log.d("failed","success:"+command.getException().getMessage());
        }
    });


//        databaseReference.(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//
//                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
//
//                    list.add(imageUploadInfo);
//                }
//
//                adapter = new RecycleViewAdapter(getApplicationContext(), list);
//
//                recyclerView.setAdapter(adapter);
//
//                // Hiding the progress dialog.
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                // Hiding the progress dialog.
//                progressDialog.dismiss();
//
//            }
//        });

    }
}