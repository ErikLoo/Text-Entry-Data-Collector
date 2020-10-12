package com.example.inAppKeyboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyCharacterKeyboard extends LinearLayout implements View.OnClickListener {
//    private Button button1, button2, button3, button4,
//            button5, button6, button7, button8,
//            button9, button0, buttonDelete, buttonEnter;

    private Button button_q, button_w, button_e, button_r, button_t, button_y, button_u, button_i, button_o, button_p,
    button_a,button_s,button_d,button_f,button_g,button_h,button_j,button_k,button_l,
            button_z,button_x,button_c,button_v,button_b,button_n,button_m,
    button_del,button_space;

    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;

    private float mLastX,mLastY,mStartY;
    private boolean mIsBeingDragged,mTouchSlop;

    private TextView touch_x,touch_y;

    private Activity myActivity;

//    log time
    private Workbook wb;
    private int row_count=1;
    private String inputChar;
    private String intendedChar;
    private long startTime;
    private long endTime;

//    log touch
    private int touch_id = 0;
    private long touchTime;

    private int accel_id = 1;
    private long accelTime;

    private int gyro_id = 2;
    private long gyrcoTime;
    private int[] row_count_array = {1,1,1};

    private EditText editText;

    private Spannable WordtoSpan;

    private int cursor_pos = 0;

    private String myChar;

    public MyCharacterKeyboard(Context context) {
        this(context, null, 0);
    }

    public MyCharacterKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCharacterKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.character_keyboard, this, true);
        button_q = (Button) findViewById(R.id.button_q);
        button_q.setOnClickListener(this);
        button_w = (Button) findViewById(R.id.button_w);
        button_w.setOnClickListener(this);
        button_e = (Button) findViewById(R.id.button_e);
        button_e.setOnClickListener(this);
        button_r = (Button) findViewById(R.id.button_r);
        button_r.setOnClickListener(this);
        button_t = (Button) findViewById(R.id.button_t);
        button_t.setOnClickListener(this);
        button_y = (Button) findViewById(R.id.button_y);
        button_y.setOnClickListener(this);
        button_u = (Button) findViewById(R.id.button_u);
        button_u.setOnClickListener(this);
        button_i = (Button) findViewById(R.id.button_i);
        button_i.setOnClickListener(this);
        button_o = (Button) findViewById(R.id.button_o);
        button_o.setOnClickListener(this);
        button_p = (Button) findViewById(R.id.button_p);
        button_p.setOnClickListener(this);

        button_a = (Button) findViewById(R.id.button_a);
        button_a.setOnClickListener(this);
        button_s = (Button) findViewById(R.id.button_s);
        button_s.setOnClickListener(this);
        button_d = (Button) findViewById(R.id.button_d);
        button_d.setOnClickListener(this);
        button_f = (Button) findViewById(R.id.button_f);
        button_f.setOnClickListener(this);
        button_g = (Button) findViewById(R.id.button_g);
        button_g.setOnClickListener(this);
        button_h = (Button) findViewById(R.id.button_h);
        button_h.setOnClickListener(this);
        button_j = (Button) findViewById(R.id.button_j);
        button_j.setOnClickListener(this);
        button_k = (Button) findViewById(R.id.button_k);
        button_k.setOnClickListener(this);
        button_l = (Button) findViewById(R.id.button_l);
        button_l.setOnClickListener(this);

        button_z = (Button) findViewById(R.id.button_z);
        button_z.setOnClickListener(this);
        button_x = (Button) findViewById(R.id.button_x);
        button_x.setOnClickListener(this);
        button_c = (Button) findViewById(R.id.button_c);
        button_c.setOnClickListener(this);
        button_v = (Button) findViewById(R.id.button_v);
        button_v.setOnClickListener(this);
        button_b = (Button) findViewById(R.id.button_b);
        button_b.setOnClickListener(this);
        button_n = (Button) findViewById(R.id.button_n);
        button_n.setOnClickListener(this);
        button_m = (Button) findViewById(R.id.button_m);
        button_m.setOnClickListener(this);
        button_del = (Button) findViewById(R.id.button_del);
        button_del.setOnClickListener(this);
        button_space = (Button) findViewById(R.id.button_space);
        button_space.setOnClickListener(this);

        keyValues.put(R.id.button_q, "q");
        keyValues.put(R.id.button_w, "w");
        keyValues.put(R.id.button_e, "e");
        keyValues.put(R.id.button_r, "r");
        keyValues.put(R.id.button_t, "t");
        keyValues.put(R.id.button_y, "y");
        keyValues.put(R.id.button_u, "u");
        keyValues.put(R.id.button_i, "i");
        keyValues.put(R.id.button_o, "o");
        keyValues.put(R.id.button_p, "p");

        keyValues.put(R.id.button_a, "a");
        keyValues.put(R.id.button_s, "s");
        keyValues.put(R.id.button_d, "d");
        keyValues.put(R.id.button_f, "f");
        keyValues.put(R.id.button_g, "g");
        keyValues.put(R.id.button_h, "h");
        keyValues.put(R.id.button_j, "j");
        keyValues.put(R.id.button_k, "k");
        keyValues.put(R.id.button_l, "l");

        keyValues.put(R.id.button_z, "z");
        keyValues.put(R.id.button_x, "x");
        keyValues.put(R.id.button_c, "c");
        keyValues.put(R.id.button_v, "v");
        keyValues.put(R.id.button_b, "b");
        keyValues.put(R.id.button_n, "n");
        keyValues.put(R.id.button_m, "m");
        keyValues.put(R.id.button_space, " ");

        myActivity = (Activity) this.getContext();
