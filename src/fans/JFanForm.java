package fans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

/*
 * Authors : Aden Downey down0100, Daniel Wilson wils0496
 */
public class JFanForm extends javax.swing.JFrame {

    /**
     * Creates new form JFanForm
     */
    public JFanForm() {
        initComponents();
        // Assigns a new gridLayout for the constructed form
        setLayout(new GridLayout());

        // Makes the menu for selecting the color scheme
        JMenuBar menus = new JMenuBar();
        this.setJMenuBar(menus);
        menus.add(makeFileMenu());

        startButton = new JButton("Start");
        SpinnerModel sm = new SpinnerNumberModel(0, 0, 6, 1);
        startSpinner = new JSpinner(sm);
        startButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                // Adds initial fan panels based on user input into the jSpinner
                int c = (int) startSpinner.getValue();
                for (int i = 0; i < c; i++) {
                    setTheme(0);
                    remove(startButton);
                    remove(startSpinner);

                    add(buttons);
                    add(fans);
                    add(sliders);

                    addFanAndControls();
                    pack();
                    okToAdd = true;
                }
            }
        });
        this.add(startButton);
        this.add(startSpinner);
        pack();
        setResizable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 334, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 231, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addFanAndControls() {
        if (numPanels < 6) {
            // Increase number of panels in the frame, up to a limit of 6 at a time
            numPanels++;

            // Create a new fan panel
            FanPanel fanPanel = new FanPanel();
            fanPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            // Set the panel layout to FlowLayout
            fanPanel.add(new JLabel(String.valueOf(numPanels)));
            fans.add(fanPanel);
            // Adds the new panel to an array of fanPanels
            fanArray.add(fanPanel);

            // Create a new timer for each new fan panel, ensuring independent action
            Timer timer = new Timer(10, fanPanel);
            timerArray.add(timer);
            // Start timer at creation
            timer.start();
            // Initialize fan speed to zero
            fanPanel.startSlowingSpeeding(0);

            // Panel for the fan slider to control fan speed
            JPanel sliderPanel = new JPanel(new BorderLayout());
            sliderPanel.setBorder(new EtchedBorder());
            sliderPanel.add(new JLabel(String.valueOf(numPanels)), BorderLayout.NORTH);
            JSlider slider = initialiseSlider(fanPanel);

            slider.setSize(new Dimension(slider.getWidth(), sliderPanel.getHeight()));
            sliderPanel.add(slider, BorderLayout.SOUTH);
            sliders.add(sliderPanel);

            // Panel to hold buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(new TitledBorder(String.valueOf(numPanels)));
            // Separate panels to make layout aesthetics easier
            buttonPanel.setLayout(new java.awt.GridLayout(4, 1));

            // Create each new button
            JButton on = new JButton("ON");
            on.addMouseListener(new java.awt.event.MouseAdapter() {
                // Adds event listener for mouse click
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    fanPanel.start(slider.getValue());
                }
            });
            JButton off = new JButton("OFF");
            off.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    fanPanel.startSlowingSpeeding(0);
                }

            });
            // Creates REVERSE button to reverse fan direction
            JButton reverse = new JButton("REVERSE");
            reverse.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    fanPanel.reverse();
                }
            });
            // Creates REPAINT button for changing color theme
            JButton repaint = new JButton("REPAINT");
            repaint.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    fanPanel.setColourTheme(fanTheme);
                    setComponentThemes(fanTheme, fanPanel, buttonPanel, sliderPanel);
                    repaint();
                }
            });
            buttonPanel.setSize(on.getPreferredSize().width, 200);
            sliderPanel.setSize(slider.getPreferredSize().width, 200);
            // Add each button to the buttonPanel
            buttonPanel.add(on);
            buttonPanel.add(off);
            buttonPanel.add(reverse);
            buttonPanel.add(repaint);
            // Add buttonPanel to the container
            buttons.add(buttonPanel);

            globalActionEvents(fanPanel, buttonPanel, sliderPanel);

            setComponentThemes(fanTheme, fanPanel, buttonPanel, sliderPanel);
        } else {
            // Error message if user inputs too many initial fans
            JOptionPane.showMessageDialog(this, "Too many panels");
        }
    }

    private void removePanel() {
        // Handles the removal of fan panels and their components
        if (numPanels > 0) {
            numPanels--;
            FanPanel p = fanArray.remove(numPanels);
            buttons.remove(numPanels);
            fans.remove(numPanels);
            sliders.remove(numPanels);
            repaint();
            pack();
        }
    }

    private void removeAllPanels() {
        // Removes all panels from the frame
        while (fanArray.size() > 0) {
            removePanel();
        }
    }

    private void globalActionEvents(FanPanel fanPanel, JPanel buttonPanel, JPanel sliderPanel) {
        // Global event handlers for the buttons at the top of the window(global controls)
        globalStart.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                fanPanel.start(globalSlider.getValue());
            }
        });
        globalStop.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                fanPanel.startSlowingSpeeding(0);
            }
        });
        globalReverse.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                fanPanel.reverse();
            }
        });
        globalRepaint.addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                fanPanel.setColourTheme(fanTheme);
                setComponentThemes(fanTheme, fanPanel, buttonPanel, sliderPanel);
                repaint();
            }
        });
        globalSlider.addChangeListener(new ChangeListener() {
            // Handles changes to the global speed slider
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = globalSlider.getValue();
                fanPanel.startSlowingSpeeding(value);
            }
        });
    }

    private JSlider initialiseSlider(FanPanel fanPanel) {
        // Initialises the functionality of fan sliders of the individual fan panels 
        JSlider slider = new JSlider(SwingConstants.VERTICAL);
        slider.setMaximum(10);
        slider.setMinimum(0);
        slider.setMajorTickSpacing(1);
        slider.setValue(5);
        fanPanel.startSlowingSpeeding(slider.getValue());

        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                fanPanel.startSlowingSpeeding(value);
            }
        });

        return slider;
    }

    private void setTheme(int n) {
        // Handles the themes of the individual fan panels
        switch (n) {
            case 0:
                fanTheme = FanPanel.ColourTheme.BlackAndWhite;
                break;
            case 1:
                fanTheme = FanPanel.ColourTheme.GreenMachine;
                break;
            case 2:
                fanTheme = FanPanel.ColourTheme.PeppermintBaby;
                break;
            case 3:
                fanTheme = FanPanel.ColourTheme.NeonDemon;
                break;
            case 4:
                fanTheme = FanPanel.ColourTheme.SimplyRed;
                break;
        }
    }

    private void setComponentThemes(FanPanel.ColourTheme theme, FanPanel fanPanel, JPanel buttonPanel, JPanel sliderPanel) {
        fanPanel.setColourTheme(theme);
        switch (theme) {
            case BlackAndWhite:
                for (Component b : buttonPanel.getComponents()) {
                    b.setBackground(new JButton().getBackground());
                    b.setForeground(Color.BLACK);
                }
                buttonPanel.setBackground(new JPanel().getBackground());
                sliderPanel.setBackground(new JPanel().getBackground());
                break;
            case GreenMachine:
                for (Component b : buttonPanel.getComponents()) {
                    b.setBackground(new Color(186, 255, 133));
                    b.setForeground(new Color(116, 153, 46));
                }
                buttonPanel.setBackground(Color.GREEN);
                sliderPanel.setBackground(Color.GREEN);
                break;
            case NeonDemon:
                for (Component b : buttonPanel.getComponents()) {
                    b.setBackground(Color.BLACK);
                    b.setForeground(Color.WHITE);
                }
                buttonPanel.setBackground(Color.MAGENTA);
                sliderPanel.setBackground(Color.MAGENTA);
                break;
            case PeppermintBaby:
                for (Component b : buttonPanel.getComponents()) {
                    b.setBackground(Color.WHITE);
                    b.setForeground(Color.BLACK);
                }
                buttonPanel.setBackground(Color.PINK);
                sliderPanel.setBackground(Color.PINK);
                break;
            case SimplyRed:
                for (Component b : buttonPanel.getComponents()) {
                    b.setBackground(Color.WHITE);
                    b.setForeground(Color.BLACK);
                }
                buttonPanel.setBackground(Color.RED);
                sliderPanel.setBackground(Color.RED);
                break;
        }
    }

    private void writeState() {
        // Handles the saving of fan panel color scheme data to a text file for later loading
        if (numPanels > 0) {
            try {
                // Prompt user for file name to save
                String fileName = JOptionPane.showInputDialog("Please enter a filename to write to");
                File file = new File(fileName);
                file.createNewFile();

                FileWriter fw = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fw);

                // Writes each color theme of each individual fan panel, in order,  
                // to a file using "|" as a delimiter between different fan panels
                System.out.println("Writing to: " + fileName);
                String delimiter = "|";
                for (FanPanel p : fanArray) {
                    String line = p.toString();
                    System.out.println("\t-" + line);
                    line += delimiter;
                    writer.write(line);
                }
                writer.close();
            } catch (Exception ex) {
                // Error message for write error
                JOptionPane.showMessageDialog(this, "Error writing to file.");
            }
        } else {
            // Error message in case user tries to save with no panels to save
            JOptionPane.showMessageDialog(this, "No panels to write.");
        }
    }

    private void readState() {
        // Handles the loading of fan panel data stored in a text file from a previous save 
        FileReader fr = null;
        try {
            // Prompts user for the file name of the theme data
            String fileName = JOptionPane.showInputDialog("Please enter a filename to load");
            File file = new File(fileName);
            fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);

            // Prepares the frame for new fan panels
            if (!okToAdd) {
                // Removes the initial start button and slider if they are present
                // as we are loading a pre existing configuration
                remove(startButton);
                remove(startSpinner);

                add(buttons);
                add(fans);
                add(sliders);

                okToAdd = true;
            } else {
                // If there is an existing configuration, clear it to prepare 
                // for new configuration being loaded
                removeAllPanels();
            }

            String line = reader.readLine();
            // Double escape character '\' as '|' is a regex operator
            String themes[] = line.split("\\|");
            for (String s : themes) {
                // Retrieve the integer value of the theme
                int theme = FanPanel.ColourTheme.valueOf(s).ordinal();
                setTheme(theme);
                addFanAndControls();
            }
            pack();
            repaint();
            setTheme(0);
        } catch (Exception ex) {
            // Error message for read error
            JOptionPane.showMessageDialog(this, "Error reading from file");
        } finally {
            try {
                fr.close();
            } catch (Exception ex) {

            }
        }
    }

    private JComponent makeFileMenu() {
        // Creates the menu for selection of color theme
        JMenu themeMenu = new JMenu("Themes");
        menuBar.add(themeMenu);

        for (FanPanel.ColourTheme c : FanPanel.ColourTheme.values()) {
            JMenuItem jmi = new JMenuItem(c.toString());
            themeMenu.add(jmi);
            jmi.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    fanTheme = c;
                }
            });
        }

        // Create new button for creating a new window
        JButton newWindow = new JButton("NEW WINDOW");
        newWindow.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JFanForm newFrame = new JFanForm();
                windowArray.add(newFrame);
                // Handles the prompt to save on exit and invokes writeState if the user chooses "yes"
                newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                newFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        // Adds a new windowEvent listener to activate on closing the window
                        String ObjButtons[] = {"Yes", "No"};
                        // Prompt the user to save on closing the window
                        int PromptResult = JOptionPane.showOptionDialog(null, "Do you want to save?", "Save Theme ", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[0]);
                        if (PromptResult == JOptionPane.YES_OPTION) {
                            newFrame.writeState();
                            windowArray.remove(newFrame);
                            if (windowArray.isEmpty()) {
                                System.exit(0);
                            }
                        } else if (PromptResult == JOptionPane.NO_OPTION) {
                            windowArray.remove(newFrame);
                            if (windowArray.isEmpty()) {
                                System.exit(0);
                            }

                        }
                    }
                });
                newFrame.setVisible(true);
            }
        });
        menuBar.add(newWindow);

        // Creates button to handle the adding of new fan panels to the frame
        JButton addPanel = new JButton("ADD");
        addPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (okToAdd) {
                    // Checks if there is an initial fan panel to determine whether 
                    // to allow the user to add new fan panels to the frame
                    addFanAndControls();
                    repaint();
                    pack();
                }
            }
        });
        menuBar.add(addPanel);

        // Creates button to handle the removal of new fan panels to the frame
        JButton removeButton = new JButton("REMOVE");
        removeButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (okToAdd) {
                    // Checks if there is an initial fan panel to determine whether 
                    // to allow the user to remove fan panels from the frame
                    removePanel();
                }
            }
        });
        menuBar.add(removeButton);

        // Add new instances of buttons for the new windows being created
        globalStart = new JButton("START");
        menuBar.add(globalStart);
        globalStop = new JButton("STOP");
        menuBar.add(globalStop);
        globalReverse = new JButton("REVERSE");
        menuBar.add(globalReverse);
        globalRepaint = new JButton("REPAINT");
        menuBar.add(globalRepaint);

        globalSlider = new JSlider(0, 10);
        globalSlider.setOrientation(SwingConstants.HORIZONTAL);
        menuBar.add(globalSlider);

        JButton save = new JButton("SAVE STATE");
        save.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                writeState();
            }
        });
        menuBar.add(save);

        JButton load = new JButton("LOAD STATE");
        load.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                readState();
            }
        });
        menuBar.add(load);

        return menuBar;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFanForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFanForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFanForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFanForm.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                firstWindow = new JFanForm();
                // Add created window to an array to facilitate saving all instances
                windowArray.add(firstWindow);
                firstWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                // Handles the prompt to save on exit and invokes writeState if the user chooses "yes"
                firstWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent we) {
                        // Adds a new windowEvent listener to activate on closing the window
                        String ObjButtons[] = {"Yes", "No"};
                        // Prompt the user to save on closing the window
                        int PromptResult = JOptionPane.showOptionDialog(null, "Do you want to save?", "Save Theme", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, ObjButtons, ObjButtons[0]);
                        if (PromptResult == JOptionPane.YES_OPTION) {
                            firstWindow.writeState();
                            windowArray.remove(firstWindow);
                            if (windowArray.isEmpty()) {
                                System.exit(0);
                            }
                        } else if (PromptResult == JOptionPane.NO_OPTION) {
                            windowArray.remove(firstWindow);
                            if (windowArray.isEmpty()) {
                                System.exit(0);
                            }
                        }
                    }
                });
                firstWindow.setVisible(true);
            }
        });
    }

    private static JFanForm firstWindow;

    int numPanels = 0;
    private boolean okToAdd = false;

    JPanel fans = new JPanel(new GridLayout(0, 3));
    JPanel buttons = new JPanel(new GridLayout(0, 3));
    JPanel sliders = new JPanel(new GridLayout(0, 3));

    static ArrayList<JFanForm> windowArray = new ArrayList<>();
    ArrayList<FanPanel> fanArray = new ArrayList<>();
    ArrayList<Timer> timerArray = new ArrayList<>();

    JButton startButton;
    JSpinner startSpinner;

    JButton globalStart;
    JButton globalStop;
    JButton globalReverse;
    JButton globalRepaint;
    JSlider globalSlider;

    JMenuBar menuBar = new JMenuBar();

    FanPanel.ColourTheme fanTheme = FanPanel.ColourTheme.BlackAndWhite;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
