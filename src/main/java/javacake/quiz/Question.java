package javacake.quiz;

import javacake.exceptions.CakeException;

public class Question {
    private String question;
    private String answer;
    private String userAnswer;
    private int numberOfOptions;

    public Question(String question, String answer, int options) {
        this.question = question;
        this.answer = answer;
        this.numberOfOptions = options;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getNumberOfOptions() {
        return numberOfOptions;
    }

    public void setUserAnswer(String userInput) {
        userAnswer = userInput;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    /**
     * Checks if user's answer to the question is correct.
     * @param input user's inputted answer
     * @return true if input matches answer, false otherwise.
     */
    public boolean isAnswerCorrect(String input) throws CakeException {
        int tmp = Integer.parseInt(input);
        if (tmp < 1 || tmp > numberOfOptions) {
            throw new CakeException("[!] Please enter option number between 1 and " + numberOfOptions + "! [!]\n");
        }
        return (input.trim().equals(answer));
    }
}
