package com.example.viewsample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    ListView lvTodo;
    ArrayList<ToDoItem> toDoList;
    ToDoAdapter adapter;

    private void addItem(ArrayList<ToDoItem> toDoList, String name, String detail) {
        ToDoItem item = new ToDoItem();
        item.setName(name);
        item.setDetail(detail);
        toDoList.add(item);
        adapter.notifyDataSetChanged();
    }

    private boolean removeItem(ArrayList<ToDoItem> toDoList) {
        if (toDoList.size() >= 1) {
            toDoList.remove(0);
            adapter.notifyDataSetChanged();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        /*
         * button listener
         */
        Button addBtn = findViewById(R.id.btn_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("listener", "Button(ADD) is clicked");
                addItem(toDoList, "hello", "world");
                Log.d("debug", "toDoList.size: " + toDoList.size());
            }
        });

        final Button removeBtn = findViewById(R.id.btn_remove);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("listener", "Button(ADD) is clicked");
                removeItem(toDoList);
                Log.d("debug", "toDoList.size: " + toDoList.size());
                Toast.makeText(TestActivity.this,
                        "First To Do is removed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        toDoList = new ArrayList<>();
        ToDoItem item;
        item = new ToDoItem();
        item.setName("hello");
        item.setDetail("hello hello");
        toDoList.add(item);

        // todo: adapter を自作
        adapter = new ToDoAdapter(TestActivity.this);
        adapter.setToDoList(toDoList);

        lvTodo = findViewById(R.id.lv_test);
        lvTodo.setAdapter(adapter);

        lvTodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                // Log.d("listener", "view.getId(): " + view.getId());
                switch (view.getId()) {
                    case R.id.cb_done_in_lv:
                        removeItem(toDoList);
                        break;
                    case R.id.cb_star_in_lv:
                        // TODO: 2019/10/03 starを付けた時の処理をかく 
                        break;

                    default:
                        // ボタン以外の領域をタップしたとき(viewはなく-1が入るはず)
                        // タップしたアイテムの取得
                        Log.d("debug", "Tap: " + i);

                        ListView listView = (ListView)parent;
                        ToDoItem item = (ToDoItem) listView.getItemAtPosition(i);  // SampleListItemにキャスト

                        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                        builder.setTitle("Tap No. " + i);
                        builder.setMessage(item.getName());
                        builder.show();
                        break;

                }

            }
        });

    }
}
