package javacake;

import edu.emory.mathcs.backport.java.util.Collections;
import javacake.exceptions.CakeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Logic {

    private String defaultFilePath = "content/MainList";
    private static String currentFilePath = "content/MainList";

    private List<String> listOfFiles = new ArrayList<>();
    private static boolean isDirectory = true;
    private int numOfFiles = 0;

    /**
     * Private constructor to ensure exactly one logic object.
     */
    private Logic() {

    }

    private static final Logic INSTANCE = new Logic();

    /**
     * Using Singleton pattern to ensure exactly one logic object.
     * @return The only instance of logic object.
     */
    public static Logic getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the starting file path to application content.
     * @return starting file path to application content.
     */
    public String getDefaultFilePath() {
        return defaultFilePath;
    }

    /**
     * Stores all files in the currentFilePath into listOfFiles.
     */
    public void loadFiles() throws CakeException {
        String[] tempListFiles = currentFilePath.split("/");
        int currFileSlashCounter = tempListFiles.length;
        listOfFiles.clear();
        try {
            CodeSource src = Logic.class.getProtectionDomain().getCodeSource();
            boolean isJarMode = true;
            if (src != null) { //jar
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                while (true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null) {
                        isJarMode = false;
                        break;
                    }
                    String name = e.getName();
                    //System.out.println(name);
                    if (name.startsWith(currentFilePath)) {
                        String[] listingFiles = name.split("/");
                        if (listingFiles.length == currFileSlashCounter + 1) {
                            System.out.println(name + " == " + currFileSlashCounter);
                            listOfFiles.add(listingFiles[currFileSlashCounter]);
                            Collections.sort(listOfFiles);
                            numOfFiles = listOfFiles.size();
                        }
                    }
                }
            }

            if (!isJarMode) { //non-jar
                InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(currentFilePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String currentLine;
                while ((currentLine = br.readLine()) != null) {
                    //System.out.println(currentLine);
                    listOfFiles.add(currentLine);
                }
                Collections.sort(listOfFiles);
                numOfFiles = listOfFiles.size();
                br.close();
            }
        } catch (NullPointerException | IOException e) {
            throw new CakeException("Content not found!" + "\nPls key 'back' or 'list' to view previous content!");
        }
    }

    public int getNumOfFiles() {
        return numOfFiles;
    }

    /**
     * Method is only invoked when List command is called.
     */
    public void setDefaultFilePath() {
        currentFilePath = defaultFilePath;
    }

    public String getFullFilePath() {
        return currentFilePath;
    }

    /**
     * Method is invoked when GoTo command is called.
     * Based on the index, return the particular filePath.
     *
     * @param index Index of the new path found in filePathQueries.
     * @return the particular filePath based on the input index.
     */
    public String gotoFilePath(int index) throws CakeException {

        try {
            return listOfFiles.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new CakeException(e.getMessage() + "\n Pls key 'back' or 'list' to view previous content");
        }
    }

    /**
     * Update the currentFilePath by concatenating the updatedPath.
     * updatedPath is given by gotoFilePath method.
     *
     * @param updatedPath particular path to be updated into currentFilePath.
     */
    public void updateFilePath(String updatedPath) {
        currentFilePath += ("/" + updatedPath);
    }

    /**
     * Checks if file in currentFilePath is a directory or file.
     * Returns once if it is a directory.
     * Returns twice if it is a file.
     * Used for BackCommand.
     */
    public void backToPreviousPath() {
        if (!currentFilePath.equals(defaultFilePath)) {
            if (!currentFilePath.contains(".txt")) {
                currentFilePath = gotoParentFilePath(currentFilePath);
            } else {
                currentFilePath = gotoParentFilePath(gotoParentFilePath(currentFilePath));
            }
        }
    }

    /**
     * Creates a file path to parent directory by
     * removing child file or directory name from filePath.
     * @param filePath input file path to be reduced.
     * @return file path to parent directory relative to initial file path.
     */
    public String gotoParentFilePath(String filePath) {
        String[] filesCapture = filePath.split("/");
        StringBuilder reducedFilePath = new StringBuilder();
        for (int i = 0; i < filesCapture.length - 1; i++) {
            reducedFilePath.append(filesCapture[i]).append("/");
        }
        String parentFilePath = reducedFilePath.toString();
        parentFilePath = parentFilePath.substring(0, parentFilePath.length() - 1);
        return parentFilePath;
    }


    /**
     * Displays the all directories found in currentFilePath.
     */
    public String displayDirectories() {
        StringBuilder sb = new StringBuilder();
        sb.append("Here are the ").append(listOfFiles.size()).append(" subtopics available!\n");
        for (String queries : listOfFiles) {
            sb.append(queries).append("\n");
        }
        sb.append("Key in the index to learn more about the topic or do the quiz!").append("\n");
        return sb.toString();
    }

    /**
     * Reads the content in content text file.
     *
     * @throws CakeException When the text file in currentFilePath is not found.
     */
    public String readQuery() throws CakeException {
        try {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(currentFilePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String sentenceRead;
            while ((sentenceRead = br.readLine()) != null) {
                sb.append(sentenceRead).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new CakeException("Unable to read text file");
        }
    }

    /**
     * Checks current directory contains directory or text files.
     *
     * @return true if current directory contains directory.
     */
    public boolean containsDirectory() {
        if (isDirectory) {
            return true;
        }
        return false;
    }


    /**
     * Calls insertQueries method to instantiate new file paths.
     * Checks if new file path accesses a directory or file.
     * If directory, display all the file names within the directory.
     * Else read the content of the text file.
     * @return String of formatted file names or text file content.
     * @throws CakeException when file or directory is not found.
     */
    public String processQueries() throws CakeException {
        insertQueries();
        if (isDirectory) {
            return displayDirectories();
        } else {
            return readQuery();
        }
    }

    /**
     * Clears all file paths in filePathQueries.
     * Load all files in currentFilePath.
     * Update isDirectory if current directory contains directories.
     * Adds new list of file names in filePathQueries to be processed.
     */
    public void insertQueries() throws CakeException {
        clearQueries();
        loadFiles();
        for (String listOfFile : listOfFiles) {
            if (listOfFile.contains(".txt")) {
                isDirectory = false;
            } else {
                isDirectory = true;
            }
        }

    }

    /**
     * Clears all entries in listOfFiles.
     */
    public void clearQueries() {
        listOfFiles.clear();
    }

}