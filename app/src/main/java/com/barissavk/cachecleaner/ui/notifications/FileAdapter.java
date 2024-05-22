package com.barissavk.cachecleaner.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barissavk.cachecleaner.R;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<File> files;
    private OnItemClickListener clickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(File file);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(File file);
    }

    public FileAdapter(List<File> files, OnItemClickListener clickListener, OnDeleteClickListener deleteClickListener) {
        this.files = files;
        this.clickListener = clickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        File file = files.get(position);
        holder.textFileName.setText(file.getName());
        holder.textFileSize.setText(file.isDirectory() ? "Directory" : file.length() + " bytes");

        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(file));

        holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView textFileName;
        TextView textFileSize;
        Button btnDelete;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            textFileName = itemView.findViewById(R.id.textFileName);
            textFileSize = itemView.findViewById(R.id.textFileSize);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}