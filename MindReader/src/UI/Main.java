package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import java.awt.FlowLayout;
import java.awt.ScrollPane;

import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Color;
import java.util.List;

import javax.swing.JTextField;

import MindChart.SynchronizedChart;


public class Main {

  
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BaseUI window = new BaseUI();
                    window.display();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }   
}