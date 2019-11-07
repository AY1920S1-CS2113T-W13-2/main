package javacake.commands;

import javacake.Logic;
import javacake.exceptions.CakeException;
import javacake.storage.StorageManager;
import javacake.ui.Ui;

public abstract class Command {
    protected CmdType type;
    protected String input;

    /**
     * Types of commands that are possible.
     */
    public enum CmdType {
        EXIT, LIST, FIND, DONE, DELETE, TODO, DEADLINE, REMIND, VIEWSCH,
        EDIT, BACK, GOTO, QUIZ, HELP, OVERVIEW, CREATE_NOTE, EDIT_NOTE,
        LIST_NOTE, DELETE_NOTE, VIEW_NOTE
    }

    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t',
        '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':', '.', ','};

    public abstract String execute(Logic logic, Ui ui, StorageManager storageManager) throws CakeException;

    /**
     * Checks if input command has no other parameter appended.
     * @param inputCommand Command input from user.
     * @throws CakeException If parameter is appended to command.
     */
    public static void checksParam(String inputCommand) throws CakeException {
        String bySpaces = "\\s+";
        String[] subStrings = inputCommand.split(bySpaces);
        if (subStrings.length > 1) {
            throw new CakeException("Please ensure there is no parameter(s) for this command");
        }
    }

    /**
     * Checks for illegal character methods for commands.
     * @param inputCommand Command input from user
     * @return True if command contains illegal characters.
     */
    public static boolean containsIllegalCharacter(String inputCommand) {
        String bySpaces = "\\s+";
        String[] subStrings = inputCommand.split(bySpaces);
        if (subStrings.length > 1) {
            String word = subStrings[1];
            return checkForIllegalChar(word);
        }
        return false;
    }

    /**
     * Checks for illegal characters in a keyword.
     * @param word Parameter within a input command.
     * @return True if illegal character is found.
     */
    private static boolean checkForIllegalChar(String word) {
        for (char illegalChar : ILLEGAL_CHARACTERS) {
            return (word.indexOf(illegalChar) >= 0);
        }
        return false;
    }


    /**
     * Method to check whether command is of type exit.
     * @return true if type is exit, false otherwise
     */
    public boolean isExit() {
        return this.type == CmdType.EXIT;
    }
}
