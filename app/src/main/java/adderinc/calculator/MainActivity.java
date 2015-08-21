package adderinc.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Stack;


public class MainActivity extends Activity {

    static final String KEY_RESULT = "resultField";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        TextView tv = (TextView) findViewById(R.id.result);
        CharSequence curTxt = tv.getText();
        savedInstanceState.putCharSequence(KEY_RESULT, curTxt);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        TextView tv = (TextView) findViewById(R.id.result);
        CharSequence curTxt = savedInstanceState.getCharSequence(KEY_RESULT);
        tv.setText(curTxt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkOperator(char item) {
        switch (item) {
            case '+':
            case '-':
            case '*':
            case '/':
                return true;
            default:
                return false;
        }
    }

    private boolean checkDigit(char item) {
        switch (item) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;
        }
    }

    private boolean checkFloatDigit(char item) {
        return (checkDigit(item) || item == '.');
    }

    private boolean checkExtraDecimal(CharSequence curTxt) {
        int lastNumDecCount = 0;
        for (int i = curTxt.length() - 1; i >= 0; i--) {
            if (curTxt.charAt(i) == '.') {
                lastNumDecCount++;
            } else if (!checkDigit(curTxt.charAt(i))) {
                break;
            }
        }
        if (lastNumDecCount >= 2) {
            return true;
        }
        return false;
    }

    private CharSequence popThenReplaceLast(CharSequence curTxt, char replacement) {
        curTxt = curTxt.subSequence(0, curTxt.length() - 1);
        StringBuilder temp = new StringBuilder(curTxt);
        temp.setCharAt(temp.length() - 1, replacement);
        return temp;
    }

    private CharSequence replaceLast(CharSequence curTxt, char replacement) {
        StringBuilder temp = new StringBuilder(curTxt);
        temp.setCharAt(temp.length() - 1, replacement);
        return temp;
    }

