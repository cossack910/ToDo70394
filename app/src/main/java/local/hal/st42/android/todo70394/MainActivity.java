package local.hal.st42.android.todo70394;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /**
     *新規登録モードを表す定数フィールド
     */
    static final int MODE_INSERT = 1;
    /*
     *更新モードを表す定数フィールド
     */
    static final int MODE_EDIT = 2;
    /**
     *タスクリスト用ListView
     */
    private ListView _lvTaskList;
    /**
     *データベースヘルパーオブジェクト
     */
    private DatabaseHelper _helper;
    /**
     *タスクの完了状態表示を示すフィールド
     * _task_flg = 2の時は全タスク表示、_task_flg = 0の時は未完了タスク表示,_task_flg = 1の時は完了タスク表示
     */
    private int _task_flg = 2;
    //
    private static final String ToDo70394_NAME = "ToDo70394File";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(ToDo70394_NAME, MODE_PRIVATE);
        _task_flg = settings.getInt(ToDo70394_NAME,_task_flg);

        _lvTaskList = findViewById(R.id.lvTaskList);
        _lvTaskList.setOnItemClickListener(new ListItemClickListener());

        _helper = new DatabaseHelper(getApplicationContext());

    }

    @Override
    protected void onResume(){
        super.onResume();
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor;
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);

        if(_task_flg == 2){
            //TaskList = createTaskList(DataAccess.findAll(db));
            cursor = DataAccess.findAll(db);
            _tvTaskStatus.setText(R.string.menu_task_all);
        }else if(_task_flg == 0){
            //TaskList = createTaskList(DataAccess.findIncomplete(db));
            cursor = DataAccess.findIncomplete(db);
            _tvTaskStatus.setText(R.string.menu_task_incomplete);
        }else if(_task_flg == 1){
            //TaskList = createTaskList(DataAccess.findComplete(db));
            cursor = DataAccess.findComplete(db);
            _tvTaskStatus.setText(R.string.menu_task_complete);
        }

        String[] from = {"name", "deadline", "done"};
        int[] to = {R.id.lvTaskName, R.id.lvDeadline, R.id.cbDoneCheck};
        //SimpleAdapter adapter = new SimpleAdapter(this, TaskList, R.layout.row, from, to);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.row, null, from, to, 0);
        adapter.setViewBinder(new CustomViewBinder());
        _lvTaskList.setAdapter(adapter);
        setNewCursor(_task_flg);

    }

    private void setNewCursor(int _task_flg) {
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor = DataAccess.findAll(db);
        if(_task_flg == 2){
            cursor = DataAccess.findAll(db);
        }else if(_task_flg == 0){
            cursor = DataAccess.findIncomplete(db);
        }else if(_task_flg == 1){
            cursor = DataAccess.findComplete(db);
        }

        SimpleCursorAdapter adapter = (SimpleCursorAdapter) _lvTaskList.getAdapter();
        adapter.changeCursor(cursor);
    }

    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            int viewid = view.getId();
            switch(viewid){
                case R.id.lvDeadline:
                    int idxDeadline = cursor.getColumnIndex("deadline");
                    String strDeadline = cursor.getString(idxDeadline);
                    String _strDeadline = strDeadline.replaceFirst("-","").replaceFirst("-","");
                    strDeadline = strDeadline.replaceFirst("-","年").replaceFirst("-","月") + "日";
                    int intDeadline = Integer.parseInt(_strDeadline);
                    int intToday = Integer.parseInt(getTodayMain());

                    TextView _lvDeadline = (TextView) view;

                    if(intDeadline == intToday){
                        strDeadline = "期限: 今日";
                        _lvDeadline.setTextColor(Color.BLUE);
                    }else if(intDeadline >= intToday){
                        _lvDeadline.setTextColor(Color.GREEN);
                    }else if(intDeadline <= intToday){
                        _lvDeadline.setTextColor(Color.RED);
                    }
                    _lvDeadline.setText(strDeadline);
                    return true;
                case R.id.cbDoneCheck:
                    int idIdx = cursor.getColumnIndex("_id");
                    long id = cursor.getLong(idIdx);
                    CheckBox _cbDoneCheck = (CheckBox) view;
                    int doneCheck = cursor.getInt(columnIndex);
                    boolean checked = false;

                    if(doneCheck == 1){
                        checked = true;
                    }
                    _cbDoneCheck.setChecked(checked);
                    _cbDoneCheck.setTag(id);
                    _cbDoneCheck.setOnClickListener(new OnCheckBoxClickListener());
                    return true;
            }
            return false;
        }
    }


    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    /**
     * MenuAddアイコンのボタンが押された時のメソッド
     * @param item
     */
    public void onAddButtonClick(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
        intent.putExtra("mode", MODE_INSERT);
        startActivity(intent);
    }

    /**
     * リストがクリックされた時のリスナクラス
     */
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Cursor item = (Cursor) parent.getItemAtPosition(position);
            int idxId = item.getColumnIndex("_id");
            Long idNo = item.getLong(idxId);

            Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
            intent.putExtra("mode", MODE_EDIT);
            intent.putExtra("idNo", idNo);
            startActivity(intent);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem _mnTaskStatus = menu.findItem(R.id.menuTaskStatus);
        switch(_task_flg){
            case 2:
                _mnTaskStatus.setTitle(R.string.menu_task_all);
                break;
            case 0:
                _mnTaskStatus.setTitle(R.string.menu_task_incomplete);
                break;
            case 1:
                _mnTaskStatus.setTitle(R.string.menu_task_complete);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getSharedPreferences(ToDo70394_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        int itemId = item.getItemId();
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor;
        ArrayList<HashMap<String, String>> TaskList = new ArrayList<>();
        SimpleCursorAdapter adapter;
        String[] from = {"name", "deadline", "done"};
        int[] to = {R.id.lvTaskName, R.id.lvDeadline, R.id.cbDoneCheck};
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);
        switch (itemId) {
            case R.id.menuTaskAll:
                _tvTaskStatus.setText(R.string.menu_task_all);
                _task_flg = 2;
                adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.row, null, from, to, 0);
                adapter.setViewBinder(new CustomViewBinder());
                _lvTaskList.setAdapter(adapter);
                break;
            case R.id.menuTaskIncomplete:
                _tvTaskStatus.setText(R.string.menu_task_incomplete);
                _task_flg = 0;
                adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.row, null, from, to, 0);
                adapter.setViewBinder(new CustomViewBinder());
                _lvTaskList.setAdapter(adapter);
                break;
            case R.id.menuTaskComplete:
                _tvTaskStatus.setText(R.string.menu_task_complete);
                _task_flg = 1;
                adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.row, null, from, to, 0);
                adapter.setViewBinder(new CustomViewBinder());
                _lvTaskList.setAdapter(adapter);
                break;
        }
        setNewCursor(_task_flg);
        //
        invalidateOptionsMenu();

        editor.putInt(ToDo70394_NAME,_task_flg);
        editor.commit();
        return super.onOptionsItemSelected(item);

    }

    private class OnCheckBoxClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CheckBox cbDoneCheck = (CheckBox) view;
            boolean isChecked = cbDoneCheck.isChecked();
            long id = (Long) cbDoneCheck.getTag();
            SQLiteDatabase db = _helper.getWritableDatabase();
            DataAccess.changePhoneChecked(db, id, isChecked);
            setNewCursor(_task_flg);
        }
    }
    /*
     *今日の日付取得
     */
    private String getTodayMain() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }
}
