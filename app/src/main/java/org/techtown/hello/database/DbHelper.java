package org.techtown.hello.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/* 'MainActivity.java' > onCreate() 단계에서 테이블 생성 및 onUpgrade() 메소드를 통해
    안드로이 실행 시 DB를 지속 사용 가능할 수 있도록 SqLiteOpenHelper 클래스 사용 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "To-Do List";                // SQLiteOpenHelper 클래스를 통해 사용할 DB 명 선언
    private static final int DB_Ver = 1;                               // DB 버전 선언
    private static final String DB_TABLE = "toDo";                     // DB 테이블명 선언
    private static final String DB_COLUMN = "toDo_name";               // DB 컬럼명 선언
    private static final String DB_COLUMN_TIME = "toDo_created_on";    // DB 컬럼 생성시간 선언


    /*
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
    */
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_Ver);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, %s DATETIME NOT NULL)",
                                      DB_TABLE, DB_COLUMN, DB_COLUMN_TIME);
        db.execSQL(query);
    }

    // Update table
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE);
        db.execSQL(query);
        onCreate(db);
    }

    // Insert ToDo
    public void insertNewToDo(String toDo, String toDo_created_on){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN, toDo);
        values.put(DB_COLUMN_TIME, toDo_created_on);

        db.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Delete ToDo
    public void deleteToDo(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, DB_COLUMN + " = ?", new String[]{task});

        db.close();
    }

    // get 'ToDoList' -> Send 'ToDoList' -> to 'MainActivity.java' > loadToDoList()
    public ArrayList<String> getToDoList(){
            ArrayList<String> toDo_List = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE, new String[]{DB_COLUMN}, null, null, null, null, null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(DB_COLUMN);
            toDo_List.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        return toDo_List;
    }
}
