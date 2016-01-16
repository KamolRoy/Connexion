/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import java.sql.*;

/**
 *
 * @author kamol
 */
public class MSSQLConnection {

    static final String DRIVER = "net.sourceforge.jtds.jdbcx.JtdsDataSource";
    static final String DATABASE_URL = "jdbc:jtds:sqlserver://GP-PC-1005484:1433/RTinfo";
    Connection connection = null;
    PreparedStatement pst = null;

    void takingLog(String username, String SiteName,String Task) {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, "mk5", "mk5mnm");
            String sql = "insert into rtinfo.dbo.site_reset_history(Username,SiteName,ResetTime,Task)values(?,?,getdate(),?)";

            pst = connection.prepareStatement(sql);
            pst.clearParameters();
            pst.setObject(1, username);
            pst.setObject(2, SiteName);
            pst.setObject(3, Task);
            pst.executeUpdate();
            pst.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.close();
            } catch (Exception e1) {
            }
        }
    }
}
