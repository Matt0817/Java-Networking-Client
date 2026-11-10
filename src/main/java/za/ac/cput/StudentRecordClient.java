/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 *222648767
 * @author smitm
 */

public class StudentRecordClient implements ActionListener {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    private JFrame frame;
    private JTextField nameField;
    private JTextField idField;
    private JTextField scoreField;
    private JTextArea recordsTextArea;

    private JButton addButton;
    private JButton retrieveButton;
    private JButton exitButton;

    public StudentRecordClient() {
        connectToServer();
        createAndShowGUI();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 6666);

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected to server.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not connect to server. Start the server first."
            );
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Student Record Management");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(550, 400);
        frame.setLayout(new BorderLayout(5, 5));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        inputPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Student Number:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Student Score:"));
        scoreField = new JTextField();
        inputPanel.add(scoreField);

        frame.add(inputPanel, BorderLayout.NORTH);

        addButton = new JButton("Add Record");
        retrieveButton = new JButton("Retrieve Records");
        exitButton = new JButton("Exit");

        addButton.addActionListener(this);
        retrieveButton.addActionListener(this);
        exitButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(addButton);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        recordsTextArea = new JTextArea(10, 40);
        recordsTextArea.setEditable(false);

        frame.add(
                new JScrollPane(recordsTextArea),
                BorderLayout.SOUTH
        );

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeConnection();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addStudentRecord() {
        try {
            if (out == null || in == null) {
                JOptionPane.showMessageDialog(
                        frame, "Not connected to the server."
                );
                return;
            }

            String name = nameField.getText().trim();
            String id = idField.getText().trim();
            String scoreText = scoreField.getText().trim();

            if (name.isEmpty() || id.isEmpty() || scoreText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame, "Please complete all fields."
                );
                return;
            }

            double score = Double.parseDouble(scoreText);

            Student student = new Student(name, id, score);

            out.writeObject("ADD");
            out.flush();

            out.writeObject(student);
            out.flush();

            String response = (String) in.readObject();

            JOptionPane.showMessageDialog(frame, response);

            nameField.setText("");
            idField.setText("");
            scoreField.setText("");
            nameField.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    frame, "Student score must be a number."
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    frame, "Error adding student: " + ex.getMessage()
            );
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(
                    frame, "Could not read server response."
            );
        }
    }

    private void retrieveStudentRecords() {
        try {
            if (out == null || in == null) {
                JOptionPane.showMessageDialog(
                        frame, "Not connected to the server."
                );
                return;
            }

            out.writeObject("RETRIEVE");
            out.flush();

            List<Student> studentList
                    = (ArrayList<Student>) in.readObject();

            displayStudentRecords(studentList);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    frame, "Error retrieving records: " + ex.getMessage()
            );
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(
                    frame, "Could not read student records."
            );
        }
    }

    private void displayStudentRecords(List<Student> studentList) {
        recordsTextArea.setText("");

        if (studentList.isEmpty()) {
            recordsTextArea.setText("No student records found.");
            return;
        }

        for (Student student : studentList) {
            recordsTextArea.append(student.toString() + "\n");
        }
    }

    private void closeConnection() {
        try {
            if (out != null && in != null && socket != null
                    && !socket.isClosed()) {
                out.writeObject("EXIT");
                out.flush();

                String response = (String) in.readObject();
                System.out.println(response);
            }
        } catch (IOException ex) {
            System.out.println("Connection closing: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("Could not read exit response.");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }

            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addStudentRecord();
        } else if (e.getSource() == retrieveButton) {
            retrieveStudentRecords();
        } else if (e.getSource() == exitButton) {
            closeConnection();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentRecordClient());
    }
}
