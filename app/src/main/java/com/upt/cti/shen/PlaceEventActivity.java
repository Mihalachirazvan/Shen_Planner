package com.upt.cti.shen;

import static com.upt.cti.shen.utils.Event.eventsList;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.shen.utils.Event;

public class PlaceEventActivity extends AppCompatActivity {
    private EditText edt_address;
    private Switch sw_like_adr;
    private Button bt_add_pictures;
    private Button bt_view_picture;
    private Event event = new Event("", false,false);
    private int PICK_IMAGE_MULTIPLE = 1;
    private int initial_length_photos;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_page);
        edt_address = (EditText) findViewById(R.id.edt_address);
        sw_like_adr = (Switch) findViewById(R.id.sw_like_adr);
        bt_add_pictures = (Button) findViewById(R.id.bt_add_pictures);
        bt_view_picture = (Button) findViewById(R.id.bt_view_pictures);
        event = (Event) getIntent().getSerializableExtra("Event");
        init();



        bt_add_pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                intent.setType("image/*");

                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
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
    private void init() {


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.event = new Event(extras.getString("eventName"), false,false);
        }
        initial_length_photos = eventsList.get(positionEvent()).getmArrayUri().size();
        if (eventsList.get(positionEvent()).getLocation_txt().length()!=0) {
            edt_address.setText(eventsList.get(positionEvent()).getLocation_txt());
        } else {
            edt_address.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (edt_address.getText().length() != 0) {
                        eventsList.get(positionEvent()).setLocation_txt(edt_address.getText().toString());
                        saveEventAddress();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        if (eventsList.get(positionEvent()).isLike_address()) {
            sw_like_adr.setChecked(true);
        }
            sw_like_adr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    eventsList.get(positionEvent()).setLike_address(b);
                    saveEventLikeAddress();
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
                eventsList.get(positionEvent()).getmArrayUri().add(imageurl);
                saveEventPhoto();
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
    public void saveEventLikeAddress() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("activities").document(eventsList.get(positionEvent()).getName()).
                update("like_address", eventsList.get(positionEvent()).isLike_address());
    }
    public void saveEventAddress() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("activities").document(eventsList.get(positionEvent()).getName()).
                update("location_txt", eventsList.get(positionEvent()).getLocation_txt());

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
