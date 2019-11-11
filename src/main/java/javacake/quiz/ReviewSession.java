package javacake.quiz;

import javacake.commands.BackCommand;
import javacake.exceptions.CakeException;
import javacake.Logic;
import javacake.storage.StorageManager;
import javacake.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import static javacake.quiz.QuestionList.MAX_QUESTIONS;

public class ReviewSession implements QuizManager {
    private static final Logger LOGGER = Logger.getLogger(ReviewSession.class.getPackageName());
    private QuestionList answeredQuestions;
    private boolean isExitReview = false;

    /**
     * ReviewSession constructor to load the list of questions to review.
     *
     * @param questionList list of questions from a quiz session. userAnswer in all questions must not be null.
     */
    public ReviewSession(QuestionList questionList) {
        LOGGER.setUseParentHandlers(true);
        LOGGER.setLevel(Level.INFO);
        LOGGER.entering(getClass().getName(), "ReviewSession");
        answeredQuestions = questionList;
        LOGGER.exiting(getClass().getName(), "ReviewSession");
    }

    /**
     * Method to get question string, user answer and correct answer strings.
     *
     * @param index the index of the question, between 0 and MAX_QUESTIONS-1.
     * @return the string to be outputted.
     */
    @Override
    public String getQuestion(int index) {
        LOGGER.info("Reviewing question" + index);
        String message = "Type the question number to navigate to that question.\n"
                + "Type \"back\" to return to table of contents.\n";
        return message + answeredQuestions.getQuestion(index) + "\n\n" + answeredQuestions.getAnswers(index);
    }

    /**
     * Parses valid user input for review session.
     * @param index index of current question. Unused.
     * @param input user input which can be index which is valid between 1 and MAX_QUESTIONS, or "back".
     * @return Valid question index between 0 and MAX_QUESTIONS-1, or BackCommand identifier.
     * @throws CakeException if input is neither a valid question index or "back".
     */
    @Override
    public String parseInput(int index, String input) throws CakeException {
        if (input.equals("back")) {
            LOGGER.info("User chose to go BACK");
            return "!@#_BACK";
        } else if (isValidInput(input)) {
            int tmp = Integer.parseInt(input) - 1;
            return String.valueOf(tmp); // echo back input with proper indexing for the next getQuestion
        } else {
            LOGGER.warning("user question index out of range: " + input);
            throw new CakeException("That question number is out of range! Try again.\n\n"
                    + getQuestion(index));
        }
    }

    /**
     * Executes the review of a quiz after a quiz is completed. For CLI mode.
     *
     * @param logic how far the program is currently in in the table of contents.
     * @param ui the UI responsible for inputs and outputs of the program.
     * @param storageManager storage container.
     * @return execution of back command when input is equal to "back".
     * @throws CakeException This method does not throw this exception.
     */
    public String execute(Logic logic, Ui ui, StorageManager storageManager) throws CakeException {
        int index = 0;
        while (!isExitReview) {
            ui.showLine();
            try {
                Question question = answeredQuestions.getQuestionList().get(index);
                ui.displayReview(question, index + 1, answeredQuestions.getQuestionList().size());
                String command = ui.readCommand();
                if (command.trim().equals("back")) {
                    //Command.checksParam(command);
                    isExitReview = true;
                } else {
                    index = Integer.parseInt(command) - 1;
                }
            } catch (IndexOutOfBoundsException e) {
                ui.showError("Invalid index! Range of question: 1 - " + answeredQuestions.getQuestionList().size());
                index = 0;
            } catch (NumberFormatException e) {
                ui.showError("That isn't a number! Try again.");
            }
        }
        ui.showLine();
        return new BackCommand("back").execute(logic, ui, storageManager);
    }

    private static boolean isValidInput(String input) throws CakeException {
        int tmp;
        try {
            tmp = Integer.parseInt(input);
            if (tmp > MAX_QUESTIONS || tmp <= 0) {
                return false;
            }
        } catch (NumberFormatException | NullPointerException e) {
            throw new CakeException("You can't use that command here or number is out of range! " 
                    + "Type a valid question number or \"back\".");
        }
        return true;
    }
}
