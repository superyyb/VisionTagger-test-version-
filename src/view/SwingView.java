package view;

import java.io.File;
import java.awt.Image;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import model.DetectionResult;

public class SwingView implements View{

    @Override
    public void display(DetectionResult result) {
        JFrame frame = new JFrame("VisionTagger Detection Result");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        // Image Panel
        File imgFile = new File(result.getImage().getStoragePath());
        JLabel imageLabel = new JLabel();
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(imgFile.getPath());
            Image scaled = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setText("Image not found: " + result.getImage().getStoragePath());
        }
        

        // Labels Panel
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        StringBuilder sb = new StringBuilder("Detected Labels:\n");
        for (Label label : result.getLabels()) {
            sb.append(String.format(" - %s (%.2f%%)\n", label.getName(), label.getConfidence()));
        }
        textArea.setText(sb.toString());

        // Layout
        frame.add(imageLabel, BorderLayout.CENTER);
        frame.add(new JScrollPane(textArea), BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
