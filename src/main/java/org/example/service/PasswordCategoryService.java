package org.example.service;

import org.example.dao.PasswordCategoryDao;
import org.example.model.PasswordCategory;

import java.util.List;

public class PasswordCategoryService {

    private final PasswordCategoryDao dao =
            new PasswordCategoryDao();

    private String lastErrorMessage;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public boolean addCategory(int userId, String categoryName) {

        if (categoryName == null || categoryName.trim().isEmpty()) {
            lastErrorMessage = "Category name is required.";
            return false;
        }

        boolean result = dao.addCategory(userId, categoryName);

        if (!result) {
            lastErrorMessage = "Failed to add category.";
            return false;
        }
        return true;
    }

    public List<PasswordCategory> getCategories(int userId) {
        return dao.getCategoriesByUser(userId);
    }

    public boolean deleteCategory(int categoryId, int userId) {

        boolean result = dao.deleteCategory(categoryId, userId);

        if (!result) {
            lastErrorMessage = "Failed to delete category.";
            return false;
        }
        return true;
    }
}
