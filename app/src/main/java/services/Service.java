package services;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.upt.cti.shen.R;


public class Service extends AppCompatActivity {

    private Button btDate;
    private Button btTimeStart;
    private Button btTimeEnd;
    private Button btLocation;
    int PLACE_PICKER_REQUEST = 1;
    int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.task_page);
//        btDate = (Button) findViewById(R.id.btDate);
//        btTimeStart = (Button) findViewById(R.id.btTimeStart);
//        btTimeEnd = (Button) findViewById(R.id.btTimeEnd);
//        btLocation = (Button) findViewById(R.id.btLocation);
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Please select a date");
        MaterialDatePicker materialDatePicker = builder.build();

        btDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "Date_Picker");
            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                btDate.setText(materialDatePicker.getHeaderText().toString());
            }
        });

    }

    public void clicked(View view) throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
//        switch (view.getId()) {
//            case R.id.btTimeStart:
//                System.out.println("aici");
//                TimePickerDialog.OnTimeSetListener onTimeSetListenerStart = new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
//                        hour = sHour;
//                        minute = sMinute;
//                        btTimeStart.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
//
//                    }
//
//                };
//                TimePickerDialog timePickerDialogStart = new TimePickerDialog(this, onTimeSetListenerStart, hour, minute, true);
//                timePickerDialogStart.setTitle("Please select time");
//                timePickerDialogStart.show();
//                break;
//            case R.id.btTimeEnd:
//                TimePickerDialog.OnTimeSetListener onTimeSetListenerEnd = new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
//                        hour = sHour;
//                        minute = sMinute;
//                        btTimeEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", sHour, sMinute));
//                    }
//                };
//                TimePickerDialog timePickerDialogEnd = new TimePickerDialog(this, onTimeSetListenerEnd, hour, minute, true);
//                timePickerDialogEnd.setTitle("Please select time");
//                timePickerDialogEnd.show();
//                break;
//            case R.id.btLocation:
//
//                System.out.println("Location");
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
//
//                break;
//        }
    }
}
