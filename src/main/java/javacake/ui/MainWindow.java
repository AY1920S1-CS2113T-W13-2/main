package javacake.ui;

import javacake.Duke;
import javacake.commands.BackCommand;
import javacake.commands.EditNoteCommand;
import javacake.exceptions.DukeException;
import javacake.quiz.QuestionList;
import javacake.quiz.QuestionType;
import javacake.quiz.QuizSession;
import javacake.quiz.ReviewSession;
import javacake.storage.Profile;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.logging.Level;

import static javacake.quiz.QuestionList.MAX_QUESTIONS;

/**
 * Controller for MainWindow. Provides the layout for the other controls.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton = new Button();
    @FXML
    private HBox topBar;
    @FXML
    private VBox avatarScreen;
    @FXML
    private ScrollPane taskScreen;
    @FXML
    private VBox taskContainer;
    @FXML
    private ScrollPane noteScreen;
    @FXML
    private VBox noteContainer;
    @FXML
    private Button themeModeButton;
    public static boolean isLightMode = true;

    private Duke duke;
    private Stage primaryStage;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/padoru.png"));

    private QuizSession quizSession;
    private ReviewSession reviewSession;
    private QuestionList tempQuestionList;
    private boolean isQuiz = false;
    private int index = 0;
    private boolean isResult = false;
    private boolean isReview = false;
    private boolean isStarting = true;
    private boolean isTryingReset = false;
    private boolean isWritingNote = false;
    private String input = "";
    private String response = "";

    /**
     * Initialise the Main Window launched.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        taskScreen.vvalueProperty().bind(taskContainer.heightProperty());
        noteScreen.vvalueProperty().bind(noteContainer.heightProperty());
        avatarScreen.getChildren().add(AvatarScreen.setAvatar(AvatarScreen.AvatarMode.HAPPY));
        topBar.getChildren().add(new TopBar());
        TopBar.setUpProgressBars();

        if (duke.isFirstTimeUser) {
            response = Ui.showWelcomeMsgPhaseA(duke.isFirstTimeUser);
            showContentContainer();
        } else {
            response = Ui.showWelcomeMsgPhaseA(duke.isFirstTimeUser)
                    + Ui.showWelcomeMsgPhaseB(duke.isFirstTimeUser, duke.userName, duke.userProgress);
            showContentContainer();
        }
    }



    public void setDuke(Duke d) {
        duke = d;
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }


    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        //CHECKSTYLE:OFF
        try {
            input = userInput.getText();
            // get input first, don't get response first...
            userInput.clear();
            Duke.logger.log(Level.INFO, input);
            AvatarScreen.avatarMode = AvatarScreen.AvatarMode.HAPPY;
            if (input.contains("exit")) {
                // find out if exit condition
                handleExit();
                System.out.println("EXIT");
            } else if (isStarting && Duke.isFirstTimeUser) { //set up new username
                handleStartAndFirstTime();
                System.out.println("start and first");
            } else if (isTryingReset) { //confirmation of reset
                handleResetConfirmation();
                System.out.println("resetting time");
            } else if (isWritingNote) {
                if (input.equals("/save")) {
                    isWritingNote = false;
                    response = EditNoteCommand.successSaveMessage();
                } else {
                    response = EditNoteCommand.writeSaveGui(input);
                }
                showContentContainer();
            } else {
                if (input.length() >= 8 && input.substring(0, 8).equals("deadline")) {
                    response = duke.getResponse(input);
                    response = response.replaceAll("✓", "\u2713");
                    response = response.replaceAll("✗", "\u2717");
                    showTaskContainer();
                    System.out.println("deadline setting");
                } else if (isResult) { // On results screen
                    response = quizSession.parseInput(0, input);
                } else if (isReview) {
                    response = reviewSession.parseInput(0, input);
                } else if (isQuiz) {
                    //Must be quizSession: checking of answers
                    handleGuiQuiz();
                    showContentContainer();
                    System.out.println("quiz answer checking");
                } else if (isStarting) {
                    //default start: finding of response
                    isStarting = false;
                    response = duke.getResponse(input);
                    if (response.contains("!@#_EDIT_NOTE")) {
                        Duke.logger.log(Level.INFO, "Response: " + response);
                        isWritingNote = true;
                        response = EditNoteCommand.getHeadingMessage();
                        showContentContainer();
                        EditNoteCommand.clearTextFileContent();
                    } else {
                        showContentContainer();
                        System.out.println("starting BUT not firsttime");
                    }
                }

                if (response.contains("!@#_QUIZ")) {
                    //checks for first execution of quizCommand
                    isQuiz = true;
                    Duke.logger.log(Level.INFO, "Response: " + response);
                    response = initQuizSession(response);
                    showContentContainer();
                    System.out.println("quiz first time");
                }
                if (response.contains("Confirm reset")) {
                    //checks if resetCommand was executed
                    System.out.println("CHECKING RESET");
                    Duke.logger.log(Level.INFO, "Awaiting confirmation of reset");
                    isTryingReset = true;
                    showContentContainer();
                    System.out.println("reset command");
                }
                if (response.equals("!@#_REVIEW")) {
                    isResult = false;
                    isReview = true;
                    reviewSession = new ReviewSession(tempQuestionList);
                    response = reviewSession.getQuestion(0);
                    showContentContainer();
                }
                if (response.equals("!@#_BACK")) {
                    isReview = false;
                    isResult = false;
                    // TODO link BackCommand to here
                }
                if (isReview && isNumeric(response)) {
                    reviewSession.getQuestion(Integer.parseInt(response));
                    showContentContainer();
                }

                //System.out.println("End->Next");
            }
        } catch (DukeException e) {
            response = e.getMessage();
            showContentContainer();
            Duke.logger.log(Level.WARNING, e.getMessage());
        }
        //CHECKSTYLE:ON
    }

    @FXML
    private void handleGuiMode() {
        if (isLightMode) { //switches to Dark theme
            isLightMode = false;
            this.setStyle("-fx-background-color: black");
            sendButton.setStyle("-fx-background-color: #333; -fx-border-color: black;");
            themeModeButton.setStyle("-fx-background-color: #333; -fx-border-color: black;");
            topBar.setStyle("-fx-background-color: #BBB; -fx-border-color: grey;");
            userInput.setStyle("-fx-background-color: #9999; -fx-background-radius: 10;");
            dialogContainer.setStyle("-fx-background-color: grey;");
            avatarScreen.setStyle("-fx-background-color: grey;");
            taskContainer.setStyle("-fx-background-color: grey;");
            noteContainer.setStyle("-fx-background-color: grey;");
        } else { //switches to Light theme
            isLightMode = true;
            this.setStyle("-fx-background-color: white");
            sendButton.setStyle("-fx-background-color: #FF9EC7; -fx-border-color: white;");
            themeModeButton.setStyle("-fx-background-color: #FF9EC7; -fx-border-color: white;");
            topBar.setStyle("-fx-background-color: #EE8EC7; -fx-border-color: white;");
            userInput.setStyle("-fx-background-color: #EE8EC7;"
                    + " -fx-background-radius: 10;");
            dialogContainer.setStyle("-fx-background-color: pink;");
            avatarScreen.setStyle("-fx-background-color: #FEE;");
            taskContainer.setStyle("-fx-background-color: pink;");
            noteContainer.setStyle("-fx-background-color: pink;");
        }
    }


    private String initQuizSession(String cmdMode) throws DukeException {
        switch (cmdMode) {
        case "!@#_QUIZ_1":
            quizSession = new QuizSession(QuestionType.BASIC, false);
            break;
        case "!@#_QUIZ_2":
            quizSession = new QuizSession(QuestionType.OOP, false);
            break;
        case "!@#_QUIZ_3":
            quizSession = new QuizSession(QuestionType.EXTENSIONS, false);
            break;
        case "!@#_QUIZ_4":
            quizSession = new QuizSession(QuestionType.ALL, false);
            break;
        default:
        }
        return quizSession.getQuestion(0);
    }

    private void handleExit() {
        response = duke.getResponse(input);
        showContentContainer();
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> primaryStage.hide());
        pause.play();
    }

    private void handleStartAndFirstTime() throws DukeException {
        duke.userName = input;
        duke.profile.overwriteName(duke.userName);
        response = Ui.showWelcomeMsgPhaseB(duke.isFirstTimeUser, duke.userName, duke.userProgress);
        showContentContainer();
        isStarting = false;
    }

    private void handleResetConfirmation() throws DukeException {
        if (input.equals("yes")) {
            //resets
            Profile.resetProfile();
            duke.profile = new Profile();
            duke.userProgress = duke.profile.getTotalProgress();
            duke.userName = duke.profile.getUsername();
            duke.isFirstTimeUser = true;
            response = "Reset confirmed!\nPlease type in new username:\n";
            TopBar.resetProgress();
            isStarting = true;
        } else {
            response = "Reset cancelled.\nType 'list' to get list of available commands.";
        }
        showContentContainer();
        isTryingReset = false;
    }

    private void handleGuiQuiz() throws DukeException {
        quizSession.parseInput(index++, input);
        if (index < MAX_QUESTIONS) {
            response = quizSession.getQuestion(index);
        } else {
            tempQuestionList = quizSession.getQuestionList();
            isQuiz = false;
            isResult = true;
            response = quizSession.getQuizResult();
            if (quizSession.scoreGrade == QuizSession.ScoreGrade.BAD) {
                AvatarScreen.avatarMode = AvatarScreen.AvatarMode.POUT;
            } else if (quizSession.scoreGrade == QuizSession.ScoreGrade.OKAY) {
                AvatarScreen.avatarMode = AvatarScreen.AvatarMode.SAD;
            }
        }
    }

    private void showContentContainer() {
        dialogContainer.getChildren().clear();
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(response, dukeImage));
    }

    private void showTaskContainer() {
        taskContainer.getChildren().clear();
        taskContainer.getChildren().add(
                DialogBox.getTaskDialog(response));
    }

    private void showNoteContainer() {
        noteContainer.getChildren().clear();
        noteContainer.getChildren().add(
                DialogBox.getTaskDialog(response));
    }

    private static boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}