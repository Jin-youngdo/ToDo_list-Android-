package org.techtown.hello;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.techtown.hello.R;
import org.techtown.hello.database.DbHelper;

public class MainActivity extends AppCompatActivity {

    ListView listToDo;
    ArrayAdapter<String> mAdapter;
    DbHelper dbHelper;

    TextView progress_value;
    ProgressBar progressBar;
    int progress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);
        listToDo = findViewById(R.id.listTodo);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(progress);
        progress_value = findViewById(R.id.progress_value);

        loadToDoList();
    }

    /* 1. DBHelper > getToDoList() 함수를 통해 반환된 내장 DB 내에 존재하는 'ToDoList'를 불러오기
       2. ArrayAdapter 객체를 통해 ListView 객체인 'ListToDo'를 시각화 */
    public void loadToDoList(){     // DBHelper > arrayList 객체인 'toDo_List' 불러오기
        ArrayList<String> toDoList = dbHelper.getToDoList();
        if (mAdapter == null){
            mAdapter = new ArrayAdapter<String>(this, R.layout.row,R.id.toDo_title, toDoList);
            listToDo.setAdapter(mAdapter);
        }
        else{
            mAdapter.clear();
            mAdapter.addAll(toDoList);
            mAdapter.notifyDataSetChanged();
        }
    }


    /* Option Menu 생성
       1. res>menu> Menu Resource 파일 생성
       2. onCreateOptionsMenu() 오버라이딩
       3. onOptionsItemSelected() 오버라이딩
    */

    // 2. MenuInflater를 통해 OptionMenu 객체인 menu.xml inflate하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }


    /* 3. onOptionsItemSelected() 함수 오버라이딩을 통해
          menu.xml > 'add_toDo_list' 버튼 클릭 시 대화상자를 생성,
          대화상자 속 'Add' 버튼 클릭 시 호출되는 onClick() 함수 오버라이딩
          onClick() -> 대화상자속 EditText 객체 'editText'의 텍스트를 불러와 내장 DB에 쿼리 삽입
                       insertNewToDo() 함수 호출 및 프로그레스바 진행도 조절
          대화상자 속 'Cancle' 버튼 클릭 시 null 반환
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_toDo_list:
                final EditText editText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add New To Do List")
                        .setMessage("What do you want me to add?")
                        .setView(editText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String toDo = String.valueOf(editText.getText());
                                Date currentTime = Calendar.getInstance().getTime();
                                dbHelper.insertNewToDo(toDo, currentTime.toString());
                                Toast.makeText(getApplicationContext(), "ToDo created!", Toast.LENGTH_SHORT).show();
                                loadToDoList();
                                progress += 1;
                                progressBar.setProgress(progress);
                                progress_value.setText(String.valueOf(progress));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // To-DO List 삭제 및 프로그레스바 진행도 핸들링
    public void deleteToDo(View view){
        View parent = (View) view.getParent();
        TextView toDoTextView = parent.findViewById(R.id.toDo_title);
        String toDo = String.valueOf(toDoTextView.getText());
        dbHelper.deleteToDo(toDo);
        Toast.makeText(getApplicationContext(), "ToDo deleted!", Toast.LENGTH_SHORT).show();
        loadToDoList();
        progress -= 1;
        progressBar.setProgress(progress);
        progress_value.setText(String.valueOf(progress));
    }
}
