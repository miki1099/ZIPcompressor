import javax.swing.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipOpenManager extends Thread{
    public static final int BUFFER = 4096;
    JProgressBar progressBar;
    private File file;
    private long bytes;
    private long bytesWrite;

    public ZipOpenManager(JProgressBar progressBar, File file){
        this.progressBar = progressBar;
        this.file = file;
        bytes = 0l;
        bytesWrite = 0l;
    }

    @Override
    public void run() {
        try {
            unZipFiles(file);
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unZipFiles(File zipFile) throws IOException {
        bytes = zipFile.length();
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showDialog(chooser.getRootPane(), "Unzip to directory");

        if(option == JFileChooser.APPROVE_OPTION) {
            unzip(zipFile.getPath(), chooser.getSelectedFile().getAbsolutePath());
        }

    }

    private void unzip(String zipFilePath, String destDirectory) throws IOException{
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

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
            bytesWrite += BUFFER;
            progressBar.setValue(Math.round((int) ((double) bytesWrite/bytes) * 100));
        }
        bos.close();
    }
}
