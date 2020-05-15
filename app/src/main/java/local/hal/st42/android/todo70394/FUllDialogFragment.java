package local.hal.st42.android.todo70394;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.widget.Toast;

public class FUllDialogFragment  extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("確認！");
        builder.setMessage("このタスクを削除してもよろしいですか？");
        builder.setPositiveButton("削除",new DialogButtonClickListener());
        builder.setNegativeButton("キャンセル",new DialogButtonClickListener());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * ダイアログのボタンが押されたときの処理が記述されたメンバクラス
     */
    public class DialogButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Activity parent = getActivity();
            String msg = "";
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    msg = "タスクを削除しました。";
                    ToDoEditActivity shopeditActivity = (ToDoEditActivity) getActivity();
                    shopeditActivity.listDelete();
                    Toast.makeText(parent, msg, Toast.LENGTH_SHORT).show();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    }
}
