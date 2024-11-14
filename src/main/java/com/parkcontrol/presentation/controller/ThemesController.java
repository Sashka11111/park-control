package com.parkcontrol.presentation.controller;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.parkcontrol.domain.setting.ThemeManager;
import javafx.fxml.FXML;
import javafx.application.Application;
import javafx.scene.control.*;

public class ThemesController {

  @FXML
  private RadioButton cupertinoDarkRadioButton;

  @FXML
  private RadioButton darkThemeRadioButton;

  @FXML
  private RadioButton draculaRadioButton;

  @FXML
  private RadioButton lightThemeRadioButton;

  @FXML
  private RadioButton nordDarkThemeRadioButton;

  @FXML
  private RadioButton nordLightRadioButton;

  private String theme;
  private ToggleGroup themeToggleGroup;

  @FXML
  void initialize() {
    theme = ThemeManager.getCurrentTheme() != null ?
        ThemeManager.getCurrentTheme() :
        new PrimerLight().getUserAgentStylesheet();
    setInitialTheme();
    setupThemeToggleGroup();

    themeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> applyTheme());
 }

  private void setInitialTheme() {
    if (theme.equals(new PrimerDark().getUserAgentStylesheet())) {
      darkThemeRadioButton.setSelected(true);
    } else if (theme.equals(new PrimerLight().getUserAgentStylesheet())) {
      lightThemeRadioButton.setSelected(true);
    } else if (theme.equals(new NordLight().getUserAgentStylesheet())) {
      nordLightRadioButton.setSelected(true);
    } else if (theme.equals(new NordDark().getUserAgentStylesheet())) {
      nordDarkThemeRadioButton.setSelected(true);
    } else if (theme.equals(new CupertinoDark().getUserAgentStylesheet())) {
      cupertinoDarkRadioButton.setSelected(true);
    } else if (theme.equals(new Dracula().getUserAgentStylesheet())) {
      draculaRadioButton.setSelected(true);
    }
  }

  private void setupThemeToggleGroup() {
    themeToggleGroup = new ToggleGroup();
    lightThemeRadioButton.setToggleGroup(themeToggleGroup);
    darkThemeRadioButton.setToggleGroup(themeToggleGroup);
    nordLightRadioButton.setToggleGroup(themeToggleGroup);
    nordDarkThemeRadioButton.setToggleGroup(themeToggleGroup);
    cupertinoDarkRadioButton.setToggleGroup(themeToggleGroup);
    draculaRadioButton.setToggleGroup(themeToggleGroup);
  }

  private void applyTheme() {
    String selectedTheme = switch ((RadioButton) themeToggleGroup.getSelectedToggle()) {
      case RadioButton rb when rb == lightThemeRadioButton -> new PrimerLight().getUserAgentStylesheet();
      case RadioButton rb when rb == darkThemeRadioButton -> new PrimerDark().getUserAgentStylesheet();
      case RadioButton rb when rb == nordLightRadioButton -> new NordLight().getUserAgentStylesheet();
      case RadioButton rb when rb == nordDarkThemeRadioButton -> new NordDark().getUserAgentStylesheet();
      case RadioButton rb when rb == cupertinoDarkRadioButton -> new CupertinoDark().getUserAgentStylesheet();
      case RadioButton rb when rb == draculaRadioButton -> new Dracula().getUserAgentStylesheet();
      default -> new PrimerLight().getUserAgentStylesheet();
    };

    Application.setUserAgentStylesheet(selectedTheme);
    setTheme(selectedTheme);
  }

  private void setTheme(String userAgentStylesheet) {
    theme = userAgentStylesheet;
    ThemeManager.setCurrentTheme(userAgentStylesheet);
  }
}

