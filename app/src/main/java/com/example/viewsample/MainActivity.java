package com.example.viewsample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // 繰り返し使用するためメンバ変数とした(onCreateにて定義される)
    private EditText etName;
    private DatabaseHelper2 helper;
    private ToDoAdapter adapter;
    private ArrayList<ToDoItem> toDoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view の取得
        etName = findViewById(R.id.et_name);
        ListView lvShow = findViewById(R.id.lv_show);
        final CheckBox cbStar = findViewById(R.id.cb_star);

        // [+] ボタンを押したときの処理
        Button insertBtn = findViewById(R.id.btn_plus);
        insertBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String name = etName.getText().toString();

                // nameが空欄の場合，再入力を促す
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "ToDo の名前が未入力だーよ！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // リストに追加
                boolean isStar = cbStar.isChecked();
                ToDoItem item = createItem(name, isStar);
                insertData(item);

                // EditTextを空欄に戻す
                etName.setText("");
                Log.d("listener", "[+] btn is onCllck()");
            }
        });

        //文字入力後エンターキーが押された場合の挙動
        // [+]ボタン押下時と基本的に同じ処理
        etName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                String name = etName.getText().toString();

                // InputMethodManager inputMethodManager=  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
                    // nameが空欄の場合，再入力を促す
                    if (name.isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "ToDo の名前が未入力だーよ！", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    // リストに追加
                    boolean isStar = cbStar.isChecked();
                    ToDoItem item = createItem(name, isStar);
                    insertData(item);

                    // EditTextを空欄に戻す
                    etName.setText("");
                    Log.d("listener", "Enter key is onKey()");

                    //キーボードを閉じる
                    //inputMethodManager.hideSoftInputFromWindow(etName.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }
                return false;
            }
        });

        // toDoList を初期化，DBからデータを読み込む
        helper = new DatabaseHelper2(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM testdb", null);

            toDoList = new ArrayList<>();
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                ToDoItem item = new ToDoItem();
                int idxName = cursor.getColumnIndex("name");
                item.setName(cursor.getString(idxName));
                int idxDetail = cursor.getColumnIndex("detail");
                item.setDetail(cursor.getString(idxDetail));
                int idxTimeStamp = cursor.getColumnIndex("timestamp");
                item.setTimeStamp(cursor.getString(idxTimeStamp));
                int idxIsStar = cursor.getColumnIndex("isstar");
                item.setIsStar(cursor.getString(idxIsStar));
                toDoList.add(item);
            }
        } finally {
            db.close();
        }

        // adapter作成して Listview に適用
        adapter = new ToDoAdapter(MainActivity.this);
        adapter.setToDoList(toDoList);
        lvShow.setAdapter(adapter);

        /*
         * ListView のリスナ
         * 左の□：押すとそのtodoを削除
         * 右の☆：押すと「重要」になる
         * 文字　：押すとポップアップで内容がでる
         */
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int i, long l) {

                ListView listView = (ListView)parent;
                final ToDoItem item = (ToDoItem) listView.getItemAtPosition(i);

                switch (view.getId()) {
                    case R.id.cb_done_in_lv:
                        // TODO: 2019/10/03 □ → ☑ になるアニメーションの後に消える -- FKM
                        deleteData(item, i);
                        break;
                    case R.id.cb_star_in_lv:
                        // TODO: 2019/10/03 starを付けた時の処理をかく -- OGW
                        break;

                    default:
                        // NOTE: ボタン以外の領域をタップしたとき(viewはnull?でgetId()には-1が入るはず)
                        // TODO: 2019/10/08 ポップアップではなく右からスライドインする画面にする
                        Log.d("debug", "ListView Tap: " + i);

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(item.getName())
                                .setMessage(item.getDetail() + "\n" + item.getTimeStamp())
                                .setPositiveButton("できたー", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("debug", "完了 is clicked");
                                        deleteData(item, i);

                                    }
                                })
                                .setNegativeButton("まだー", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("debug", "まだー is clicked");
                                    }
                                })
                                .show();
                        break;

                }

            }
        });

    }

    // 指定された名前で要素を作成する
    // 作成時点のtimeStampを取得
    private ToDoItem createItem(String name, boolean isStar) {

        ToDoItem item = new ToDoItem();
        item.setName(name);
        item.setIsStar(isStar ? "1" : "0");
        DateFormat f_dt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String timeStamp = f_dt.format(new Date());  // ex. 2017/05/24 15:35:00
        item.setTimeStamp(timeStamp);
        return item;
    }

    // DBへの書き込み + リストビューへの追加
    private void insertData(ToDoItem item) {

        String name = item.getName();
        String detail = item.getDetail();
        String timeStamp = item.getTimeStamp();
        String isStar = item.getIsStar();

        //detailに""をセット()
        if(detail == null){
            item.setDetail("null");
            detail = item.getDetail();
        }

        toDoList.add(0, item);

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO testdb (name, detail, timestamp, isstar) VALUES (?, ?, ?, ?);",
                    new String[]{name, detail, timeStamp, isStar});
            adapter.notifyDataSetChanged();
            Cursor cursor = db.rawQuery("SELECT * FROM testdb", null);
            Log.d("debug", "insertData() is called: name=" + name);
        }
        finally {
            db.close();
        }
    }

    // DB から削除　＋　リストビューから削除
    private void deleteData(ToDoItem item, int index) {

        String name = item.getName();
        String detail = item.getDetail();
        String timeStamp = item.getTimeStamp();

        //detailに""をセット()
        if(detail == null){
            item.setDetail("null");
            detail = item.getDetail();
        }
        toDoList.remove(index);
        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM testdb WHERE name=? AND detail=? AND timestamp=?;",
                    new String[]{name, detail, timeStamp});
            adapter.notifyDataSetChanged();

            Cursor cursor = db.rawQuery("SELECT * FROM testdb", null);
            Log.d("debug", "deleteData() is called: name = " + name);
        }
        finally {
            db.close();
        }
    }


}