/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.atr.dni.bmi.desktop.model.utils;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author kharada
 */
public class IntegerInputVerifierForJTextField extends InputVerifier {

    // This class verifies inputs of a JTextField.
    @Override
    public boolean verify(JComponent input) {
        boolean verified = false;
        JTextField tf = (JTextField) input;
        try {
            Integer.parseInt(tf.getText());
            verified = true;
        } catch (NumberFormatException numberFormatException) {

            JOptionPane.showMessageDialog(null, "The value is not correct. Input a correct value.\nCorrect value : Integer between -2147483648 and 2147483647  ", "Input Value Error", JOptionPane.ERROR_MESSAGE);
            UIManager.getLookAndFeel().provideErrorFeedback(input);
        }
        return verified;
    }
}
