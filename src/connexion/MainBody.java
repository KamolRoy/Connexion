/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Image;
import java.awt.Toolkit;

import java.util.*;
import javax.swing.table.TableRowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
//import java.text.ParseException;
import java.awt.Dimension;
import java.awt.Color;

import defaultclass.*;

/**
 *
 * @author kamol
 */
public class MainBody {

    static final String DRIVER = "net.sourceforge.jtds.jdbcx.JtdsDataSource";
    static final String DATABASE_URL = "jdbc:jtds:sqlserver://GP-PC-1005484:1433/RTinfo";
    static final String DRIVER_ROC = "net.sourceforge.jtds.jdbcx.JtdsDataSource";
    static final String DATABASE_URL_ROC = "jdbc:jtds:sqlserver://bstation:1433/ROC_TEMP";
    static final String DEFAULT_QUERY = "select * from BS_Fault";
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    PreparedStatement pst = null;
    private Socket client;
    private BufferedReader br;
    private PrintWriter pw;
    private String HLogIn = new String("LGI:OP=" + '"' + "surveillance" + '"' + ",PWD=" + '"' + "nocuser" + '"' + ';');
    private String selectedElement, connectSite, selectedSite, BSCName, BSC_IP, Server_IP, SubCenter, Version, SiteIndex, SiteName, TRXID, TRXName, Task = "", message = "";
    private int noOfRow;
    private Vector SiteInfo, E1_Port, Subrack_No, Slot_No, Port_No, TRXList, CellList, secondCabTRX;
    private Object[][] data;
    private String[] colName = {"Site", "Site Type", "BSC"};
    private boolean blockOption, selectTable = false;
    private boolean siteConnected = false;
    private JTextField N_JFA = new JTextField(12);
    private JTextField JTF = new JTextField(12);
    private JTextArea JTA = new JTextArea();
    private JLabel N_JL1 = new JLabel();
    private JPanel treePanel = new JPanel(new BorderLayout());
    //* Declear Button

    private JButton alarmConfiguration = new JButton("Alarm Config");
    private JButton alarmButton = new JButton("Current Alarm");
    private JButton historyAlarm = new JButton("History Alarm");
    private JButton channelStatus = new JButton("Channel Status");
    private JButton channelMonitor = new JButton("CH Monitor");
    private JButton interferenceStatus = new JButton("Interference");
    private JButton boardState = new JButton("Board State");
    private JButton boardTemp = new JButton("Board Temp");
    private JButton roomTemp = new JButton("Room Temp");
    private JButton portInfo = new JButton("Port Info");
    private JButton portStatus = new JButton("Port Status");
    private JButton resetBTS = new JButton("Reset BTS");
    private JButton lock_unlock=new JButton("  Lock-Unlock   ");
    private JButton alarmBoard = new JButton("Alarm Board");
    private JPopupMenu popupMenu;
    private JMenuItem blockTRX, unblockTRX, resetTRX, attributesTRX, blockSecondCab, unblockSecondCab;
    private JList contentList;
    private DefaultListModel model = new DefaultListModel();
    //* Calling DefaultClass
    TelnetResult6900 telnet = new TelnetResult6900();
    Reset6900 reset6900 = new Reset6900();
    MSSQLConnection MSC = new MSSQLConnection();
    private String M2KUser = null;
    private String M2KPassword = null;
    private boolean localResetOption;

