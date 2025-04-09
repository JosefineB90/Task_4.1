package com.example.taskmanager41;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText editTitle, editDescription, editDueDate;
    Button buttonAdd;
    RecyclerView recyclerView;

    DBHelper dbHelper;
    ArrayList<HashMap<String, String>> taskList;
    TaskAdapter adapter;

    boolean isEditing = false;
    int editingTaskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editDueDate = findViewById(R.id.editDueDate);
        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DBHelper(this);
        taskList = new ArrayList<>();

        // Setup date picker for consistent format
        editDueDate.setFocusable(false);
        editDueDate.setOnClickListener(v -> showDatePicker());

        adapter = new TaskAdapter(this, taskList, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(HashMap<String, String> task) {
                isEditing = true;
                editingTaskId = Integer.parseInt(task.get("id"));
                editTitle.setText(task.get("title"));
                editDescription.setText(task.get("description"));

                String dbDate = task.get("due_date");
                String[] parts = dbDate.split("-");
                if (parts.length == 3) {
                    String displayDate = parts[2] + "-" + parts[1] + "-" + parts[0];
                    editDueDate.setText(displayDate);
                    editDueDate.setTag(dbDate); // preserve original
                }

                buttonAdd.setText("Update Task");
            }

            @Override
            public void onDeleteClick(HashMap<String, String> task) {
                int id = Integer.parseInt(task.get("id"));
                dbHelper.deleteTask(id);
                loadTasks();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadTasks();

        buttonAdd.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String dbDate = (String) editDueDate.getTag(); // always save in yyyy-MM-dd

            if (title.isEmpty()) {
                editTitle.setError("Title is required");
                return;
            }

            if (dbDate == null || dbDate.isEmpty()) {
                editDueDate.setError("Please select a due date");
                return;
            }

            if (isEditing && editingTaskId != -1) {
                dbHelper.updateTask(editingTaskId, title, description, dbDate);
                isEditing = false;
                editingTaskId = -1;
                buttonAdd.setText("Add Task");
            } else {
                dbHelper.insertTask(title, description, dbDate);
            }

            clearInputs();
            loadTasks();
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dbFormat = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    String displayFormat = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear);
                    editDueDate.setText(displayFormat);
                    editDueDate.setTag(dbFormat); // store the correct format for DB
                }, year, month, day);

        datePickerDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTasks() {
        taskList.clear();
        Cursor cursor = dbHelper.getAllTasksSortedByDate();

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> task = new HashMap<>();
                task.put("id", cursor.getString(cursor.getColumnIndexOrThrow("id")));
                task.put("title", cursor.getString(cursor.getColumnIndexOrThrow("title")));
                task.put("description", cursor.getString(cursor.getColumnIndexOrThrow("description")));
                task.put("due_date", cursor.getString(cursor.getColumnIndexOrThrow("due_date")));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void clearInputs() {
        editTitle.setText("");
        editDescription.setText("");
        editDueDate.setText("");
        editDueDate.setTag(null);
    }
}
