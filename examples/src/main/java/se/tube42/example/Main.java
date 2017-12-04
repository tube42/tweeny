package se.tube42.example;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import se.tube42.lib.tweeny.*;
import se.tube42.example.common.*;

public class Main
extends Frame
{
    private static final String desc[] = {
        "demo1", "Basic tweening and animation example",
        "demo2", "Tween tail operations", 
        "demo3", "Different ease euqations",
        "demo4", "finish() example for running user code",
        "demo5", "finish() callback example",
        "demo6", "Using TweenHelper"
        };
        
    protected final WindowAdapter closeMe = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    };

         
    protected final WindowAdapter closeOther = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            e.getWindow().setVisible(false);
            Main.this.setVisible(true);
        }
    };

    public Main()
    {
        Button b;

        setTitle("Tweeny examples");
        setLayout(new BorderLayout(12, 12));

        Panel panel = new Panel(new GridLayout(desc.length / 2, 2, 12, 12));
        for(int i = 0; i < desc.length; i += 2) {
            panel.add(b = new Button(desc[i + 0]));
            panel.add(new Label(desc[i + 1]));
            final int n = 1 + i / 2;
            b.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) { openDemo(n); }
            });
        }

        add(panel, BorderLayout.CENTER);

        panel = new Panel();        
        panel.add(b = new Button("Close"));
        b.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        } ) ;   
        add(panel, BorderLayout.SOUTH);


        Label l = new Label("Welcome to tweeny demo, now with a 1990 UI", Label.CENTER);
        l.setForeground(Color.RED);
        add(l, BorderLayout.NORTH);


        pack();
        addWindowListener(closeMe);
        setVisible(true);
    }


    private void openDemo(int n) 
    {
        BaseWindow w = null;

        switch(n) {
            case 1: w = new Demo1(); break;
            case 2: w = new Demo2(); break;
            case 3: w = new Demo3(); break;
            case 4: w = new Demo4(); break;
            case 5: w = new Demo5(); break;
            case 6: w = new Demo6(); break;
        }

        if(w != null) {
            this.setVisible(false);
            w.addWindowListener(closeOther);
        }
    }

    public static void main(String []args)
    {
        new Main();
    }
}