    MainBody() {
        try {
            String sqlquery = "select SiteName,SiteType,BSC_Name from rtinfo.dbo.H_Site_Bsc order by SiteName";
            ResultSet resultset = Conn(sqlquery);
            resultset.last();
            noOfRow = resultset.getRow();
            data = new Object[noOfRow][3];

            resultset.first();
            SiteInfo = new Vector();

            for (int j = 0; j < noOfRow; j++) {
                for (int i = 0; i < 3; i++) {
                    data[j][i] = resultset.getString(i + 1);
                }
                resultset.next();

            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        popupMenu = new JPopupMenu();
        blockTRX = new JMenuItem("Block TRX");
        unblockTRX = new JMenuItem("Unblock TRX");
        resetTRX = new JMenuItem("Reset TRX");
        attributesTRX = new JMenuItem("TRX Attributes");
        blockSecondCab = new JMenuItem("Block Second Cab");
        unblockSecondCab = new JMenuItem("UnBlock Secon Cab");

        popupMenu.add(blockTRX);
        popupMenu.add(unblockTRX);
        popupMenu.add(resetTRX);
        popupMenu.add(attributesTRX);
        popupMenu.add(blockSecondCab);
        popupMenu.add(unblockSecondCab);
    }

    public JPanel Structure(final String username, boolean resetOption) {
        localResetOption = resetOption;
        final JPanel JP = new JPanel(new BorderLayout(12, 5));

        JPanel JPN1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        JPN1.add(N_JL1);
        JButton connectButton = new JButton("Connect");
        JButton searchButton = new JButton("Search");
        searchButton.setMnemonic('S');
        JPanel JPN2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        JPN2.add(N_JFA);
        JPN2.add(connectButton);
        JPN2.add(searchButton);

        JPanel JPN = new JPanel(new BorderLayout());
        JPN.add(JPN1, BorderLayout.EAST);
        JPN.add(JPN2, BorderLayout.WEST);

        //JPanel ButtonBody = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        JPanel ButtonBody = new JPanel(new BorderLayout());

        int ButtonW = 125;
        int ButtonH = 14;

        // Set button Mnemonic ans Size
        alarmConfiguration.setMnemonic('N');
        alarmConfiguration.setMaximumSize(new Dimension(ButtonW, ButtonH));
        alarmBoard.setMnemonic('P');
        alarmBoard.setMaximumSize(new Dimension(ButtonW, ButtonH));
        alarmButton.setMnemonic('A');
        alarmButton.setMaximumSize(new Dimension(ButtonW, ButtonH));
        historyAlarm.setMnemonic('H');
        historyAlarm.setMaximumSize(new Dimension(ButtonW, ButtonH));
        channelStatus.setMnemonic('C');
        channelStatus.setMaximumSize(new Dimension(ButtonW, ButtonH));
        channelMonitor.setMnemonic('M');
        channelMonitor.setMaximumSize(new Dimension(ButtonW, ButtonH));
        interferenceStatus.setMaximumSize(new Dimension(ButtonW, ButtonH));
        boardState.setMaximumSize(new Dimension(ButtonW, ButtonH));
        boardTemp.setMaximumSize(new Dimension(ButtonW, ButtonH));
        roomTemp.setMaximumSize(new Dimension(ButtonW, ButtonH));
        portInfo.setMnemonic('o');
        portInfo.setMaximumSize(new Dimension(ButtonW, ButtonH));
        portStatus.setMnemonic('P');
        portStatus.setMaximumSize(new Dimension(ButtonW, ButtonH));
        resetBTS.setMnemonic('R');
        resetBTS.setMaximumSize(new Dimension(ButtonW, ButtonH));
        resetBTS.setEnabled(false);
        blockTRX.setEnabled(false);
        unblockTRX.setEnabled(false);
        resetTRX.setEnabled(false);
        blockSecondCab.setEnabled(false);
        unblockSecondCab.setEnabled(false);
        lock_unlock.setEnabled(false);

        if (resetOption) {
            resetBTS.setEnabled(true);
            blockTRX.setEnabled(true);
            unblockTRX.setEnabled(true);
            resetTRX.setEnabled(true);
            lock_unlock.setEnabled(true);
        }


        JLabel DipLabel = new JLabel(" Dip Status");
        JLabel LJ1 = new JLabel("");
        JLabel LJ2 = new JLabel("");

        Box ButtonBox = Box.createVerticalBox();
        int Gap = 3;
        int Gpaw = 7;
       
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(alarmButton);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(historyAlarm);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(channelStatus);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(channelMonitor);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(interferenceStatus);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(boardState);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(boardTemp);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(roomTemp);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(alarmConfiguration);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(resetBTS);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(lock_unlock);
        ButtonBox.add(DipLabel);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(portInfo);
        ButtonBox.add(Box.createHorizontalStrut(Gpaw));
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(portStatus);
        ButtonBox.add(Box.createVerticalStrut(Gap));
        ButtonBox.add(alarmBoard);



        JLabel JL1 = new JLabel(" ");
        JLabel JL2 = new JLabel(" ");

        //treePanel.setMaximumSize(new Dimension(280, 150));
        treePanel.setBackground(Color.WHITE);
        treePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //treePanel.add(LJ,BorderLayout.CENTER);
        model.addElement(" ");
        contentList = new JList(model);
        contentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);



        JScrollPane jsp = new JScrollPane(contentList);
        treePanel.add(jsp);


        ButtonBox.add(Box.createVerticalStrut(Gap));

        ButtonBody.add(ButtonBox, BorderLayout.NORTH);
        ButtonBody.add(treePanel, BorderLayout.CENTER);
        ButtonBody.add(JL1, BorderLayout.WEST);
        //ButtonBody.add(JL2,BorderLayout.EAST);



        JP.add(JPN, BorderLayout.NORTH);

        JP.add(ButtonBody, BorderLayout.WEST);
        JP.add(new JScrollPane(JTA), BorderLayout.CENTER);
        JP.add(LJ1, BorderLayout.EAST);
        JP.add(LJ2, BorderLayout.SOUTH);



        N_JFA.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                siteConnected = false;
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    connectButtonAction();
                }

            }
        });

        contentList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                selectedElement = (String) contentList.getSelectedValue();
                if (connectSite != null && selectedElement.length() > 3) {
                    if (selectedElement.contains("...")) {
                        blockOption = false;
                        String display = (connectSite + "\n" + "\n" + "Selected Element : " + selectedElement + "\n" + "\n");
                        String Cell = selectedElement.substring(0, 7);
                        String Site = selectedElement.substring(0, 6);
                        String Command = "LST GTRX: IDTYPE=BYNAME, BTSNAME=\"" + Site + "\", CELLNAME=\"" + Cell + "\";";
                        String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                        //result=result.substring(result.indexOf("LST GTRX"));
                        JTA.setText(display + result);
                    } else {
                        blockOption = true;
                        String display = (connectSite + "\n" + "\n" + "Selected Element : " + selectedElement + "\n" + "\n");
                        TRXName = selectedElement.substring(0, selectedElement.indexOf("(")).trim();
                        TRXID = selectedElement.substring(selectedElement.indexOf("(") + 1, selectedElement.indexOf(")"));
                        String Command = "DSP BTSATTR: OBJTP=TRX, TRXID=" + TRXID.trim() + ", TRXATTR=OPSTAT&AST;";
                        String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                        //result=result.substring(result.indexOf("DSP BTSATTR"));
                        JTA.setText(display + result);
                    }
                }
            }

            public void mousePressed(MouseEvent event) {
                checkForTriggerEvent(event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                checkForTriggerEvent(event);
            }

            public void checkForTriggerEvent(MouseEvent event) {
                if (event.isPopupTrigger()) {
                    popupMenu.show(event.getComponent(), event.getX(), event.getY());
                }
            }
        });

        connectButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                connectButtonAction();
            }
        });

        searchButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JTF.setText(N_JFA.getText());
                SearchSite SS = new SearchSite();
                SS.setVisible(true);
            }
        });



 alarmConfiguration.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    String S = ("\n" + "\n" + "Alarm configuration Status" + "\n" + "----------------------" + "\n");
                    JTA.setText(connectSite + S);
                    if (Version.contains("BSC6900")) {
                        String Command = "LST BTSENVALMPORT: IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\";";
                        String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                        JTA.setText(JTA.getText() + result);

                    } else {
                        //System.out.println(E1_Port.size());

                        JTA.setText(connectSite + S);
                        String command = "DSP CHNSTAT:IDXTYPE=BYNAME,BTSNAME=\"" + SiteName + "\";";

                        //JTA.setText(JTA.getText() +"\n"+ command);
                        S = TelResult(command, BSC_IP.trim(), Server_IP.trim());
                        JTA.setText(JTA.getText() + S);
                    }

                }
            }
        });




        alarmButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();
                } else {
                    if (Version.contains("BSC6900")) {
                        String Message = "\n" + " \n" + "Currnet Alarm of site:" + SiteName + "\n" + "\n";
                        String Command = "LST ALMAF: LOCINFOKEY=\"Site Name:" + SiteName + "\", CNT=1000;";
                        String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                        if (result.contains("Corresponding results not found")) {
                            Message = Message + "!!! No Alarm Found !!!";
                            JTA.setText(connectSite + Message);
                            Message = "";
                        } else {
                            String[] AlarmList = result.split("\n");
                            for (int i = 0; i < AlarmList.length; i++) {
                                if (AlarmList[i].trim().startsWith("ALARM")) {
                                    int j = AlarmList[i + 2].indexOf("=");
                                    Message = Message + (AlarmList[i + 2].substring(j + 2).trim()) + " (" + (AlarmList[i + 3].substring(j + 2).trim()) + " )\n";
                                }
                            }
                            JTA.setText(connectSite + Message);
                            Message = "";

                        }
                    } else {
                        String Command = "LST ALMAF: CNT=1000;";
                        String Message = TelResult(Command, BSC_IP.trim(), Server_IP.trim());

                        String[] AlarmList = Message.split("ALARM");
                        Message = "\n" + " \n" + "Currnet Alarm of site:" + SiteName + "\n" + "\n";
                        int fault = 0;
                        for (int i = 0; i < AlarmList.length; i++) {
                            if (AlarmList[i].contains(SiteName.trim())) {
                                fault = fault + 1;
                                String[] AlarmLine = AlarmList[i].split("\n");
                                try {
                                    Message = Message + (AlarmLine[2].substring(25) + " ( " + AlarmLine[3].substring(25) + " )") + "\n";
                                } catch (Exception e3) {
                                    //System.out.println("External alarm processing error e3: ");
                                    //e3.printStackTrace();
                                    Message = Message + AlarmList[i];
                                }

                            }
                        }
                        if (fault == 0) {
                            Message = Message + "!!! No Alarm Found !!!";
                        }
                        JTA.setText(connectSite + Message);
                        Message = "";
                    }
                }

            }
        });






    alarmBoard.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();
                } else {
                    if (Version.contains("BSC6900")) {
                        String Message = "\n" + " \n" + "Currnet Alarm of site:" + SiteName + "\n" + "\n";
                        String Command = "LST ALMAF: LOCINFOKEY=\"Site Name:" + SiteName + "\", CNT=1000;";
                        String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                        if (result.contains("Corresponding results not found")) {
                            Message = Message + "!!! No Alarm Found !!!";
                            JTA.setText(connectSite + Message);
                            Message = "";
                        } else {
                            String[] AlarmList = result.split("\n");
                            for (int i = 0; i < AlarmList.length; i++) {
                                if (AlarmList[i].trim().startsWith("Alarm")) {
                                    Message = Message + (AlarmList[i + 2].trim()) +(AlarmList[i + 3].trim())+"\n";

                                }
                            }
                            JTA.setText(connectSite + Message);
                            Message = "";

                        }
                    } 
                }

            }
        });




















        historyAlarm.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    String Command = "LST ALMLOG: ALMTP=all, LOCINFOKEY=\"Site Name:" + SiteName + "\";";
                    String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                    String Message = "\n" + "\n" + Command;
                    Message = Message + "\n" + " \n" + "History Alarm of site:" + SiteName + "\n" + "\n";
                    Message = Message + "Alarm Name" + "\t" + "\t" + "\t" + "  Alarm Raised Time" + "\t" + "  Clear Time" + "\n";
                    Message = Message + "------------------" + "\t" + "\t" + "\t" + "  -----------------------------" + "\t" + "   -----------------------------" + "\n";

                    String[] AlarmList = result.split("ALARM");

                    for (int i = 1; i < AlarmList.length; i++) {
                        String[] AlarmLine = AlarmList[i].split("\n");
                        try {
                            String AlarmName = "-", AlarmRaisedTime = "-", ClearTime = "<<Not Clear>>";
                            for (int j = 1; j < AlarmLine.length; j++) {
                                if (AlarmLine[j].contains("Alarm name")) {
                                    String[] AlarmText = AlarmLine[j].split("=");
                                    AlarmName = AlarmText[1].trim();
                                    AlarmName = rightPad(AlarmName, 50);
                                    //System.out.println(AlarmText[0]);
                                    //System.out.println(AlarmText[1]);

                                } else if (AlarmLine[j].contains("Alarm raised time")) {
                                    String[] AlarmText = AlarmLine[j].split("=");
                                    AlarmRaisedTime = AlarmText[1].trim();

                                } else if (AlarmLine[j].contains("Cleared time")) {
                                    String[] AlarmText = AlarmLine[j].split("=");
                                    ClearTime = AlarmText[1].trim();

                                }

                            }
                            //System.out.println(AlarmName+"\t"+AlarmRaisedTime);
                            if (AlarmName.contains("BATTERY")) {
                                Message = Message + AlarmName.trim();
                            } else if (AlarmName.contains("-")) {
                                Message = Message + "\t" + AlarmRaisedTime.trim() + "\t" + ClearTime.trim() + "\n";
                            } else {
                                Message = Message + AlarmName.trim() + "\t" + AlarmRaisedTime.trim() + "\t" + ClearTime.trim() + "\n";
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Message = Message + AlarmList[i];
                        }

                    }
                    JTA.setText(connectSite + Message);
                    Message = "";
                }
            }
        });

        channelStatus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    String S = ("\n" + "\n" + "Channel Status" + "\n" + "----------------------" + "\n");
                    JTA.setText(connectSite + S);
                    if (Version.contains("BSC6900")) {
                        String Command = "DSP CHNSTAT: OBJTYPE=SITE, IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\";";
                        String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                        JTA.setText(JTA.getText() + result);

                    } else {
                        //System.out.println(E1_Port.size());

                        JTA.setText(connectSite + S);
                        String command = "DSP CHNSTAT:IDXTYPE=BYNAME,BTSNAME=\"" + SiteName + "\";";

                        //JTA.setText(JTA.getText() +"\n"+ command);
                        S = TelResult(command, BSC_IP.trim(), Server_IP.trim());
                        JTA.setText(JTA.getText() + S);
                    }

                }
            }
        });

        channelMonitor.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();
                } else {
                    if (Version.contains("BSC6900")) {
                        String command = "DSP CHNSTAT: OBJTYPE=SITE, IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\";";
                        ChannelImage_1 CI = new ChannelImage_1(command, BSC_IP, Server_IP, SiteName);
                        CI.setVisible(true);
                    } else {
                        String command = "DSP CHNSTAT:IDXTYPE=BYNAME,BTSNAME=\"" + SiteName + "\";";
                        ChannelImage CI = new ChannelImage(command, BSC_IP, Server_IP, SiteName);
                        CI.setVisible(true);
                    }

                }
            }
        });

        interferenceStatus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String S = ("\n" + "\n" + "Channel Status" + "\n" + "----------------------" + "\n");
                JTA.setText(connectSite + S);
                String Command = "DSP CHNJAM: OBJTYPE=SITE, IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\";";
                String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                JTA.setText(JTA.getText() + result);
            }
        });

        boardState.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    String BoardName = "", BoardNo = "", BoardState = "", Message = "";
                    String Command = "DSP BTSATTR: OBJTP=SITE, IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\", SITEATTR=HWCFG;";
                    JTA.setText(connectSite);
                    String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                    JTA.setText(JTA.getText() + "\n" + "\n" + Command + "\n" + "\n" + "Board Status" + "\n" + "---------------------" + "\n" + "\n");

                    String[] BoardList = result.split("BTS");
                    for (int i = 4; i < BoardList.length; i++) {
                        String[] BoardLine = BoardList[i].split("\n");
                        for (int j = 0; j < BoardLine.length; j++) {
                            if (BoardLine[j].contains("Board type")) {
                                String[] BoardName_Sub = BoardLine[j].split("=");
                                BoardName = BoardName_Sub[1];
                            } else if (BoardLine[j].contains("Board No")) {
                                String[] BoardNo_Sub = BoardLine[j].split("=");
                                BoardNo = BoardNo_Sub[1];
                            } else if (BoardLine[j].contains("Board state")) {
                                String[] BoardState_Sub = BoardLine[j].split("=");
                                BoardState = BoardState_Sub[1];
                            }

                        }

                        Message = Message + BoardName.trim() + "-" + BoardNo.trim() + " = " + BoardState.trim() + "\n";

                    }
                    JTA.setText(JTA.getText() + Message);
                }
            }
        });

        boardTemp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    String BoardName = "", BoardNo = "", BoardTemp = "", Message = "";
                    String Command = "DSP BTSBRD: INFOTYPE=RUNPARA, IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\";";
                    JTA.setText(connectSite);
                    String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                    JTA.setText(JTA.getText() + "\n" + "\n" + Command + "\n" + "\n" + "Board Temperature" + "\n" + "----------------------------" + "\n" + "\n");

                    String[] BoardList = result.split("BTS");
                    for (int i = 3; i < BoardList.length; i++) {
                        String[] BoardLine = BoardList[i].split("\n");
                        for (int j = 0; j < BoardLine.length; j++) {
                            if (BoardLine[j].contains("Board type")) {
                                String[] BoardName_Sub = BoardLine[j].split("=");
                                BoardName = BoardName_Sub[1];
                            }
                            if (BoardLine[j].contains("Board No")) {
                                String[] BoardNo_Sub = BoardLine[j].split("=");
                                BoardNo = BoardNo_Sub[1];
                            }
                            if (BoardLine[j].contains("BoardTemp") || BoardLine[j].contains("Temperature of Power Amplifier") || BoardLine[j].contains("Temperature of Air Outlet")) {
                                String[] BoardTemp_Sub = BoardLine[j].split("=");
                                BoardTemp = BoardTemp_Sub[1];
                            }

                        }

                        Message = Message + BoardName.trim() + "-" + BoardNo.trim() + " = " + BoardTemp.trim() + "\n";

                    }
                    JTA.setText(JTA.getText() + Message);
                }
            }
        });

        roomTemp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    String[] Temp = new String[2];
                    int j = 0;
                    String Command = "DSP BTSENVSTAT: IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\", ISMULTIDEMU=NO;";
                    JTA.setText(connectSite);
                    String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                    JTA.setText(JTA.getText() + "\n" + "\n" + Command + "\n" + "\n" + "Room Temperature" + "\n" + "----------------------------" + "\n" + "\n");

                    String[] TempList = result.split("\n");
                    String Message = "Current Temperature = " + TempList[9].substring(5).trim() + "\n";
                    Message = Message + "Temperature Upper Limit = " + TempList[16].substring(20, 45).trim();

                    JTA.setText(JTA.getText() + Message);
                }
            }
        });

        resetBTS.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    M2000info();
                    if (M2KUser != null || M2KPassword != null) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "It will perform a Level-4 Reset \n Do you want to Continue with " + SiteName + "?", "Confirmation....", JOptionPane.YES_NO_OPTION);
                        if (confirmation == 0) {
                            String Command = "RST BTS: TYPE=BTSSOFT, IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\", LEVEL=4-LEVEL;";

                            JTA.setText(connectSite);
                            String result = reset6900.Reset6900(Command, BSC_IP, Server_IP, M2KUser, M2KPassword);
                            JTA.setText(JTA.getText() + "\n" + "\n" + "A Level-4 reset has been performed to site " + SiteName + " with username :" + username + "\n" + "\n" + result);
                            Task = "ResetBTS";
                            MSC.takingLog(username, SiteName, Task);
                        }

                    }

                }
            }
        });

        portInfo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();

                } else {
                    System.out.println(E1_Port.size());
                    String S = ("\n" + "\n" + "E1_Port" + "\t" + "Subrack_No" + "\t" + "Slot_No" + "\t" + "Port_No" + "\n" + "-------------------------------------------------------------------------------");
                    JTA.setText(connectSite + S);


                    for (int i = 0; i
                            < E1_Port.size(); i++) {
                        S = ("\n" + E1_Port.elementAt(i) + "\t" + Subrack_No.elementAt(i) + "\t" + Slot_No.elementAt(i) + "\t" + Port_No.elementAt(i));
                        JTA.setText(JTA.getText() + S);


                    }
                }
            }
        });

        portStatus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (!siteConnected) {
                    notConnected();
                } else {
                    if (Version.contains("BSC6900")) {
                        String S = ("\n" + "\n" + "E1_Port" + "\t" + "Port_Status" + "\n" + "--------------------------------------" + "\n");
                        JTA.setText(connectSite + S);

                        for (int i = 0; i < E1_Port.size(); i++) {
                            String portNo = (String) Port_No.elementAt(i);
                            if (!portNo.contains("NULL")) {
                                //DSP E1T1: SRN=2, SN=24, BT=OIUa, PN=16;
                                String Command = "DSP E1T1: SRN=" + Subrack_No.elementAt(i) + ", SN=" + Slot_No.elementAt(i) + ", PN=" + Port_No.elementAt(i) + ", BT=OIUa;";
                                String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                                String[] S1 = result.split("\n");
                                String S2 = E1_Port.elementAt(i) + "\t" + S1[8].substring(68) + "\n";
                                JTA.setText(JTA.getText() + S2);
                                //String command = "DSP OPT: SRN=" + Subrack_No.elementAt(i) + ", SN=" + Slot_No.elementAt(i) + ", PN=" + Port_No.elementAt(i) + ", J2_TYPE=SHOWTYPE_HEX;";
                                //S = TelResult(command, BSC_IP, Server_IP);
                                //String[] S1 = S.split("\n");
                                //String S2 = E1_Port.elementAt(i) + "\t" + S1[12].substring(22) + "\n";
                                //JTA.setText(JTA.getText() + S2);
                            }

                        }


                    } else {
                        String S = ("\n" + "\n" + "E1_Port" + "\t" + "Port_Status" + "\n" + "--------------------------------------" + "\n");
                        JTA.setText(connectSite + S);

                        for (int i = 0; i < E1_Port.size(); i++) {
                            String command = "DSP OPT: SRN=" + Subrack_No.elementAt(i) + ", SN=" + Slot_No.elementAt(i) + ", PN=" + Port_No.elementAt(i) + ", J2_TYPE=SHOWTYPE_HEX;";
                            S = TelResult(command, BSC_IP, Server_IP);
                            String[] S1 = S.split("\n");
                            String S2 = E1_Port.elementAt(i) + "\t" + S1[12].substring(22) + "\n";
                            JTA.setText(JTA.getText() + S2);
                        }
                    }
                }
            }
        });

        channelStatus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String display = (connectSite + "\n" + "\n" + "Selected Element : " + SiteName + "\n" + "\n");
                String Command = "DSP CHNSTAT: OBJTYPE=CELL, CELLIDTYPE=BYNAME, CELLNAME=\"" + SiteName + "\";";
                String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                JTA.setText(display + result);
            }
        });

        blockTRX.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (blockOption) {
                    M2000info();
                    if (M2KUser != null || M2KPassword != null) {
                        String Command = "SET GTRXADMSTAT: TRXID=" + TRXID + ", ADMSTAT=Lock;";
                        JTA.setText(connectSite);
                        String result = reset6900.Reset6900(Command, BSC_IP, Server_IP, M2KUser, M2KPassword);
                        JTA.setText(JTA.getText() + "\n" + "\n" + TRXName + " is blocked with username : " + username + "\n" + "\n" + result);
                        Task = "blockTRX," + TRXName;
                        MSC.takingLog(username, SiteName, Task);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a TRX", "Selection Error:", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        unblockTRX.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (blockOption) {
                    M2000info();
                    if (M2KUser != null || M2KPassword != null) {
                        String Command = "SET GTRXADMSTAT: TRXID=" + TRXID + ", ADMSTAT=Unlock;";
                        JTA.setText(connectSite);
                        String result = reset6900.Reset6900(Command, BSC_IP, Server_IP, M2KUser, M2KPassword);
                        JTA.setText(JTA.getText() + "\n" + "\n" + TRXName + " is Unblocked with username : " + username + "\n" + "\n" + result);
                        Task = "UnblockTRX," + TRXName;
                        MSC.takingLog(username, SiteName, Task);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a TRX", "Selection Error:", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        resetTRX.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (blockOption) {
                    M2000info();
                    if (M2KUser != null || M2KPassword != null) {
                        String Command = "RST TRX: TRXID=" + TRXID + ";";
                        JTA.setText(connectSite);
                        String result = reset6900.Reset6900(Command, BSC_IP, Server_IP, M2KUser, M2KPassword);
                        JTA.setText(JTA.getText() + "\n" + "\n" + TRXName + " is given reset with username : " + username + "\n" + "\n" + result);
                        Task = "ResetTRX," + TRXName;
                        MSC.takingLog(username, SiteName, Task);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a TRX", "Selection Error:", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        attributesTRX.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (blockOption) {
                    String display = (connectSite + "\n" + "\n");
                    String Command = "LST GTRXDEV: TRXID=" + TRXID + ", LstFormat=VERTICAL;";
                    String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                    JTA.setText(display + result);
                }
            }
        });

        blockSecondCab.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (blockOption) {
                    M2000info();
                    if (M2KUser != null || M2KPassword != null) {
                        JTA.setText(connectSite + "\n" + "\n");

                        for (int i = 0; i < secondCabTRX.size(); i++) {
                            String S = (String) secondCabTRX.elementAt(i);
                            String localTRX = S.substring(0, S.indexOf("(")).trim();
                            String localTRXID = S.substring(S.indexOf("(") + 1, S.indexOf(")"));
                            String Command = "SET GTRXADMSTAT: TRXID=" + localTRXID + ", ADMSTAT=Lock;";
                            String result = reset6900.Reset6900(Command, BSC_IP, Server_IP, M2KUser, M2KPassword);
                            JTA.setText(JTA.getText() + "\n" + localTRX + " having TRX ID  " + localTRXID + " is blocked");
                        }

                        JTA.setText(JTA.getText() + "\n" + "\n" + "Second Cabinet TRX are made blocked with username:  " + username);
                        Task = "Block 2nd Cab";
                        MSC.takingLog(username, SiteName, Task);

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a TRX", "Selection Error:", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        unblockSecondCab.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (blockOption) {
                    M2000info();
                    if (M2KUser != null || M2KPassword != null) {
                        JTA.setText(connectSite + "\n" + "\n");

                        for (int i = 0; i < secondCabTRX.size(); i++) {
                            String S = (String) secondCabTRX.elementAt(i);
                            String localTRX = S.substring(0, S.indexOf("(")).trim();
                            String localTRXID = S.substring(S.indexOf("(") + 1, S.indexOf(")"));
                            String Command = "SET GTRXADMSTAT: TRXID=" + localTRXID + ", ADMSTAT=Unlock;";
                            String result = reset6900.Reset6900(Command, BSC_IP, Server_IP, M2KUser, M2KPassword);
                            JTA.setText(JTA.getText() + "\n" + localTRX + " having TRX ID  " + localTRXID + " is unblocked");
                        }

                        JTA.setText(JTA.getText() + "\n" + "\n" + "Second Cabinet TRX are made unblocked with username:  " + username);
                        Task = "Unblock 2nd Cab";
                        MSC.takingLog(username, SiteName, Task);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a TRX", "Selection Error:", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        return JP;
    }

    void M2000info() {
        if (M2KUser == null || M2KPassword == null) {
            JPasswordField pwd = new JPasswordField(20);
            M2KUser = JOptionPane.showInputDialog("Provide Your M2000 Username");
            int action = JOptionPane.showConfirmDialog(null, pwd, "Provide Your M2000 Passowrd", JOptionPane.OK_CANCEL_OPTION);
            if (action >= 0) {
                M2KPassword = new String(pwd.getPassword());

            }
        }
        boolean fault = reset6900.CheckUser(BSC_IP, Server_IP, M2KUser, M2KPassword);

        if (fault) {
            JOptionPane.showMessageDialog(null, "Username / Password Error ", "Username / Password Error", JOptionPane.ERROR_MESSAGE);
            M2KUser = null;
            M2KPassword = null;
        }
    }

    ResultSet Conn(String sqlquery) {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, "mk5", "mk5mnm");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(sqlquery);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultset;
    }

    String Conn_Roc(String sqlquery) {
        ResultSet localResultset = null;
        String SC = "";
        try {
            Class.forName(DRIVER_ROC);
            Connection localCon = DriverManager.getConnection(DATABASE_URL_ROC, "nm", "nm@gp");
            Statement localStatement = localCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            localResultset = localStatement.executeQuery(sqlquery);
            localResultset.last();
            int noOfRow0 = localResultset.getRow();
            if (noOfRow0 == 0) {
                SC = "NA";
            } else {
                SC = localResultset.getString("SubCenter");
            }
            localStatement.close();
            localCon.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return SC;
    }

    class SearchSite extends JFrame {

        SearchSite() {
            super("Search Site");
            setSize(300, 300);
            setLocationRelativeTo(null);
            setResizable(false);
            selectTable = false;

            Toolkit kit = Toolkit.getDefaultToolkit();
            Image img = kit.createImage("Other\\ConneXion.jpg");
            setIconImage(img);

            JPanel JP1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            JP1.add(JTF);
            MyTableModel tableModel = new MyTableModel();

            final JTable JT = new JTable(tableModel);

            JT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            final TableRowSorter<TableModel> shorter = new TableRowSorter<TableModel>(tableModel);
            JT.setRowSorter(shorter);
            //JT.setRowSelectionInterval(0, 0);
            JT.addMouseListener(
                    new MouseAdapter() {

                        public void mouseClicked(MouseEvent evt) {
                            selectTable = true;
                            try {
                                int row = JT.getSelectedRow();
                                selectedSite = (String) JT.getValueAt(row, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

            JTF.addKeyListener(new KeyAdapter() {

                public void keyReleased(KeyEvent e) {
                    try {
                        //JT.setRowSelectionInterval(0, 0);
                        JTextField textField = (JTextField) e.getSource();
                        String text = textField.getText();

                        int key = e.getKeyCode();
                        if (key == KeyEvent.VK_ENTER) {

                            JT.setRowSelectionInterval(0, 0);
                            int row = JT.getSelectedRow();
                            selectTable = true;
                            selectedSite = (String) JT.getValueAt(row, 0);
                            performOK();
                            dispose();

                        }

                        try {
                            shorter.setRowFilter(RowFilter.regexFilter(JTF.getText().toUpperCase(), 0));
                            shorter.setSortKeys(null);
                            //shorter.setRowFilter(RowFilter.regexFilter(text, 3));

                        } catch (Exception pse) {
                            JOptionPane.showMessageDialog(null, "Bad regex Pattern", "Bad regex Pattern", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (Exception pse) {
                        System.out.println("Site Search Error: E");
                        JOptionPane.showMessageDialog(null, "Site Search Error", "Site Search Error:", JOptionPane.ERROR_MESSAGE);
                    }

                }

                public void keyTyped(KeyEvent e) {
                    // TODO: Do something for the keyTyped event
                    //JT.setRowSelectionInterval(0, 0);
                }

                public void keyPressed(KeyEvent e) {
                    // TODO: Do something for the keyPressed event
                    //JT.selectAll();
                }
            });

            JButton JB1 = new JButton("OK");
            JB1.setMnemonic('O');
            JButton JB2 = new JButton("Close");
            JB2.setMnemonic('C');
            JPanel JP2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            JP2.add(JB1);
            JP2.add(JB2);

            JB1.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    performOK();
                    dispose();
                }
            });

            JB2.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            add(JP1, BorderLayout.NORTH);
            add(new JScrollPane(JT), BorderLayout.CENTER);
            add(JP2, BorderLayout.SOUTH);
        }
    }

    class MyTableModel extends AbstractTableModel {

        public int getRowCount() {
            return noOfRow;
        }

        public int getColumnCount() {
            return 3;
        }

        public Object getValueAt(int r, int c) {
            return data[r][c];
        }

        public String getColumnName(int c) {
            return colName[c];
        }
    }

    String TelResult(String Command, String BSC_IP, String Server_IP) {
        String Result = "";


        try {
            client = new Socket(InetAddress.getByName(Server_IP), 31114);
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw = new PrintWriter(client.getOutputStream(), true);
            pw.println(HLogIn);


            String EndMark = "END";


            do {
                try {
                    message = br.readLine();


                } catch (Exception e1) {
                    e1.printStackTrace();


                }
            } while (!message.contains("END"));
            pw.println("REG NE:IP=" + BSC_IP + ";");


            do {
                try {
                    message = br.readLine();


                } catch (Exception e1) {
                    e1.printStackTrace();


                }
            } while (!message.contains("END"));
            pw.println(Command);


            do {
                try {
                    message = br.readLine();


                    if (message.contains("To be continued")) {
                        EndMark = "reports in total";


                    }
                    Result = Result + message + "\n";


                } catch (Exception e1) {
                    e1.printStackTrace();


                }
            } while (!message.contains(EndMark));
            client.close();



        } catch (Exception e2) {
            System.out.println("Exception e2 :>>");
            e2.printStackTrace();

        }
        return Result;


    }

    void connectButtonAction() {
        SiteName = N_JFA.getText().toUpperCase();


        if (SiteName.length() < 6) {
            JOptionPane.showMessageDialog(null, "Site Name must be 6 letter", "Under Length Site Name Error:", JOptionPane.ERROR_MESSAGE);
            JTA.setText("");
            N_JL1.setText("");

        } else {
            JTA.setText("");
            siteConnected = true;
            //String SiteName = "JPGDG1";


            try {
                String sqlquery = "select * from rtinfo.dbo.H_Site_Bsc where SiteName = '" + SiteName + "'";
                ResultSet resultset = Conn(sqlquery);
                ResultSetMetaData metadata = resultset.getMetaData();
                int noOfColumns = metadata.getColumnCount();
                resultset.last();
                int noOfRow = resultset.getRow();
                if (noOfRow == 0) {
                    JOptionPane.showMessageDialog(null, "No information found for the site: \n" + SiteName, "Site Not Found Error:", JOptionPane.ERROR_MESSAGE);
                    //JTA.setText("No information found for the site: \n" + SiteName);
                    statement.close();
                    connection.close();
                } else {
                    resultset.first();
                    BSCName = resultset.getString("Bsc_Name");
                    BSC_IP = resultset.getString("Bsc_IP");
                    Server_IP = resultset.getString("Server_IP");
                    SiteIndex = resultset.getString("SiteIndex");
                    Version = (String) resultset.getString("BSC_Version");

                    sqlquery = "select SiteCode,SubCenter from roc_siteifo_oss_q where SiteCode like '%_01%' and SiteCode like '%" + SiteName + "%'";
                    SubCenter = Conn_Roc(sqlquery);

                    //* Check for site version
                    if (Version.contains("BSC6900")) {
                        channelMonitor.setEnabled(true);
                        historyAlarm.setEnabled(true);
                        interferenceStatus.setEnabled(true);
                        boardState.setEnabled(true);
                        boardTemp.setEnabled(true);
                        roomTemp.setEnabled(true);
                    } else {
                        channelMonitor.setEnabled(true);
                        historyAlarm.setEnabled(false);
                        interferenceStatus.setEnabled(false);
                        boardState.setEnabled(false);
                        boardTemp.setEnabled(false);
                        roomTemp.setEnabled(false);
                    }
                    sqlquery = "select * from rtinfo.dbo.h_site_port where BSC_Name='" + BSCName.trim() + "' and SiteName='" + SiteName + "'";
                    ResultSet resultset1 = Conn(sqlquery);
                    resultset1.last();
                    int noOfRow1 = resultset1.getRow();
                    if (noOfRow1 == 0) {
                        JOptionPane.showMessageDialog(null, "No Port information found for the site: \n" + SiteName, "Site Not Found Error:", JOptionPane.ERROR_MESSAGE);
                        JTA.setText("No Port information found for the site: \n" + SiteName);
                    } else {
                        E1_Port = new Vector();
                        Subrack_No = new Vector();
                        Slot_No = new Vector();
                        Port_No = new Vector();


                        if (!E1_Port.isEmpty()) {
                            E1_Port.removeAllElements();
                            Subrack_No.removeAllElements();
                            Slot_No.removeAllElements();
                            Port_No.removeAllElements();

                        }
                        resultset1.first();
                        for (int i = 0; i
                                < noOfRow1; i++) {
                            //System.out.println("Port No:"+(String) resultset1.getString("Port_No"));
                            E1_Port.addElement(resultset1.getString("E1_Port"));
                            Subrack_No.addElement(resultset1.getString("Subrack_No"));
                            Slot_No.addElement(resultset1.getString("Slot_No"));
                            Port_No.addElement(resultset1.getString("Port_No"));
                            resultset1.next();
                        }
                    }

                    statement.close();
                    connection.close();
                    if (Version.contains("BSC6900")) {
                        TRXList = new Vector();
                        CellList = new Vector();
                        secondCabTRX = new Vector();
                        if (!TRXList.isEmpty()) {
                            TRXList.removeAllElements();
                            CellList.removeAllElements();
                            secondCabTRX.removeAllElements();
                        }

                        model.clear();

                        String Command = "LST GTRX: IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\";";
                        String result = telnet.TelnetResult6900(Command, BSC_IP, Server_IP);
                        result = result.substring(result.indexOf("List TRX"), result.indexOf("Number of results"));
                        String[] S1 = result.split("\n");
                        int posTRX = S1[2].indexOf("TRX No.");
                        int posTRXID = S1[2].indexOf("TRX ID");
                        int posCab = S1[2].indexOf("Cabinet No.");
                        CellList.add(S1[4].substring(21, 28));
                        for (int i = 4; i < S1.length - 1; i++) {
                            TRXList.add(S1[i]);
                            String S2 = S1[i].substring(21, 28);
                            if (!(CellList.indexOf(S2) > -1)) {
                                CellList.add(S2);
                            }
                        }


                        for (int i = 0; i < CellList.size(); i++) {
                            model.addElement(CellList.elementAt(i) + "...");
                            //System.out.println(CellList.elementAt(i));
                            for (int j = 0; j < TRXList.size(); j++) {
                                String S3 = (String) TRXList.elementAt(j);
                                if (S3.contains((String) CellList.elementAt(i))) {
                                    String cabNo = S3.substring(posCab, posCab + 5).trim();
                                    model.addElement("    TRX-" + S3.substring(posTRX, posTRX + 5).trim() + " (" + S3.substring(posTRXID, posTRXID + 7).trim() + ") [" + cabNo + "]");
                                    if (cabNo.contains("3")) {
                                        secondCabTRX.addElement("    TRX-" + S3.substring(posTRX, posTRX + 5).trim() + " (" + S3.substring(posTRXID, posTRXID + 7).trim() + ")");

                                    }
                                }
                            }
                        }

                        if (secondCabTRX.size() == 0) {
                            blockSecondCab.setEnabled(false);
                            unblockSecondCab.setEnabled(false);
                        } else if (secondCabTRX.size() > 0 && localResetOption) {
                            blockSecondCab.setEnabled(true);
                            unblockSecondCab.setEnabled(true);
                        }

                        //for(int i=0;i<secondCabTRX.size();i++){
                        //    System.out.println(secondCabTRX.elementAt(i));
                        //}

                    } else {
                        model.clear();
                        model.addElement(" ");
                    }

                    String SJL1 = String.format("SC: %s    BSC: %s    M2K: %s",SubCenter.toUpperCase(), BSCName.toUpperCase(),Server_IP);
                    N_JL1.setText(SJL1);
                    N_JL1.setHorizontalTextPosition(SwingConstants.RIGHT);
                    connectSite = "<<< Connected for site: " + SiteName + " >>>";
                    JTA.setText(JTA.getText() + connectSite);


                }
            } catch (Exception e1) {
                System.out.println("Exception e1 :>>");
                e1.printStackTrace();


            }
        }
    }

    void performOK() {
        if (!selectTable) {
            JOptionPane.showMessageDialog(null, "No Site is Selected", "No Site Selection Error:", JOptionPane.ERROR_MESSAGE);


        } else {
            N_JFA.setText(selectedSite.trim());

        }
    }

    public static String rightPad(String s, int width) {
        return String.format("%-" + width + "s", s).replace(' ', '.');
    }

    void notConnected() {
        JOptionPane.showMessageDialog(null, "No Site is Connected", "No Site Connection Error:", JOptionPane.ERROR_MESSAGE);
        JTA.setText("");
        N_JL1.setText("");
    }
}
