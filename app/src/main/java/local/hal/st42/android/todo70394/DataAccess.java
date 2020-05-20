package local.hal.st42.android.todo70394;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DataAccess {
    /**
     * 全データ検索メソッド
     */
    public static Cursor findAll(SQLiteDatabase db){
        String sql = "SELECT _id, name, deadline, done, note FROM tasks";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }
    /**
     * 未完了タスク検索メソッド
     */
    public static Cursor findIncomplete(SQLiteDatabase db){
        String sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE done = 0";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }
    /**
     * 完了タスク検索メソッド
     */
    public static Cursor findComplete(SQLiteDatabase db){
        String sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE done = 1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    /**
     * 主キーによる検索
     */
    public static Task findByPK(SQLiteDatabase db, long id){
        String sql = "SELECT _id, name, deadline, done, note FROM tasks WHERE _id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        Task result = null;
        if(cursor.moveToFirst()){
            int idxName = cursor.getColumnIndex("name");
            int idxDeadline = cursor.getColumnIndex("deadline");
            int idxDone = cursor.getColumnIndex("done");
            int idxNote = cursor.getColumnIndex("note");
            String name = cursor.getString(idxName);
            String deadline = cursor.getString(idxDeadline);
            long done = cursor.getLong(idxDone);
            String note = cursor.getString(idxNote);

            result = new Task();
            result.setId(id);
            result.setName(name);
            result.setDeadline(deadline);
            result.setDone(done);
            result.setNote(note);
        }
        return result;
    }
    /**
     *タスク情報を更新するメソッド
     *
     * @param db SQLiteDatabaseオブジェクト
     * @param id 主キー値
     * @param name タスク名
     * @param done 完了フラグ
     * @param deadline 期限
     * @param note メモ内容
     * @return 更新件数
     */
    public static int update(SQLiteDatabase db, long id, String name, String deadline, long done, String note) {
        String sql = "UPDATE tasks SET name = ?, deadline = ?, done = ?, note = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, name);
        stmt.bindString(2, deadline);
        stmt.bindLong(3, done);
        stmt.bindString(4, note);
        stmt.bindLong(5, id);
        int result = stmt.executeUpdateDelete();
        return result;
    }


    /**
     * タスク情報を新規登録するメソッド
     *
     * @param db SQLiteDatabaseオブジェクト
     * @param name タスク名
     * @param deadline 期限
     ** @param note メモ内容
     * @return 登録されたレコードキー
     */
    public static long insert(SQLiteDatabase db, String name, String deadline, String note) {
        String sql = "INSERT INTO tasks(name, deadline, note) VALUES(?,?,?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, name);
        stmt.bindString(2,deadline);
        stmt.bindString(3,note);
        long id = stmt.executeInsert();
        return id;
    }

    /**
     * タスク情報を削除するメソッド
     *
     * @param db SQLiteDatabaseメソッド
     * @param id 主キー値
     * @return 削除件数
     */
    public static int delete(SQLiteDatabase db, long id){
        String sql = "DELETE FROM tasks WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1, id);
        int result = stmt.executeUpdateDelete();
        return result;
    }
}
