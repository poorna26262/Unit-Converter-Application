package com.example.unitconverter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {

    private Spinner categorySpinner;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private EditText valueInput;
    private TextView resultText;
    private final Map<String, String[]> unitOptions = new LinkedHashMap<>();

    private final String[] categories = {"Length", "Weight", "Volume", "Temperature"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorySpinner = findViewById(R.id.categorySpinner);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        valueInput = findViewById(R.id.valueInput);
        resultText = findViewById(R.id.resultText);
        Button convertButton = findViewById(R.id.convertButton);
        Button swapButton = findViewById(R.id.swapButton);

        configureUnits();
        setSpinnerAdapter(categorySpinner, categories);
        updateUnitSpinners(0);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(position);
                resultText.setText(R.string.result_placeholder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        convertButton.setOnClickListener(v -> convertValue());
        swapButton.setOnClickListener(v -> {
            int from = fromSpinner.getSelectedItemPosition();
            int to = toSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(to);
            toSpinner.setSelection(from);
            resultText.setText(R.string.result_placeholder);
        });
    }

    private void configureUnits() {
        unitOptions.put("Length", new String[]{
                "Metres (m)", "Kilometres (km)", "Centimetres (cm)",
                "Millimetres (mm)", "Miles (mi)", "Feet (ft)", "Inches (in)"
        });
        unitOptions.put("Weight", new String[]{
                "Kilograms (kg)", "Grams (g)", "Milligrams (mg)",
                "Pounds (lb)", "Ounces (oz)"
        });
        unitOptions.put("Volume", new String[]{
                "Litres (L)", "Millilitres (mL)", "Cubic metres (m³)",
                "US gallons (gal)", "US cups (cup)"
        });
        unitOptions.put("Temperature", new String[]{
                "Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)"
        });
    }

    private void setSpinnerAdapter(Spinner spinner, String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void updateUnitSpinners(int categoryPosition) {
        String category = categories[categoryPosition];
        String[] units = unitOptions.get(category);
        setSpinnerAdapter(fromSpinner, units);
        setSpinnerAdapter(toSpinner, units);
        if (units.length > 1) {
            toSpinner.setSelection(1);
        }
    }

    private void convertValue() {
        String raw = valueInput.getText().toString().trim();

        if (raw.isEmpty()) {
            showInputError("Please enter a value.");
            return;
        }

        final double input;
        try {
            input = Double.parseDouble(raw);
            if (Double.isNaN(input) || Double.isInfinite(input)) {
                showInputError("Please enter a valid number.");
                return;
            }
        } catch (NumberFormatException exception) {
            showInputError("Input must be numeric.");
            return;
        }

        String category = categorySpinner.getSelectedItem().toString();
        String fromUnit = fromSpinner.getSelectedItem().toString();
        String toUnit = toSpinner.getSelectedItem().toString();

        if ("Temperature".equals(category) && input < -273.15
                && fromUnit.startsWith("Celsius")) {
            showInputError("Temperature cannot be below absolute zero.");
            return;
        }
        if ("Temperature".equals(category) && input < -459.67
                && fromUnit.startsWith("Fahrenheit")) {
            showInputError("Temperature cannot be below absolute zero.");
            return;
        }
        if ("Temperature".equals(category) && input < 0
                && fromUnit.startsWith("Kelvin")) {
            showInputError("Kelvin cannot be negative.");
            return;
        }

        double converted = convert(category, input, fromUnit, toUnit);
        String formatted = new DecimalFormat("#,##0.########").format(converted);
        resultText.setText(String.format(Locale.getDefault(), "%s = %s",
                formatted, toUnit));
    }

    private double convert(String category, double value, String from, String to) {
        if (from.equals(to)) return value;

        switch (category) {
            case "Length":
                return convertByBase(value, lengthFactor(from), lengthFactor(to));
            case "Weight":
                return convertByBase(value, weightFactor(from), weightFactor(to));
            case "Volume":
                return convertByBase(value, volumeFactor(from), volumeFactor(to));
            case "Temperature":
                return convertTemperature(value, from, to);
            default:
                return value;
        }
    }

    // Factors convert the selected unit into the category's base unit.
    // Length base: metre. Weight base: kilogram. Volume base: litre.
    private double convertByBase(double value, double fromFactor, double toFactor) {
        return value * fromFactor / toFactor;
    }

    private double lengthFactor(String unit) {
        if (unit.startsWith("Metres")) return 1.0;
        if (unit.startsWith("Kilometres")) return 1000.0;
        if (unit.startsWith("Centimetres")) return 0.01;
        if (unit.startsWith("Millimetres")) return 0.001;
        if (unit.startsWith("Miles")) return 1609.344;
        if (unit.startsWith("Feet")) return 0.3048;
        if (unit.startsWith("Inches")) return 0.0254;
        throw new IllegalArgumentException("Unknown length unit: " + unit);
    }

    private double weightFactor(String unit) {
        if (unit.startsWith("Kilograms")) return 1.0;
        if (unit.startsWith("Grams")) return 0.001;
        if (unit.startsWith("Milligrams")) return 0.000001;
        if (unit.startsWith("Pounds")) return 0.45359237;
        if (unit.startsWith("Ounces")) return 0.028349523125;
        throw new IllegalArgumentException("Unknown weight unit: " + unit);
    }

    private double volumeFactor(String unit) {
        if (unit.startsWith("Litres")) return 1.0;
        if (unit.startsWith("Millilitres")) return 0.001;
        if (unit.startsWith("Cubic metres")) return 1000.0;
        if (unit.startsWith("US gallons")) return 3.785411784;
        if (unit.startsWith("US cups")) return 0.2365882365;
        throw new IllegalArgumentException("Unknown volume unit: " + unit);
    }

    private double convertTemperature(double value, String from, String to) {
        double celsius;
        if (from.startsWith("Celsius")) {
            celsius = value;
        } else if (from.startsWith("Fahrenheit")) {
            celsius = (value - 32.0) * 5.0 / 9.0;
        } else {
            celsius = value - 273.15;
        }

        if (to.startsWith("Celsius")) return celsius;
        if (to.startsWith("Fahrenheit")) return celsius * 9.0 / 5.0 + 32.0;
        if (to.startsWith("Kelvin")) return celsius + 273.15;
        throw new IllegalArgumentException("Unknown temperature unit: " + to);
    }

    private void showInputError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        valueInput.setError(message);
    }
}
