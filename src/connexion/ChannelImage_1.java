/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.Collections;
import defaultclass.*;

/**
 *
 * @author kamol
 */
public class ChannelImage_1 extends JFrame {

    public Color col[] = new Color[8];
    public Color col0, col1, col2, col3, col4, col5, col6, col7;
    private Socket client;
    private BufferedReader br;
    private PrintWriter pw;
    private String HLogIn = new String("LGI:OP=" + '"' + "surveillance" + '"' + ",PWD=" + '"' + "nocuser" + '"' + ';');
    private String TS_S, command_S, BSC_IP_S, Server_IP_S, message = "";
    private int len, TRXPosition, TSPosition, ChannelPosition, StatusPosition,StatusPositionEnd;
    private Vector TRX, TS, TRX_D, TRXTS, currentChannel, channelStatus;
    private Vector TSA[] = new Vector[8];
    private Vector TSB[] = new Vector[8];
    private Vector TSC[] = new Vector[8];
    private SortedVector TRX_DI;
    TelnetResult6900 telnet = new TelnetResult6900();

    ChannelImage_1(String command, String BSC_IP, String Server_IP, String SiteName) {
        super("Channel Status: " + SiteName);
        setSize(830, 550);
        setResizable(false);
        setLocationRelativeTo(null);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage("Other\\ConneXion.jpg");
        setIconImage(img);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        command_S = command;
        BSC_IP_S = BSC_IP;
        Server_IP_S = Server_IP;

        add(new JScrollPane(refreshwork()));
        //add(JP2,BorderLayout.SOUTH);


    }

