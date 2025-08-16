package vallegrande.edu.pe.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NotificationManager {

    public static void showToast(Component parent, String message) {
        JWindow toast = new JWindow(SwingUtilities.getWindowAncestor(parent));
        toast.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(50, 50, 50, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Roboto", Font.PLAIN, 14));
        panel.add(label);

        toast.add(panel);
        toast.pack();

        int x = parent.getLocationOnScreen().x + (parent.getWidth() - toast.getWidth()) / 2;
        int y = parent.getLocationOnScreen().y + parent.getHeight() - toast.getHeight() - 50;
        toast.setLocation(x, y);
        toast.setOpacity(0.9f);
        toast.setVisible(true);

        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toast.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}