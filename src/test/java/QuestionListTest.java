import javacake.exceptions.DukeException;
import javacake.quiz.Question;
import javacake.quiz.QuestionList;
import javacake.quiz.QuestionType;
import org.junit.jupiter.api.Test;

import static javacake.quiz.QuestionList.MAX_QUESTIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

public class QuestionListTest {
    @Test
    public void initializeBasicQuizTest() throws DukeException {
        QuestionList test = new QuestionList(QuestionType.BASIC);

        // check if there are the correct number and type of test questions
        assertEquals(test.getQuestionList().size(), MAX_QUESTIONS);
        for (Question qn : test.getQuestionList()) {
            assertEquals(qn.getClass(), Question.class);
        }

        // check if there are any duplicate questions
        Set<Question> set = new HashSet<>(test.getQuestionList());
        assertEquals(set.size(), test.getQuestionList().size());
    }
}
