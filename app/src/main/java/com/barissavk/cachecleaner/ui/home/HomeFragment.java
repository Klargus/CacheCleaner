package com.barissavk.cachecleaner.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.barissavk.cachecleaner.databinding.FragmentHomeBinding;

import java.io.File;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        showStorageInfo();

        // Telefon özelliklerini al
        String model = Build.MODEL;
        String brand = Build.BRAND;
        String osVersion = Build.VERSION.RELEASE;
        int apiLevel = Build.VERSION.SDK_INT;
        String cpuModel = Build.SUPPORTED_ABIS[0];
        //long totalMemory = Runtime.getRuntime().totalMemory();
        //String formattedMemory = formatMemorySize(totalMemory);

        long totalInternalMemory = getTotalInternalMemorySize();
        String formattedInternalMemory = formatMemorySize(totalInternalMemory);

        // TextView öğelerini bul
        TextView modelTextView = binding.modelTextView;
        TextView brandTextView = binding.brandTextView;
        TextView osVersionTextView = binding.osVersionTextView;
        TextView apiLevelTextView = binding.apiLevelTextView;
        TextView cpuModelTextView = binding.cpuModelTextView;
        TextView memoryTextView = binding.memoryTextView;

        // TextView'lerde telefon özelliklerini göster
        modelTextView.setText("Model: " + model);
        brandTextView.setText("Brand: " + brand);
        osVersionTextView.setText("OS Version: " + osVersion);
        apiLevelTextView.setText("API Level: " + apiLevel);
        cpuModelTextView.setText("CPU Model: " + cpuModel);
        memoryTextView.setText("Total Memory: " + formattedInternalMemory);

        return root;
    }

    public static String formatMemorySize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp-1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    private void showStorageInfo() {
        // Depolama bilgilerini al
        String path = Environment.getDataDirectory().getPath();
        StatFs stat = new StatFs(path);

        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long freeBlocks = stat.getAvailableBlocksLong();

        // Toplam ve kullanılabilir alanları hesapla
        long totalSize = totalBlocks * blockSize;
        long freeSize = freeBlocks * blockSize;
        long usedSize = totalSize - freeSize;

        // Kullanılan ve kalan alanları göster
        binding.textViewUsedStorage.setText("Kullanılan: " + formatSize(usedSize));
        binding.textViewFreeStorage.setText("Kalan: " + formatSize(freeSize));

        // ProgressBar'ı güncelle
        int progress = (int) ((usedSize * 100) / totalSize);
        binding.progressBarStorage.setProgress(progress);
    }

    private String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}