package DaoTest;

import org.example.dao.SecurityQuestionsDao;
import org.example.model.SecurityQuestion;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SecurityQuestionDaoTest {

    private static final Logger logger =
            LoggerFactory.getLogger(SecurityQuestionDaoTest.class);

    private static SecurityQuestionsDao securityQuestionsDao;
    private static int existingQuestionId;

    @BeforeAll
    static void init() {
        securityQuestionsDao = new SecurityQuestionsDao();
        logger.info("SecurityQuestionsDaoTest started");
    }

    @Test
    @Order(1)
    void getAllQuestions_success() {
        List<SecurityQuestion> questions =
                securityQuestionsDao.getAllQuestions();

        assertNotNull(questions);
        assertFalse(questions.isEmpty());

        existingQuestionId = questions.getFirst().getQuestionId();
        logger.info("getAllQuestions_success passed, size={}", questions.size());
    }

    @Test
    @Order(2)
    void getById_success() {
        SecurityQuestion question =
                securityQuestionsDao.getById(existingQuestionId);

        assertNotNull(question);
        assertEquals(existingQuestionId, question.getQuestionId());
        assertNotNull(question.getQuestionText());

        logger.info("getById_success passed");
    }

    @Test
    @Order(3)
    void getById_notFound() {
        SecurityQuestion question =
                securityQuestionsDao.getById(99999);

        assertNull(question);
        logger.info("getById_notFound passed");
    }

    @Test
    @Order(4)
    void getAllQuestions_noException() {
        List<SecurityQuestion> questions =
                securityQuestionsDao.getAllQuestions();

        assertNotNull(questions);
        logger.info("getAllQuestions_noException passed");
    }
}

