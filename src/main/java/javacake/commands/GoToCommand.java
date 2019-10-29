package javacake.commands;

import javacake.Duke;
import javacake.exceptions.DukeException;
import javacake.quiz.QuestionType;
import javacake.quiz.QuizSession;
import javacake.storage.Profile;
import javacake.Logic;
import javacake.storage.Storage;
import javacake.storage.StorageManager;
import javacake.ui.Ui;
import javacake.quiz.Question;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class GoToCommand extends Command {

    private Queue<String> index = new LinkedList<>();

    /**
     * constructor for goto command. Contains a queue of index in which user wants to navigate into.
     * @param inputCommand Parsed goto command by user
     */
    public GoToCommand(String inputCommand) {
        if (inputCommand.matches("\\d+")) { //check if input is numeric
            index.add(inputCommand);
        } else {
            String[] buffer = inputCommand.split("\\.");
            for (int i = 0; i < buffer.length; i++) {
                index.add(buffer[i]);
            }
        }
    }

    /**
     * Execute jumping to given index.
     * @param logic tracks current location in program
     * @param ui the Ui responsible for outputting messages
     * @param storageManager storage container.
     * @throws DukeException Error thrown when unable to close reader
     */
    public String execute(Logic logic, Ui ui, StorageManager storageManager)
            throws DukeException {
        int intIndex = Integer.parseInt(index.poll()) - 1;
        logic.updateFilePath(logic.gotoFilePath(intIndex));
        String filePath = logic.getFullFilePath();
        if (filePath.contains("Quiz")) {
            if (filePath.contains("1. Java Basics")) {
                QuizSession.setProfile(storageManager.profile);
                if (Duke.isCliMode()) {
                    return new QuizSession(QuestionType.BASIC, Duke.isCliMode())
                            .execute(logic, ui, storageManager);
                } else {
                    logic.insertQueries();
                    QuizSession.logic = logic;
                    return "!@#_QUIZ_1";
                }
            } else if (filePath.contains("2. Object-Oriented Programming")) {
                QuizSession.setProfile(storageManager.profile);
                if (Duke.isCliMode()) {
                    return new QuizSession(QuestionType.OOP, Duke.isCliMode())
                            .execute(logic, ui, storageManager);
                } else {
                    logic.insertQueries();
                    QuizSession.logic = logic;
                    return "!@#_QUIZ_2";
                }
            } else if (filePath.contains("3. Extensions")) {
                QuizSession.setProfile(storageManager.profile);
                if (Duke.isCliMode()) {
                    return new QuizSession(QuestionType.EXTENSIONS, Duke.isCliMode())
                            .execute(logic, ui, storageManager);
                } else {
                    logic.insertQueries();
                    QuizSession.logic = logic;
                    return "!@#_QUIZ_3";
                }
            } else {
                QuizSession.setProfile(storageManager.profile);
                if (Duke.isCliMode()) {
                    return new QuizSession(QuestionType.ALL, Duke.isCliMode())
                            .execute(logic, ui, storageManager);
                } else {
                    logic.insertQueries();
                    QuizSession.logic = logic;
                    return "!@#_QUIZ_4";
                }
            }
        }
        logic.insertQueries();
        if (logic.containsDirectory()) {
            if (index.size() != 0) {
                return execute(logic, ui, storageManager);
            }
            return (logic.displayDirectories());
        } else {
            logic.updateFilePath(logic.gotoFilePath(0));
            if (index.size() != 0) {
                return execute(logic, ui, storageManager);
            }
            return (logic.readQuery());
        }
    }
}
