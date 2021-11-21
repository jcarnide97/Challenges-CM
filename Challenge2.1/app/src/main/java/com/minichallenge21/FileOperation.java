package com.minichallenge21;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public class FileOperation {
    final Executor executor = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onCompleteRead(String result);
    }

    public void createFile(String title, FragmentActivity fragmentActivity) {
        executor.execute(()->{
            FileOutputStream fos = null;
            String fileName = title + ".txt";
            try {
                fos = fragmentActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void deleteFile(String title, FragmentActivity fragmentActivity) {
        executor.execute(()->{
            File filePath = new File(fragmentActivity.getFilesDir() + "/" + title + ".txt");
            if (filePath.exists()) {
                filePath.delete();
            }
        });
    }

    public AtomicReference<String> loadFile(String title, FragmentActivity fragmentActivity, Callback callback) {
        AtomicReference<String> finalNoteText = new AtomicReference<>("");
        executor.execute(()->{
            String noteText = "";
            File noteFile = new File(fragmentActivity.getFilesDir() + "/" + title + ".txt");
            if (noteFile.exists()) {
                Scanner sc = null;
                try {
                    sc = new Scanner(noteFile);
                    while (sc.hasNextLine()) {
                        noteText += sc.nextLine() + "\n";
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            finalNoteText.set(noteText);
            handler.post(()->{
                callback.onCompleteRead(finalNoteText.get());
            });
        });
        return finalNoteText;
    }

    public void writeToFile(String title, String noteDetails, FragmentActivity fragmentActivity) {
        executor.execute(()->{
            FileOutputStream fos = null;
            String fileName = title + ".txt";
            try {
                fos = fragmentActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(noteDetails.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String getNoteDetails(String title, FragmentActivity fragmentActivity) {
        String noteDetails = "";
        try {
            FileInputStream fis = fragmentActivity.openFileInput(title + ".txt");
            InputStreamReader isr = new InputStreamReader(fis);

            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }

            noteDetails = stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return noteDetails;
    }
}
