package com.sscompany.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int numberOfCourses = 0;
    Toast message;
    ArrayList <String> courseNames;
    ArrayList <Integer> courseCredits;
    public static final String PREFS_NAME = "MyPrefsFile2";
    public CheckBox dontShowAgain;
    String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        message = Toast.makeText(MainActivity.this, "The maximum number of course you can have is 8!!!", Toast.LENGTH_SHORT);

        loadData();

        System.out.println("courseNames.size - onCreate : " + courseNames.size());

        if(courseNames.size() == 0)
        {
            LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
            View child = getLayoutInflater().inflate(R.layout.new_course, null);
            layout.addView(child);
            TextView neww = child.findViewById(R.id.new_course);
            numberOfCourses++;
            neww.setTextSize(17);
            courseNames.add("New Course");

            neww.setOnLongClickListener(editName);


            Spinner spinner = (Spinner) findViewById(R.id.new_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.letterGrades, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);

            saveData();
            loadData();
        }
        else
        {
            numberOfCourses = courseNames.size();
            for(int i = 0; i < courseNames.size(); i++)
            {
                LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
                View child = getLayoutInflater().inflate(R.layout.new_course, null);
                layout.addView(child);

                TextView name = child.findViewById(R.id.new_course);
                EditText credit = child.findViewById(R.id.new_credit);

                name.setOnLongClickListener(editName);
                String courseName = courseNames.get(i);
                name.setText(courseName);
                name.setTextSize(17);
                if(courseCredits.size() > i)
                    if(courseCredits.get(i) != null)
                        credit.setText(courseCredits.get(i).toString(), TextView.BufferType.EDITABLE);

                saveData();
                loadData();
            }
        }

        ViewGroup viewGroups = (ViewGroup)findViewById(R.id.linearLayout);

        for (int i = 0; i < viewGroups.getChildCount(); i++)
        {
            ViewGroup childViewGroup = (ViewGroup) viewGroups.getChildAt(i);

            System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

            for(int k = 0; k < childViewGroup.getChildCount(); k++)
            {
                ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                System.out.println("text: " + childViewG1.toString());

                for(int m = 0; m < childViewG1.getChildCount(); m++) {
                    final View childView = (View) childViewG1.getChildAt(m);
                    if (childView instanceof EditText)
                    {
                        ((EditText) childView).addTextChangedListener(textWatcher);
                    }

                    if (childView instanceof Spinner)
                    {
                        Spinner spinner = (Spinner) childView.findViewById(R.id.new_spinner);
                        // Create an ArrayAdapter using the string array and a default spinner layout
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.letterGrades, android.R.layout.simple_spinner_item);
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        spinner.setAdapter(adapter);
                    }

                }
            }
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.skip);
        adb.setView(eulaLayout);
        adb.setTitle("Attention");
        adb.setMessage("\nInstructions: \n" +
                "\t\n" +
                "\t* You can add a new course using \"ADD COURSE\" button.\n" +
                "\t\n" +
                "\t* You can remove the course using trash can button on the right of every course.\n" +
                "\t\n" +
                "\t* You can change name of course by long pressing the name of the course.\n" +
                "\t\n" +
                "\t* You can calculate the GPA using \"CALCULATE GPA\" button.\n");

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                // Do what you want to do on "OK" action

                return;
            }
        });

        if (!skipMessage.equals("checked")) {
            adb.show();
        }


    }



    private View.OnLongClickListener editName = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View v)
        {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Enter Name of the Course");

            final EditText input2 = new EditText(MainActivity.this);
            input2.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            input2.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            input2.setLines(1);
            int maxLength = 11;
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
            input2.setFilters(FilterArray);
            input2.setText(((TextView)v).getText().toString(), TextView.BufferType.EDITABLE);

            final EditText input = input2;

            LinearLayout layout = new LinearLayout(MainActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(60, 0, 60, 0);

            layout.addView(input, params);

            alertDialog.setView(layout);

            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            courseName = input.getText().toString();
                            if (courseName.length() < 3) {
                                message.cancel();
                                message = Toast.makeText(getApplicationContext(), "Course Name should be at least 3 characters!", Toast.LENGTH_LONG);
                                message.show();
                            }
                            else if (courseName != null && courseName.length() >= 3)
                            {
                                if(v instanceof TextView)
                                {
                                    ((TextView) v).setText(courseName);
                                }

                                courseNames = new ArrayList<String>();
                                courseCredits = new ArrayList<Integer>();

                                ViewGroup viewGroup = (ViewGroup)findViewById(R.id.linearLayout);

                                for (int i = 0; i < viewGroup.getChildCount(); i++)
                                {
                                    ViewGroup childViewGroup = (ViewGroup) viewGroup.getChildAt(i);

                                    System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

                                    for(int k = 0; k < childViewGroup.getChildCount(); k++)
                                    {
                                        ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                                        System.out.println("text: " + childViewG1.toString());

                                        for(int m = 0; m < childViewG1.getChildCount(); m++) {
                                            View childView = (View) childViewG1.getChildAt(m);

                                            if (childView instanceof TextView && !(childView instanceof EditText))
                                            {
                                                String name = ((TextView) childView).getText().toString();

                                                System.out.println("Course names in for: " + name);
                                                if(!name.equals(""))
                                                    courseNames.add(name);
                                            }

                                            if (childView instanceof EditText)
                                            {
                                                String creditString = ((EditText) childView).getText().toString();
                                                int credit = 0;
                                                if(!creditString.equals(""))
                                                {
                                                    credit = Integer.parseInt(creditString);
                                                    courseCredits.add(credit);
                                                }
                                                else
                                                    courseCredits.add(null);

                                                System.out.println("Course credits in for: " + credit);
                                            }
                                        }
                                    }
                                }

                                System.out.println("courseNames.size - remove - end - before save: " + courseNames.size());
                                System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

                                saveData();
                                loadData();

                                System.out.println("courseNames.size - remove - end - after load : " + courseNames.size());
                                System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());
                            }

                        }
                    });

            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();

            return true;

        }
    };

    private TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s)
        {
            //String creditString = s.toString();
            //int credit = Integer.parseInt(creditString);

            courseNames = new ArrayList<String>();
            courseCredits = new ArrayList<Integer>();

            ViewGroup viewGroup = (ViewGroup)findViewById(R.id.linearLayout);

            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                ViewGroup childViewGroup = (ViewGroup) viewGroup.getChildAt(i);

                System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

                for(int k = 0; k < childViewGroup.getChildCount(); k++)
                {
                    ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                    System.out.println("text: " + childViewG1.toString());

                    for(int m = 0; m < childViewG1.getChildCount(); m++) {
                        View childView = (View) childViewG1.getChildAt(m);

                        if (childView instanceof TextView && !(childView instanceof EditText))
                        {
                            String name = ((TextView) childView).getText().toString();

                            System.out.println("Course names in for: " + name);
                            if(!name.equals(""))
                                courseNames.add(name);
                        }

                        if (childView instanceof EditText)
                        {
                            String creditString = ((EditText) childView).getText().toString();
                            int credit = 0;
                            if(!creditString.equals(""))
                            {
                                credit = Integer.parseInt(creditString);
                                courseCredits.add(credit);
                            }
                            else
                                courseCredits.add(null);

                            System.out.println("Course credits in for: " + credit);
                        }
                    }
                }
            }

            System.out.println("courseNames.size - remove - end - before save: " + courseNames.size());
            System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

            saveData();
            loadData();

            System.out.println("courseNames.size - remove - end - after load : " + courseNames.size());
            System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            courseNames = new ArrayList<String>();
            courseCredits = new ArrayList<Integer>();

            ViewGroup viewGroup = (ViewGroup)findViewById(R.id.linearLayout);

            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                ViewGroup childViewGroup = (ViewGroup) viewGroup.getChildAt(i);

                System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

                for(int k = 0; k < childViewGroup.getChildCount(); k++)
                {
                    ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                    System.out.println("text: " + childViewG1.toString());

                    for(int m = 0; m < childViewG1.getChildCount(); m++) {
                        View childView = (View) childViewG1.getChildAt(m);

                        if (childView instanceof TextView && !(childView instanceof EditText))
                        {
                            String name = ((TextView) childView).getText().toString();

                            System.out.println("Course names in for: " + name);
                            if(!name.equals(""))
                                courseNames.add(name);
                        }

                        if (childView instanceof EditText)
                        {
                            String creditString = ((EditText) childView).getText().toString();
                            int credit = 0;
                            if(!creditString.equals(""))
                            {
                                credit = Integer.parseInt(creditString);
                                courseCredits.add(credit);
                            }
                            else
                                courseCredits.add(null);

                            System.out.println("Course credits in for: " + credit);
                        }
                    }
                }
            }

            System.out.println("courseNames.size - remove - end - before save: " + courseNames.size());
            System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

            saveData();
            loadData();

            System.out.println("courseNames.size - remove - end - after load : " + courseNames.size());
            System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());
        }

        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            courseNames = new ArrayList<String>();
            courseCredits = new ArrayList<Integer>();

            ViewGroup viewGroup = (ViewGroup)findViewById(R.id.linearLayout);

            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                ViewGroup childViewGroup = (ViewGroup) viewGroup.getChildAt(i);

                System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

                for(int k = 0; k < childViewGroup.getChildCount(); k++)
                {
                    ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                    System.out.println("text: " + childViewG1.toString());

                    for(int m = 0; m < childViewG1.getChildCount(); m++) {
                        View childView = (View) childViewG1.getChildAt(m);

                        if (childView instanceof TextView && !(childView instanceof EditText))
                        {
                            String name = ((TextView) childView).getText().toString();

                            System.out.println("Course names in for: " + name);
                            if(!name.equals(""))
                                courseNames.add(name);
                        }

                        if (childView instanceof EditText)
                        {
                            String creditString = ((EditText) childView).getText().toString();
                            int credit = 0;
                            if(!creditString.equals(""))
                            {
                                credit = Integer.parseInt(creditString);
                                courseCredits.add(credit);
                            }
                            else
                                courseCredits.add(null);

                            System.out.println("Course credits in for: " + credit);
                        }
                    }
                }
            }

            System.out.println("courseNames.size - remove - end - before save: " + courseNames.size());
            System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

            saveData();
            loadData();

            System.out.println("courseNames.size - remove - end - after load : " + courseNames.size());
            System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());
        }
    };

    public void addCourse(View view)
    {
        if(numberOfCourses == 8)
        {
            message.cancel();
            message = Toast.makeText(MainActivity.this, "The maximum number of course you can have is 8!!!", Toast.LENGTH_SHORT);
            message.show();
            return;
        }
        courseName = "New Course";
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
        View child = getLayoutInflater().inflate(R.layout.new_course, null);
        layout.addView(child);
        TextView neww = child.findViewById(R.id.new_course);
        neww.setTextSize(17);
        neww.setOnLongClickListener(editName);
        courseNames.add(courseName);
        numberOfCourses++;

        saveData();
        loadData();

        Spinner spinner = (Spinner) child.findViewById(R.id.new_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.letterGrades, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void saveData()
    {
        SharedPreferences names = getSharedPreferences("courseNames", MODE_PRIVATE);
        SharedPreferences.Editor editorForNames = names.edit();
        Gson gsonForNames = new Gson();
        String jsonForNames = gsonForNames.toJson(courseNames);
        editorForNames.putString("Course Name List", jsonForNames);
        editorForNames.apply();

        SharedPreferences credits = getSharedPreferences("courseCredits", MODE_PRIVATE);
        SharedPreferences.Editor editorForCredits = credits.edit();
        Gson gsonForCredits = new Gson();
        String jsonForCredits = gsonForCredits.toJson(courseCredits);
        editorForCredits.putString("Course Credit List", jsonForCredits);
        editorForCredits.apply();
    }

    public void loadData()
    {
        courseNames = new ArrayList<>();
        SharedPreferences names = getSharedPreferences("courseNames", MODE_PRIVATE);
        Gson gsonForNames = new Gson();
        String jsonForNames = names.getString("Course Name List", null);
        Type typeOfNames = new TypeToken<ArrayList<String>>(){}.getType();
        courseNames = gsonForNames.fromJson(jsonForNames, typeOfNames);

        if(courseNames == null)
        {
            courseNames = new ArrayList<>();
        }

        courseCredits = new ArrayList<>();
        SharedPreferences credits = getSharedPreferences("courseCredits", MODE_PRIVATE);
        Gson gsonForCredits = new Gson();
        String jsonForCredits = credits.getString("Course Credit List", null);
        Type typeOfCredits = new TypeToken<ArrayList<Integer>>(){}.getType();
        courseCredits = gsonForCredits.fromJson(jsonForCredits, typeOfCredits);

        if(courseCredits == null)
        {
            courseCredits = new ArrayList<>();
        }

    }

    public void remove(View view)
    {
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
        layout.removeView(((ViewGroup) view.getParent().getParent()));
        ((ViewGroup) view.getParent()).removeAllViews();

        numberOfCourses--;

        courseNames = new ArrayList<String>();
        courseCredits = new ArrayList<Integer>();

        System.out.println("courseNames.size - remove - start : " + courseNames.size());

        ViewGroup viewGroup = (ViewGroup)findViewById(R.id.linearLayout);

        for (int i = 0; i < viewGroup.getChildCount(); i++)
        {
            ViewGroup childViewGroup = (ViewGroup) viewGroup.getChildAt(i);

            System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

            for(int k = 0; k < childViewGroup.getChildCount(); k++)
            {
                ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                System.out.println("text: " + childViewG1.toString());

                for(int m = 0; m < childViewG1.getChildCount(); m++) {
                    View childView = (View) childViewG1.getChildAt(m);
                    if (childView instanceof TextView && !(childView instanceof EditText))
                    {
                        String name = ((TextView) childView).getText().toString();

                        System.out.println("Course names in for: " + name);
                        if(!name.equals(""))
                            courseNames.add(name);
                    }

                    if (childView instanceof EditText)
                    {
                        String creditString = ((EditText) childView).getText().toString();
                        int credit = 0;
                        if(!creditString.equals(""))
                        {
                            credit = Integer.parseInt(creditString);
                            courseCredits.add(credit);
                        }
                        else
                            courseCredits.add(null);

                        System.out.println("Course credits in for: " + credit);
                    }
                }
            }
        }

        System.out.println("courseNames.size - remove - end - before save: " + courseNames.size());
        System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

        saveData();
        loadData();

        System.out.println("courseNames.size - remove - end - after load : " + courseNames.size());
        System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

    }

    public void calculate(View view)
    {
        int sumOfCredits = 0;
        double sumOfGrades = 0;
        int cc = 0;
        double GPA;

        System.out.println("GPA: dsdsdsds");


        ViewGroup viewGroup = (ViewGroup)findViewById(R.id.linearLayout);

        System.out.println("ChildCount: " + viewGroup.getChildCount());

        for (int i = 0; i < viewGroup.getChildCount(); i++)
        {
            System.out.println("GPA: uyyuyuyi");
            ViewGroup childViewGroup = (ViewGroup) viewGroup.getChildAt(i);

            System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

            for(int k = 0; k < childViewGroup.getChildCount(); k++)
            {
                ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                System.out.println("text: " + childViewG1.toString());

                for(int m = 0; m < childViewG1.getChildCount(); m++) {
                    View childView = (View) childViewG1.getChildAt(m);
                    if (childView instanceof EditText) {
                        String credit = ((EditText) childView).getText().toString();
                        System.out.println("Credit: " + credit);

                        if(credit.equals(""))
                        {
                            message.cancel();
                            message = Toast.makeText(MainActivity.this, "Please, enter the credit of course.", Toast.LENGTH_LONG);
                            message.show();
                            return;
                        }

                        cc = Integer.parseInt(credit);
                        if (cc > 10 || cc == 0) {
                            message.cancel();
                            message = Toast.makeText(MainActivity.this, "The maximum 'Credit' you can enter is 10!!!", Toast.LENGTH_LONG);
                            message.show();
                            return;
                        } else {
                            sumOfCredits += cc;
                        }
                    } else if (childView instanceof Spinner) {
                        String letter = ((Spinner) childView).getSelectedItem().toString();
                        System.out.println("Letter: " + letter);
                        if (letter.equals("-")) {
                            message.cancel();
                            message = Toast.makeText(MainActivity.this, "Please select a letter grade!", Toast.LENGTH_LONG);
                            message.show();
                            return;
                        } else {

                            if (letter.equals("A")) {
                                sumOfGrades += cc * 4.0;
                            } else if (letter.equals("A-")) {
                                sumOfGrades += cc * 3.7;
                            } else if (letter.equals("B+")) {
                                sumOfGrades += cc * 3.3;
                            } else if (letter.equals("B")) {
                                sumOfGrades += cc * 3.0;
                            } else if (letter.equals("B-")) {
                                sumOfGrades += cc * 2.7;
                            } else if (letter.equals("C+")) {
                                sumOfGrades += cc * 2.3;
                            } else if (letter.equals("C")) {
                                sumOfGrades += cc * 2;
                            } else if (letter.equals("C-")) {
                                sumOfGrades += cc * 1.7;
                            } else if (letter.equals("D+")) {
                                sumOfGrades += cc * 1.3;
                            } else if (letter.equals("D")) {
                                sumOfGrades += cc * 1;
                            } else if (letter.equals("F")) {
                                sumOfGrades += cc * 0;
                            }

                        }

                    }
                }
            }
        }

        GPA = sumOfGrades / sumOfCredits;

        System.out.println("GPA: " + GPA);

        TextView GPAText = findViewById(R.id.GPA);

        String gpa = String.format("%.2f", GPA);

        GPAText.setText("GPA : " + gpa);
        GPAText.setTextSize(30);

        courseNames = new ArrayList<String>();
        courseCredits = new ArrayList<Integer>();

        ViewGroup viewGroups = (ViewGroup)findViewById(R.id.linearLayout);

        for (int i = 0; i < viewGroups.getChildCount(); i++)
        {
            ViewGroup childViewGroup = (ViewGroup) viewGroups.getChildAt(i);

            System.out.println("ChildCountChild: " + childViewGroup.getChildCount());

            for(int k = 0; k < childViewGroup.getChildCount(); k++)
            {
                ViewGroup childViewG1 = (ViewGroup) childViewGroup.getChildAt(k);

                System.out.println("text: " + childViewG1.toString());

                for(int m = 0; m < childViewG1.getChildCount(); m++) {
                    View childView = (View) childViewG1.getChildAt(m);

                    if (childView instanceof TextView && !(childView instanceof EditText))
                    {
                        String name = ((TextView) childView).getText().toString();

                        System.out.println("Course names in for: " + name);
                        if(!name.equals(""))
                            courseNames.add(name);
                    }

                    if (childView instanceof EditText)
                    {
                        String creditString = ((EditText) childView).getText().toString();
                        int credit = 0;
                        if(!creditString.equals(""))
                        {
                            credit = Integer.parseInt(creditString);
                            courseCredits.add(credit);
                        }
                        else
                            courseCredits.add(null);

                        System.out.println("Course credits in for: " + credit);
                    }
                }
            }
        }

        System.out.println("courseNames.size - remove - end - before save: " + courseNames.size());
        System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

        saveData();
        loadData();

        System.out.println("courseNames.size - remove - end - after load : " + courseNames.size());
        System.out.println("courseCredits.size - remove - end - before save: " + courseCredits.size());

    }
}
