package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.domain.validation.CategoryValidator;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.Category;
import com.parkcontrol.persistence.repository.contract.CategoryRepository;
import com.parkcontrol.persistence.repository.impl.CategoryRepositoryImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.util.List;

public class CategoryController {

  @FXML
  private TableColumn<Category, Integer> categoryColIdCategory;

  @FXML
  private TableColumn<Category, String> categoryColNameCategory;

  @FXML
  private TableView<Category> categoryTableView;

  @FXML
  private TextField addCategory;

  @FXML
  private Button btnAdd;

  @FXML
  private Button btnClear;

  @FXML
  private Button btnDelete;

  @FXML
  private Button btnEdit;

  private final CategoryRepository categoryRepository;

  public CategoryController() {
    this.categoryRepository = new CategoryRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    categoryColIdCategory.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().id()).asObject());
    categoryColNameCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
    loadCategories();
    categoryTableView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            addCategory.setText(newValue.name());
          }
        }
    );
    btnAdd.setOnAction(event -> onAddClicked());
    btnClear.setOnAction(event -> onClearClicked());
    btnDelete.setOnAction(event -> onDeleteClicked());
    btnEdit.setOnAction(event -> onEditClicked());
  }

  private void loadCategories() {
    List<Category> categories = categoryRepository.getAllCategories();
    ObservableList<Category> categoryViewModels = FXCollections.observableArrayList();
    categoryViewModels.setAll(categories);
    categoryTableView.setItems(categoryViewModels);
  }

  private void onAddClicked() {
    String categoryName = addCategory.getText().trim();
    List<Category> existingCategories = categoryRepository.getAllCategories();
    String validationMessage = CategoryValidator.validateCategoryName(categoryName, existingCategories);
    if (validationMessage == null) {
      Category newCategory = new Category(0, categoryName);
      try {
        categoryRepository.addCategory(newCategory);
        loadCategories();
        addCategory.clear();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Не вдалося додати категорію: " + e.getMessage());
        e.printStackTrace();
      }
    } else {
      AlertController.showAlert(validationMessage);
    }
  }

  private void onClearClicked() {
    addCategory.clear();
  }

  private void onDeleteClicked() {
    Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
    if (selectedCategory != null) {
      try {
        int categoryId = selectedCategory.id();
        List<Integer> byCategoryId = categoryRepository.getParkingSpotByCategoryId(categoryId);
        if (byCategoryId.isEmpty()) {
          categoryRepository.deleteCategory(categoryId);
          loadCategories();
          onClearClicked();
        } else {
          AlertController.showAlert("Цю категорію не можна видалити, оскільки вона використовується іншими елементами.");
        }
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Не вдалося видалити категорію: " + e.getMessage());
        e.printStackTrace();
      }
    } else {
      AlertController.showAlert("Категорія для видалення не обрана.");
    }
  }
  private void onEditClicked() {
    Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
    if (selectedCategory != null) {
      String updatedCategoryName = addCategory.getText().trim();
      if (updatedCategoryName.isEmpty()) {
        AlertController.showAlert("Назва категорії не може бути порожньою.");
        return;
      }
      Category updatedCategory = new Category(selectedCategory.id(), updatedCategoryName); // Створюємо новий об'єкт Category з оновленими даними
      try {
        categoryRepository.updateCategory(updatedCategory); // Викликаємо метод із об'єктом Category
        loadCategories();
        addCategory.clear();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Не вдалося оновити категорію: " + e.getMessage());
        e.printStackTrace();
      }
    } else {
      AlertController.showAlert("Категорія для редагування не обрана.");
    }
  }

}
