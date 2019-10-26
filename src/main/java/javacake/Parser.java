package javacake;


import javacake.commands.AddCommand;
import javacake.commands.ChangeColorCommand;
import javacake.commands.BackCommand;
import javacake.commands.Command;
import javacake.commands.CreateNoteCommand;
import javacake.commands.EditNoteCommand;
import javacake.commands.ExitCommand;
import javacake.commands.GoToCommand;
import javacake.commands.HelpCommand;
import javacake.commands.ListCommand;
import javacake.commands.OverviewCommand;
import javacake.commands.ResetCommand;
import javacake.commands.ScoreCommand;
import javacake.exceptions.DukeException;
import javacake.ui.MainWindow;

public class Parser {

    /**
     * Allows the user input to be parsed before running 'execute'.
     * @param inputCommand String inputted by user, which needs to be parsed
     *              to identify the intent
     * @return a subclass of the Command Class along
     *         with their respective intent
     * @throws DukeException Shows error when unknown command is inputted
     */
    public static Command parse(String inputCommand) throws DukeException {
        String[] buffer = inputCommand.split("\\s+");
        String input = buffer[0];
        helper(input);
        if (input.equals("exit")) {
            return new ExitCommand();
        } else if (input.equals("list")) {
            return new ListCommand();
        } else if (input.equals("back")) {
            return new BackCommand();
        } else if (input.equals("help")) {
            return new HelpCommand(inputCommand);
        } else if (input.equals("score")) {
            return new ScoreCommand();
        } else if (input.equals("reset")) {
            return new ResetCommand();
        } else if (input.equals("goto")) {
            if (inputCommand.length() <= 4) {
                throw new DukeException("Please specify index number in 'goto' command!");
            }
            return new GoToCommand(inputCommand.substring(5));
        } else if (input.equals("overview")) {
            return new OverviewCommand();
        } else if (input.equals("createnote")) {
            return new CreateNoteCommand(inputCommand);
        } else if (input.equals("editnote")) {
            return new EditNoteCommand(inputCommand);
        } else if (input.equals("deadline")) {
            return new AddCommand(inputCommand);
        } else if (input.equals("change")) {
            MainWindow.isChanged = true;
            return new ChangeColorCommand();
        } else {
            throw new DukeException("OOPS!!! I'm sorry, but I don't know what that means.");
        }
    }

    /**
     * Method to help handle small typo made by user.
     * Types of typo handled are:
     * 1) if user types one alphabet wrongly, eg. trre instead of tree.
     * 2) if user accidentally types extra or less letter, eg. treee or tre instead of tree.
     */
    private static void helper(String input) throws DukeException {
        String[] commands = {"exit", "list", "back", "help", "score", "reset",
                             "goto", "overview", "deadline", "editnote", "createnote"};
        for (int i = 0; i < commands.length; i++) {
            boolean isTypo = false;
            String command = commands[i];
            int length = command.length();

            if (length == input.length()) {
                int similarity = 0;
                for (int j = 0; j < length; j++) {
                    if (input.charAt(j) == command.charAt(j)) {
                        similarity++;
                    }
                }
                if (similarity + 1 == length) {
                    isTypo = true;
                }
            }

            boolean isOneLetterApart = false;

            if (command.length() == input.length() + 1 || command.length() == input.length() - 1) {
                isOneLetterApart = true;
            }

            if (!command.equals(input) && isOneLetterApart) {
                if (command.contains(input) || input.contains(command)) {
                    isTypo = true;
                }
            }

            if (isTypo) {
                throw new DukeException("Sorry, but do you mean this : " + command);
            }
        }
    }

}
