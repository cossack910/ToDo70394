package local.hal.st42.android.todo75039;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final int MODE_INSERT = 1;
    static final int MODE_EDIT = 2;
    private ListView _lvTaskList;
    private DatabaseHelper _helper;
    private int _task_flg = 2;  // add
    Map<Integer,String> map_flag = new HashMap<>();


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
        Cursor cursor = DataAccess.findAll(db, _task_flg);

        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);
        map_flag.put(2, "全タスク");
        map_flag.put(1, "完了タスク");
        map_flag.put(0, "未完了タスク");

        _tvTaskStatus.setText(map_flag.get(_task_flg));


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

    public void onAddButtonClick(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
        intent.putExtra("mode", MODE_INSERT);
        startActivity(intent);
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Cursor item = (Cursor) parent.getItemAtPosition(position);
            int itemId = item.getColumnIndex("_id");
            long idNo = item.getLong(itemId);

            Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
            intent.putExtra("mode", MODE_EDIT);
            intent.putExtra("idNo", idNo);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        SQLiteDatabase db = _helper.getWritableDatabase();
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);

        switch (itemId) {
            case R.id.menuTaskAll:
                _task_flg = 2;
                break;
            case R.id.menuTaskIncomplete:
                _task_flg = 0;
                break;
            case R.id.menuTaskComplete:
                _task_flg = 1;
                 break;
        }
        DataAccess.findAll(db, _task_flg);
        _tvTaskStatus.setText(map_flag.get(_task_flg));

        onResume();

        return super.onOptionsItemSelected(item);
    }
}
