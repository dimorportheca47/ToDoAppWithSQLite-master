package com.example.viewsample;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

public class MenuTodo extends AppCompatActivity {
    //ToDo:削除ボタン、スターボタンの追加
    //ToDo:MainActivityの画面で各ToDoになんの詳細メニューが書かれてるかをアイコンで表示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_todo);

        //前画面からの情報を取得
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String detail = intent.getStringExtra("detail");
        String timeStamp = intent.getStringExtra("timeStamp");
        String isStar = intent.getStringExtra("isStar");
        String isArchive = intent.getStringExtra("isArchive");
        String deadline = intent.getStringExtra("deadline");
        String reminder = intent.getStringExtra("reminder");
        String isRepeat = intent.getStringExtra("isRepeat");
        String memo = intent.getStringExtra("memo");

        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Toolbarのアイテムがタップされた場合に呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
                finish();
        }
        return super.onOptionsItemSelected(item);
    }



}
