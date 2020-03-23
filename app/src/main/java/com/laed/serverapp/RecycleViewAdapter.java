package com.laed.serverapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.uylab.serverapp.R;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    Context context;
    List<StorageReference> MainImageUploadInfoList;

    public RecycleViewAdapter(Context context, List<StorageReference> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);

        // holder.imageNameTextView.setText(UploadInfo.getImageName());

        //Loading image from Glide library.
        //  Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imageView);
        StorageReference reference = MainImageUploadInfoList.get(position);
        final long ONE_MEGABYTE = 5024 * 5024;
        reference.getBytes(ONE_MEGABYTE).addOnCompleteListener(command1 -> {
            if(command1.isSuccessful())
            {
                byte[] bytes = command1.getResult();
                Glide.with(context).load(bytes).into(holder.imageView);
            }
            else{

            }
        });
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView imageNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageview);

        }
    }
}