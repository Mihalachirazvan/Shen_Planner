package com.upt.cti.shen;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static com.upt.cti.shen.utils.Event.eventsList;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.shen.utils.Event;
import com.upt.cti.shen.utils.FirebaseObject;

import java.util.HashMap;
import java.util.Map;


public class AnniversaryEventActivity extends AppCompatActivity {

    private TextView txt_title;
    private Switch sw_gift;
    private Button bt_add_pictures;
    private Button bt_view_picture;
    private Event event = new Event("", false);
    private int PICK_IMAGE_MULTIPLE = 1;
    private int initial_length_photos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anniversary_page);
        txt_title = (TextView) findViewById(R.id.txt_title);
        sw_gift = (Switch) findViewById(R.id.sw_gift);
        bt_add_pictures = (Button) findViewById(R.id.bt_add_pictures);
        bt_view_picture = (Button) findViewById(R.id.bt_view_pictures);
        event = (Event) getIntent().getSerializableExtra("Event");
        init();

        bt_add_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
        bt_view_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("Images",eventsList.get(positionEvent()).getmArrayUri());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    eventsList.get(positionEvent()).getmArrayUri().add(imageurl);

                }
                saveEventPhoto();
            } else {
                Uri imageurl = data.getData();
                System.out.println(imageurl);
                eventsList.get(positionEvent()).getmArrayUri().add(imageurl);
                saveEventPhoto();
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.event = new Event(extras.getString("eventName"), extras.getBoolean("eventWithGift"));
        }
        initial_length_photos = eventsList.get(positionEvent()).getmArrayUri().size();
        System.out.println(initial_length_photos);
        txt_title.setText(event.getName());
        if(event.isGift()) {
         sw_gift.setChecked(true);
        }
            sw_gift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    eventsList.get(positionEvent()).setGift(b);
                    saveEventGift();
                }
            });
    }
    private int positionEvent() {
        for(int j=0 ;j < eventsList.size();j++) {
            if(eventsList.get(j).getName().equals(event.getName())) {
                return j;
            }
        }
        return -1;
    }
    public void saveEventGift() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("activities").document(eventsList.get(positionEvent()).getName()).
                update("gift", eventsList.get(positionEvent()).isGift());
        }
    public void saveEventPhoto() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        for(int i = initial_length_photos ; i < eventsList.get(positionEvent()).getmArrayUri().size();i++) {
            System.out.println(i);
            db.collection("activities").document(eventsList.get(positionEvent()).getName()).
                    update("mArrayUri", FieldValue.arrayUnion(eventsList.get(positionEvent()).getmArrayUri().get(i).toString()));
        }
    }

}
