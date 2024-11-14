package com.parkcontrol.persistence.repository.impl;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.entity.Category;
import com.parkcontrol.persistence.repository.contract.CategoryRepository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepositoryImpl implements CategoryRepository {
  private DataSource dataSource;

  public CategoryRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void addCategory(Category category) throws EntityNotFoundException {
    String sql = "INSERT INTO Categories(category_name) VALUES (?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, category.name());
      preparedStatement.executeUpdate();
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        category.id();
      } else {
        throw new SQLException("Не вдалося додати категорію, не отримано ідентифікатор.");
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Не вдалося додати категорію.", e);
    }
  }
  @Override
  public void updateCategory(Category category) throws EntityNotFoundException {
    String sql = "UPDATE Categories SET category_name = ? WHERE category_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, category.name());
      preparedStatement.setInt(2, category.id());
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Не вдалося оновити категорію, категорію не знайдено.");
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Не вдалося оновити категорію.", e);
    }
  }

  @Override
  public void deleteCategory(int categoryId) throws EntityNotFoundException {
    String sql = "DELETE FROM Categories WHERE category_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, categoryId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Не вдалося видалити категорію, категорію не знайдено.");
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Не вдалося видалити категорію.", e);
    }
  }

  @Override
  public List<Category> getAllCategories() {
    List<Category> categories = new ArrayList<>();
    String sql = "SELECT * FROM Categories";
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        Category category = new Category(resultSet.getInt("category_id"), resultSet.getString("category_name"));
        categories.add(category);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося отримати всі категорії.", e);
    }
    return categories;
  }

  // Метод для зв'язку багато-до-багатьох
  @Override
  public List<Integer> getParkingSpotByCategoryId(int categoryId) {
    List<Integer> spotIds = new ArrayList<>();
    String sql = "SELECT spot_id FROM ParkingSpotCategories WHERE category_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, categoryId);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        spotIds.add(resultSet.getInt("spot_id"));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося отримати місця паркування за ідентифікатором категорії.", e);
    }
    return spotIds;
  }
}