    JPanel refreshwork() {
        TRX = new Vector();
        TS = new Vector();
        TRXTS = new Vector();
        currentChannel = new Vector();
        channelStatus = new Vector();

        String S = telnet.TelnetResult6900(command_S, BSC_IP_S, Server_IP_S);
        S = S.substring(S.indexOf("Display channel state"), S.indexOf("Number of results") + 17);
        //System.out.println(S);
        String[] S1 = S.split("\n");
        TRXPosition = S1[2].indexOf("TRX No");
        TSPosition = S1[2].indexOf("Channel No");
        ChannelPosition = S1[2].indexOf("Current Type");
        StatusPosition = S1[2].indexOf("Sub Channel Status");
        StatusPositionEnd=S1[2].indexOf("Location Group No.");
        //System.out.println(TRXPosition+" :"+TSPosition+" :"+StatusPosition);

        S1 = S.split("END");

        for (int j = 0; j < S1.length; j++) {
            try {
                String S2 = S1[j].substring(S1[j].indexOf("Display channel state"), S1[j].indexOf("To be continued"));
                String[] S3 = S2.split("\n");
                len = S3.length;

                for (int i = 4; i < len ; i++) {

                    //System.out.println(S3[i]);
                    //System.out.println(S3[i].substring(TRXPosition, TRXPosition+4).trim());
                    //System.out.println(S3[i].substring(TSPosition, TSPosition+3).trim());
                    TRX.addElement(S3[i].substring(TRXPosition, TRXPosition + 4).trim());
                    TS.addElement(S3[i].substring(TSPosition, TSPosition + 3).trim());
                    TRXTS.addElement("TRX" + S3[i].substring(TRXPosition, TRXPosition + 4).trim() + "-" + S3[i].substring(TSPosition, TSPosition + 3).trim());
                    currentChannel.addElement(S3[i].substring(ChannelPosition, ChannelPosition + 11).trim());
                    channelStatus.addElement(S3[i].substring(StatusPosition, StatusPositionEnd-1).trim());
                }
            } catch (Exception e4) {
                String S2 = S1[j].substring(S1[j].indexOf("Display channel state"), S1[j].indexOf("Number of results"));
                String[] S3 = S2.split("\n");
                for (int i = 4; i < S3.length - 1; i++) {
                    TRX.addElement(S3[i].substring(TRXPosition, TRXPosition + 4).trim());
                    TS.addElement(S3[i].substring(TSPosition, TSPosition + 3).trim());
                    TRXTS.addElement("TRX" + S3[i].substring(TRXPosition, TRXPosition + 4).trim() + "-" + S3[i].substring(TSPosition, TSPosition + 3).trim());
                    currentChannel.addElement(S3[i].substring(ChannelPosition, ChannelPosition + 11).trim());
                    channelStatus.addElement(S3[i].substring(StatusPosition, StatusPosition + 12).trim());
                }
            }
        }
        //for (int i = 0; i < TRX.size(); i++) {
        //    System.out.println(TRX.elementAt(i) + "\t" + TS.elementAt(i) + "\t" + TRXTS.elementAt(i)+ "\t" + currentChannel.elementAt(i)  + "\t" + channelStatus.elementAt(i));
        //}

        // ***************** Count Total No of TRX *******************************

        TRX_DI = new SortedVector();
        TRX_D = new Vector();
        String TRX_N = (String) TRX.elementAt(0);
        TRX_DI.addElement(Integer.parseInt(TRX_N));
        for (int i = 1; i < TRX.size(); i++) {
            if (!TRX_N.contains((String) TRX.elementAt(i))) {
                TRX_N = (String) TRX.elementAt(i);
                TRX_DI.addElement(Integer.parseInt(TRX_N));
            }
        }
        for (int i = 0; i < TRX_DI.size(); i++) {
            TRX_D.addElement(TRX_DI.elementAt(i).toString());
        }

        //for (int i = 0; i < TRX_D.size(); i++) {
        //    System.out.println(TRX_D.elementAt(i));
        //}


        JPanel JP[] = new JPanel[TRX_D.size()];
        Box subBody = Box.createVerticalBox();

        for (int abc = 0; abc < TRX_D.size(); abc++) {

            for (int i = 0; i < 8; i++) {
                TSA[i] = new Vector();
                TSB[i] = new Vector();
                TSC[i] = new Vector();
                if (!TSA[i].isEmpty()) {
                    TSA[i].removeAllElements();
                    TSB[i].removeAllElements();
                    TSC[i].removeAllElements();
                }
            }

            JP[abc] = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            JLabel JL = new JLabel("TRX-" + TRX_D.elementAt(abc) + ">>  ");
            JP[abc].add(JL);


            for (int i = 0; i < TRX.size(); i++) {
                String TRXTS_S = (String) TRX.elementAt(i);
                String Compare = (String) TRX_D.elementAt(abc);

                if (TRXTS_S.contains(Compare) && (TRXTS_S.length() == Compare.length())) {

                    //System.out.println(TRX.elementAt(i) + "\t" + TS.elementAt(i) + "\t" + TRXTS.elementAt(i) + "\t" + currentChannel.elementAt(i) + "\t" + channelStatus.elementAt(i));
                    TS_S = (String) TS.elementAt(i);
                    if (TS_S.contains("0")) {
                        TSA[0].addElement("0");
                        TSB[0].addElement((String) currentChannel.elementAt(i));
                        TSC[0].addElement((String) channelStatus.elementAt(i));
                    } else if (TS_S.contains("1")) {
                        TSA[1].addElement("1");
                        TSB[1].addElement((String) currentChannel.elementAt(i));
                        TSC[1].addElement((String) channelStatus.elementAt(i));
                    } else if (TS_S.contains("2")) {
                        TSA[2].addElement("2");
                        TSB[2].addElement((String) currentChannel.elementAt(i));
                        TSC[2].addElement((String) channelStatus.elementAt(i));
                    } else if (TS_S.contains("3")) {
                        TSA[3].addElement("3");
                        TSB[3].addElement((String) currentChannel.elementAt(i));
                        TSC[3].addElement((String) channelStatus.elementAt(i));
                    } else if (TS_S.contains("4")) {
                        TSA[4].addElement("4");
                        TSB[4].addElement((String) currentChannel.elementAt(i));
                        TSC[4].addElement((String) channelStatus.elementAt(i));
                    } else if (TS_S.contains("5")) {
                        TSA[5].addElement("5");
                        TSB[5].addElement((String) currentChannel.elementAt(i));
                        TSC[5].addElement((String) channelStatus.elementAt(i));
                    } else if (TS_S.contains("6")) {
                        TSA[6].addElement("6");
                        TSB[6].addElement((String) currentChannel.elementAt(i));
                        TSC[6].addElement((String) channelStatus.elementAt(i));
                    } else if (TS_S.contains("7")) {
                        TSA[7].addElement("7");
                        TSB[7].addElement((String) currentChannel.elementAt(i));
                        TSC[7].addElement((String) channelStatus.elementAt(i));
                    }
                }
            }

            //for (int i = 0; i < 8; i++){
            //    System.out.println(TSA[i]+" : "+TSB[i]+"    : "+TSC[i]);
            //}


            
            for (int i = 0; i < 8; i++) {
                if (TSA[i].size() == 1) {
                    //System.out.print(TSA[i].elementAt(0) + "\t");
                    //System.out.print(TSB[i].elementAt(0) + "\t");

                    String CName = (String) TSB[i].elementAt(0);

                    Color c;
                    String state = (String) TSC[i].elementAt(0);
                    //System.out.print(state.trim() + "\t");

                    c = SelectColor(state.trim());
                    JP[abc].add(TCHPanel(CName, c));
                    //System.out.println();
                } else if (TSA[i].size() == 2) {
                    //System.out.print(TSA[i].elementAt(0) + "\t");
                    //System.out.print(TSB[i].elementAt(0) + "\t");

                    String CName = (String) TSB[i].elementAt(0);
                    Color c[] = new Color[2];
                    for (int j = 0; j < 2; j++) {
                        String state = (String) TSC[i].elementAt(j);
                        c[j] = SelectColor(state.trim());
                        //System.out.print(state.trim() + "\t");
                    }
                    //System.out.println();
                    JP[abc].add(TCH2Panel(CName, c[0], c[1]));
                }
                else if (TSA[i].size() >2 ) {
                    //System.out.print(TSA[i].elementAt(0) + "\t");
                    //System.out.print(TSB[i].elementAt(0) + "\t");
                    String CName = (String) TSB[i].elementAt(0);
                    Color c[] = new Color[8];
                    //System.out.println("Timeslot : "+TSA[i].elementAt(0));
                    for (int j = 0; j < 8; j++) {
                        String state = (String) TSC[i].elementAt(j);
                        c[j] = SelectColor(state.trim());
                        //System.out.print(state.trim() + "\t");
                    }
                    //System.out.println();
                    JP[abc].add(SDCCHPanel(CName, c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7]));
                }

            } 
            JP[abc].setBorder(BorderFactory.createLineBorder(Color.lightGray));
            subBody.add(JP[abc]);
        }

