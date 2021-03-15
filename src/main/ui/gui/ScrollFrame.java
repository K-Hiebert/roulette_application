package ui.gui;

import javax.swing.*;
import java.awt.*;

// represents frame class that has scroll feature
public class ScrollFrame extends JFrame {

    JScrollPane scrollPane;

    // MODIFIES: this
    // EFFECTS: creates frame that can be scrolled vertically, and displays input text
    public ScrollFrame(JTextArea textArea, String title) {
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new FlowLayout());
        add(scrollPane);
        setSize(1200, 1200);
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle(title);
    }
}
