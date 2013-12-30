package com.cameron.infixcalculator;

import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.GridLayout;
import android.widget.Button;
import java.util.*;
import android.widget.TextView;
import com.cameron.infixcalculator.InfixEvaluator.SyntaxErrorException;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DisplayActivity extends ActionBarActivity {

    private boolean clearOnInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_main);

        clearOnInput = true;

        int screenWidth, screenHeight;

        Display dis = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        dis.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        GridLayout gridView = (GridLayout) findViewById(R.id.grid);
        for(int i = 0; i < gridView.getChildCount(); i++) {
            Class child = gridView.getChildAt(i).getClass();
            if(child == Button.class) {
                Button button = (Button) gridView.getChildAt(i);
                button.setWidth((int) (screenWidth * .25));
                button.setHeight((int) (screenHeight * .1));
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                messageBox("About This Application", "It was developed by Cameron Roe.");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addCharacter(View view) {
        Button clickedButton = (Button) view;
        try {
            Double.parseDouble(clickedButton.getText().toString());
            if(clearOnInput) {
                clearScreen(null);
                clearOnInput = false;
            }
        } catch(Exception e) {
            // Don't clear, just add character
            clearOnInput = false;
        }
        TextView expr = (TextView) findViewById(R.id.screen);
        if(view == findViewById(R.id.btnExp)) {
            expr.append("^");
        } else {
            String charToAppend = clickedButton.getText().toString();
            expr.append(charToAppend);
        }
    }

    public void clearScreen(View view) {
        TextView screen = (TextView) findViewById(R.id.screen);
        screen.setText("");
        if(view != null) {
            screen.setText("0");
            clearOnInput = true;
        }
    }

    public void setMemory(View view) {
        double value = 0.0;
        try {
            value = Double.parseDouble(((TextView) findViewById(R.id.screen)).getText().toString());
        } catch(Exception e) {
            messageBox("Memory Error", "Memory can only hold numbers.");
        }
        CalculatorMemory.get().setValue(value);
    }

    public void recallMemory(View view) {
        TextView screen = (TextView) findViewById(R.id.screen);
        screen.append(Double.toString(CalculatorMemory.get().getValue()));
    }

    public void clearMemory(View view) {
        CalculatorMemory.clearMemory();
    }

    public void backspace(View view) {
        TextView screen = (TextView) findViewById(R.id.screen);
        String currScreen = screen.getText().toString();
        String newScreen = "";
        if(currScreen.length() > 1) {
            newScreen = currScreen.substring(0, currScreen.length() - 1);
        } else {
            newScreen = "0";
            clearOnInput = true;
        }
        screen.setText(newScreen);
    }

    public void evaluateExpression(View view) throws SyntaxErrorException {
        InfixEvaluator evaluator = new InfixEvaluator();
        TextView screen = (TextView) findViewById(R.id.screen);
        String expr = screen.getText().toString();
        double result = evaluator.eval(expr);
        switch(evaluator.checkError()) {
            case 0:
                screen.setText(Double.toString(result));
                break;
            case 1:
                messageBox("Infix Error", "You did not input a proper infix expression.");
                break;
            case 2:
                messageBox("Stack Error", "For some reason, the stack did not fully empty itself.");
                break;
            case 3:
                messageBox("Input Error", "You should only be using operands and operators.");
                break;
            case 4:
                messageBox("Parenthesis Error", "You did not properly open and close your parenthesis.");
                break;
            case 404:
                messageBox("Input Error", "Dude, you should put an expression in.");
                break;
            case 403:
                messageBox("Division by 0", "I'm afraid I can't let you do that user.");
                break;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void messageBox(String title, String message) {
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(title);
        messageBox.setMessage(message);
        messageBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        messageBox.setCancelable(true);
        messageBox.create().show();
    }

}
