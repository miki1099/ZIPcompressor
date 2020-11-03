import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ZipSaveManager extends Thread{

    public static final int BUFFER = 4096;
    JProgressBar progressBar;
    private List<File> files;
    private int filesCounter;
    private int filesSaved;
    public ZipSaveManager(JProgressBar progressBar, List<File> files){
        this.progressBar = progressBar;
        this.files = files;
        filesCounter = 0;
        filesSaved = 0;
    }

    @Override
    public void run() {
            getPathAndZipFiles(files);
            progressBar.setValue(100);
            this.interrupt();
    }

    private int countFiles(List<File> files){
        int fileCounter = 0;
        for (File file : files)
        {
           fileCounter += countChildren(file,file.getName());
        }
        return fileCounter + 1;
    }

    private int countChildren(File file, String fileName){
        int counter = 0;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File childFile : children) {
                counter++;
                counter += countChildren( childFile, fileName + File.separator + childFile.getName());
            }
            return counter;
        }
        return counter;
    }

    public void getPathAndZipFiles(List<File> files){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setSelectedFile(new File(System.getProperty("user.dir") + File.separator + "compressed.zip"));
        int tmp = chooser.showDialog(chooser.getRootPane(), "Add to archive");
        filesCounter = countFiles(files);
        if(tmp == JFileChooser.APPROVE_OPTION){
            zipFiles(files, chooser.getSelectedFile());
        }

    }

    private void zipFiles(List<File> files, File path){
        byte tmpData[] = new byte[BUFFER];
        try
        {
            ZipOutputStream zOutS = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(path), BUFFER));

            for (File file : files)
            {
                zip(zOutS, file, tmpData, file.getName());
            }

            zOutS.close();
        }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(new JDialog(), "Problem with IO.");
        }
    }

    private void zip(ZipOutputStream zipOut, File fileToZip, byte [] tmpData, String fileName) throws IOException{
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(File.separator)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + File.separator));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                filesSaved++;
                zip(zipOut, childFile, tmpData, fileName + File.separator + childFile.getName());
                progressBar.setValue((int) Math.round(((double)filesSaved/filesCounter)*100));
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        int length;
        while ((length = fis.read(tmpData)) >= 0) {
            zipOut.write(tmpData, 0, length);
        }
        fis.close();
    }
}

