package local.hal.st42.android.todo70394;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
     * test
     */
    private int _task_flg = 2;
    static final int RESULT_TODOEDITACTIVITY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _lvTaskList = findViewById(R.id.lvTaskList);
        _lvTaskList.setOnItemClickListener(new ListItemClickListener());

        _helper = new DatabaseHelper(getApplicationContext());
    }

    @Override
    protected void onResume(){
        super.onResume();
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor = DataAccess.findAll(db);
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);
        if(_task_flg == 2){
            cursor = DataAccess.findAll(db);
            _tvTaskStatus.setText(R.string.menu_task_all);
        }else if(_task_flg == 0){
            cursor = DataAccess.findIncomplete(db);
            _tvTaskStatus.setText(R.string.menu_task_incomplete);
        }else if(_task_flg == 1){
            cursor = DataAccess.findComplete(db);
            _tvTaskStatus.setText(R.string.menu_task_complete);
        }

        String[] from = {"name", "deadline"};
        int[] to = { android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, cursor, from, to, 0);
        _lvTaskList.setAdapter(adapter);
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
        intent.putExtra("taskFlg", _task_flg);
        //startActivity(intent);
        startActivityForResult(intent, RESULT_TODOEDITACTIVITY);
    }

    /**
     * リストがクリックされた時のリスナクラス
     */
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Cursor item = (Cursor) parent.getItemAtPosition(position);
            int idxId = item.getColumnIndex("_id");
            long idNo = item.getLong(idxId);

            Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
            intent.putExtra("mode", MODE_EDIT);
            intent.putExtra("idNo", idNo);
            intent.putExtra("taskFlg", _task_flg);
            //startActivity(intent);
            startActivityForResult(intent, RESULT_TODOEDITACTIVITY);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor;
        SimpleCursorAdapter adapter;
        String[] from = {"name", "deadline"};
        int[] to = { android.R.id.text1, android.R.id.text2};
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);
        switch (itemId) {
            case R.id.menuTaskAll:
                //System.out.println("aaaaaallllllllllllll");
                //
                _tvTaskStatus.setText(R.string.menu_task_all);
                _task_flg = 2;
                cursor = DataAccess.findAll(db);
                adapter = new SimpleCursorAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, cursor, from, to, 0);
                _lvTaskList.setAdapter(adapter);
                break;
            case R.id.menuTaskIncomplete:
                //System.out.println("未未未未未未未未未未完了");
                //
                _tvTaskStatus.setText(R.string.menu_task_incomplete);
                _task_flg = 0;
                cursor = DataAccess.findIncomplete(db);
                adapter = new SimpleCursorAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, cursor, from, to, 0);
                _lvTaskList.setAdapter(adapter);
                break;
            case R.id.menuTaskComplete:
                //System.out.println("完了完了完了完了完了完了");
                //
                _tvTaskStatus.setText(R.string.menu_task_complete);
                _task_flg = 1;
                cursor = DataAccess.findComplete(db);
                adapter = new SimpleCursorAdapter(getApplicationContext(),android.R.layout.simple_list_item_2, cursor, from, to, 0);
                _lvTaskList.setAdapter(adapter);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        _task_flg = intent.getIntExtra("taskFlg",-1);
    }
}
