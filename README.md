# Unit Converter Android App (Java + XML)

A beginner-friendly Android Studio project. It converts between **length, weight, volume, and temperature** units.

## Features

- Numeric value input using `EditText`
- Category selector using `Spinner`
- Source and target unit dropdowns using `Spinner`
- Convert and Swap buttons
- Result display with the target unit label
- Toast and field-level validation for empty/non-numeric input
- Unit dropdowns reset when the category changes
- Absolute-zero validation for temperature
- No external libraries or network permissions required

## Requirements

- Android Studio (recent stable version)
- JDK 17 (Android Studio's bundled JDK is recommended)
- Android SDK Platform 35
- Android device or emulator running Android 6.0 (API 23) or newer

## Open and run

1. Extract `UnitConverterAndroid.zip`.
2. Open the extracted `UnitConverterAndroid` folder in Android Studio.
3. Allow Gradle sync to finish. If prompted, install Android SDK Platform 35.
4. Select an emulator or connect an Android device with USB debugging enabled.
5. Press **Run**.

This project uses Android Gradle Plugin 8.6.1 and Java. A Gradle wrapper is not included; Android Studio can use its configured Gradle installation or you can generate a wrapper from the project root.

## Conversion formula notes

- Length converts through metres.
- Weight converts through kilograms.
- Volume converts through litres.
- Temperature converts through Celsius as an intermediate value.

Examples:
- 100 centimetres = 1 metre
- 1 kilogram = 1,000 grams
- 1 litre = 1,000 millilitres
- Fahrenheit to Celsius: `(°F - 32) × 5 / 9`
- Celsius to Fahrenheit: `°C × 9 / 5 + 32`
- Kelvin = Celsius + 273.15

## Project structure

```text
UnitConverterAndroid/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/unitconverter/MainActivity.java
│       └── res/
│           ├── layout/activity_main.xml
│           └── values/
│               ├── strings.xml
│               └── styles.xml
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Main concepts

- `EditText`: reads the number entered by the user.
- `Spinner`: lets the user select a category and units.
- `Button`: starts conversion or swaps units.
- `TextView`: displays the formatted result.
- `Toast`: provides short validation feedback.
- Java conversion methods: convert units using base units and temperature formulas.

## Test cases

| Category | Input | From | To | Expected |
|---|---:|---|---|---:|
| Length | 100 | Centimetres | Metres | 1 |
| Length | 1 | Miles | Kilometres | 1.609344 |
| Weight | 1000 | Grams | Kilograms | 1 |
| Weight | 1 | Kilograms | Pounds | about 2.20462262 |
| Volume | 1 | Litres | Millilitres | 1000 |
| Temperature | 0 | Celsius | Fahrenheit | 32 |
| Temperature | 273.15 | Kelvin | Celsius | 0 |

Also test:
- Empty input: should show a Toast.
- Non-numeric input: should show a Toast.
- Changing category: should reset both unit dropdowns.
- Swap: should exchange source and target units.

## References

- Android `Spinner`: https://developer.android.com/develop/ui/views/components/spinner
- Android `EditText` / text fields: https://developer.android.com/develop/ui/views/components/text-fields
- Android `Toast`: https://developer.android.com/guide/topics/ui/notifiers/toasts
