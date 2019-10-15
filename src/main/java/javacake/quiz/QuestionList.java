package javacake.quiz;

import javacake.DukeException;
import javacake.ProgressStack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class QuestionList {
    public ProgressStack progressStack;
    private ArrayList<Question> chosenQuestions;
    private static final int TOTALNUMOFBASICQNS = 15;
    private static final int TOTALNUMOFOOPQNS = 3;
    private static final int TOTALNUMOFUSEFULEXTENSIONQNS = 3;
    /** The maximum number of questions in one session of a quiz. */
    public static final int MAX_QUESTIONS = 3;

    public QuestionList() {
        chosenQuestions = new ArrayList<>(MAX_QUESTIONS);
    }

    private ArrayList<BasicQuestion> initBasicList() throws DukeException {
        ArrayList<BasicQuestion> basicQuestionList = new ArrayList<>();
        for (int i = 1; i <= TOTALNUMOFBASICQNS; i++) {
            try {
                String filePath = "content/MainList/1. Java Basics/4. Quiz/Qn" + i + ".txt";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                String currentLine;
                String qns = new String();
                while ((currentLine = bufferedReader.readLine()) != null) {
                    qns = qns + currentLine + "\n";
                }
                qns = qns.substring(0,qns.length() - 1); // to remove the last appended new line character
                String[] questions = qns.split("\\|\\s*");
                basicQuestionList.add(new BasicQuestion(questions[0], questions[1]));
            } catch (IOException e) {
                throw new DukeException("File not found!");
            }
        }
        return basicQuestionList;
    }

    private ArrayList<OopQuestion> initOopList() throws DukeException {
        ArrayList<OopQuestion> oopQuestionList = new ArrayList<>();
        for (int i = 1; i <= TOTALNUMOFOOPQNS; i++) {
            try {
                String filePath = "content/MainList/ListIndex2/oop/Quiz/Qn" + i + ".txt";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                String currentline;
                String qns = new String();

                while ((currentline = bufferedReader.readLine()) != null) {
                    qns = qns + currentline + "\n";
                }
                qns = qns.substring(0,qns.length() - 1); // to remove the last appended new line character
                String[] questions = qns.split("\\|\\s*");
                oopQuestionList.add(new OopQuestion(questions[0], questions[1]));

            } catch (IOException e) {
                throw new DukeException("File not found!");
            }
        }
        return oopQuestionList;
    }

    private ArrayList<ExtensionQuestion> initExtensionList() throws DukeException {
        ArrayList<ExtensionQuestion> extensionQuestionList = new ArrayList<>();
        for (int i = 1; i <= TOTALNUMOFUSEFULEXTENSIONQNS; i++) {

            try {
                String filePath = "content/MainList/ListIndex2/oop/Quiz/Qn" + i + ".txt";
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                String currentLine; 
                String qns = new String();

                while ((currentLine = bufferedReader.readLine()) != null) {
                    qns = qns + currentLine + "\n";
                }
                qns = qns.substring(0,qns.length() - 1); // to remove the last appended new line character
                String[] questions = qns.split("\\|\\s*");
                extensionQuestionList.add(new ExtensionQuestion(questions[0], questions[1]));

            } catch (IOException e) {
                throw new DukeException("File not found!");
            }
        }
        return extensionQuestionList;
    }

    /**
     * Randomly selects MAX_QUESTIONS number of questions from the list of all questions.
     * @return ArrayList of Question of size MAX_QUESTIONS.
     */
    public ArrayList<Question> pickQuestions() throws DukeException {
        ArrayList<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(initBasicList());
        allQuestions.addAll(initOopList());
        allQuestions.addAll(initExtensionList());
        assert (allQuestions.size() >= MAX_QUESTIONS);

        Random rand = new Random();
        ArrayList<Integer> chosenNumbers = new ArrayList<>();

        for (int i = 0; i < MAX_QUESTIONS; i++) {
            int randomNum;
            do {
                randomNum = rand.nextInt(allQuestions.size());
            } while (chosenNumbers.contains(randomNum)); // prevents repeat questions
            chosenNumbers.add(randomNum);
            try {
                chosenQuestions.add(allQuestions.get(randomNum));
            } catch (IndexOutOfBoundsException e) {
                throw new DukeException("Something went wrong when loading the quiz: index out of bounds.");
            }
        }
        return chosenQuestions;
    }

    /**
     * Randomly selects MAX_QUESTIONS number of questions of the specified topic from the list of all questions.
     * @param type QuestionType of questions to be selected.
     * @return ArrayList of Question of specified topic of size MAX_QUESTIONS.
     */
    public ArrayList<Question> pickQuestions(Question.QuestionType type) throws DukeException {
        ArrayList<Question> tempList = new ArrayList<>();
        switch (type) {
        case BASIC:
            assert (initBasicList().size() >= MAX_QUESTIONS);
            tempList.addAll(initBasicList());
            break;
        case OOP:
            assert (initOopList().size() >= MAX_QUESTIONS);
            tempList.addAll(initOopList());
            break;
        case EXTENSIONS:
            assert (initExtensionList().size() >= MAX_QUESTIONS);
            tempList.addAll(initExtensionList());
            break;
        default:
            tempList.addAll(initBasicList());
            tempList.addAll(initOopList());
            tempList.addAll(initExtensionList());
            assert (tempList.size() >= MAX_QUESTIONS);
            break;
        }

        Random rand = new Random();
        ArrayList<Integer> chosenNumbers = new ArrayList<>();

        for (int i = 0; i < MAX_QUESTIONS; i++) {
            int randomNum;
            do {
                randomNum = rand.nextInt(tempList.size());
            } while (chosenNumbers.contains(randomNum)); // prevents repeat questions
            chosenNumbers.add(randomNum);
            try {
                chosenQuestions.add(tempList.get(randomNum));
            } catch (IndexOutOfBoundsException e) {
                throw new DukeException("Something went wrong when loading the quiz: index out of bounds.");
            }
        }
        return chosenQuestions;
    }
}
