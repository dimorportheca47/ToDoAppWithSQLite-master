package com.example.viewsample;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(name);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



}
