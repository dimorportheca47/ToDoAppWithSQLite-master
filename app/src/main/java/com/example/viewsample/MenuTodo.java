package com.example.viewsample;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

public class MenuTodo extends AppCompatActivity {

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
