package com.example.viewsample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// SQLite に対してSQLを実行するためのクラス
// NOTE: onCreate() はアプリの初回起動時のみ実行されるので，
// NOTE: 変更した場合は一度アンインストールしてから実行する

//ToDo:各ToDoに対して、詳細メニューのデータをDBに結びつける
//期限:deadline
//リマインダー:reminder
//繰り返すかどうか:repeat
//サブタスク:subtask ToDo:subtaskの構造を決定する
//メモ:memo
//添付ファイル:attached
//コメント:comment ToDo:コメントはチャットのような形で実装

//DBへの保存は各項目を記入後、決定ボタン（チェックボタン）を押してMenuToDo画面に戻るタイミング
public class DatabaseHelper2 extends SQLiteOpenHelper {

    // データベースの各種定数
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TestDB.db";
    private static final String TABLE_NAME = "testdb";
    private static final String _ID = "_id";
    private static final String COLUMN_TITLE = "name";
    private static final String COLUMN_DETAIL = "detail";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_STAR = "isstar";
    private static final String COLUMN_ARCHIVE = "isarchive";

    // SQL文
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DETAIL + " TEXT, " +
                    COLUMN_TIMESTAMP + " TEXT, " +
                    COLUMN_STAR + " TEXT, " +
                    COLUMN_ARCHIVE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブルの作成
        db.execSQL(
                SQL_CREATE_ENTRIES
        );

        Log.d("debug", "onCreate(SQLiteDatabase db)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // アップデートを判定，古いバージョンは削除し新規作成
        db.execSQL(
                SQL_DELETE_ENTRIES
        );
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