    private void autoScrollTV() {
        final ScrollView sv = (ScrollView) findViewById(R.id.resultScrollView);
        sv.post(new Runnable() {
            public void run() {
                sv.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void insertCharToTextField(View v) {
        TextView tv = (TextView) findViewById(R.id.result);

        CharSequence prevTxt = tv.getText();
        if (prevTxt.length() >= 1 &&
                prevTxt.charAt(prevTxt.length() - 1) == ')') {
            tv.append("*");
        }

        // insert button pressed
        switch (v.getId()) {
            case R.id.button0:
                tv.append("0");
                break;
            case R.id.button1:
                tv.append("1");
                break;
            case R.id.button2:
                tv.append("2");
                break;
            case R.id.button3:
                tv.append("3");
                break;
            case R.id.button4:
                tv.append("4");
                break;
            case R.id.button5:
                tv.append("5");
                break;
            case R.id.button6:
                tv.append("6");
                break;
            case R.id.button7:
                tv.append("7");
                break;
            case R.id.button8:
                tv.append("8");
                break;
            case R.id.button9:
                tv.append("9");
                break;
            case R.id.buttonAdd:
                tv.append("+");
                break;
            case R.id.buttonSub:
                tv.append("-");
                break;
            case R.id.buttonMult:
                tv.append("*");
                break;
            case R.id.buttonDiv:
                tv.append("/");
                break;
            case R.id.buttonDot:
                tv.append(".");
                break;
        }

        CharSequence curTxt = tv.getText();
        char curLast = curTxt.charAt(curTxt.length() - 1);
        char prevLast = ' ';
        if (curTxt.length() > 1) {
            prevLast = curTxt.charAt(curTxt.length() - 2);
        }

        // check previous last character for special cases
        if (curLast == '.') {
            if (checkExtraDecimal(curTxt)) {
                popChar(v);
            } else if (!checkDigit(prevLast)) {
                curTxt = replaceLast(curTxt, '0');
                tv.setText(curTxt);
                tv.append(".");
            }
        } else {
            if (curTxt.length() > 1) {
                if ((prevLast == '+' && curLast == '+') ||
                        (prevLast == '-' && curLast == '-') ||
                        (prevLast == '*' && curLast == '*') ||
                        (prevLast == '/' && curLast == '/')) {
                    popChar(v);
                } else if (checkOperator(prevLast) && checkOperator(curLast)) {
                    curTxt = popThenReplaceLast(curTxt, curLast);
                    if (curTxt.length() > 1) {
                        prevLast = curTxt.charAt(curTxt.length() - 2);
                    }
                    tv.setText(curTxt);
                } else if (checkOperator(curLast) && prevLast == '.') {
                    curTxt = replaceLast(curTxt, '0');
                    tv.setText(curTxt);
                    tv.append(String.valueOf(curLast));
                }
            }
            if (checkOperator(curLast) && curLast != '-') {
                if (curTxt.length() == 1 || prevLast == '(') {
                    popChar(v);
                }
            }
        }

        autoScrollTV();
    }

    public void paren(View v) {
        TextView tv = (TextView) findViewById(R.id.result);
        CharSequence curTxt = tv.getText();
        int leftParenCount = 0;
        int rightParenCount = 0;

        for (int i = 0; i < curTxt.length(); i++) {
            if (curTxt.charAt(i) == '(') {
                leftParenCount++;
            }
        }
        for (int i = 0; i < curTxt.length(); i++) {
            if (curTxt.charAt(i) == ')') {
                rightParenCount++;
            }
        }

        if (curTxt.length() <= 0) {
            tv.append("(");
        } else {
            if (curTxt.charAt(curTxt.length() - 1) == '.') {
                tv.append("0");
            }
            if (checkOperator(curTxt.charAt(curTxt.length() - 1))) {
                tv.append("(");
            } else if (rightParenCount == leftParenCount) {
                tv.append("*(");
            } else {
                tv.append(")");
            }
        }

        autoScrollTV();
    }

    public void popChar(View v) {
        TextView tv = (TextView) findViewById(R.id.result);
        CharSequence curTxt = tv.getText();
        if (curTxt.length() <= 1) {
            tv.setText("");
            return;
        }
        curTxt = curTxt.subSequence(0, curTxt.length() - 1);
        tv.setText(curTxt);
    }

    public void allClear(View v) {
        TextView tv = (TextView) findViewById(R.id.result);
        tv.setText("");
    }

    private double performOperation(double leftNum, double rightNum, char operation) {
        switch (operation) {
            case '+':
                return (leftNum + rightNum);
            case '-':
                return (leftNum - rightNum);
            case '*':
                return (leftNum * rightNum);
            case '/':
                return (leftNum / rightNum);
            default:
                return Double.NEGATIVE_INFINITY;
        }
    }

    public void parseResult(View v) {
        TextView tv = (TextView) findViewById(R.id.result);
        String curTxt = tv.getText().toString();
        if (curTxt.length() <= 2) {
            return;
        }

        double lOperand, rOperand;
        boolean priority = false;
        boolean readingNum = false;
        String curNum = "";

        Stack<Double> operands = new Stack<Double>();
        Stack<Character> operators = new Stack<Character>();

        for (int i = 0; i < curTxt.length(); i++) {

            char curChar = curTxt.charAt(i);

            if (!readingNum && checkDigit(curChar)) {
                curNum = String.valueOf(curChar);
                readingNum = true;
            } else if (readingNum && checkFloatDigit(curChar)) {
                curNum += curChar;
            }
            if (readingNum && (i == curTxt.length()-1 || !checkFloatDigit(curChar))) {
                operands.push(Double.parseDouble(curNum));
                readingNum = false;
                if (priority) {
                    rOperand = operands.pop();
                    lOperand = operands.pop();
                    operands.push(
                            performOperation(lOperand, rOperand, operators.pop()));
                    priority = false;
                }
            }

            if (checkOperator(curChar)) {
                operators.push(curChar);
                if (curChar == '*' || curChar == '/') {
                    priority = true;
                }
            } else if (curChar == '(') {
                operators.push(curChar);
                priority = false;
            } else if (curChar == ')') { // calculate until '('
                char curOp = operators.pop();
                while (curOp != '(') {
                    rOperand = operands.pop();
                    lOperand = operands.pop();
                    operands.push(
                            performOperation(lOperand, rOperand, curOp));
                    curOp = operators.pop();
                }
            }
        }
        while (!operators.empty()) {
            rOperand = operands.pop();
            lOperand = operands.pop();
            operands.push(
                    performOperation(lOperand, rOperand, operators.pop()));
        }
        curNum = (operands.pop()).toString();
        tv.setText(curNum);
    }

    // parse string for int/float/str:
    /*for (int i = 0; i < firstWord.length(); i++) {
        char cur = firstWord.charAt(i);
        if (    isDigit(cur) ||
                (i == 0 && cur == '-')) {
            continue;
        }
        if (cur == '.' && numDots == 0) {
            flagInt = false;
            flagFlt = true;
            numDots++;
            continue;
        }
        flagFlt = flagInt = false;
        flagStr = true;
        break;
    }*/
}
