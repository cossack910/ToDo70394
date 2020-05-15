package local.hal.st42.android.todo70394;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.FragmentManager;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class ToDoEditActivity extends AppCompatActivity {

    /**
     *新規登録モードかを表すフィールド
     */
    private int _mode = MainActivity.MODE_INSERT;

    /**
     * 更新モードの際、現在表示しているメモ情報のデータベース上の主キー値。
     */
    private long _idNo = 0;

    /**
     * データベースヘルパーオブジェクト
     */
    private DatabaseHelper _helper;

    private String deadline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);
        //アクションバーの戻る機能
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        _helper = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        _mode = intent.getIntExtra("mode", MainActivity.MODE_INSERT);

        if(_mode == MainActivity.MODE_INSERT){
            deadline = getToday();
            TextView tvTaskEdit = findViewById(R.id.tvTaskEdit);
            tvTaskEdit.setText(R.string.tv_regist_title);
            TextView deadlineDay = findViewById(R.id.deadlineDay);
            deadlineDay.setText(deadline);
        } else {
            TextView tvTaskEdit = findViewById(R.id.tvTaskEdit);
            tvTaskEdit.setText(R.string.tv_edit_title);

            _idNo = intent.getLongExtra("idNo", 0);
            SQLiteDatabase db = _helper.getWritableDatabase();
            Task taskData = DataAccess.findByPK(db, _idNo);

            EditText edTaskName = findViewById(R.id.edTaskName);
            edTaskName.setText(taskData.getName());

            TextView tvDeadlineDay = findViewById(R.id.deadlineDay);
            tvDeadlineDay.setText(taskData.getDeadline());

            Switch taskComplete = findViewById(R.id.taskComplete);
            if(taskData.getDone() == 1){
                taskComplete.setChecked(true);
            }

            EditText edTaskDetail = findViewById(R.id.edTaskDetail);
            edTaskDetail.setText(taskData.getNote());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(_mode == MainActivity.MODE_INSERT){
            inflater.inflate(R.menu.menu_options_regist, menu);
        }else{
            inflater.inflate(R.menu.menu_options_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();//Activityを閉じる
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * MenuSaveアイコンのボタンが押された時のメソッド
     * @param item
     */
    public void onSaveButtonClick(MenuItem item){
        EditText edTaskName = findViewById(R.id.edTaskName);
        String taskName = edTaskName.getText().toString();
        if(taskName.equals("")){
            Toast.makeText(ToDoEditActivity.this, R.string.msg_name,Toast.LENGTH_SHORT).show();
        }else{
            EditText edTaskDetail = findViewById(R.id.edTaskDetail);
            String taskDetail = edTaskDetail.getText().toString();
            SQLiteDatabase db = _helper.getWritableDatabase();
            if(_mode == MainActivity.MODE_INSERT){
                DataAccess.insert(db, taskName, deadline, taskDetail);
            }else{
                long done;
                Switch taskComplete = findViewById(R.id.taskComplete);
                TextView deadlineDay = findViewById(R.id.deadlineDay);
                deadline = deadlineDay.getText().toString();
                if(taskComplete.isChecked()){
                    done = 1;
                }else{
                    done = 0;
                }
                DataAccess.update(db, _idNo, taskName, deadline, done, taskDetail);
            }
            finish();
        }
    }

    /**
     *削除ボタンが押された時にダイアログを表示するメソッド
     */
    public void onDeleteButtonClick(MenuItem item){
        FUllDialogFragment dialog = new FUllDialogFragment();
        FragmentManager manager = getFragmentManager();
        dialog.show(manager, "FullDialogFragment");
    }

    /**
     *期限の表示をタッチした時のイベント処理メソッド
     */
    public void showDatePickerDialog(View view){
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH);
        int nowDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(ToDoEditActivity.this, new DatePickerDialogOnDateSetListener(), nowYear, nowMonth, nowDayOfMonth);
        dialog.show();
    }

    /**
     * 日付選択された時のメンバクラス
     */
    private class DatePickerDialogOnDateSetListener implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
            deadline = year + "年" + (month + 1) + "月" + dayOfMonth + "日";
            TextView deadlineDay = findViewById(R.id.deadlineDay);
            deadlineDay.setText(deadline);
        }

    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    /**
     * 表示されたダイアログの削除ボタンが押されたときに、データ削除を行うメソッド
     */
    public void  listDelete(){
        SQLiteDatabase db = _helper.getWritableDatabase();
        DataAccess.delete(db, _idNo);
        finish();
    }

    /*
     *今日の日付取得
     */
    private String getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }
}
