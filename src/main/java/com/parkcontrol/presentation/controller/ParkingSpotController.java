package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.domain.validation.ParkingSpotValidator;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.Category;
import com.parkcontrol.persistence.entity.ParkingSpot;
import com.parkcontrol.persistence.repository.impl.CategoryRepositoryImpl;
import com.parkcontrol.persistence.repository.impl.ParkingSpotRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;

import java.util.List;

public class ParkingSpotController {

  @FXML
  private Button addButton;

  @FXML
  private Button clearFieldsButton;

  @FXML
  private Button deleteButton;

  @FXML
  private Button editButton;

  @FXML
  private TableColumn<ParkingSpot, String> levelColumn;

  @FXML
  private CheckComboBox<Category> categoryComboBox;

  @FXML
  private TextField levelField;

  @FXML
  private TableView<ParkingSpot> parkingSpotTable;

  @FXML
  private TableColumn<ParkingSpot, String> sectionColumn;

  @FXML
  private TextField sectionField;

  @FXML
  private TextField searchTextField;

  @FXML
  private TableColumn<ParkingSpot, String> sizeColumn;

  @FXML
  private ComboBox<String> sizeComboBox;

  @FXML
  private TableColumn<ParkingSpot, String> spotNumberColumn;

  @FXML
  private TextField spotNumberField;

  @FXML
  private TableColumn<ParkingSpot, String> statusColumn;

  @FXML
  private ComboBox<String> statusComboBox;

  private List<ParkingSpot> parkingSpots;
  private ParkingSpotRepositoryImpl parkingSpotRepository;
  private CategoryRepositoryImpl categoryRepository;

