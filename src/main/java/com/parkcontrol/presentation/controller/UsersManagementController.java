package com.parkcontrol.presentation.controller;

import com.parkcontrol.domain.exception.EntityNotFoundException;
import com.parkcontrol.persistence.connection.DatabaseConnection;
import com.parkcontrol.persistence.entity.User;
import com.parkcontrol.persistence.entity.UserRole;
import com.parkcontrol.persistence.repository.contract.UserRepository;
import com.parkcontrol.persistence.repository.impl.UserRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class UsersManagementController {

  @FXML
  private TableView<User> usersTableView;
  @FXML
  private TableColumn<User, String> usernameColumn;
  @FXML
  private TableColumn<User, String> roleColumn;

  @FXML
  private ComboBox<UserRole> roleComboBox;
  @FXML
  private Button changeRoleButton;
  @FXML
  private TextField usernameField;
  @FXML
  private Button deleteButton;
  @FXML
  private Label roleLabel;
  @FXML
  private Label userLabel;

  private final UserRepository userRepository;

  public UsersManagementController() {
    this.userRepository = new UserRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  private void initialize() {
    usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username()));
    roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().role().toString()));

    loadUsers();
    loadRoles();

    usersTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillForm(newValue));

    changeRoleButton.setOnAction(event -> changeRole());
    deleteButton.setOnAction(event -> deleteUser());
  }

  private void loadUsers() {
    List<User> users = userRepository.findAll();
    ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
    usersTableView.setItems(userObservableList);
  }

  private void loadRoles() {
    roleComboBox.getItems().setAll(UserRole.values());
  }

  private void fillForm(User user) {
    if (user != null) {
      usernameField.setText(user.username());
      roleComboBox.setValue(user.role());
    }
  }

  private void changeRole() {
    String username = usernameField.getText();
    UserRole newRole = roleComboBox.getValue();

    if (username != null && !username.isEmpty() && newRole != null) {
      try {
        userRepository.updateUserRole(username, newRole);
        AlertController.showAlert( "Успішно змінено роль");
        loadUsers();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert( "Користувача не знайдено");
      }
    } else {
      AlertController.showAlert( "Будь ласка, заповніть усі поля");
    }
    clearFields();
  }

  private void deleteUser() {
    String username = usernameField.getText();

    if (username != null && !username.isEmpty()) {
      try {
        userRepository.deleteUser(username);
        AlertController.showAlert("Користувача успішно видалено");
        loadUsers();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Користувача не знайдено");
      }
    } else {
      AlertController.showAlert("Будь ласка, введіть ім'я користувача");
    }
    clearFields();
  }

  private void clearFields() {
    usernameField.clear();
    roleComboBox.setValue(null);
  }
}
