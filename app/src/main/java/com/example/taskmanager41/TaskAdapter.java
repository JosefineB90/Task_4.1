package com.example.taskmanager41;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> taskList;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(HashMap<String, String> task);
        void onDeleteClick(HashMap<String, String> task);
    }

    public TaskAdapter(Context context, ArrayList<HashMap<String, String>> taskList, OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        HashMap<String, String> task = taskList.get(position);

        holder.textViewTitle.setText(task.get("title"));
        holder.textViewDescription.setText(task.get("description"));
        holder.textViewDate.setText(task.get("due_date"));

        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(task));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(task));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription, textViewDate;
        Button buttonEdit, buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
