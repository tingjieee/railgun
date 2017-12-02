package org.railgun.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.railgun.Controls;
import org.railgun.FileManager;
import org.railgun.Timer;
import org.railgun.vm.Interpreter;

/**
 * Created by hinus on 2017/11/30.
 */
public class RunHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
        Timer timer = new Timer();
        Controls.getInstance().setTimer(timer);
        timer.setDaemon(true);
        timer.start();

        FileManager fileManager = FileManager.getInstance();
        String binaryFileName = fileManager.getBinaryFileName();
        if (binaryFileName == null) {
            String cmd = "python D:\\hack\\ddl.py " + fileManager.getSourceFileName();
            try {
                File file = new File("D:\\hack\\compile.bat");
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                bw.write("D:");
                bw.newLine();
                bw.write("cd hack");
                bw.newLine();
                bw.write(cmd);
                bw.newLine();
                bw.close();
            } catch (Exception e) {
                System.out.println("错误：" + e);
            }

            Process p = null;
            try {
                p = Runtime.getRuntime().exec("cmd /c start D:\\\\hack\\\\compile.bat");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int exitVal = 0;

            try {
                exitVal = p.waitFor();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            fileManager.setBinaryFileName("D:\\hack\\output.rgb");
            File selectedFile = new File(fileManager.getBinaryFileName());
            byte[] sourceContent = new byte[(int) selectedFile.length()];

            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                fileInputStream.read(sourceContent);
                fileManager.setBinaryContent(sourceContent);

                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Interpreter.getInstance().run(FileManager.getInstance().getBinaryContent());
    }
}