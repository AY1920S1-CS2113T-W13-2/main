package javacake.commands;

import javacake.DukeException;
import javacake.Profile;
import javacake.ProgressStack;
import javacake.Storage;
import javacake.Ui;
import javacake.quiz.Question;

import java.util.ArrayList;

public class ReviewCommand extends Command{

    private ArrayList<Question> answeredQuestions;

    /**
     * ReviewCommand constructor to load the list of questions to review.
     *
     * @param chosenQuestions list of questions from a quiz session. userAnswer in all questions must not be null.
     */
    public ReviewCommand(ArrayList<Question> chosenQuestions) {
        answeredQuestions = chosenQuestions;
    }

    /**
     * Executes the review of a quiz after a quiz is completed.
     *
     * @param progressStack how far the program is currently in in the table of contents.
     * @param ui the UI responsible for inputs and outputs of the program.
     * @param storage Storage to write updated data.
     * @param profile Profile of the user.
     * @return
     * @throws DukeException This method does not throw this exception.
     */
    @Override
    public String execute(ProgressStack progressStack, Ui ui, Storage storage, Profile profile) throws DukeException {
        int index = 0;
        while (true) {
            ui.showLine();
            try {
                Question question = answeredQuestions.get(index);
                ui.displayReview(question, index + 1, answeredQuestions.size());
            } catch (IndexOutOfBoundsException e) {
                ui.showError("Invalid index! Range of question: 1 - " + answeredQuestions.size());
            }

            String next = ui.readCommand();
            if (next.trim().equals("back")) {
                break;
            }
            try {
                index = Integer.parseInt(next) - 1;
            } catch (NumberFormatException e) {
                ui.showError("Invalid index! Range of question: 1 - " + answeredQuestions.size());
            }
        }
        ui.showLine();
        return new BackCommand().execute(progressStack, ui, storage, profile);
    }
}
