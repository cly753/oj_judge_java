package oj.judge.common;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class Problem {
    public Long id;
    public boolean specialJudge = false;

    public String resourcesHash = "";

    public byte[] problemResourcesZip;
    public List<String> input;
    public List<String> output;

    public double timeLimit; // in ms. 0 for not specified.
    public double memoryLimit; // in MB. 0 for not specified.

    public int totalCase;

    public Problem(Long id, byte[] problemResourcesZip, double timeLimit, double memoryLimit, String resourcesHash, boolean specialJudge) {
        this.id = id;
        this.problemResourcesZip = problemResourcesZip;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.resourcesHash = resourcesHash;
        this.specialJudge = specialJudge;

        extract(problemResourcesZip);
    }

    private void extract(byte[] problemResourcesZip) {
        input  = new ArrayList<String>();
        output = new ArrayList<String>();
        problemResourcesZip = null;
        totalCase = 0;

//        try {
//            final int BUFFER = 2048;
//            BufferedOutputStream dest = null;
//            ByteArrayInputStream bais = new ByteArrayInputStream(problemResourcesZip);
//            CheckedInputStream checksum = new CheckedInputStream(bais, new Adler32());
//            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
//            ZipEntry entry;
//            while((entry = zis.getNextEntry()) != null) {
//                System.out.println("Extracting: " + entry);
//                int count;
//                byte data[] = new byte[BUFFER];
//                // write the files to the disk
//                FileOutputStream fos = new FileOutputStream(entry.getName());
//                dest = new BufferedOutputStream(fos, BUFFER);
//                while ((count = zis.read(data, 0, BUFFER)) != -1) {
//                    dest.write(data, 0, count);
//                }
//                dest.flush();
//                dest.close();
//            }
//            zis.close();
//            System.out.println("Checksum: " + checksum.getChecksum().getValue());
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            totalCase = 0;
//        }
    }

    public boolean saveInput(int caseNo, Path path) {
        try {
            OpenOption[] options = new OpenOption[] { WRITE, CREATE, TRUNCATE_EXISTING };
            BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("US-ASCII"), options);
            writer.write(input.get(caseNo), 0, input.get(caseNo).length());
            writer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String toString() {
        String ret = "Problem...\nid = " + id + "\nspecialJudge = " + specialJudge;
        ret += "\nresourcesHash: " + resourcesHash;
        ret += "\ntimeLimit = " + timeLimit + "\nmemoryLimit = " + memoryLimit + "\ntotalCase = " + totalCase;
        for (int i = 0; i < input.size(); i++) {
            ret += "\n\tCase " + i + ": ";
            ret += "\n\t\t[input ]\n" + input.get(i);
            ret += "\n\t\t[output]\n" + output.get(i);
        }
        return ret;
    }
}
