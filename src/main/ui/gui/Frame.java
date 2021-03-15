package ui.gui;

import javax.swing.*;

// represents general frame class with specifications
public class Frame extends JFrame {

    // MODIFIES: this
    // EFFECTS: creates frame based on title, width, and height that has not Layout
    Frame(String title, int width, int height) {
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);
        this.setSize(width,height);
        this.setVisible(true);
        this.setLayout(null);
    }
}