  public ParkingSpotController() {
    this.parkingSpotRepository = new ParkingSpotRepositoryImpl(new DatabaseConnection().getDataSource());
    this.categoryRepository = new CategoryRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    sectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().section()));
    levelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().level())));
    spotNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().spotNumber()));
    statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status()));
    sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().size()));

    sizeComboBox.getItems().addAll("Стандартне", "Велике", "Для інвалідів");
    statusComboBox.getItems().addAll("Вільне", "Зайняте");

    loadParkingSpots();
    loadCategories();

    parkingSpotTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showParkingSpotDetails(newValue));

    clearFieldsButton.setOnAction(event -> clearFields());
    addButton.setOnAction(event -> addParkingSpot());
    editButton.setOnAction(event -> editParkingSpot());
    deleteButton.setOnAction(event -> deleteParkingSpot());
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filterParkingSpots(newValue));
  }

  private void loadParkingSpots() {
    parkingSpots = parkingSpotRepository.findAll();
    parkingSpotTable.setItems(FXCollections.observableArrayList(parkingSpots));
  }

  private void loadCategories() {
    List<Category> categories = categoryRepository.getAllCategories();
    categoryComboBox.getItems().addAll(categories);
    categoryComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Category category) {
        return category != null ? category.name() : "";
      }

      @Override
      public Category fromString(String string) {
        return null;
      }
    });
  }
  private void showParkingSpotDetails(ParkingSpot parkingSpot) {
    if (parkingSpot != null) {
      sectionField.setText(parkingSpot.section());
      levelField.setText(String.valueOf(parkingSpot.level()));
      spotNumberField.setText(parkingSpot.spotNumber());
      statusComboBox.setValue(parkingSpot.status());
      sizeComboBox.setValue(parkingSpot.size());

      categoryComboBox.getCheckModel().clearChecks();
      List<Integer> categories = parkingSpotRepository.getCategoriesByParkingSpotId(parkingSpot.spotId());
      for (Category category : categoryComboBox.getItems()) {
        if (categories.contains(category.id())) {
          categoryComboBox.getCheckModel().check(category);
        }
      }
    } else {
      clearFields();
    }
  }@FXML
  private void addParkingSpot() {
    try {
      String section = sectionField.getText().trim();
      int level = Integer.parseInt(levelField.getText().trim());  // Перевірка чи є число
      String spotNumber = spotNumberField.getText().trim();
      String status = statusComboBox.getValue();
      String size = sizeComboBox.getValue();

      // Створення нового паркувального місця (spotId початково 0)
      ParkingSpot newParkingSpot = new ParkingSpot(0, section, level, spotNumber, status, size);

      // Валідація паркувального місця
      String validationMessage = ParkingSpotValidator.validateParkingSpot(newParkingSpot, parkingSpots);

      if (validationMessage == null) {
        // Додавання паркувального місця до репозиторію
        int generatedSpotId = parkingSpotRepository.addParkingSpot(newParkingSpot);  // Додавання в репозиторій і отримання ID

        if (generatedSpotId > 0) {
          // Тепер додаємо категорії
          for (Category category : categoryComboBox.getCheckModel().getCheckedItems()) {
            parkingSpotRepository.addCategoryToParkingSpot(generatedSpotId, category.id());
          }

          // Перезавантаження списку паркувальних місць та очищення полів
          loadParkingSpots();
          clearFields();

          // Повідомлення про успішне додавання
          AlertController.showAlert("Паркувальне місце успішно додано.");
        } else {
          // Якщо ID не отримано
          AlertController.showAlert("Помилка при додаванні паркувального місця.");
        }
      } else {
        // Якщо валідація не пройшла
        AlertController.showAlert(validationMessage);
      }
    } catch (NumberFormatException e) {
      // Обробка помилки, якщо рівень не є числом
      AlertController.showAlert("Поверх повинен бути числом.");
    }
  }

  @FXML
  private void editParkingSpot() {
    ParkingSpot selectedParkingSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
    if (selectedParkingSpot != null) {
      // Створення об'єкта ParkingSpot з оновленими значеннями
      ParkingSpot updatedParkingSpot = new ParkingSpot(
          selectedParkingSpot.spotId(),
          sectionField.getText().trim(),
          Integer.parseInt(levelField.getText().trim()),
          spotNumberField.getText().trim(),
          statusComboBox.getValue(),
          sizeComboBox.getValue()
      );

      // Валідація
      String validationMessage = ParkingSpotValidator.validateParkingSpot(updatedParkingSpot, parkingSpots);
      if (validationMessage != null) {
        AlertController.showAlert(validationMessage);
        return;  // Якщо валідація не пройдена, зупиняємо виконання
      }

      try {
        // Оновлення паркувального місця в репозиторії
        parkingSpotRepository.updateParkingSpot(updatedParkingSpot);

        // Оновлення категорій для цього паркувального місця
        List<Integer> currentCategoryIds = parkingSpotRepository.getCategoriesByParkingSpotId(updatedParkingSpot.spotId());
        for (Integer categoryId : currentCategoryIds) {
          // Видалення старих категорій
          parkingSpotRepository.removeCategoryFromParkingSpot(updatedParkingSpot.spotId(),categoryId);
        }

        // Додавання нових категорій
        for (Category category : categoryComboBox.getCheckModel().getCheckedItems()) {
          parkingSpotRepository.addCategoryToParkingSpot(updatedParkingSpot.spotId(), category.id());
        }

        // Завантаження нових даних і очищення полів
        loadParkingSpots();
        clearFields();

        // Повідомлення про успішне оновлення
        AlertController.showAlert("Паркувальне місце успішно оновлено.");

      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Паркувальне місце з ID " + selectedParkingSpot.spotId() + " не знайдено.");
      }
    } else {
      // Якщо користувач не вибрав паркувальне місце
      AlertController.showAlert("Будь ласка, виберіть паркувальне місце для редагування.");
    }
  }

  @FXML
  private void deleteParkingSpot() {
    ParkingSpot selectedParkingSpot = parkingSpotTable.getSelectionModel().getSelectedItem();
    if (selectedParkingSpot != null) {
      try {
        parkingSpotRepository.deleteParkingSpot(selectedParkingSpot.spotId());
        loadParkingSpots();
        clearFields();
      } catch (Exception e) {
        AlertController.showAlert("Не вдалося видалити паркувальне місце: " + e.getMessage());
      }
    } else {
      AlertController.showAlert("Оберіть паркувальне місце для видалення.");
    }
  }

  private void filterParkingSpots(String searchText) {
    List<ParkingSpot> filteredList = parkingSpots.stream()
        .filter(spot -> spot.section().toLowerCase().contains(searchText.toLowerCase()) ||
            spot.spotNumber().toLowerCase().contains(searchText.toLowerCase()) ||
            spot.status().toLowerCase().contains(searchText.toLowerCase()) ||
            spot.size().toLowerCase().contains(searchText.toLowerCase()) ||
            String.valueOf(spot.level()).contains(searchText))
        .toList();

    parkingSpotTable.setItems(FXCollections.observableArrayList(filteredList));

    if (filteredList.isEmpty()) {
      parkingSpotTable.setPlaceholder(new Label("На жаль, даних немає"));
    }
  }

  private void clearFields() {
    sectionField.clear();
    levelField.clear();
    spotNumberField.clear();
    statusComboBox.setValue(null);
    sizeComboBox.setValue(null);
    categoryComboBox.getCheckModel().clearChecks();
  }
}
