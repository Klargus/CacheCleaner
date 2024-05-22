package com.barissavk.cachecleaner.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.barissavk.cachecleaner.R;

import java.io.File;
import java.text.DecimalFormat;

public class DashboardFragment extends Fragment {

    private TextView cacheSizeTextView;
    private Button clearCacheButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        cacheSizeTextView = view.findViewById(R.id.cacheSizeTextView);
        clearCacheButton = view.findViewById(R.id.clearCacheButton);

        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeCache();
            }
        });

        return view;
    }

    private void initializeCache() {
        long size = 0;
        File dir = getContext().getCacheDir();
        size += getDirSize(dir);
        size += getDirSize(dir);
        cacheSizeTextView.setText(readableFileSize(size));
        showToast(dir.getAbsolutePath());
    }

    public long getDirSize(File dir){
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void showToast(String message) {
        Context context = getActivity();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}