//        System.out.println("Myactivity: " + myActivty);

        if (ActivityCompat.checkSelfPermission(myActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) myActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return;
        }

//        generate the doc and the header of the doc
//        wb=new HSSFWorkbook();
//
////        create a sheet for each type data and
//        String [] input_list = {"character","start time","end time"};
//        generate_header("input_time", input_list);
//
//        String [] touch_list = {"time","x","y"};
//        generate_header("touch_data", touch_list);
//
//        String [] accel_list = {"time","accel_x","accel_y","accel_z"};
//        generate_header("accel_data", accel_list);
//
//        String [] gyro_list = {"time","gyro_x","gyro_y","gyro_z"};
//        generate_header("gyro_data", gyro_list);
        init_excel();
    }

    private void init_excel(){
        wb=new HSSFWorkbook();

//        create a sheet for each type data and
        String [] input_list = {"intended character","typed character","start time","end time"};
        generate_header("input_time", input_list);

        String [] touch_list = {"time","x","y"};
        generate_header("touch_data", touch_list);

        String [] accel_list = {"time","accel_x","accel_y","accel_z"};
        generate_header("accel_data", accel_list);

        String [] gyro_list = {"time","gyro_x","gyro_y","gyro_z"};
        generate_header("gyro_data", gyro_list);

//        reset the all the counters
        row_count_array[0] = 1;
        row_count_array[1] = 1;
        row_count_array[2] = 1;
        row_count=1;
    }

    private void generate_header(String sheetName,String[] headerList){
        Cell cell=null;
        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Now we are creating sheet
        Sheet sheet=null;
        sheet = wb.createSheet(sheetName);
        //Now column and row
        Row row =sheet.createRow(0);

        for (int i=0;i<headerList.length;i++){
            cell=row.createCell(i);
            cell.setCellValue(headerList[i]);
            cell.setCellStyle(cellStyle);

        }

        sheet.setColumnWidth(0,(10*200));
        sheet.setColumnWidth(1,(10*200));

    }


    private void add_character_rows(){
        Cell cell=null;
        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Now we are creating sheet
        Sheet sheet=null;
        sheet = wb.getSheet("input_time");
        //Now column and row
        Row row =sheet.createRow(row_count);

        cell=row.createCell(0);
        cell.setCellValue(intendedChar);
        cell.setCellStyle(cellStyle);


        cell=row.createCell(1);
        cell.setCellValue(inputChar);
        cell.setCellStyle(cellStyle);

        cell=row.createCell(2);
        cell.setCellValue(startTime);
        cell.setCellStyle(cellStyle);


        cell=row.createCell(3);
        cell.setCellValue(endTime);
        cell.setCellStyle(cellStyle);

        sheet.setColumnWidth(0,(10*200));
        sheet.setColumnWidth(1,(10*200));

//        move to the next row
        row_count++;
    }

    public void add_data_rows(String sheetName,int sheet_id,long time,float[] data){
        Cell cell=null;
        CellStyle cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //Now we are creating sheet
        Sheet sheet=null;
        sheet = wb.getSheet(sheetName);
        //Now column and row
        Row row =sheet.createRow(row_count_array[sheet_id]);

//        add time
        cell=row.createCell(0);
        cell.setCellValue(time);
        cell.setCellStyle(cellStyle);

//        add other data like touch data, accel_data and gyro data
        for (int i=0;i<data.length;i++){
            cell=row.createCell(i+1);
            cell.setCellValue(data[i]);
            cell.setCellStyle(cellStyle);

        }

        row_count_array[sheet_id]++;
    }

    public void output_excel(String text_entry){
        File file = new File(myActivity.getExternalFilesDir(null),"text_entry_"+text_entry+".xls");
        FileOutputStream outputStream =null;

        try {
            outputStream=new FileOutputStream(file);
            wb.write(outputStream);
//            Toast.makeText(getContext(),"saving " + "text_entry_"+text_entry+".xls" + myActivity.getExternalFilesDir(null),Toast.LENGTH_LONG).show();


            Toast toast =  Toast.makeText(getContext(),"saving " + "text_entry_"+text_entry+".xls" + myActivity.getExternalFilesDir(null),Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        } catch (java.io.IOException e) {
            e.printStackTrace();

            Toast.makeText(getContext(),"file generation failed",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        init_excel();
    }

    @Override
    public void onClick(View view) {
//        String myChar;
        editText = (EditText) myActivity.findViewById(R.id.editText);
        WordtoSpan = new SpannableString(editText.getText());
//        set text color to black
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(160,160,160)), 0, editText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (inputConnection == null)
            return;

        if (view.getId() == R.id.button_del) {
//            CharSequence selectedText = inputConnection.getSelectedText(0);

//            if (TextUtils.isEmpty(selectedText)) {
////                inputConnection.deleteSurroundingText(1, 0);
//                editText.setSelection(cursor_pos);
//            } else {
////                inputConnection.commitText("", 1);
//                cursor_pos--;
//                editText.setSelection(cursor_pos);
//
//            }
            myChar = "Invalid Del";
            intendedChar = "";
            Toast toast =  Toast.makeText(getContext(),"swipe left here to delete" ,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
//            intendedChar = "";
//
//
////            inputConnection.commitText("a", 1);
////            inputConnection.deleteSurroundingText(1, 0);
//
//            if (cursor_pos>0){cursor_pos--;}
//
//            if (cursor_pos<editText.getText().length()) {
//                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, cursor_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                editText.setText(WordtoSpan);
//                editText.setSelection(cursor_pos);
//            }
//            deleteChar();
        } else {
            String value = keyValues.get(view.getId());
            myChar = value;
//            get the intended character

            if (cursor_pos<editText.getText().length()){
                intendedChar = Character.toString(editText.getText().charAt(cursor_pos));
            }else{
                intendedChar = "EOS";
            }

            if (intendedChar.equals(" ")){intendedChar="Space";}

            if (value.equals(" ")){myChar = "Space";}

//            used to tigger a onTextChange response
//            inputConnection.commitText("a", 1);
//            inputConnection.deleteSurroundingText(1, 0);

            if (cursor_pos<editText.getText().length()) {
                cursor_pos++;
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, cursor_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editText.setText(WordtoSpan);
                editText.setSelection(cursor_pos);
            }

        }

//      perform haptic feedback
//        input end time and the character itself
//       log the time when the finger leaves the key
        endTime = System.currentTimeMillis();
        inputChar = myChar;
//        add to row in here
        add_character_rows();

        view.performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
        );

//        need to cast getContext to the interface type in order to access the method
        ((InputStatusTracker) getContext()).updateCharCount();

//        update the char count in the main function

    }

    public void setInputConnection(InputConnection ic) {
        inputConnection = ic;
    }

    public void deleteChar(){
        if (editText!=null){

//            Toast toast =  Toast.makeText(getContext(),"delete a character" ,Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(160,160,160)), 0, editText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            intendedChar = "";
            myChar = "Del";
            if (cursor_pos>0){cursor_pos--;}

            if (cursor_pos<editText.getText().length()) {
//                Toast.makeText(getContext(),"cur pos: " + cursor_pos, Toast.LENGTH_SHORT).show();
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, cursor_pos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editText.setText(WordtoSpan);
                editText.setSelection(cursor_pos);
            }

            endTime = System.currentTimeMillis();
            inputChar = myChar;
//        add to row in here
            add_character_rows();

//        need to cast getContext to the interface type in order to access the method
            ((InputStatusTracker) getContext()).updateCharCount();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        mLastX = event.getX();
        mLastY = event.getY();

        touch_x = (TextView) myActivity.findViewById(R.id.touch_x);
        touch_y = (TextView) myActivity.findViewById(R.id.touch_y);


//        Always return false as we are not trying to intercept the touch event
//        We just want to get the (x,y) of the touch
        touch_x.setText(Float.toString(mLastX));
        touch_y.setText(Float.toString(mLastY));
//        System.out.println(touch_x.getText());
//        input start time
        int action = MotionEventCompat.getActionMasked(event);
        switch(action) {
            case (MotionEvent.ACTION_DOWN):
//                only record the time when it is down
                startTime = System.currentTimeMillis();
        }

        touchTime = System.currentTimeMillis();
        float[] touch_data = {mLastX,mLastY};
//        Toast.makeText(getContext(),"touch_data x: " + touch_data[0], Toast.LENGTH_SHORT).show();
        add_data_rows("touch_data",touch_id,touchTime,touch_data);
        return false;
    }

    public void generateExcel(){
        Toast.makeText(getContext(),"Typing complete. Saving data...", Toast.LENGTH_SHORT).show();
    }

    public void reset_cursor(){
        cursor_pos = 0;
        editText.setSelection(cursor_pos);
    }

    public int getCursorPos(){
        return cursor_pos;
    }

}