        JPanel Mainbody1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        Mainbody1.add(subBody);

        return Mainbody1;
    }

    Color SelectColor(String state) {
        Color color;
        if (state.contains("Working")) {
            color = Color.green;
        } else if (state.contains("Idle")) {
            color = Color.lightGray;
        } else if (state.contains("Lock")) {
            color = Color.blue;
        }else {
            color = Color.red;
        }

        return color;
    }

    JPanel SDCCHPanel(String CName, Color c0, Color c1, Color c2, Color c3, Color c4, Color c5, Color c6, Color c7) {
        JLabel JL = new JLabel(CName);
        SDCCH sDCCH = new SDCCH(c0, c1, c2, c3, c4, c5, c6, c7);
        sDCCH.setPreferredSize(new Dimension(84, 14));
        sDCCH.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel JPsub = new JPanel(new BorderLayout());
        JPsub.add(JL, BorderLayout.NORTH);
        JPsub.add(sDCCH, BorderLayout.SOUTH);

        return JPsub;
    }

    JPanel TCHPanel(String CName, Color c0) {
        JLabel JL = new JLabel(CName);
        TCH tCH = new TCH(c0);
        tCH.setPreferredSize(new Dimension(84, 14));
        tCH.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel JPsub = new JPanel(new BorderLayout());
        JPsub.add(JL, BorderLayout.NORTH);
        JPsub.add(tCH, BorderLayout.SOUTH);

        return JPsub;
    }

    JPanel TCH2Panel(String CName, Color c0, Color c1) {
        JLabel JL = new JLabel(CName);
        TCH2 tCH = new TCH2(c0, c1);
        tCH.setPreferredSize(new Dimension(84, 14));
        tCH.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JPanel JPsub = new JPanel(new BorderLayout());
        JPsub.add(JL, BorderLayout.NORTH);
        JPsub.add(tCH, BorderLayout.SOUTH);

        return JPsub;
    }

    public class SDCCH extends JPanel {

        public Color C0, C1, C2, C3, C4, C5, C6, C7;

        SDCCH(Color c0, Color c1, Color c2, Color c3, Color c4, Color c5, Color c6, Color c7) {
            C0 = c0;
            C1 = c1;
            C2 = c2;
            C3 = c3;
            C4 = c4;
            C5 = c5;
            C6 = c6;
            C7 = c7;
        }

        public void paintComponent(Graphics g) {
            //g.drawString("Kamol", 1, 15);

            g.setColor(C0);
            g.fillOval(1, 1, 10, 10);

            g.setColor(C1);
            g.fillOval(11, 1, 10, 10);

            g.setColor(C2);
            g.fillOval(21, 1, 10, 10);

            g.setColor(C3);
            g.fillOval(31, 1, 10, 10);

            g.setColor(C4);
            g.fillOval(41, 1, 10, 10);

            g.setColor(C5);
            g.fillOval(51, 1, 10, 10);

            g.setColor(C6);
            g.fillOval(61, 1, 10, 10);

            g.setColor(C7);
            g.fillOval(71, 1, 10, 10);

        }
    }

    public class TCH extends JPanel {

        public Color C0;

        TCH(Color c0) {
            C0 = c0;
        }

        public void paintComponent(Graphics g) {
            //g.drawString("Kamol", 1, 15);
            g.setColor(C0);
            g.fillOval(1, 1, 10, 10);
        }
    }

    public class TCH2 extends JPanel {

        public Color C0, C1;

        TCH2(Color c0, Color c1) {
            C0 = c0;
            C1 = c1;
        }

        public void paintComponent(Graphics g) {
            //g.drawString("Kamol", 1, 15);
            g.setColor(C0);
            g.fillOval(1, 1, 10, 10);

            g.setColor(C1);
            g.fillOval(11, 1, 10, 10);
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
        }
        return Result;
    }

    public class SortedVector extends Vector {

        public SortedVector() {
            super();
        }

        public void addElement(Object o) {
            super.addElement(o);
            Collections.sort(this);
        }
    }

    public static void main(String args[]) {
        String SiteName, BSC_IP, Server_IP;
        SiteName = "JPSKB1";
        BSC_IP = "10.190.16.28";
        Server_IP = "10.191.16.139";
        String command = "DSP CHNSTAT: OBJTYPE=SITE, IDTYPE=BYNAME, BTSNAME=\"" + SiteName + "\";";
        ChannelImage_1 CI = new ChannelImage_1(command, BSC_IP, Server_IP, SiteName);
        CI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CI.setVisible(true);

    }
}

