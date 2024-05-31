package songsorter.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import songsorter.music.EmotionPoint;

import java.io.File;

/**
 * Class to handle all Python based tasks within a virtual environment that works from the Caches folder on MacOS only
 */
public class PythonEnvHandler {
    /** The filepath of all the caches for the application */
    public static final String OUTPUT_PATH = System.getProperty("user.home") + "/Library/Caches/" + "io.github.etashj.songsorter/"; 

    private String pip = OUTPUT_PATH + "venv/bin/pip"; 
    private String python = OUTPUT_PATH + "venv/bin/python3"; 
    private String venv = OUTPUT_PATH + "venv";

    /**
     * Creates a Python environment and installs all python dependencies via Pip. 
     * <p> Note: This process can be intensive and may take some time </p>
     * @throws PythonError if Python is not installed on the user's system. 
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public PythonEnvHandler() throws PythonError, InterruptedException, IOException {
    	this.init();
        this.installPackages();
    }
    
    /**
     * Gets the filepath of the virtual environment's pip installation. 
     * @return filepath of pip installation
     */
    public String getPip() {
        return this.pip;
    }

    /**
     * Gets the filepath of the virtual environment's python3 installation. 
     * @return filepath of python3 installation
     */
    public String getPython() {
        return this.python;
    }

    /**
     * Creates the venv in the cache directory
     * @throws PythonError if Pyhton is not installed or is on the wrong path
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public void init() throws PythonError, InterruptedException, IOException {
        File f = new File(venv); 
        if(!f.exists()) { 
            String[] pythonCommand = {"python3.9", "-m", "venv", OUTPUT_PATH + "venv"}; 
            Process process = Runtime.getRuntime().exec(pythonCommand);

            int exitCode = process.waitFor();
            
    
            if (exitCode != 0) throw new PythonError("Unable to find Python installation, code " + exitCode); 

            pip = OUTPUT_PATH + "venv/bin/pip"; 
            python = OUTPUT_PATH + "venv/bin/python3"; 

            // Install setuptools
            pythonCommand = new String[]{pip, "install", "setuptools"}; 

            process = Runtime.getRuntime().exec(pythonCommand);

            exitCode = process.waitFor();
            
    
            if (exitCode != 0) throw new PythonError("Unable to find Python installation, code " + exitCode); 

            // Install setuptools
            pythonCommand = new String[]{pip, "install", "--upgrade", "pip"}; 

            process = Runtime.getRuntime().exec(pythonCommand);

            exitCode = process.waitFor();
            
    
            if (exitCode != 0) throw new PythonError("Unable to find Python installation, code " + exitCode); 
        }
    }

    /**
     * Prints out the version of the python venv
     * @throws PythonError if Pyhton is not installed or is on the wrong path, or the venv is not intitialized.
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public void version() throws PythonError, IOException, InterruptedException {
        String[] pythonCommand = {python, " --version"}; 
        System.out.println("Executing command: " + Arrays.toString(pythonCommand));
        Process process = Runtime.getRuntime().exec(pythonCommand);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            
            String line;
            System.out.println("Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
            System.out.println("Errors:");
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }

        int exitCode = process.waitFor();
        

        if (exitCode != 0) throw new PythonError("Code " + exitCode); 
    }

    /**
     * Installed all packages required via pip. 
     * @throws PythonError If venv/pip is not initialized or installed. 
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public void installPackages() throws PythonError, IOException, InterruptedException {
        
        Path outputPath = Paths.get(OUTPUT_PATH + "models");
        if (!Files.exists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {}
        }

        outputPath = Paths.get(OUTPUT_PATH + "scripts");
        if (!Files.exists(outputPath)) {
            try {
                Files.createDirectories(outputPath);
            } catch (IOException e) {}
        }

        ClassLoader c = this.getClass().getClassLoader(); 

        InputStream modelIS = c.getResourceAsStream("models/model_state_dict.pth");
        InputStream predictorIS = c.getResourceAsStream("models/predictor_state_dict.pth");
        InputStream reqsIS = c.getResourceAsStream("requirements.txt");
        InputStream demoIS = c.getResourceAsStream("demo.py");
        InputStream modelsIS = c.getResourceAsStream("models.py");

        Files.copy(modelIS, Paths.get(OUTPUT_PATH + "models/model_state_dict.pth"), StandardCopyOption.REPLACE_EXISTING); 
        Files.copy(predictorIS, Paths.get(OUTPUT_PATH + "models/predictor_state_dict.pth"), StandardCopyOption.REPLACE_EXISTING); 
        Files.copy(reqsIS, Paths.get(OUTPUT_PATH + "requirements.txt"), StandardCopyOption.REPLACE_EXISTING); 
        Files.copy(demoIS, Paths.get(OUTPUT_PATH + "scripts/demo.py"), StandardCopyOption.REPLACE_EXISTING); 
        Files.copy(modelsIS, Paths.get(OUTPUT_PATH + "scripts/models.py"), StandardCopyOption.REPLACE_EXISTING); 

        String[] pythonCommand = {pip, "install",  "-r", OUTPUT_PATH + "requirements.txt"}; 
    
        System.out.println("Executing command: " + Arrays.toString(pythonCommand));
        Process process = Runtime.getRuntime().exec(pythonCommand);

        int exitCode = process.waitFor();
        

        if (exitCode != 0) throw new PythonError("Unable to find Python installation, code " + exitCode); 

    }

    /**
     * Computes the emotions via a Long Short Term Memory neural network and returns the string Pytorch tensor. 
     * @param fp Path to the mp3 file
     * @return A string of the printed PyTorch tensor of the output of the machine leanring model
     * @throws PythonError If an error occurs within python, python is not installed, or the venv is not initilaized. 
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public String runCommand(String fp) throws PythonError, IOException, InterruptedException {
        String[] pythonCommand = {python, OUTPUT_PATH + "scripts/demo.py", fp}; 
        System.out.println("Executing command: " + Arrays.toString(pythonCommand));
        Process process = Runtime.getRuntime().exec(pythonCommand);

        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        

        if (exitCode != 0) throw new PythonError("Unable to find Python installation, code " + exitCode); 

        return output.toString(); 
    }

    /**
     * Parses the output of Python to an array of {@code EmotionPoint} objects which are typically linked to a song
     * @param s The string input which should be taken from {@code runCommand()}, a printed Pytorch tensor of dimesnions nx2. 
     * @return the array of {@code EmotionPoint} objects that was shown in a Torch tensor. 
     */
    public static EmotionPoint[] parseOut(String s) {
        s = s.substring(8, s.lastIndexOf(']')); 
        String[] splitted = s.split(",");
        EmotionPoint[] points = new EmotionPoint[splitted.length/2]; 
        for (int i=0; i<splitted.length-1; i+=2) {
            points[i/2] = new EmotionPoint(
                Double.parseDouble(splitted[i].substring(splitted[i].indexOf('[')+1)), 
                Double.parseDouble(splitted[i+1].substring(0, splitted[i+1].length()-1).strip())
            ); 
        }
        return points; 
    }
    
    /**
     * Cleares the cache folder of all downloaded songs, but not the venv or any of the scripts. 
     * Deleting the venv must be done manually, but can clear upwards of 2 GB. 
     */
    public static void clearCache() {
        File[] files = new File(OUTPUT_PATH).listFiles();
        if(files!=null) { 
            for(File f: files) {
                if (!f.getName().equals("venv") && !f.getName().equals("requirements.txt") && 
                    !f.getName().equals("scripts") && !f.getName().equals("models"))
                    f.delete();
            }
        }
    }
}
