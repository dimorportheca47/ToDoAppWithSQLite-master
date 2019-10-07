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

// TODO: 2019/10/03 ListViewの上に[ToDoを追加]バーが欲しい --FKM
// TODO: 2019/10/03 todoを押したときにdialogではなく新しい画面に遷移させる -- OGW
// TODO: 2019/10/03 [完了済みtodoを表示] ＆ deleteData() の改修 -- OGW
// TODO: 2019/10/03 star時の処理 -- OGW
// TODO: 2019/10/03 （オプションメニューの作成）
// TODO: 2019/10/03 （mipmap icon 作成）
// test

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etDetail;
    private DatabaseHelper2 helper;
    private ToDoAdapter adapter;
    private ArrayList<ToDoItem> toDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view の取得
        etName = findViewById(R.id.et_name);
//        etDetail = findViewById(R.id.et_detail);
        ListView lvShow = findViewById(R.id.lv_show);

        /*
         * set button listener
         */
        //Button testBtn = findViewById(R.id.btn_test);
//        testBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, TestActivity.class);
//                startActivity(intent);
//            }
//        });

        Button insertBtn = findViewById(R.id.btn_plus);
        insertBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = helper.getWritableDatabase();

                String name = etName.getText().toString();
                //String detail = etDetail.getText().toString();

                // nameが空欄の場合，再入力を促す
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "ToDo の名前が未入力だーよ！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // リストに追加
                ToDoItem item = new ToDoItem();
                item.setName(name);
                DateFormat f_dt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                String timeStamp = f_dt.format(new Date());  // ex. 2017/05/24 15:35:00
                item.setTimeStamp(timeStamp);
                insertData(item);
                Log.d("debug", "timestamp is setted");

                // EditTextを空欄に戻す
                etName.setText("");
                //etDetail.setText("");
                Log.d("listener", "INSERT btn is onCllck()");
            }
        });

        /*
         * DBを読んで，toDoList に書き込む
         */
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
                item.setTimeStamp((cursor.getString(idxTimeStamp)));
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
         * 右の□：押すと「重要」になる
         * 文字　：押すとポップアップで内容がでる
         */
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int i, long l) {
                // Log.d("listener", "view.getId(): " + view.getId());
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
                        // ボタン以外の領域をタップしたとき(viewはなく-1が入るはず)
                        // タップしたアイテムの取得
                        Log.d("debug", "Tap: " + i);

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
        //文字入力後エンターキーが押された場合の挙動
        //EditText(etName)にリスナを設定
        etName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SQLiteDatabase db = helper.getWritableDatabase();

                String name = etName.getText().toString();
                //String detail = etDetail.getText().toString();



                InputMethodManager inputMethodManager=  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
                    // nameが空欄の場合，再入力を促す
                    if (name.isEmpty()) {
                        Toast.makeText(getApplicationContext(),
                                "ToDo の名前が未入力だーよ！", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    // リストに追加
                    ToDoItem item = new ToDoItem();
                    item.setName(name);
                    //item.setDetail(detail);
                    insertData(item);

                    // EditTextを空欄に戻す
                    etName.setText("");
                    //etDetail.setText("");
                    Log.d("listener", "Enter key is onKey()");

//                    //キーボードを閉じる
//                    inputMethodManager.hideSoftInputFromWindow(etName.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }
                return false;
            }
        });
    }


    /*
     * 以下DB関係のメソッド
     */
    private void insertData(ToDoItem item) {
        /*
         * DBへの書き込み + リストビューへの追加
         */
        String name = item.getName();
        String detail = item.getDetail();
        String timeStamp = item.getTimeStamp();

        //detailに""をセット()
        if(detail == null){
            item.setDetail("null");
            detail = item.getDetail();
        }

        toDoList.add(0, item);

        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO testdb (name, detail, timestamp) VALUES (?, ?, ?);",
                    new String[]{name, detail, timeStamp});
            adapter.notifyDataSetChanged();
            Cursor cursor = db.rawQuery("SELECT * FROM testdb", null);
            Log.d("debug", "insertData() is called");
            Log.d("debug", "name:"+name+", detail:"+detail + " " + timeStamp);
            Log.d("debug", "rows : " + cursor.getCount());
        }
        finally {
            db.close();
        }
    }

    private void deleteData(ToDoItem item, int index) {
        /*
         * DB から削除　＋　リストビューから削除
         */
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
            Log.d("debug", "deleteData() is called");
            Log.d("debug", "name:"+name+", detail:"+detail);
            Log.d("debug", "rows : " + cursor.getCount());

        }
        finally {
            db.close();
        }
    }


}