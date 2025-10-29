package com.androidstudy.helloworldalc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_EXPRESSION = "expression";

    private TextView display;
    private final StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.text_display);

        if (savedInstanceState != null) {
            String savedExpression = savedInstanceState.getString(KEY_EXPRESSION, "");
            expression.append(savedExpression);
            updateDisplay();
        } else {
            updateDisplay();
        }

        int[] digitButtonIds = new int[] {
                R.id.button_zero,
                R.id.button_one,
                R.id.button_two,
                R.id.button_three,
                R.id.button_four,
                R.id.button_five,
                R.id.button_six,
                R.id.button_seven,
                R.id.button_eight,
                R.id.button_nine
        };

        View.OnClickListener digitListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                appendToExpression(button.getText().toString());
            }
        };

        for (int id : digitButtonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(digitListener);
        }

        int[] operatorButtonIds = new int[] {
                R.id.button_add,
                R.id.button_subtract,
                R.id.button_multiply,
                R.id.button_divide
        };

        View.OnClickListener operatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                appendOperator(button.getText().toString());
            }
        };

        for (int id : operatorButtonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(operatorListener);
        }

        Button decimalButton = findViewById(R.id.button_decimal);
        decimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendDecimal();
            }
        });

        Button clearButton = findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearExpression();
            }
        });

        Button deleteButton = findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLastCharacter();
            }
        });

        Button equalsButton = findViewById(R.id.button_equals);
        equalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateExpression();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EXPRESSION, expression.toString());
    }

    private void appendToExpression(String value) {
        expression.append(value);
        updateDisplay();
    }

    private void appendOperator(String operator) {
        if (expression.length() == 0) {
            if ("-".equals(operator)) {
                expression.append(operator);
            }
            updateDisplay();
            return;
        }

        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperator(lastChar)) {
            expression.setCharAt(expression.length() - 1, operator.charAt(0));
        } else {
            String expr = expression.toString();
            int existingOperatorIndex = findOperatorIndex(expr);
            if (existingOperatorIndex != -1 && existingOperatorIndex != expr.length() - 1) {
                return;
            }
            expression.append(operator);
        }
        updateDisplay();
    }

    private void appendDecimal() {
        int lastOperatorIndex = findLastOperatorIndex();
        int startIndex = lastOperatorIndex + 1;
        for (int i = startIndex; i < expression.length(); i++) {
            if (expression.charAt(i) == '.') {
                return;
            }
        }
        if (expression.length() == 0 || isOperator(expression.charAt(expression.length() - 1))) {
            expression.append("0");
        }
        expression.append('.');
        updateDisplay();
    }

    private void clearExpression() {
        expression.setLength(0);
        updateDisplay();
    }

    private void deleteLastCharacter() {
        if (expression.length() > 0) {
            expression.deleteCharAt(expression.length() - 1);
        }
        updateDisplay();
    }

    private void evaluateExpression() {
        if (expression.length() == 0) {
            return;
        }

        String expr = expression.toString();
        int operatorIndex = findOperatorIndex(expr);

        if (operatorIndex == -1 || operatorIndex == expr.length() - 1) {
            showError();
            return;
        }

        String left = expr.substring(0, operatorIndex);
        String right = expr.substring(operatorIndex + 1);

        if (left.isEmpty() || right.isEmpty()) {
            showError();
            return;
        }

        try {
            double operand1 = Double.parseDouble(left);
            double operand2 = Double.parseDouble(right);
            char operator = expr.charAt(operatorIndex);
            double result;

            switch (operator) {
                case '+':
                    result = operand1 + operand2;
                    break;
                case '-':
                    result = operand1 - operand2;
                    break;
                case '*':
                    result = operand1 * operand2;
                    break;
                case '/':
                    if (operand2 == 0) {
                        showError();
                        return;
                    }
                    result = operand1 / operand2;
                    break;
                default:
                    showError();
                    return;
            }

            String resultText = formatResult(result);
            expression.setLength(0);
            expression.append(resultText);
            updateDisplay();
        } catch (NumberFormatException e) {
            showError();
        }
    }

    private String formatResult(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private int findOperatorIndex(String expr) {
        int start = expr.startsWith("-") ? 1 : 0;
        for (int i = start; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (isOperator(c)) {
                return i;
            }
        }
        return -1;
    }

    private int findLastOperatorIndex() {
        for (int i = expression.length() - 1; i >= 0; i--) {
            if (isOperator(expression.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private void updateDisplay() {
        if (display == null) {
            return;
        }
        if (expression.length() == 0) {
            display.setText("0");
        } else {
            display.setText(expression.toString());
        }
    }

    private void showError() {
        Toast.makeText(this, R.string.error_invalid_expression, Toast.LENGTH_SHORT).show();
        clearExpression();
    }
}
