package view;

import java.io.File;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.DetectionResult;
import model.Label;

/**
 * Enhanced Swing-based GUI implementation of the View interface (Pro Version).
 * 
 * <p>This view renders detection results in a modern, visually appealing graphical user interface
 * using Java Swing. Features include:
 * <ul>
 *   <li>Modern color scheme and styling</li>
 *   <li>Card-based layout for labels</li>
 *   <li>Visual confidence indicators with progress bars</li>
 *   <li>Rounded corners and shadows</li>
 *   <li>Improved typography and spacing</li>
 *   <li>Labels sorted by confidence</li>
 * </ul>
 * 
 * @author VisionTagger Team
 * @version 2.0
 */
public class SwingViewPro implements View {

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(66, 133, 244); // Google Blue
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94); // Dark Blue-Gray
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light Gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color ACCENT_COLOR = new Color(76, 175, 80); // Green

    @Override
    public void display(DetectionResult result) {
        JFrame frame = new JFrame("VisionTagger Pro - Detection Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(0, 0));

        // Main container with background color
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Image Panel with modern styling
        JPanel imagePanel = createImagePanel(result);
        mainPanel.add(imagePanel, BorderLayout.CENTER);

        // Labels Panel with cards
        JPanel labelsPanel = createLabelsPanel(result);
        JScrollPane scrollPane = new JScrollPane(labelsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    /**
     * Creates a styled header panel with title.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JLabel titleLabel = new JLabel("VisionTagger Detection Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SECONDARY_COLOR);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    /**
     * Creates a styled image panel with rounded corners effect.
     */
    private JPanel createImagePanel(DetectionResult result) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(BACKGROUND_COLOR);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JPanel imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.dispose();
            }
        };
        imageContainer.setOpaque(false);
        imageContainer.setPreferredSize(new Dimension(500, 400));
        imageContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        File imgFile = new File(result.getImage().getStoragePath());
        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        imageLabel.setForeground(TEXT_COLOR);

        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(imgFile.getPath());
            Image originalImage = icon.getImage();
            
            // Calculate scaled dimensions maintaining aspect ratio
            int maxWidth = 450;
            int maxHeight = 350;
            int originalWidth = icon.getIconWidth();
            int originalHeight = icon.getIconHeight();
            
            double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
            int scaledWidth = (int) (originalWidth * scale);
            int scaledHeight = (int) (originalHeight * scale);
            
            Image scaled = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } else {
            imageLabel.setText("<html><div style='text-align: center; padding: 20px;'>" +
                             "Image not found<br><br>" +
                             "<span style='color: #999; font-size: 12px;'>" +
                             result.getImage().getStoragePath() + "</span></div></html>");
        }

        imageContainer.add(imageLabel);
        imagePanel.add(imageContainer, BorderLayout.CENTER);

        return imagePanel;
    }

    /**
     * Creates a panel with label cards showing confidence scores.
     */
    private JPanel createLabelsPanel(DetectionResult result) {
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setBackground(BACKGROUND_COLOR);
        labelsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Section header
        JLabel sectionLabel = new JLabel("Detected Labels");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionLabel.setForeground(SECONDARY_COLOR);
        sectionLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        labelsPanel.add(sectionLabel);
        labelsPanel.add(Box.createVerticalStrut(5));

        if (result.getLabels().isEmpty()) {
            JLabel noLabelsLabel = new JLabel("<html><div style='text-align: center; color: #999; padding: 20px;'>" +
                                            "No labels detected</div></html>");
            noLabelsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            labelsPanel.add(noLabelsLabel);
        } else {
            // Sort labels by confidence (highest first)
            java.util.List<Label> sortedLabels = new java.util.ArrayList<>(result.getLabels());
            sortedLabels.sort((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()));

            for (Label label : sortedLabels) {
                JPanel labelCard = createLabelCard(label);
                labelsPanel.add(labelCard);
                labelsPanel.add(Box.createVerticalStrut(10));
            }
        }

        return labelsPanel;
    }

    /**
     * Creates a modern card component for a single label with confidence visualization.
     */
    private JPanel createLabelCard(Label label) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Label name
        JLabel nameLabel = new JLabel(label.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        card.add(nameLabel, BorderLayout.WEST);

        // Confidence panel
        JPanel confidencePanel = new JPanel(new BorderLayout(10, 5));
        confidencePanel.setOpaque(false);

        // Confidence percentage
        JLabel confidenceLabel = new JLabel(String.format("%.1f%%", label.getConfidence()));
        confidenceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confidenceLabel.setForeground(PRIMARY_COLOR);
        confidenceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        confidencePanel.add(confidenceLabel, BorderLayout.EAST);

        // Progress bar for confidence
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) Math.round(label.getConfidence()));
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(200, 8));
        progressBar.setMaximumSize(new Dimension(200, 8));
        
        // Color based on confidence
        Color barColor;
        if (label.getConfidence() >= 80) {
            barColor = ACCENT_COLOR; // Green for high confidence
        } else if (label.getConfidence() >= 50) {
            barColor = PRIMARY_COLOR; // Blue for medium confidence
        } else {
            barColor = new Color(255, 152, 0); // Orange for low confidence
        }
        progressBar.setForeground(barColor);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        
        confidencePanel.add(progressBar, BorderLayout.CENTER);
        card.add(confidencePanel, BorderLayout.EAST);

        return card;
    }
}

