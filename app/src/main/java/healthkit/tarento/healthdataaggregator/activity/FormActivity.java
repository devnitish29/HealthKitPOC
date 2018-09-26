package healthkit.tarento.healthdataaggregator.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import healthkit.tarento.healthdataaggregator.R;
import healthkit.tarento.healthdataaggregator.model.UserInfo;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edName,edDob,edPhKin1,edPhKin2,edphDoc;
    Button btnSave;
    Spinner spGender,spWeight,spHeight;
    Calendar myCalendar = Calendar.getInstance();
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        sp = getSharedPreferences("USER_DATA",MODE_PRIVATE);
        editor = sp.edit();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("User Detail");
        toolbar.setTitleTextColor(Color.WHITE);
        edDob = findViewById(R.id.edDob);
        edName = findViewById(R.id.edName);
        edPhKin1 = findViewById(R.id.edphKin1);
        edPhKin2 = findViewById(R.id.edphKin2);
        edphDoc = findViewById(R.id.edphDoc);
        btnSave = findViewById(R.id.btnSave);
        spGender =findViewById(R.id.spGender);
        spWeight =findViewById(R.id.spWeight);
        spHeight =findViewById(R.id.spHeight);

        edDob.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };


    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edDob.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.edDob:
                new DatePickerDialog(FormActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnSave:
                saveData();
        }

    }

    private void saveData() {


        UserInfo userInfo = new UserInfo();
        userInfo.setName(edName.getText().toString().trim());
        userInfo.setDob(edDob.getText().toString().trim());
        userInfo.setGender(spGender.getSelectedItem().toString());
        userInfo.setWeight(spWeight.getSelectedItem().toString());
        userInfo.setHeight(spHeight.getSelectedItem().toString());
        userInfo.setPhKin1(edPhKin1.getText().toString().trim());
        userInfo.setPhKin2(edPhKin2.getText().toString().trim());
        userInfo.setPhDoc(edphDoc.getText().toString().trim());

        Gson gson = new Gson();
        String userDataString = gson.toJson(userInfo);
        editor.putString("USER",userDataString);
        editor.commit();
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);


    }
}
