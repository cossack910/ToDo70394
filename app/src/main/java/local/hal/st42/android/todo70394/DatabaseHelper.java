package local.hal.st42.android.todo70394;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * データベース名の定数フィールド
     */
    private static final String DATABASE_NAME = "todo70394.db";
    /**
     *バージョンを表す定数フィールド
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * コンストラクタ
     * @param context コンテキスト
     */
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE tasks (");
        sb.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append("name TEXT NOT NULL,");
        sb.append("deadline TEXT,");
        sb.append("done INTEGER DEFAULT 0,");
        sb.append("note TEXT");
        sb.append(");");
        String sql = sb.toString();

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}
}
