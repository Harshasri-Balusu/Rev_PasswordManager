package ServiceTest;

import org.example.dao.PasswordCategoryDao;
import org.example.model.PasswordCategory;
import org.example.service.PasswordCategoryService;
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
class PasswordCategoryServiceTest {

    @Spy
    private PasswordCategoryService passwordCategoryService;

    @Mock
    private PasswordCategoryDao passwordCategoryDao;

    @BeforeEach
    void setUp() throws Exception {
        inject("dao", passwordCategoryDao);
    }

    private void inject(String fieldName, Object mock) throws Exception {
        Field field = PasswordCategoryService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(passwordCategoryService, mock);
    }

    @Test
    void addCategory_success() {
        when(passwordCategoryDao.addCategory(1, "Social"))
                .thenReturn(true);

        boolean result =
                passwordCategoryService.addCategory(1, "Social");

        assertTrue(result);
    }

    @Test
    void addCategory_blankName() {
        boolean result =
                passwordCategoryService.addCategory(1, "   ");

        assertFalse(result);
        assertEquals(
                "Category name is required.",
                passwordCategoryService.getLastErrorMessage()
        );
    }

    @Test
    void addCategory_daoFailure() {
        when(passwordCategoryDao.addCategory(1, "Social"))
                .thenReturn(false);

        boolean result =
                passwordCategoryService.addCategory(1, "Social");

        assertFalse(result);
        assertEquals(
                "Failed to add category.",
                passwordCategoryService.getLastErrorMessage()
        );
    }

    @Test
    void getCategories_success() {
        PasswordCategory c = new PasswordCategory();
        c.setCategoryId(10);
        c.setCategoryName("Work");

        when(passwordCategoryDao.getCategoriesByUser(1))
                .thenReturn(List.of(c));

        List<PasswordCategory> result =
                passwordCategoryService.getCategories(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    void deleteCategory_success() {
        when(passwordCategoryDao.deleteCategory(10, 1))
                .thenReturn(true);

        boolean result =
                passwordCategoryService.deleteCategory(10, 1);

        assertTrue(result);
    }

    @Test
    void deleteCategory_failure() {
        when(passwordCategoryDao.deleteCategory(10, 1))
                .thenReturn(false);

        boolean result =
                passwordCategoryService.deleteCategory(10, 1);

        assertFalse(result);
        assertEquals(
                "Failed to delete category.",
                passwordCategoryService.getLastErrorMessage()
        );
    }
}

