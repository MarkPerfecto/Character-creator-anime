package finalsOOP;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage; // Add this import for BufferedImage
import javax.imageio.ImageIO; // Add this import for ImageIO
import java.io.File;
import java.io.IOException;


public class CharacterCreator extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private ImagePanel imagePanel;
    private JButton nextButton;
    private JButton prevButton;
    private JButton saveButton; // Button for saving the image
    private JLabel characterLabel;
    private JLabel menuLabel;
    private JPanel controlPanel;
    private JPanel topPanel;

    private ImageIcon[] characterImages;
    private int currentIndex;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CharacterCreator frame = new CharacterCreator();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public CharacterCreator() {
        setTitle("Character Creator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        characterImages = new ImageIcon[4];
        for (int i = 0; i < 4; i++) {
            characterImages[i] = new ImageIcon(getClass().getResource("girl" + (i + 1) + ".png"));
        }
        currentIndex = 0;

        imagePanel = new ImagePanel();
        imagePanel.setPreferredSize(new Dimension(100, 100)); // Set the preferred size of the image panel
        contentPane.add(imagePanel, BorderLayout.CENTER);

        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(Color.WHITE);
        contentPane.add(topPanel, BorderLayout.NORTH);

        menuLabel = new JLabel("Menu");
        menuLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(menuLabel);

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); // Change to vertical layout
        controlPanel.setBackground(Color.WHITE);
        contentPane.add(controlPanel, BorderLayout.SOUTH);

        controlPanel.add(Box.createVerticalGlue()); // Add glue for centering

        // Create "Prev" button
        prevButton = new JButton("Prev");
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentIndex = (currentIndex - 1 + characterImages.length) % characterImages.length;
                updateCharacterLabel();
                imagePanel.repaint();
            }
        });
        controlPanel.add(prevButton);

        // Create character label
        characterLabel = new JLabel("Character 1");
        controlPanel.add(characterLabel);

        // Create "Next" button
        nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentIndex = (currentIndex + 1) % characterImages.length;
                updateCharacterLabel();
                imagePanel.repaint();
            }
        });
        controlPanel.add(nextButton);

        // Create "Save" button
        saveButton = new JButton("Save as PNG");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveImageAsPNG();
            }
        });
        controlPanel.add(saveButton);

        controlPanel.add(Box.createVerticalGlue()); // Add glue for centering

        updateCharacterLabel();
    }

    private class ImagePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (characterImages[currentIndex] != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                int diameter = Math.min(getWidth(), getHeight()); // Diameter of the circle
                Ellipse2D.Double circle = new Ellipse2D.Double((getWidth() - diameter) / 2, (getHeight() - diameter) / 2, diameter, diameter);
                g2d.setClip(circle);

                Image img = characterImages[currentIndex].getImage();
                int imgWidth = img.getWidth(null);
                int imgHeight = img.getHeight(null);
                double aspectRatio = (double) imgWidth / imgHeight;

                int drawWidth = diameter;
                int drawHeight = diameter;

                // Adjust width or height to maintain aspect ratio
                if (aspectRatio > 1) { // Landscape
                    drawHeight = (int) (diameter / aspectRatio);
                } else { // Portrait or square
                    drawWidth = (int) (diameter * aspectRatio);
                }

                // Draw image centered within the circular clip
                int x = (getWidth() - drawWidth) / 2;
                int y = (getHeight() - drawHeight) / 2;
                g2d.drawImage(img, x, y, drawWidth, drawHeight, null);
                g2d.dispose();
            }
        }
    }

    private void updateCharacterLabel() {
        characterLabel.setText("Character " + (currentIndex + 1));
    }

    // Method to save the current image as a PNG file
    private void saveImageAsPNG() {
        if (characterImages[currentIndex] != null) {
            try {
                // Get the circular bounds
                int diameter = Math.min(imagePanel.getWidth(), imagePanel.getHeight());
                int x = (imagePanel.getWidth() - diameter) / 2;
                int y = (imagePanel.getHeight() - diameter) / 2;

                // Create a BufferedImage with circular dimensions
                BufferedImage image = new BufferedImage(imagePanel.getWidth(), imagePanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();

                // Clip outside the circle
                Shape clip = new Ellipse2D.Double(x, y, diameter, diameter);
                g2d.setClip(clip);

                // Draw the entire panel to capture only the circular area due to clipping
                imagePanel.paint(g2d);

                g2d.dispose();

                // Crop to get only circle part of larger canvas if needed 
                BufferedImage croppedImage = image.getSubimage(x, y, diameter, diameter);

                // Save the circular croppedImage 
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Image");
                fileChooser.setSelectedFile(new File("character_image.png"));
                int userSelection = fileChooser.showSaveDialog(this);
                 
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    ImageIO.write(croppedImage , "png", fileToSave);
                    JOptionPane.showMessageDialog(this, "Image saved successfully!");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No character image to save!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }





}
