package com.example.inAppKeyboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements InputStatusTracker {
    private TextView touch_x,touch_y;
    private MyCharacterKeyboard keyboard;
    private String inputText;
    private TextView input_text_view;
    private InputStream is;
    private BufferedReader reader;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyro;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private float a_x,a_y,a_z;
    private TextView accel_x,accel_y,accel_z;
    private TextView gyro_x,gyro_y,gyro_z;
    private TextView char_count;

    private Button show_data_but;
    private ConstraintLayout data_view;

    private EditText editText;
    private Button next_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        touch_x = (TextView) findViewById(R.id.touch_x);
        touch_y = (TextView) findViewById(R.id.touch_y);

//        initalize keyboard
//        final EditText editText = (EditText) findViewById(R.id.editText);
        editText = (EditText) findViewById(R.id.editText);

        keyboard = (MyCharacterKeyboard) findViewById(R.id.character_keyboard);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setTextIsSelectable(true);


        ReadTextFile();

//       read the first line of the txt file
        inputText = readTextLine();

//        Spannable WordtoSpan = new SpannableString(inputText);
//        WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        editText.setText(WordtoSpan);

        input_text_view = (TextView) findViewById(R.id.input_text);
        input_text_view.setText(inputText);

        editText.setText(inputText);
        editText.setTextColor(Color.rgb(160,160,160));
//        editText.setTextColor(color.);

        char_count = (TextView) findViewById(R.id.char_count);
        char_count.setText("(0/"+inputText.length()+")");
//        set cursor to the beginning to the text
//        editText.setCursorVisible(true);
//      add cursor
        editText.requestFocus();
        editText.setSelection(0);

        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);

//        initalize sensors
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(mAccelSensorListener, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        accel_x = (TextView) findViewById(R.id.accel_x);
        accel_y = (TextView) findViewById(R.id.accel_y);
        accel_z = (TextView) findViewById(R.id.accel_z);

        senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

//        only need to register the listener once then it listens to all the sensor events
        senSensorManager.registerListener(mGyroSensorListener, senGyro , SensorManager.SENSOR_DELAY_NORMAL);
        gyro_x = (TextView) findViewById(R.id.gyro_x);
        gyro_y = (TextView) findViewById(R.id.gyro_y);
        gyro_z = (TextView) findViewById(R.id.gyro_z);

//        final Button next_but = (Button) findViewById(R.id.next_but);
        next_but = (Button) findViewById(R.id.next_but);

//        hide the native keyboard if the editTExt is clicked
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                disable the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                editText.setSelection(keyboard.getCursorPos());
            }
        });


        next_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboard.output_excel(inputText);
//                empty the text box
                editText.setText("");
//                read the next line of text
                inputText = readTextLine();
                input_text_view.setText(inputText);
                editText.setText(inputText);
                if (inputText!=null){char_count.setText("(0/"+inputText.length()+")");}
                keyboard.reset_cursor();
                next_but.setVisibility(View.INVISIBLE);

            }
        });

        show_data_but = (Button) findViewById(R.id.show_data);
//        show_data_but.setText("SHOW");

        data_view = (ConstraintLayout) findViewById(R.id.data_view);

        data_view.setVisibility(View.INVISIBLE);

        show_data_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data_view.getVisibility()==View.VISIBLE){
                    data_view.setVisibility(View.INVISIBLE);
//                    show_data_but.setText("SHOW");
//                    editText.setVisibility(View.INVISIBLE);
                }
                else{
                    data_view.setVisibility(View.VISIBLE);
//                    show_data_but.setText("HIDE");
//                    editText.setVisibility(View.VISIBLE);
//                    editText.requestFocus();

                }
            }
        });



    }

    private void ReadTextFile() {
        String string = "";
        is = this.getResources().openRawResource(R.raw.phrases2_test);
        reader = new BufferedReader(new InputStreamReader(is));
    }

    private String readTextLine(){
        String string = "";

        try {
            string = reader.readLine();
            if (string == null){
//                close the stream is the end has been reached
                Toast toast =  Toast.makeText(getApplicationContext(),"All tasks complete",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                keyboard.setVisibility(View.GONE);
                is.close();
            }else{
                return string;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return string;
    }


    private SensorEventListener mAccelSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor mySensor = event.sensor;
//        Acceleration force along the x axis (including gravity).
//            long curTime = System.currentTimeMillis();
//        System.out.println("accel: " + mySensor);
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

//            long curTime = System.currentTimeMillis();

//            only update the reading after 100 ms
//                if ((curTime - lastUpdate) > 100) {
//                    lastUpdate = curTime;

                last_x = x;
                last_y = y;
                last_z = z;

//                System.out.println("x: " + Float.toString(last_x));
//                System.out.println("y: " + Float.toString(last_y));
//                System.out.println("z: " + Float.toString(last_z));

                accel_x.setText(Float.toString(last_x));
                accel_y.setText(Float.toString(last_y));
                accel_z.setText(Float.toString(last_z));

                long accel_time = System.currentTimeMillis();
                float[] accel_data = {last_x,last_y,last_z};
                keyboard.add_data_rows("accel_data",1,accel_time,accel_data);
//                    Log.d("MY_APP_accel_val", Float.toString(last_x));
                }
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("MY_APP_accel", sensor.toString() + " - " + accuracy);

        }
    };

    private SensorEventListener mGyroSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor mySensor = event.sensor;
//        Acceleration force along the x axis (including gravity).
            long curTime = System.currentTimeMillis();
//            System.out.println("gyro: " + (mySensor.getType()==Sensor.TYPE_GYROSCOPE));
//        System.out.println(mySensor);
            if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
                float g_x = event.values[0];
                float g_y = event.values[1];
                float g_z = event.values[2];


                a_x = g_x;
                a_y = g_y;
                a_z = g_z;

//                System.out.println("x: " + Float.toString(last_x));
//                System.out.println("y: " + Float.toString(last_y));
//                System.out.println("z: " + Float.toString(last_z));

                gyro_x.setText(Float.toString(a_x));
                gyro_y.setText(Float.toString(a_y));
                gyro_z.setText(Float.toString(a_z));

                long gyro_time = System.currentTimeMillis();
                float[] gyro_data = {a_x,a_y,a_z};
                keyboard.add_data_rows("gyro_data",2,gyro_time,gyro_data);
//                    Log.d("MY_APP_Gyro_val", Float.toString(a_x))
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("MY_APP_gyro", sensor.toString() + " - " + accuracy);

        }
    };


    public void setTouchCoordinates(float x,float y){
        System.out.println("x:" + x);
        System.out.println("y: " + y);
        touch_x.setText( Float.toString(x));
        touch_y.setText( Float.toString(y));
    }

    public void justTest(){

    }

    @Override
    public void updateCharCount() {
        int strLen = inputText.length();
//                String t_x = touch_x.getText().toString();
//                String t_y = touch_y.getText().toString();
//
        char_count.setText("("+editText.getSelectionStart() + "/"+ strLen+")");

        if (editText.getSelectionStart() == inputText.length()){
//                    make the button visible it the length is reached
            next_but.setVisibility(View.VISIBLE);
//                    output a excel file to storage
        }
        else{
            next_but.setVisibility(View.INVISIBLE);
        }
    }
}
