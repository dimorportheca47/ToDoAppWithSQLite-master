package com.example.viewsample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//FIXME: todoリストと完了済みtodoが別々のリストビューなのでスクロールが別々になっている

public class MainActivity extends AppCompatActivity {

    // 繰り返し使用するためメンバ変数とした(onCreateにて定義される)
    private EditText etName;
    private DatabaseHelper2 helper;
    private ToDoAdapter adapter;
    private ArchiveAdapter archiveAdapter;
    private ArrayList<ToDoItem> toDoList;
    private ArrayList<ToDoItem> archiveList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // view の取得
        etName = findViewById(R.id.et_name);
        ListView lvShow = findViewById(R.id.lv_show);
        final CheckBox cbStar = findViewById(R.id.cb_star);
        final ListView lvArchive = findViewById(R.id.lv_archive);

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
        try (
            Cursor cursor = db.rawQuery("SELECT * FROM testdb WHERE isarchive=?;", new String[]{"0"})
            ) {
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
                int idxIsArchive = cursor.getColumnIndex("isarchive");
                item.setIsArchive(cursor.getString(idxIsArchive));
                toDoList.add(item);
            }
        }

        try (
            Cursor cursor = db.rawQuery("SELECT * FROM testdb WHERE isarchive=?;", new String[]{"1"})
        ){
            archiveList = new ArrayList<>();
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
                int idxIsArchive = cursor.getColumnIndex("isarchive");
                item.setIsArchive(cursor.getString(idxIsArchive));
                archiveList.add(item);
            }
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
                        archiveData(item, i);
                        break;
                    case R.id.cb_star_in_lv:
                        if (item.getIsStar().equals("0")) {
                            starredData(item, i);
                        }else {
                            unStarredData(item, i);
                        }
                        break;
                    default:
                        // NOTE: ボタン以外の領域をタップしたとき(viewはnull?でgetId()には-1が入るはず)
                        // TODO: 2019/10/08 ポップアップではなく右からスライドインする画面にする
                        Log.d("debug", "ListView Tap: " + i);

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(item.getName())
                                .setMessage(item.getDetail() +
                                        "\ntimeStamp: " + item.getTimeStamp() +
                                        "\nisStar: " + item.getIsStar() +
                                        "\nisArchive: " + item.getIsArchive())
                                .setPositiveButton("できたー", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("debug", "完了 is clicked");
                                        archiveData(item, i);
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

        // adapter作成して Listview に適用
        archiveAdapter = new ArchiveAdapter(MainActivity.this);
        archiveAdapter.setArchiveList(archiveList);
        lvArchive.setAdapter(archiveAdapter);


        lvArchive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int i, long l) {

                ListView listView = (ListView)parent;
                final ToDoItem item = (ToDoItem) listView.getItemAtPosition(i);

                switch (view.getId()) {
                    case R.id.cb_done_in_lv:
                        undoData(item, i);
                        break;
                    case R.id.cb_star_in_lv:
                        // スターは押せないはず
                        break;
                    default:
                        // NOTE: ボタン以外の領域をタップしたとき(viewはnull?でgetId()には-1が入るはず)
                        // TODO: 2019/10/08 ポップアップではなく右からスライドインする画面にする
                        Log.d("debug", "ListView Tap: " + i);

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(item.getName())
                                .setMessage(item.getDetail() +
                                        "\ntimeStamp: " + item.getTimeStamp() +
                                        "\nisStar: " + item.getIsStar() +
                                        "\nisArchive: " + item.getIsArchive())
                                .show();
                        break;
                }
            }
        });

        // [完了済みのtodoを表示]ボタンを押したとき
        final Button archiveBtn = findViewById(R.id.btn_archive);
        archiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lvArchive.getVisibility() == View.VISIBLE) {
                    archiveBtn.setText(getString(R.string.btn_show_archive));
                    lvArchive.setVisibility(View.INVISIBLE);
                } else {
                    archiveBtn.setText(getString(R.string.btn_hide_archive));
                    lvArchive.setVisibility(View.VISIBLE);
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
        item.setIsArchive("0");
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
        String isArchive = item.getIsArchive();

        //detailに""をセット()
        if(detail == null){
            item.setDetail("null");
            detail = item.getDetail();
        }

        toDoList.add(0, item);

        // DBへの書き込み
        try ( SQLiteDatabase db = helper.getWritableDatabase()) {
            db.execSQL("INSERT INTO testdb (name, detail, timestamp, isstar, isarchive) VALUES (?, ?, ?, ?, ?);",
                    new String[]{name, detail, timeStamp, isStar, isArchive});
            adapter.notifyDataSetChanged();
        }
    }

    private void archiveData(ToDoItem item, int index) {

        String name = item.getName();
        String detail = item.getDetail();
        String timeStamp = item.getTimeStamp();

        //detailに""をセット()
        if(detail == null){
            item.setDetail("null");
            detail = item.getDetail();
        }
        toDoList.remove(index);
        item.setIsArchive("1");
        archiveList.add(0, item);

        // DBへの書き込み
        try ( SQLiteDatabase db = helper.getWritableDatabase()){
            db.execSQL("UPDATE testdb SET isarchive=? WHERE name=? AND detail=? AND timestamp=?;",
                    new String[]{"1", name, detail, timeStamp});
            adapter.notifyDataSetChanged();
        }
    }

    private void undoData(ToDoItem item, int index) {
        Log.d("debug", "undoData is called: idx = " + index);
        String name = item.getName();
        String detail = item.getDetail();
        String timeStamp = item.getTimeStamp();

        //detailに""をセット()
        if(detail == null){
            item.setDetail("null");
            detail = item.getDetail();
        }
        archiveList.remove(index);
        item.setIsArchive("0");
        toDoList.add(0, item);

        // DBへの書き込み
        try ( SQLiteDatabase db = helper.getWritableDatabase()){
            db.execSQL("UPDATE testdb SET isarchive=? WHERE name=? AND detail=? AND timestamp=?;",
                    new String[]{"0", name, detail, timeStamp});
            adapter.notifyDataSetChanged();
        }
    }

    private void starredData(ToDoItem item, int index) {
        Log.d("debug", "starredData is called: idx = " + index);
        String name = item.getName();
        String detail = item.getDetail();
        String timeStamp = item.getTimeStamp();

        // 一番上にあがってくる
        toDoList.remove(index);
        item.setIsStar("1");
        toDoList.add(0, item);
        adapter.notifyDataSetChanged();

        // DBへの書き込み
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.execSQL("UPDATE testdb SET isstar=? WHERE name=? AND detail=? AND timestamp=?;",
                    new String[]{"1", name, detail, timeStamp});
            adapter.notifyDataSetChanged();
        }
    }

    private void unStarredData(ToDoItem item, int index) {
        Log.d("debug", "un-starredData is called: idx = " + index);
        String name = item.getName();
        String detail = item.getDetail();
        String timeStamp = item.getTimeStamp();

        // toDoListの更新
        item.setIsStar("0");
        toDoList.set(index, item);
        adapter.notifyDataSetChanged();

        // DBへの書き込み
        try (SQLiteDatabase db = helper.getWritableDatabase()){
            db.execSQL("UPDATE testdb SET isstar=? WHERE name=? AND detail=? AND timestamp=?;",
                    new String[]{"0", name, detail, timeStamp});
            adapter.notifyDataSetChanged();
        }
    }

}