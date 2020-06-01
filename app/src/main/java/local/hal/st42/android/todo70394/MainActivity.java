package local.hal.st42.android.todo70394;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
        //
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
        ArrayList<HashMap<String, String>> TaskList = new ArrayList<>();
        TaskList = createTaskList(DataAccess.findAll(db));
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);

        if(_task_flg == 2){
            TaskList = createTaskList(DataAccess.findAll(db));
            _tvTaskStatus.setText(R.string.menu_task_all);
        }else if(_task_flg == 0){
            TaskList = createTaskList(DataAccess.findIncomplete(db));
            _tvTaskStatus.setText(R.string.menu_task_incomplete);
        }else if(_task_flg == 1){
            TaskList = createTaskList(DataAccess.findComplete(db));
            _tvTaskStatus.setText(R.string.menu_task_complete);
        }
        String[] from = {"name", "deadline"};
        int[] to = { android.R.id.text1, android.R.id.text2};
        SimpleAdapter adapter = new SimpleAdapter(this, TaskList,android.R.layout.simple_list_item_2, from, to);
        _lvTaskList.setAdapter(adapter);
    }
    /*
     *タスクリストを生成するメソッド
     * @return タスクリスト
     */
    private ArrayList<HashMap<String, String>> createTaskList(Cursor cursor){
        int indexId  = cursor.getColumnIndex( "_id");
        int indexName = cursor.getColumnIndex( "name" );
        int indexDeadline  = cursor.getColumnIndex( "deadline");
        int indexDone  = cursor.getColumnIndex( "done");
        String strId;
        String strName;
        String strDeadline;
        int intDone;
        ArrayList<HashMap<String, String>> TaskList = new ArrayList<>();
        while(cursor.moveToNext()){
            HashMap<String,String> data = new HashMap<>();
            strId = cursor.getString(indexId);
            strName = cursor.getString(indexName);
            strDeadline = cursor.getString(indexDeadline);
            //strDeadline = strDeadline.replaceFirst("-","年").replaceFirst("-","月").replace(" 00:00:00","日");
            strDeadline = strDeadline.replaceFirst("-","年").replaceFirst("-","月") + "日";
            intDone = cursor.getInt(indexDone);
            data.put("_id",strId);
            if(intDone == 1){
                data.put("name",strName + "  (完了済のタスク)");
            }else {
                data.put("name",strName);
            }

            data.put("deadline",strDeadline);
            TaskList.add(data);
        }
        return  TaskList;
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
            HashMap<String,String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
            System.out.println(item);
            String idxId = item.get("_id");
            Long idNo = Long.parseLong(idxId);

            Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
            intent.putExtra("mode", MODE_EDIT);
            intent.putExtra("idNo", idNo);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getSharedPreferences(ToDo70394_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();


        int itemId = item.getItemId();
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor;
        ArrayList<HashMap<String, String>> TaskList = new ArrayList<>();
        SimpleAdapter adapter;
        String[] from = {"name", "deadline"};
        int[] to = { android.R.id.text1, android.R.id.text2};
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);
        switch (itemId) {
            case R.id.menuTaskAll:
                _tvTaskStatus.setText(R.string.menu_task_all);
                _task_flg = 2;
                TaskList = createTaskList(DataAccess.findAll(db));
                adapter = new SimpleAdapter(this, TaskList,android.R.layout.simple_list_item_2, from, to);
                _lvTaskList.setAdapter(adapter);
                break;
            case R.id.menuTaskIncomplete:
                _tvTaskStatus.setText(R.string.menu_task_incomplete);
                _task_flg = 0;
                TaskList = createTaskList(DataAccess.findIncomplete(db));
                adapter = new SimpleAdapter(this, TaskList,android.R.layout.simple_list_item_2, from, to);
                _lvTaskList.setAdapter(adapter);
                break;
            case R.id.menuTaskComplete:
                _tvTaskStatus.setText(R.string.menu_task_complete);
                _task_flg = 1;
                TaskList = createTaskList(DataAccess.findComplete(db));
                adapter = new SimpleAdapter(this, TaskList,android.R.layout.simple_list_item_2, from, to);
                _lvTaskList.setAdapter(adapter);
                break;
        }
        editor.putInt(ToDo70394_NAME,_task_flg);
        editor.commit();
        return super.onOptionsItemSelected(item);
    }
}
