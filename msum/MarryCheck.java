package msum;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.List;
import java.io.File;

class Gui {
    JFrame frame;
    JTable table;
    DefaultTableModel tableModel;
    String name = "PS4 PKG Compatibility Checker";
    String version = "v1.00";
    String about = "<html><body>" + name + " " + version
        + "<br>Copyright (c) 2023 hippie68<br><br>"
        + "This program takes PS4 game and update PKG files and prints checksums.<br>"
        + "If checksums match, the PKG files are compatible with each other (\"married\").<br>"
        + "The program can also be run on partially downloaded files.<br><br>"
        + "Close this message, then drag and drop PKG files into the program window (or select \"File - Add...\" from the menu).<br><br>"
        + "Get the latest version at <a href='https://github.com/hippie68/ps4-pkg-compatibility-checker'>https://github.com/hippie68/ps4-pkg-compatibility-checker</a>.<br>"
        + "Report bugs and request features at <a href='https://github.com/hippie68/ps4-pkg-compatibility-checker/issues'>https://github.com/hippie68/ps4-pkg-compatibility-checker/issues</a>.</body></html>";

    class ChecksumThread extends SwingWorker<Void, Void> {
        private File file;
        private int row;
        private String checksum;

        public ChecksumThread(File file, int row) {
            this.file = file;
            this.row = row;
        }

        protected Void doInBackground() {
            checksum = MarrySum.getChecksum(file);
            return null;
        }

        protected void done() {
            table.setValueAt(checksum, row - 1, 1);
        }
    }

    private JMenuBar createJMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menuItem = new JMenuItem(new AbstractAction("Add...") {
                public synchronized void actionPerformed(ActionEvent e) {
                    var fileChooser = new JFileChooser();
                    fileChooser.setMultiSelectionEnabled(true);
                    int result = fileChooser.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File[] files = fileChooser.getSelectedFiles();
                        for (File file : files) {
                            tableModel.addRow(new String[] { file.getName() });
                            (new ChecksumThread(file, table.getRowCount())).execute();
                        }
                    }
                }
            });
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem(new AbstractAction("Quit") {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu("Help");
        menuItem = new JMenuItem(new AbstractAction("About") {
                public void actionPerformed(ActionEvent e) {
                    JEditorPane editorPane = new JEditorPane("text/html", about);
                    editorPane.addHyperlinkListener(new HyperlinkListener() {
                            @Override
                            public void hyperlinkUpdate(HyperlinkEvent evt) {
                                if (evt.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                                    try {
                                        Desktop.getDesktop().browse(evt.getURL().toURI());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                            }
                        });
                    editorPane.setEditable(false);
                    editorPane.setBackground(frame.getBackground());
                    JOptionPane.showMessageDialog(null, editorPane, "About", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        menu.add(menuItem);
        menuBar.add(menu);

        return menuBar;
    }

    Gui() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int) (screenSize.getWidth() / 3), (int) (screenSize.getHeight() / 3));
        frame.setLocationRelativeTo(null);
        frame.setTitle(name);
        frame.setJMenuBar(createJMenuBar());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, table.getFont().getSize());
        table.setFont(font);
        tableModel.addColumn("File name");
        tableModel.addColumn("Checksum");

        frame.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        tableModel.addRow(new String[] { file.getName() });
                        (new ChecksumThread(file, table.getRowCount())).execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);

        frame.setVisible(true);
    }
}

public class MarryCheck {
    public static void main(String[] args) {
        try {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows"))
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            else if (os.equals("Linux"))
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            else
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Gui();
            }
        });
    }
}
