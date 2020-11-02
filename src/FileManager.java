import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    public static List<File> getFiles(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(true);
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int tmp = chooser.showDialog(chooser.getRootPane(), "Add to archive");

        if(tmp == JFileChooser.APPROVE_OPTION){
            List<File> files = Arrays.asList(chooser.getSelectedFiles());

            return files;
        } else{
            return List.of();
        }
    }

    public static List<File> addWithoutDuplicate(List<File> inp, List<File> out){
        for(File file : inp){
            if(!out.contains(file)){
                out.add(file);
            }
        }
        return out;
    }

    public static List<String> getFileNames(List<File> files) {
        List<String> names = new ArrayList<>();
        for(File file : files){
            names.add(file.getName());
        }
        return names;
    }
}
