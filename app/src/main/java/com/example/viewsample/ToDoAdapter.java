package com.example.viewsample;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

/*
 * ArrayList<ToDoItem> の各要素を ListView に反映させるためのクラス
 * List 内のCheckbox のリスナはここで定義される
 */
public class ToDoAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater = null;
    private ArrayList<ToDoItem> toDoList;

    public ToDoAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setToDoList(ArrayList<ToDoItem> toDoList) {
        this.toDoList = toDoList;
    }

    @Override
    public int getCount() {
        return toDoList.size();
    }

    @Override
    public Object getItem(int position) {
        return toDoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return toDoList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = layoutInflater.inflate(
                R.layout.checkbox_tv_star, parent, false);
        final TextView textView = (TextView) convertView.findViewById(R.id.tv_in_lv);

        // TextView に文字列を設定
        ((TextView)convertView.findViewById(R.id.tv_in_lv)).setText(toDoList.get(position).getName());

        // CheckBox (左)にリスナを設定
        final CheckBox cbDone = (CheckBox) convertView.findViewById(R.id.cb_done_in_lv);
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // アニメーションをロードする
                //Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_motion);
                // チェック状態が変更された時の処理を記述
                if (cbDone.isChecked()) {
                    Log.d("listener", "CheckBox(Done) is checked");
                    ((ListView) parent).performItemClick(cbDone, position, R.id.cb_done_in_lv);

                    //取り消し線を引く
                    textView.setText(textView.getText());
                    TextPaint paint = textView.getPaint();
                    paint.setFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    paint.setAntiAlias(true);
                    // ListViewのアイテム要素にロードしたアニメーションを実行する
                    //convertView.startAnimation(anim);
                } else {
                    Log.d("listener", "CheckBox(Done) is Unchecked");

                    //取り消し線を消す
                    TextPaint paint = textView.getPaint();
                    paint.setFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    textView.setText(toDoList.get(position).getName());


                }

            }
        });

        // CheckBox(右)にリスナを設定
        final CheckBox cbStar = (CheckBox) convertView.findViewById(R.id.cb_star_in_lv);
        cbStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // チェック状態が変更された時の処理を記述
                if (cbDone.isChecked()) {
                    Log.d("listener", "CheckBox(Star) is checked");
                } else {
                    Log.d("listener", "CheckBox(Star) is Unchecked");

                }
            }
        });

        return convertView;
    }
}
