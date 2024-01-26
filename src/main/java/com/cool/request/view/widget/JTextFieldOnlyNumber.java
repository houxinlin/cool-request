package com.cool.request.view.widget;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

public class JTextFieldOnlyNumber extends JFormattedTextField {
    public static JTextFieldOnlyNumber newJTextFieldOnlyNumber() {
        NumberFormat longFormat = NumberFormat.getIntegerInstance();
        longFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(longFormat);
        numberFormatter.setValueClass(Long.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(0l);
        return new JTextFieldOnlyNumber(numberFormatter);
    }

    public JTextFieldOnlyNumber(NumberFormatter numberFormat) {
        super(numberFormat);
    }

}
