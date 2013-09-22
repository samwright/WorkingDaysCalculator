package io.github.samwright.workingdays; /**
 * Sample Skeleton for "MainWindow.fxml" Controller Class
 * You can copy and paste this code into your favorite IDE
 **/

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.joda.time.LocalDate;


public class MainWindow extends AnchorPane {

    private static int MAX_DURATION = 365 * 10000;

    @FXML
    private TextField durationTextField;

    @FXML
    private Label endDateLabel;

    @FXML
    private ChoiceBox<Integer> dayChoiceBox, monthChoiceBox, yearChoiceBox;

    public MainWindow() {
        // Load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set ChoiceBox options
        dayChoiceBox.getItems().addAll(intsInRangeInclusive(1, 31));
        monthChoiceBox.getItems().addAll(intsInRangeInclusive(1, 12));
        yearChoiceBox.getItems().addAll(intsInRangeInclusive(1910, 2200));

        // Setup bindings that trigger calculation
        ChangeListener<Integer> updateTrigger = new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer integer2) {
                calculate();
            }
        };

        dayChoiceBox.getSelectionModel().selectedItemProperty().addListener(updateTrigger);
        monthChoiceBox.getSelectionModel().selectedItemProperty().addListener(updateTrigger);
        yearChoiceBox.getSelectionModel().selectedItemProperty().addListener(updateTrigger);

        durationTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                calculate();
            }
        });

        // Set initial date as today
        LocalDate now = new LocalDate();System.out.println("Now is: " + now);
        dayChoiceBox.getSelectionModel().select((Integer) now.getDayOfMonth());
        monthChoiceBox.getSelectionModel().select((Integer) now.getMonthOfYear());
        yearChoiceBox.getSelectionModel().select((Integer) now.getYear());

        durationTextField.setText("1");
    }

    private List<Integer> intsInRangeInclusive(int start, int end) {
        List<Integer> ints = new ArrayList<>();
        for (int i = start; i <= end; ++i)
            ints.add(i);

        return ints;
    }

    private void calculate() {
        // Check date is valid
        Integer year = yearChoiceBox.getSelectionModel().getSelectedItem();
        Integer month = monthChoiceBox.getSelectionModel().getSelectedItem();
        Integer day = dayChoiceBox.getSelectionModel().getSelectedItem();

        LocalDate startDate;
        try {
            startDate = new LocalDate(String.format("%s-%s-%s", year, month, day));
        } catch (IllegalArgumentException e) {
            endDateLabel.setText("Date is not valid");
            return;
        }

        // Check duration is valid
        int duration;
        try {
            duration = Integer.parseInt(durationTextField.getText());
        } catch (NumberFormatException e) {
            endDateLabel.setText("Duration is not an integer");
            return;
        }

        if (duration > MAX_DURATION) {
            endDateLabel.setText("Duration is longer than 10 millenia");
            return;
        }

        LocalDate endDate = DateCalcFacade.getEndDate(startDate, duration);
        endDateLabel.setText(endDate.toString());
    }
}
