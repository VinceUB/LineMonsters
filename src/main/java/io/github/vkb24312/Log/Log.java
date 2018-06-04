package io.github.vkb24312.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Log {
    public void log(String s){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String callingClass = stackTraceElements[2] + ": ";

        s = callingClass + "\r\n\t" + s.replace("\n", "\r\n\t");

        if(isDebug){
            System.out.println(s);
        }

        try {
            FileWriter fw = new FileWriter(log, true);
            fw.write(s + "\r\n");
            fw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private File logCreator(){
        String date =
                Integer.toString(LocalDateTime.now().getDayOfMonth()) + "."
                        + Integer.toString(LocalDateTime.now().getMonthValue()) + "."
                        + Integer.toString(LocalDateTime.now().getYear());

        File path = new File("logs/" + date + "/");
        path.mkdirs();

        int i = 0;
        File testFile;
        do{
            i++;
            testFile = new File(path, "log " + i + ".txt");
        } while (testFile.exists());

        System.out.println(date);

        return testFile;
    }

    private File log;

    public Log(){
        log = logCreator();
        isDebug = false;
    }

    public Log(boolean isDebug){
        log = logCreator();
        this.isDebug = isDebug;
    }

    private boolean isDebug;
}
