package local.hal.st42.android.todo70394;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.FragmentManager;

import java.util.Date;
import java.text.SimpleDateFormat;

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
    /*
     *タスク完了期限変数
     */
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

            String[] array_DeadlineDay =taskData.getDeadline().split("-| ");
            String strDeadlineDay = array_DeadlineDay[0] + "年" + array_DeadlineDay[1] + "月" + array_DeadlineDay[2] + "日";
            TextView tvDeadlineDay = findViewById(R.id.deadlineDay);
            tvDeadlineDay.setText(strDeadlineDay);

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
     * 変数doneは完了済の場合は１、未完了の場合は０
     *
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
                TextView deadlineDay = findViewById(R.id.deadlineDay);
                String strDeadline = deadlineDay.getText().toString();
                String[] deadlineParts = strDeadline.split("年|月|日");
                //deadline = deadlineParts[0] + "-" + deadlineParts[1] + "-" + deadlineParts[2] + " " + "00:00:00";
                deadline = deadlineParts[0] + "-" + deadlineParts[1] + "-" + deadlineParts[2];
                System.out.println(deadline);
                DataAccess.insert(db, taskName, deadline, taskDetail);
            }else{
                long done;
                Switch taskComplete = findViewById(R.id.taskComplete);
                TextView deadlineDay = findViewById(R.id.deadlineDay);
                String strDeadline = deadlineDay.getText().toString();
                String[] deadlineParts = strDeadline.split("年|月|日");
                //deadline = deadlineParts[0] + "-" + deadlineParts[1] + "-" + deadlineParts[2] + " " + "00:00:00";
                deadline = deadlineParts[0] + "-" + deadlineParts[1] + "-" + deadlineParts[2];
                System.out.println(deadline);
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
        TextView tvDeadlineDay = findViewById(R.id.deadlineDay);
        String _deadlineDay = tvDeadlineDay.getText().toString();
        String[] deadlineParts = _deadlineDay.split("年|月|日");
        int nowYear = Integer.parseInt(deadlineParts[0]);
        int nowMonth = Integer.parseInt(deadlineParts[1]) - 1;
        int nowDayOfMonth = Integer.parseInt(deadlineParts[2]);
        DatePickerDialog dialog = new DatePickerDialog(ToDoEditActivity.this, new DatePickerDialogOnDateSetListener(), nowYear, nowMonth, nowDayOfMonth);
        dialog.show();
    }

    /**
     * 日付選択された時のメンバクラス
     */
    private class DatePickerDialogOnDateSetListener implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
            String strMonth;
            String strDayOfMonth;
            if(month < 10){
                strMonth = "0"+ String.valueOf(month + 1);
            }else{
                strMonth = String.valueOf(month + 1);
            }
            if(dayOfMonth < 10){
                strDayOfMonth = "0"+ String.valueOf(dayOfMonth);
            }else{
                strDayOfMonth = String.valueOf(dayOfMonth);
            }
            deadline = year + "年" + strMonth + "月" + strDayOfMonth + "日";
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
