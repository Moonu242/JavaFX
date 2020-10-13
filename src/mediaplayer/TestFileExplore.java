package mediaplayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestFileExplore {

    public static void main(String[] args) {

        File f = new File(
                "C:/download/udemy-dl-master/bootstrap-4-from-scratch-with-5-projects/01 Intro  Getting Started/001 Welcome To The Course.mp4");
        String currentFileName = f.getName();
        String currentFileAbsolutePath = f.getAbsolutePath();

        System.out.println("currentFileName : " + currentFileName);
        System.out.println("currentFileAbsolutePath : " + currentFileAbsolutePath);

        int currentFileNumber = getFileNumber(currentFileName);
        Map<Integer, String> fileMap = new HashMap<Integer, String>();
        if (f.exists()) {
            File parentFile = f.getParentFile();
            if (parentFile.isDirectory()) {
                File[] listFiles = parentFile.listFiles();
                for (File file : listFiles) {
                    int fileNumber = getFileNumber(file.getName());
                    fileMap.put(fileNumber, file.getAbsolutePath());
                }
            }
        }

        for (Map.Entry<Integer, String> entry : fileMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + " Value = " + entry.getValue());
        }

        System.out.println("value = " + fileMap.get(6));

    }

    private static int getFileNumber(String fileName) {
        int fileNumber = 0;
        String[] fileNameSplits = fileName.split(" ");
        if (fileNameSplits.length > 0) {
            String s = fileNameSplits[0];
            s = s.replaceFirst("^0+(?!$)", "");
            fileNumber = Integer.parseInt(s);
        }
        return fileNumber;
    }

}
