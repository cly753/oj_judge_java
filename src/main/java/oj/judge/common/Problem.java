package oj.judge.common;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class Problem {
    private static final String label = "Problem::";

    public Long id;
    public boolean specialJudge = false;

    public String resourcesHash = "";

    public byte[] problemResourcesZip;
    public List<String> input;
    public List<String> output;

    public int timeLimit; // in ms. 0 for not specified.
    public int memoryLimit; // in MB. 0 for not specified.

    public int totalCase;

    public Problem(Long i, String hash, byte[] zip) {
        id = i;
        resourcesHash = hash;
        problemResourcesZip = zip;

        totalCase = 0;
        timeLimit = 0;
        memoryLimit = 0;

        extract(problemResourcesZip);
    }

    private void extract(byte[] problemResourcesZip) {
        if (problemResourcesZip == null)
            return ;
        try {
            Map<String, byte[]> allResource = new HashMap<>();

            final int BUFFER = 2048;
            ByteArrayInputStream bais = new ByteArrayInputStream(problemResourcesZip);
            CheckedInputStream checksum = new CheckedInputStream(bais, new Adler32());
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
            ZipEntry entry;

            while((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + entry);
                int count;
                byte data[] = new byte[BUFFER];

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(baos, BUFFER);
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();

                allResource.put(entry.getName(), baos.toByteArray());
            }
            zis.close();

            if (Conf.debug()) System.out.println("Checksum: " + checksum.getChecksum().getValue());

            byte[] problemSpecRaw = allResource.get("problem.json");
            if (problemSpecRaw == null) {
                totalCase = 0;
                return ;
            }

            JSONObject problemSpec = new JSONObject(new String(problemSpecRaw));

            if (Conf.debug()) System.out.println(label + problemSpec.toString());

            totalCase = problemSpec.getInt("numberOfTestCases");

            JSONObject defaultSpec = problemSpec.getJSONObject("default");
            timeLimit = defaultSpec.getInt("timeLimit");
            memoryLimit = defaultSpec.getInt("memoryLimit");
            specialJudge = defaultSpec.getBoolean("specialJudge");

            input = new ArrayList<>(totalCase);
            output = new ArrayList<>(totalCase);

            for (int i = 0; i < totalCase; i++) {
                byte[] inRaw = allResource.get((i + 1) + ".in");
                byte[] outRaw = allResource.get((i + 1) + ".out");

                if (inRaw == null || outRaw == null) {
                    totalCase = 0;
                    return ;
                }

                input.add(new String(inRaw));
                output.add(new String(outRaw));
            }
        } catch (IOException e) {
            e.printStackTrace();

            totalCase = 0;
        }
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
        ret += "\ntimeLimit = " + timeLimit + " ms\nmemoryLimit = " + memoryLimit + " KBytes\ntotalCase = " + totalCase;
        for (int i = 0; i < input.size(); i++) {
            ret += "\n\tCase " + i + ": ";
            ret += "\n\t\t[input ]\n" + input.get(i);
            ret += "\n\t\t[output]\n" + output.get(i);
        }
        return ret;
    }
}
