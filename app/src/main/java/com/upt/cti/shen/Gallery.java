package com.upt.cti.shen;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.upt.cti.shen.utils.CustomGalleryAdapter;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    private ImageView selectedImageView;
    private ListView simpleGallery;
    private ArrayList<Uri> images = new ArrayList<>();
    private CustomGalleryAdapter customGalleryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        selectedImageView = (ImageView) findViewById(R.id.selectedImageView);
        simpleGallery = (ListView) findViewById(R.id.simpleGallery);

        images = (ArrayList<Uri>) getIntent().getSerializableExtra("Images");

        customGalleryAdapter = new CustomGalleryAdapter(getApplicationContext(), images); // initialize the adapter
        simpleGallery.setAdapter(customGalleryAdapter); // set the adapter
        // perform setOnItemClickListener event on the Gallery
        simpleGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set the selected image in the ImageView
                selectedImageView.setImageURI(images.get(position));

            }
        });
    }

}
