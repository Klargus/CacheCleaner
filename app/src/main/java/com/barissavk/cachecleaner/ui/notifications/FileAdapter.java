package com.barissavk.cachecleaner.ui.notifications;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
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
    private OnRenameClickListener renameClickListener;

    public interface OnItemClickListener {
        void onItemClick(File file);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(File file);
    }

    public interface OnRenameClickListener {
        void onRenameClick(File file);
    }

    public FileAdapter(List<File> files, OnItemClickListener clickListener, OnDeleteClickListener deleteClickListener, OnRenameClickListener renameClickListener) {
        this.files = files;
        this.clickListener = clickListener;
        this.deleteClickListener = deleteClickListener;
        this.renameClickListener = renameClickListener;
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

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(file);
            }
        });

        // Silme düğmesini gizle
        holder.btnDelete.setVisibility(View.GONE);

        holder.itemView.setOnLongClickListener(v -> {
            showPopupMenu(holder.itemView, file);
            return true;
        });
    }

    private void showPopupMenu(View view, File file) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.file_options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals(view.getContext().getString(R.string.rename))) {
                if (renameClickListener != null) {
                    renameClickListener.onRenameClick(file);
                }
                return true;
            } else if (title.equals(view.getContext().getString(R.string.delete))) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(file);
                }
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateFiles(List<File> newFiles) {
        this.files = newFiles;
        notifyDataSetChanged();
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