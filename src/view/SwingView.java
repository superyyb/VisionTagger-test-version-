package view;

import java.io.File;
import java.awt.Image;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.DetectionResult;
import model.Label;

/**
 * Swing-based GUI implementation of the View interface.
 * 
 * <p>This view renders detection results in a graphical user interface using Java Swing.
 * Displays the image (if found) and a scrollable list of detected labels with their
 * confidence scores in a window.
 * 
 * @author VisionTagger Team
 * @version 1.0
 */
public class SwingView implements View {

    @Override
    public void display(DetectionResult result) {
        JFrame frame = new JFrame("VisionTagger - Detection Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        // Image Panel
        File imgFile = new File(result.getImage().getStoragePath());
        JLabel imageLabel = new JLabel();
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(imgFile.getPath());
            // Scale image to fit width while maintaining aspect ratio
            int maxWidth = 580;
            int maxHeight = 300;
            Image originalImage = icon.getImage();
            int originalWidth = icon.getIconWidth();
            int originalHeight = icon.getIconHeight();
            
            // Calculate scale to fit within max dimensions
            double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);
            
            Image scaled = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setText("Image currently not available: " + result.getImage().getStoragePath());
        }
        

        // Labels Panel
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        StringBuilder sb = new StringBuilder("Detected Labels:\n");
        for (Label label : result.getLabels()) {
            sb.append(String.format(" - %s (%.2f%%)\n", label.getName(), label.getConfidence()));
        }
        textArea.setText(sb.toString());

        // Layout: Image at top (NORTH), Labels at bottom (CENTER takes remaining space)
        frame.add(imageLabel, BorderLayout.NORTH);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
