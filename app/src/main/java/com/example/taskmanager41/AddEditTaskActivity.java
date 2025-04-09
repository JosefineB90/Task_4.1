package com.example.taskmanager41;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditTaskActivity extends AppCompatActivity {

    EditText editTitle, editDescription, editDueDate;
    Button buttonSave;
    int taskId = -1; // default to -1 (means new task)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editDueDate = findViewById(R.id.editDueDate);
        buttonSave = findViewById(R.id.buttonSave);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("taskId")) {
            taskId = intent.getIntExtra("taskId", -1);
            editTitle.setText(intent.getStringExtra("title"));
            editDescription.setText(intent.getStringExtra("description"));
            editDueDate.setText(intent.getStringExtra("due_date"));
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String dueDate = editDueDate.getText().toString().trim();

                if (title.isEmpty()) {
                    editTitle.setError("Title is required");
                    return;
                }

                DBHelper dbHelper = new DBHelper(AddEditTaskActivity.this);

                if (taskId == -1) {
                    dbHelper.insertTask(title, description, dueDate);
                } else {
                    dbHelper.updateTask(taskId, title, description, dueDate);
                }

                finish();
            }
        });
    }
}
