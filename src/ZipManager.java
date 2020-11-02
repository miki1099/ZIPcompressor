import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipManager {

    public static final int BUFFER = 1024;

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
}

