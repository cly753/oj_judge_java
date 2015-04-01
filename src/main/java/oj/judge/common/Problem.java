package oj.judge.common;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
        
        totalCase = 1;
        
        // http://www.oracle.com/technetwork/articles/java/compress-1565076.html
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
}
