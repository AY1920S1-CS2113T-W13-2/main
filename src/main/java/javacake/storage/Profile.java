package javacake.storage;

import javacake.Duke;
import javacake.commands.QuizCommand;
import javacake.exceptions.DukeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;

public class Profile {
    private static String filepath = "data";
    private String username;
    private ArrayList<Integer> overalltopicsDone = new ArrayList<>();
    private ArrayList<Integer> individualTopicsDone = new ArrayList<>();
    int totalNumOfMainTopics = 4;
    int levelsOfDifficulty = 3;


    public Profile() throws DukeException {
        this("data");
    }

    /**
     * Constructor for profile.
     * @param filename String of filepath
     * @throws DukeException when unable to create profile
     */
    public Profile(String filename) throws DukeException {
        filepath = filename;
        filepath += "/save/savefile.txt";
        File file = new File(filepath);
        Duke.logger.log(Level.INFO,"Filepath: " + filepath);
        try {
            try {
                if (!file.getParentFile().getParentFile().exists()) {
                    file.getParentFile().getParentFile().mkdir();
                    file.getParentFile().mkdir();
                    file.createNewFile();
                    initialiseUser();
                    System.out.println("A" + file.getParentFile().getParentFile().getPath());
                } else if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdir();
                    file.createNewFile();
                    initialiseUser();
                    System.out.println("B" + file.getParentFile().getPath());
                } else if (!file.exists()) {
                    file.createNewFile();
                    initialiseUser();
                    System.out.println("C" + file.getPath());
                } else {
                    Duke.logger.log(Level.INFO, filepath + " is found!");
                }

            } catch (IOException e) {
                System.out.println("before reader");
                throw new DukeException("Failed to create new file");
            }

            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line;
            int count = -1;
            while ((line = reader.readLine()) != null) {
                if (count == -1) {
                    username = line;
                } else if (count < 4) {
                    overalltopicsDone.add(Integer.parseInt(line));
                } else {
                    individualTopicsDone.add(Integer.parseInt(line));
                }
                ++count;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("after reader");
            throw new DukeException("Failed to close reader");
        }
    }

    /**
     * Method to hard reset profile.
     */
    public static void resetProfile() {
        File file = new File(filepath);
        if (file.exists()) {
            file.delete();
            System.out.println("deleting: " + file.getPath());
        }
    }

    public String getUsername() {
        return username;
    }

    /**
     * Method to overwrite Username.
     * @param oldname old username
     * @throws DukeException when unable to write progress
     */
    public void overwriteName(String oldname) throws DukeException {
        username = oldname;
        writeProgress();
    }

    /**
     * Method to set topic score.
     * @param contentIdx idx of content
     * @throws DukeException when unable to write progress
     */
    public void setOverallMarks(int contentIdx, int marks) throws DukeException {
        overalltopicsDone.set(contentIdx, marks);
        writeProgress();
    }

    public void setIndividualMarks(int contentIdx, int marks) throws DukeException {
        individualTopicsDone.set(contentIdx, marks);
        writeProgress();
    }


    /**
     * Method to get topic score.
     * @param contentIdx idx of content
     * @return score of the specified topic
     */
    public int getOverallContentMarks(int contentIdx) {
        return overalltopicsDone.get(contentIdx);
    }

    public int getIndividualContentMarks(int contentIdx) {
        return individualTopicsDone.get(contentIdx);
    }

    /**
     * Method to get total progress.
     * @return total progress status
     */
    public int getTotalProgress() {
        int count = 0;
        for (int i : overalltopicsDone) {
            count += i;
        }
        return count;
    }

    /**
     * Method that creates data to be written into savefile.txt.
     */
    private void initialiseUser() throws DukeException {
        username = "NEW_USER_!@#";
        try {
            PrintWriter out = new PrintWriter(filepath);
            out.println(username);
            for (int i = 0; i < totalNumOfMainTopics * (levelsOfDifficulty + 1); ++i) {
                out.println("0");
            }
            out.close();
        } catch (FileNotFoundException e) {
            throw new DukeException("Cannot initialise file");
        }
    }

    private void writeProgress() throws DukeException {
        try {
            PrintWriter out = new PrintWriter(filepath);
            out.println(username);
            for (int i : overalltopicsDone) {
                out.println("" + i);
            }
            for (int i: individualTopicsDone) {
                out.println("" + i);
            }
            out.close();
        } catch (FileNotFoundException e) {
            throw new DukeException("Cannot initialise file");
        }
    }
}
