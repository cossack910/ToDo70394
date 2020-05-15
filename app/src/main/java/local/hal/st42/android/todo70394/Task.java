package local.hal.st42.android.todo70394;

public class Task {
    /**
     *主キーのID値
     */
    private long _id;
    /**
     * タスク名
     */
    private String _name;
    /**
     * 期限
     */
    private String _deadline;
    /**
     * タスク完了フラグ
     */
    private long _done;
    /**
     * 詳細
     */
    private String _note;

    public long getId(){
        return _id;
    }

    public void setId(long id){
        _id = id;
    }

    public String getName(){
        return _name;
    }

    public void setName(String name){
        _name = name;
    }

    public String getDeadline(){
        return _deadline;
    }

    public void setDeadline(String deadline){
        _deadline = deadline;
    }

    public long getDone(){
        return _done;
    }

    public void setDone(long done){
        _done = done;
    }

    public String getNote(){
        return _note;
    }

    public void setNote(String note){
        _note = note;
    }

}
