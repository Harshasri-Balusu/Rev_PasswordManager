package ServiceTest;

import org.example.dao.SecurityQuestionsDao;
import org.example.dao.UserSecurityAnswerDao;
import org.example.model.SecurityAnswer;
import org.example.model.SecurityQuestion;
import org.example.service.SecurityService;
import org.example.util.PasswordHashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Spy
    private SecurityService securityService;

    @Mock
    private SecurityQuestionsDao questionDao;

    @Mock
    private UserSecurityAnswerDao answerDao;

    @BeforeEach
    void setUp() throws Exception {
        inject("questionDao", questionDao);
        inject("answerDao", answerDao);
    }

    private void inject(String fieldName, Object mock) throws Exception {
        Field field = SecurityService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(securityService, mock);
    }

    @Test
    void getAllQuestions_success() {
        when(questionDao.getAllQuestions())
                .thenReturn(List.of(new SecurityQuestion()));

        List<SecurityQuestion> result =
                securityService.getAllQuestions();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void saveAnswer_success() {
        when(answerDao.save(any(SecurityAnswer.class)))
                .thenReturn(true);

        boolean result =
                securityService.saveAnswer(
                        1, 101, "MyFirstSchool"
                );

        assertTrue(result);
    }

    @Test
    void saveAnswer_blankAnswer() {
        boolean result =
                securityService.saveAnswer(
                        1, 101, "   "
                );

        assertFalse(result);
        assertEquals(
                "Security answer cannot be empty.",
                securityService.getLastErrorMessage()
        );
    }

    @Test
    void saveAnswer_daoFailure() {
        when(answerDao.save(any(SecurityAnswer.class)))
                .thenReturn(false);

        boolean result =
                securityService.saveAnswer(
                        1, 101, "Answer"
                );

        assertFalse(result);
        assertEquals(
                "Failed to save security answer.",
                securityService.getLastErrorMessage()
        );
    }

    @Test
    void getUserQuestion_success() {
        SecurityAnswer a = new SecurityAnswer();
        a.setQuestionId(101);

        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionId(101);
        q.setQuestionText("Your pet name?");

        when(answerDao.getByUserId(1)).thenReturn(a);
        when(questionDao.getById(101)).thenReturn(q);

        SecurityQuestion result =
                securityService.getUserQuestion(1);

        assertNotNull(result);
        assertEquals(101, result.getQuestionId());
    }

    @Test
    void getUserQuestion_notSet() {
        when(answerDao.getByUserId(1)).thenReturn(null);

        SecurityQuestion result =
                securityService.getUserQuestion(1);

        assertNull(result);
        assertEquals(
                "Security question not set for this user.",
                securityService.getLastErrorMessage()
        );
    }


    @Test
    void verifyAnswer_success() {
        SecurityAnswer stored = new SecurityAnswer();
        stored.setAnswerHash(
                PasswordHashUtil.hashPassword("secret")
        );

        when(answerDao.getByUserId(1)).thenReturn(stored);

        boolean result =
                securityService.verifyAnswer(1, "secret");

        assertTrue(result);
    }

    @Test
    void verifyAnswer_blankInput() {
        boolean result =
                securityService.verifyAnswer(1, "");

        assertFalse(result);
        assertEquals(
                "Security answer is required.",
                securityService.getLastErrorMessage()
        );
    }

    @Test
    void verifyAnswer_incorrectAnswer() {
        SecurityAnswer stored = new SecurityAnswer();
        stored.setAnswerHash(
                PasswordHashUtil.hashPassword("correct")
        );

        when(answerDao.getByUserId(1)).thenReturn(stored);

        boolean result =
                securityService.verifyAnswer(1, "wrong");

        assertFalse(result);
        assertEquals(
                "Incorrect security answer.",
                securityService.getLastErrorMessage()
        );
    }

    @Test
    void verifyAnswer_questionNotFound() {
        when(answerDao.getByUserId(1)).thenReturn(null);

        boolean result =
                securityService.verifyAnswer(1, "any");

        assertFalse(result);
        assertEquals(
                "Security question not found.",
                securityService.getLastErrorMessage()
        );
    }
}

