/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import javax.swing.JFrame;
import defaultclass.*;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.awt.Image;
import java.awt.Toolkit;

/**
 *
 * @author kamol
 */
public class Main extends JFrame {

    //private String SiteName = "JPGDG1";
    private static String username;
    private static boolean resetOption = false;

    Main(String username, boolean resetOption) {
        super("ConneXion ( Welcome: " + username + " !!! )");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 720);
        setResizable(false);
        setLocationRelativeTo(null);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage("Other\\ConneXion.jpg");
        setIconImage(img);

        MainBody MA = new MainBody();
        add(MA.Structure(username, resetOption));


    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        username = System.getProperty("user.name");
        String sqlquery = "select * from rtinfo.dbo.muse where uwser = '" + username + "'";
        try {
            SQL5484 sql5484 = new SQL5484();
            ResultSet resultset = sql5484.SQL5484(sqlquery);
            resultset.last();
            int noOfRow = resultset.getRow();
            if (noOfRow == 0) {
                JOptionPane.showMessageDialog(null, username.toUpperCase() + ", You are unauthorized !!!", "Authorization Check", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } else {
                String permission = (String) resultset.getString("cwnnexion");
                String resetPermission = (String) resultset.getString("cwat1");
                //System.out.println(permission.substring(2, 4));
                if (permission.substring(2, 4).equals("EA")) {

                    if (resetPermission.substring(2, 4).equals("X8")) {
                        resetOption = true;
                    }

                    Main MA = new Main(username, resetOption);
                    MA.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, username.toUpperCase() + ", You don't have permission to use this software !!!", "Permission Check", JOptionPane.INFORMATION_MESSAGE);
                    //JOptionPane.showMessageDialog(null, username.toUpperCase() + ", Due to some dependency, ConneXion is temporarily close. \n Sorry for the inconvenience !!!", "Message !!!", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }



            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
