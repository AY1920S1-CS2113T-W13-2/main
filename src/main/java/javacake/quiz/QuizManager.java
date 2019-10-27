package javacake.quiz;

import javacake.exceptions.DukeException;

import java.util.ArrayList;

public interface QuizManager {
    QuestionList questionList = null;
    String getQuestion(int index);
    String parseInput(int index, String input) throws DukeException;
}
