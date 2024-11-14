package com.parkcontrol.persistence.repository.contract;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.Category;
import java.util.List;

public interface CategoryRepository {
  void addCategory(Category category) throws EntityNotFoundException;

  List<Category> getAllCategories();

  void updateCategory(Category category) throws EntityNotFoundException;

  void deleteCategory(int id) throws EntityNotFoundException;

  // Метод для звязку багато до багатьох
  List<Integer> getParkingSpotByCategoryId(int categoryId);
}
