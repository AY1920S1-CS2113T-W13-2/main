package javacake.quiz;

import javacake.Duke;
import javacake.Parser;
import javacake.commands.Command;
import javacake.exceptions.DukeException;
import javacake.storage.Profile;
import javacake.ProgressStack;
import javacake.storage.Storage;
import javacake.ui.TopBar;
import javacake.ui.Ui;

import java.io.IOException;
import java.util.logging.Level;

public class QuizSession implements QuizManager {
    private QuestionList questionList;
    public String filePath;
    //private Question prevQuestion;
    private int currScore = 0;
    private static Profile profile;
    public ScoreGrade scoreGrade;
    int totalNumOfQns = 0;
    public static ProgressStack progressStack;
    private QuestionType qnType;
    private boolean isQuizComplete;
    public static final int MAX_QUESTIONS = 5;
    public static final double PERCENTAGE_1 = 0.5;
    public static final double PERCENTAGE_2 = 1.0;

    public enum ScoreGrade {
        BAD, OKAY, GOOD
    }

    /**
     * QuizCommand constructor for topic-based quiz.
     * @param type the topic of the quiz.
     */
    public QuizSession(QuestionType type, boolean isCli) throws DukeException {
        questionList = new QuestionList(type);
        qnType = type;
        if (!isCli) {
            this.filePath = progressStack.getFullFilePath();
            runGui();
        }
        isQuizComplete = false;
    }

    /**
     * Method to get the question string.
     * @param index question index between 0 and MAX_QUESTIONS-1.
     * @return question string.
     */
    @Override
    public String getQuestion(int index) {
        return questionList.getQuestion(index);
    }

    /**
     * Parses valid user input for quiz session.
     * @param index question index between 0 and MAX_QUESTIONS-1.
     * @param input user input, either as user's answer or commands available on results page.
     * @return command identifier if on result page, null otherwise.
     * @throws DukeException if user answer is not integer value during quiz, or command is invalid on results page.
     */
    @Override
    public String parseInput(int index, String input) throws DukeException {
        if (isQuizComplete) {
            switch (input) {
            case ("review"):
                return "!@#_REVIEW";
            case ("back"):
                // TODO tie BackCommand identifier to MainWindow
                return "!@#_BACK";
            default:
                throw new DukeException("Invalid command at this point in the program.");
            }
        } else {
            checkAnswer(index, input);
            return null;
        }
    }

    public static void setProfile(Profile profile) {
        QuizSession.profile = profile;
    }

    /**
     * Executes the quiz.
     * @param progressStack how far the program is currently in in the table of contents.
     * @param ui the UI responsible for inputs and outputs of the program.
     * @param storage Storage to write updated data.
     * @param profile Profile of the user.
     * @throws DukeException Error thrown when there is a problem with score calculation.
     * @return
     */
    public String execute(ProgressStack progressStack, Ui ui, Storage storage, Profile profile)
            throws DukeException, IOException {
        progressStack.insertQueries();
        assert !progressStack.containsDirectory();
        this.filePath = progressStack.getFullFilePath();
        totalNumOfQns = progressStack.getNumOfFiles();
        for (int i = 0; i < MAX_QUESTIONS; i++) {
            Question question = questionList.getQuestionList().get(i);
            ui.displayQuiz(question.getQuestion(), i + 1, MAX_QUESTIONS);
            String userAnswer = ui.readCommand();
            questionList.getQuestionList().get(i).setUserAnswer(userAnswer);
            if (question.isAnswerCorrect(userAnswer)) {
                currScore++;
            }
            ui.showLine();
        }
        if (currScore > MAX_QUESTIONS) {
            throw new DukeException("Something went wrong when calculating the score:\n"
                    + "Calculated score is greater than maximum possible score.");
        }

        overwriteOldScore(currScore, profile);

        ui.displayResults(currScore, MAX_QUESTIONS);
        String nextCommand = ui.readCommand();
        if (nextCommand.equals("review")) {
            return new ReviewSession(questionList).execute(progressStack, ui, storage, profile);
        } else {
            Command newCommand = Parser.parse(nextCommand);
            return newCommand.execute(progressStack, ui, storage, profile);
        }
    }

    /**
     * Method to execute but for GUI.
     */
    public void runGui() throws DukeException {
        totalNumOfQns = progressStack.getNumOfFiles();
    }

    /**
     * Method to overwrite the old score of user,
     * if it's less than the current score.
     * @param score new score of user
     * @param profile profile object of user
     * @throws DukeException error if question type is undefined
     */
    public void overwriteOldScore(int score, Profile profile) throws DukeException {
        int topicIdx;
        switch (qnType) {
        case BASIC:
            topicIdx = 0;
            break;
        case OOP:
            topicIdx = 1;
            break;
        case EXTENSIONS:
            topicIdx = 2;
            break;
        default:
            topicIdx = 3;
            break;
        }
        if (score > profile.getContentMarks(topicIdx)) {
            profile.setMarks(topicIdx, score);
            if (!Duke.isCliMode()) {
                switch (topicIdx) {
                case 0:
                    Duke.logger.log(Level.INFO, score + " YEET");
                    TopBar.progValueA = (double) score / MAX_QUESTIONS;
                    break;
                case 1:
                    TopBar.progValueB = (double) score / MAX_QUESTIONS;
                    break;
                case 2:
                    TopBar.progValueC = (double) score / MAX_QUESTIONS;
                    break;
                case 3:
                    TopBar.progValueD = (double) score / MAX_QUESTIONS;
                    break;

                default:
                }
                TopBar.progValueT = (double) profile.getTotalProgress() / (MAX_QUESTIONS * 4);
            }
        }
    }

    /**
     * Method to get the next Question.
     * @return the string containing the next question
     */
//    public String getNextQuestion() {
//        prevQuestion = chosenQuestions.get(chosenQuestions.size() - 1);
//        chosenQuestions.remove(chosenQuestions.size() - 1);
//        return prevQuestion.getQuestion();
//    }

    /**
     * Method to check if answer is correct.
     * If it is, then update the score.
     * @param input the answer inputted by the user
     * @throws DukeException error thrown if user inputs wrong type of answer.
     */
    private void checkAnswer(int index, String input) throws DukeException {
        if (!isNumeric(input)) {
            throw new DukeException("Please input answers in the form of integer");
        }
        if (questionList.setAndCheckUserAnswer(index, input)) {
            currScore++;
        }
    }

    private static boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Method to get the results of the quiz.
     * @return String containing what Cake said about the quiz.
     * @throws DukeException error thrown if failed to overwrite score.
     */
    public String getQuizResult() throws DukeException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("This is your score:");
        stringBuilder.append("    ").append(currScore).append(" / ").append(MAX_QUESTIONS).append("\n");

        if ((double)currScore / MAX_QUESTIONS <= PERCENTAGE_1) {
            stringBuilder.append("Aw, that's too bad! Try revising the topics and try again. Don't give up!");
            scoreGrade = ScoreGrade.BAD;
        } else if ((double)currScore / MAX_QUESTIONS != PERCENTAGE_2) {
            stringBuilder.append("Almost there! Clarify some of your doubts and try again.");
            scoreGrade = ScoreGrade.OKAY;
        } else {
            stringBuilder.append("Congrats! Full marks, you're amazing!");
            scoreGrade = ScoreGrade.GOOD;
        }

        stringBuilder.append("Type \"review\" to review your answers.");
        stringBuilder.append("\nType \"back\" to go back to the table of contents.");

        overwriteOldScore(currScore, profile);
        isQuizComplete = true;

        return stringBuilder.toString();
    }
}
