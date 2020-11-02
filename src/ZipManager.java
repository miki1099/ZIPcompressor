import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.file.Files.createDirectories;

public class ZipManager {

    public static final int BUFFER = 4096;

    public static void getPathAndZipFiles(List<File> files){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setSelectedFile(new File(System.getProperty("user.dir") + File.separator + "compressed.zip"));
        int tmp = chooser.showDialog(chooser.getRootPane(), "Add to archive");
        if(tmp == JFileChooser.APPROVE_OPTION){
            zipFiles(files, chooser.getSelectedFile());
        }

    }

    private static void zipFiles(List<File> files, File path){
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

    private static void zip(ZipOutputStream zipOut, File fileToZip, byte [] tmpData, String fileName) throws IOException{
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
                zip(zipOut, childFile, tmpData, fileName + File.separator + childFile.getName());
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

    public static void unZipFiles(File zipFile) throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showDialog(chooser.getRootPane(), "Unzip to directory");

        if(option == JFileChooser.APPROVE_OPTION) {
            System.out.println(chooser.getSelectedFile().getAbsolutePath());
            unzip(zipFile.getPath(), chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private static void unzip(String zipFilePath, String destDirectory) throws IOException{
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            System.out.println(destDir.mkdir());
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.getName().endsWith(File.separator)) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}

