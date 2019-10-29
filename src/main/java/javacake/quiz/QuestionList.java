package javacake.quiz;

import javacake.exceptions.DukeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class QuestionList {
    private ArrayList<Question> chosenQuestions;
    private int totalNumOfBasicQns = 15;
    private int totalNumOfOopQns = 5;
    private int totalNumOfUsefulExtensionQns = 5;
    /** The maximum number of questions in one session of a quiz. */
    public static final int MAX_QUESTIONS = 5;
    private QuestionType questionType;

    public QuestionList(QuestionType type) throws DukeException {
        questionType = type;
        chosenQuestions = pickQuestions();
    }

    public ArrayList<Question> getQuestionList() {
        return chosenQuestions;
    }

    public String getQuestion(int index) {
        return index + 1 + "/" + MAX_QUESTIONS + "\n" + chosenQuestions.get(index).getQuestion();
    }

    public String getAnswers(int index) {
        return "Your answer: " + chosenQuestions.get(index).getUserAnswer()
                + "\nCorrect answer: " + chosenQuestions.get(index).getAnswer();
    }

    public boolean setAndCheckUserAnswer(int index, String input) {
        chosenQuestions.get(index).setUserAnswer(input);
        return (chosenQuestions.get(index).isAnswerCorrect(input));
    }

    /**
     * Updates the current number of basic questions in the hardcoded file path and returns all the questions stored.
     * @return ArrayList of all the basic questions available.
     */
    private ArrayList<Question> initBasicList() throws DukeException {
        ArrayList<Question> basicQuestionList = new ArrayList<>();
        for (int i = 1; i <= totalNumOfBasicQns; i++) {
            try {
                String fileContentPath = "/content/MainList/1. Java Basics/4. Quiz/Qn" + i + ".txt";
                InputStream in = getClass().getResourceAsStream(fileContentPath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String currentLine;
                String qns = "";

                while ((currentLine = bufferedReader.readLine()) != null) {
                    qns = qns + currentLine + "\n";
                }
                qns = qns.substring(0,qns.length() - 1); // to remove the last appended new line character
                String[] questions = qns.split("\\|\\s*");
                basicQuestionList.add(new Question(questions[0], questions[1]));

            } catch (IOException e) {
                throw new DukeException("File not found!");
            }
        }
        return basicQuestionList;
    }

    /**
     * Updates the current number of oop questions in the hardcoded file path and returns all the questions stored.
     * @return ArrayList of all the oop questions available.
     */
    private ArrayList<Question> initOopList() throws DukeException {
        ArrayList<Question> oopQuestionList = new ArrayList<>();
        for (int i = 1; i <= totalNumOfOopQns; i++) {
            try {
                String fileContentPath = "/content/MainList/2. Object-Oriented Programming/5. Quiz/Qn" + i + ".txt";
                InputStream in = getClass().getResourceAsStream(fileContentPath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String currentline;
                String qns = "";

                while ((currentline = bufferedReader.readLine()) != null) {
                    qns = qns + currentline + "\n";
                }
                qns = qns.substring(0,qns.length() - 1); // to remove the last appended new line character
                String[] questions = qns.split("\\|\\s*");
                oopQuestionList.add(new Question(questions[0], questions[1]));

            } catch (IOException e) {
                throw new DukeException("File not found!");
            }
        }
        return oopQuestionList;
    }

    /**
     * Updates the current number of extension questions in the hardcoded file path
     * and returns all the questions stored.
     * @return ArrayList of all the extension questions available.
     */
    private ArrayList<Question> initExtensionList() throws DukeException {
        ArrayList<Question> extensionQuestionList = new ArrayList<>();
        for (int i = 1; i <= totalNumOfUsefulExtensionQns; i++) {
            try {
                String fileContentPath = "/content/MainList/3. Extensions/4. Quiz/Qn" + i + ".txt";
                InputStream in = getClass().getResourceAsStream(fileContentPath);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                String currentLine; 
                String qns = "";

                while ((currentLine = bufferedReader.readLine()) != null) {
                    qns = qns + currentLine + "\n";
                }
                qns = qns.substring(0,qns.length() - 1); // to remove the last appended new line character
                String[] questions = qns.split("\\|\\s*");
                extensionQuestionList.add(new Question(questions[0], questions[1]));

            } catch (IOException e) {
                throw new DukeException("File not found!");
            }
        }
        return extensionQuestionList;
    }

    /**
     * Randomly selects MAX_QUESTIONS number of questions of the specified topic from the list of all questions.
     * @return ArrayList of Question of specified topic of size MAX_QUESTIONS.
     */
    public ArrayList<Question> pickQuestions() throws DukeException {
        ArrayList<Question> tempList1 = new ArrayList<>();
        switch (questionType) {
        case BASIC:
            assert (initBasicList().size() >= MAX_QUESTIONS);
            tempList1.addAll(initBasicList());
            break;
        case OOP:
            assert (initOopList().size() >= MAX_QUESTIONS);
            tempList1.addAll(initOopList());
            break;
        case EXTENSIONS:
            assert (initExtensionList().size() >= MAX_QUESTIONS);
            tempList1.addAll(initExtensionList());
            break;
        default:
            tempList1.addAll(initBasicList());
            tempList1.addAll(initOopList());
            tempList1.addAll(initExtensionList());
            assert (tempList1.size() >= MAX_QUESTIONS);
            break;
        }

        Random rand = new Random();
        ArrayList<Integer> chosenNumbers = new ArrayList<>();
        ArrayList<Question> tempList2 = new ArrayList<>();

        for (int i = 0; i < MAX_QUESTIONS; i++) {
            int randomNum;
            do {
                randomNum = rand.nextInt(tempList1.size());
            } while (chosenNumbers.contains(randomNum)); // prevents repeat questions
            chosenNumbers.add(randomNum);
            tempList2.add(tempList1.get(randomNum));
        }
        assert (tempList2.size() == MAX_QUESTIONS);
        return tempList2;
    }
}
