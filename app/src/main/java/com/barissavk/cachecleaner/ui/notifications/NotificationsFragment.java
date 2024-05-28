package com.barissavk.cachecleaner.ui.notifications;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barissavk.cachecleaner.databinding.FragmentNotificationsBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class NotificationsFragment extends Fragment {

    private static final int REQUEST_PERMISSION = 1;
    private FragmentNotificationsBinding binding;
    private FileAdapter adapter;
    private List<File> files = new ArrayList<>();
    private Stack<File> directoryStack = new Stack<>();
    private File currentDirectory;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FileAdapter(files, this::onFileClick, this::onFileDelete, this::onFileRename);
        binding.recyclerView.setAdapter(adapter);

        binding.buttonBack.setOnClickListener(v -> onBackPressed());
        binding.buttonCreateFolder.setOnClickListener(v -> createFolder());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            loadFiles(Environment.getExternalStorageDirectory());
        }

        return root;
    }

    private void onFileClick(File file) {
        if (file.isDirectory()) {
            directoryStack.push(currentDirectory);
            loadFiles(file);
        } else {
            String extension = getFileExtension(file);
            if (extension != null && (extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg"))) {
                openImageInGallery(file);
            } else {
                Toast.makeText(getContext(), "This is not an image file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onFileDelete(File file) {
        File currentFolder = currentDirectory;

        if (file.isDirectory()) {
            deleteFolder(file);
            loadFiles(currentFolder);
        } else {
            if (file.delete()) {
                files.remove(file);
                loadFiles(currentFolder);
                Toast.makeText(getContext(), "File deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            String[] children = folder.list();
            if (children != null) {
                for (String child : children) {
                    File childFile = new File(folder, child);
                    if (childFile.isDirectory()) {
                        deleteFolder(childFile);
                    } else {
                        childFile.delete();
                    }
                }
            }
        }
        folder.delete();
    }

    private void onFileRename(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rename File");

        final EditText input = new EditText(getContext());
        input.setText(file.getName());
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString();
            File newFile = new File(file.getParent(), newName);
            if (file.renameTo(newFile)) {
                loadFiles(currentDirectory);
                Toast.makeText(getContext(), "File renamed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to rename file", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void loadFiles(File directory) {
        files.clear();
        currentDirectory = directory;

        if (!directory.exists()) {
            return;
        }

        File[] filesArray = directory.listFiles();
        if (filesArray != null) {
            for (File file : filesArray) {
                files.add(file);
            }
        }
        adapter.updateFiles(files); // update adapter with new file list
    }

    private void onBackPressed() {
        if (!directoryStack.isEmpty()) {
            File previousDirectory = directoryStack.pop();
            loadFiles(previousDirectory);
        }
    }

    private void createFolder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create Folder");

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String folderName = input.getText().toString();
            File newFolder = new File(currentDirectory, folderName);
            if (newFolder.mkdir()) {
                loadFiles(currentDirectory);
                Toast.makeText(getContext(), "Folder created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to create folder", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return null;
        }
        return name.substring(lastIndexOf + 1);
    }

    private void openImageInGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFiles(Environment.getExternalStorageDirectory());
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}