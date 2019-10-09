package com.example.viewsample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArchiveAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater = null;
    private ArrayList<ToDoItem> archiveList;

    public ArchiveAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setArchiveList(ArrayList<ToDoItem> archiveList) {
        this.archiveList = archiveList;
    }

    @Override
    public int getCount() {
        return archiveList.size();
    }

    @Override
    public Object getItem(int position) {
        return archiveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return archiveList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = layoutInflater.inflate(
                R.layout.checkbox_tv_star, parent, false);
        final TextView textView = convertView.findViewById(R.id.tv_in_lv);

        // TextView に文字列を設定
        ((TextView)convertView.findViewById(R.id.tv_in_lv)).setText(archiveList.get(position).getName());
        //取り消し線を引く
        textView.setText(textView.getText());
        TextPaint paint = textView.getPaint();
        paint.setFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        paint.setAntiAlias(true);

        // 半透明にする
        (convertView.findViewById(R.id.ll_in_lv)).setBackgroundColor(Color.parseColor("#33ffffff"));

        // 必要なら★をつける
        boolean isStar = archiveList.get(position).getIsStar().equals("1");
        ((CheckBox)convertView.findViewById(R.id.cb_star_in_lv)).setChecked(isStar);

        // CheckBox (左)にリスナを設定
        final CheckBox cbDone = convertView.findViewById(R.id.cb_done_in_lv);
        cbDone.setChecked(true);
        cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // もともとチェックされているので外した時に処理
                if (cbDone.isChecked()) {
                    Log.d("listener", "CheckBox(Done) is checked");
                } else {
                    Log.d("listener", "CheckBox(Done) is Unchecked");
                    ((ListView) parent).performItemClick(cbDone, position, R.id.cb_done_in_lv);
                }

            }
        });

        // CheckBox(右)は押せないように
        final CheckBox cbStar = convertView.findViewById(R.id.cb_star_in_lv);
        cbStar.setClickable(false);

        return convertView;
    }
}
