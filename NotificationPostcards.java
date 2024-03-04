package VoterRegistrationSystem;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class NotificationPostcards
{
    // formats dates
    static LocalDateTime thisDayAndTime = LocalDateTime.now();
    static DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // counts voters per page
    static int count = 0;

    public static void postcardNotification(Date startDate, Date endDate) throws IOException
    {
        int fontSize = 10;

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        try
        {
            // creates a pdf document writer and pdf document
            PdfWriter pWriter = new PdfWriter("C:/Test/postCard.pdf");
            PdfDocument pdf = new PdfDocument(pWriter);
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document doc = new Document(pdf);
            doc.setMargins(0f,0f,0f,0f);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new CommonFunctions.RotateEventHandler(PageSize.A4.rotate()));

            getDataForReport(doc, startDate.toString(), endDate.toString());

            doc.close();

            // print report
            String filePath = "C:/Test/postCard.pdf";
            File file = new File(filePath);

            Desktop.getDesktop().open(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // function that adds data to the report
    private static void getDataForReport(Document doc, String start, String stop) throws IOException
    {
        String line0 = "";
        String line1 = "";
        String line2 = "";
        String line3 = "";
        String line4 = "";
        String line5 = "";
        String line6 = "";
        String line7 = "";
        String line8 = "";
        String line9 = "";

        String[] voterData = new String[15];

        start = start.replace("-", "");
        stop = stop.replace("-", "");

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[] " +
                    "FROM [] " +
                    "INNER JOIN [] " +
                    "ON [].[] = [].[] " +
                    "AND [].[] " +
                    "BETWEEN '"+start+"' AND '"+stop+"' " +
                    "ORDER BY [].[] ASC, [].[]")
                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                voterData[0] = rs.getString(1);
                voterData[1] = rs.getString(2); 
                voterData[2] = rs.getString(3); 
                voterData[3] = rs.getString(4);
                voterData[4] = rs.getString(5); 
                voterData[5] = rs.getString(6); 
                voterData[6] = rs.getString(7); 
                voterData[7] = rs.getString(8);
                voterData[8] = rs.getString(9);
                voterData[9] = rs.getString(10); 
                voterData[10] = rs.getString(11);
                voterData[11] = rs.getString(12);
                voterData[12] = rs.getString(13);
                voterData[13] = rs.getString(14);
                voterData[14] = rs.getString(15);

                for (int i = 0; i < 14; i++)
                {
                    if (voterData[i] == null)
                    {
                        voterData[i] = "";
                    }
                }

                if (Objects.equals(voterData[13], start))
                {
                    String[] voterAddress = CommonFunctions.getAddress(voterData[5]);
                    String[] precinctData = getPrecinctData(voterData[7]);

                    String addressString = voterAddress[0] + " " + voterAddress[1] + " " + voterAddress[2] + " " + voterAddress[4] + " " + voterData[6];
                    String precinctString = precinctData[3] + " " + precinctData[5] + " " + precinctData[4] + precinctData[6];

                    // applied to vote
                    if (Objects.equals(voterData[8], "A") || Objects.equals(voterData[8], "T")) // if == A or T
                    {
                        line0 = CommonFunctions.addPaddingR("Your application to register to", 35) + voterData[0] + "\n";
                        line1 = CommonFunctions.addPaddingR("vote has been received and", 35) + voterData[1] + voterData[4] + ", " + voterData[2] + " " + voterData[3] + "\n";
                        line2 = CommonFunctions.addPaddingR("accepted.  The name of your", 35) + addressString + "\n";
                        line3 = CommonFunctions.addPaddingR("precinct and voting location is.", 35) + "city, state " + voterAddress[3] + "\n";
                        line4 = "\n";
                        line5 = precinctData[0] + "\n";
                        line6 = precinctString + "\n";
                        line7 = CommonFunctions.addPaddingR(precinctData[1], 35) + " Interested in becoming an Election Officer?\n";
                        line8 = CommonFunctions.addPaddingR(precinctData[2], 35) + " Volunteer at web.site or call ###-###-####.\n";
                        line9 = "\n";

                        setPDF(line0, line1, line2, line3, line4, line5, line6, line7, line8, line9, doc);
                    }

                    // applied change
                    else if (Objects.equals(voterData[8], "C"))
                    {
                        if (Objects.equals(voterData[10], "Y") || Objects.equals(voterData[11], "Y") || Objects.equals(voterData[14], "Y"))
                        {
                            line0 = CommonFunctions.addPaddingR("The Voter Registration Office", 35) + voterData[0] + "\n";
                            line1 = CommonFunctions.addPaddingR("has processed a change to your", 35) + voterData[1] + voterData[4] + ", " + voterData[2] + " " + voterData[3] + "\n";
                            line2 = CommonFunctions.addPaddingR("current voter registration.", 35) + addressString + "\n";
                            line3 = CommonFunctions.addPaddingR("Your precinct and voting", 35) + "city, state " + voterAddress[4] + "\n";
                            line4 = CommonFunctions.addPaddingR("location are shown below.", 35) + "\n";
                            line5 = "\n";
                            line6 = precinctData[0] + "\n";
                            line7 = precinctString + "\n";
                            line8 = CommonFunctions.addPaddingR(precinctData[1], 35) + " Interested in becoming an Election Officer?\n";
                            line9 = CommonFunctions.addPaddingR(precinctData[2], 35) + " Volunteer at web.site or call ###-###-####.\n";

                            setPDF(line0, line1, line2, line3, line4, line5, line6, line7, line8, line9, doc);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void setPDF(String line0, String line1, String line2, String line3, String line4, String line5, String line6, String line7, String line8, String line9, Document doc) throws IOException
    {
        int fontSize = 8;

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(10);

        Style smaller = new Style();
        PdfFont font2 = PdfFontFactory.createFont(FontConstants.COURIER);
        smaller.setFont(font2).setFontSize(8);

        Paragraph paragraph1 = new Paragraph();
        paragraph1.setMargins(0,0,0,370);

        if (count == 0)
        {
            Text text1 = new Text("\n\n\n\n\n");
            text1.setFontSize(fontSize);
            Text text2 = new Text("\n");
            text2.setFontSize(fontSize);
            Text text3 = new Text("\n");
            text3.setFontSize(fontSize);
            Text text4 = new Text("\n");
            text4.setFontSize(fontSize);
            Text text5 = new Text("\n");
            text5.setFontSize(fontSize);
            Text text6 = new Text("\n");
            text6.setFontSize(fontSize);
            Text text7 = new Text("\n");
            text7.setFontSize(fontSize);
            Text text8 = new Text("\n");
            text8.setFontSize(fontSize);
            Text text9 = new Text("\n");
            text9.setFontSize(fontSize);
            Text text10 = new Text(line0);
            text10.setFontSize(fontSize);
            Text text11 = new Text(line1);
            text11.setFontSize(fontSize);
            Text text12 = new Text(line2);
            text12.setFontSize(fontSize);
            Text text13 = new Text(line3);
            text13.setFontSize(fontSize);
            Text text14 = new Text(line4);
            text14.setFontSize(fontSize);
            Text text15 = new Text(line5);
            text15.setFontSize(fontSize);
            Text text16 = new Text(line6);
            text16.setFontSize(fontSize);
            Text text17 = new Text(line7);
            text17.setFontSize(fontSize);
            Text text18 = new Text(line8);
            text18.setFontSize(fontSize);
            Text text19 = new Text(line9);
            text19.setFontSize(fontSize);
            Text text20 = new Text("\n");
            text20.setFontSize(fontSize);
            Text text21 = new Text("\n\n\n\n\n");
            text21.setFontSize(fontSize);

            paragraph1.add(text1).addStyle(normal);
            paragraph1.add(text2).addStyle(normal);
            paragraph1.add(text3).addStyle(normal);
            paragraph1.add(text4).addStyle(normal);
            paragraph1.add(text5).addStyle(normal);
            paragraph1.add(text6).addStyle(normal);
            paragraph1.add(text7).addStyle(normal);
            paragraph1.add(text8).addStyle(normal);
            paragraph1.add(text9).addStyle(normal);
            paragraph1.add(text10).addStyle(normal);
            paragraph1.add(text11).addStyle(normal);
            paragraph1.add(text12).addStyle(normal);
            paragraph1.add(text13).addStyle(normal);
            paragraph1.add(text14).addStyle(normal);
            paragraph1.add(text15).addStyle(normal);
            paragraph1.add(text16).addStyle(normal);
            paragraph1.add(text17).addStyle(normal);
            paragraph1.add(text18).addStyle(normal);
            paragraph1.add(text19).addStyle(normal);
            paragraph1.add(text20).addStyle(normal);
            paragraph1.add(text21).addStyle(normal);

            count++;
        }
        else
        {
            Text text1 = new Text("\n\n\n\n\n");
            text1.setFontSize(fontSize);
            Text text2 = new Text("\n\n");
            text2.setFontSize(fontSize);
            Text text3 = new Text("\n\n");
            text3.setFontSize(fontSize);
            Text text4 = new Text("\n");
            text4.setFontSize(fontSize);
            Text text5 = new Text("\n");
            text5.setFontSize(fontSize);
            Text text6 = new Text("\n");
            text6.setFontSize(fontSize);
            Text text7 = new Text("\n");
            text7.setFontSize(fontSize);
            Text text8 = new Text("\n");
            text8.setFontSize(fontSize);
            Text text9 = new Text("\n");
            text9.setFontSize(fontSize);
            Text text10 = new Text(line0);
            text10.setFontSize(fontSize);
            Text text11 = new Text(line1);
            text11.setFontSize(fontSize);
            Text text12 = new Text(line2);
            text12.setFontSize(fontSize);
            Text text13 = new Text(line3);
            text13.setFontSize(fontSize);
            Text text14 = new Text(line4);
            text14.setFontSize(fontSize);
            Text text15 = new Text(line5);
            text15.setFontSize(fontSize);
            Text text16 = new Text(line6);
            text16.setFontSize(fontSize);
            Text text17 = new Text(line7);
            text17.setFontSize(fontSize);
            Text text18 = new Text(line8);
            text18.setFontSize(fontSize);
            Text text19 = new Text(line9);
            text19.setFontSize(fontSize);
            Text text20 = new Text("\n");
            text20.setFontSize(fontSize);
            Text text21 = new Text("\n\n\n");
            text21.setFontSize(fontSize);

            paragraph1.add(text1).addStyle(normal);
            paragraph1.add(text2).addStyle(normal);
            paragraph1.add(text3).addStyle(normal);
            paragraph1.add(text4).addStyle(normal);
            paragraph1.add(text5).addStyle(normal);
            paragraph1.add(text6).addStyle(normal);
            paragraph1.add(text7).addStyle(normal);
            paragraph1.add(text8).addStyle(normal);
            paragraph1.add(text9).addStyle(normal);
            paragraph1.add(text10).addStyle(normal);
            paragraph1.add(text11).addStyle(normal);
            paragraph1.add(text12).addStyle(normal);
            paragraph1.add(text13).addStyle(normal);
            paragraph1.add(text14).addStyle(normal);
            paragraph1.add(text15).addStyle(normal);
            paragraph1.add(text16).addStyle(normal);
            paragraph1.add(text17).addStyle(normal);
            paragraph1.add(text18).addStyle(normal);
            paragraph1.add(text19).addStyle(normal);
            paragraph1.add(text20).addStyle(normal);
            paragraph1.add(text21).addStyle(normal);

            count = 0;
        }

        doc.add(paragraph1);
    }

    public static String[] getPrecinctData(String precinctCode)
    {
        String[] precinctData = new String[7];

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[], " +
                    "[].[] " +
                    "FROM [].[].[]" +
                    "INNER JOIN [] " +
                    "ON [].[] = [].[] " +
                    "WHERE [] = '" + precinctCode + "'");

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // the order that these are put into the array is the same as they are listed above
                precinctData[0] = rs.getString(1); 
                precinctData[1] = rs.getString(2);
                precinctData[2] = rs.getString(3);
                precinctData[3] = rs.getString(4); 
                precinctData[4] = rs.getString(5); 
                precinctData[5] = rs.getString(6); 
                precinctData[6] = rs.getString(7);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 7; i++)
        {
            if (precinctData[i] == null)
            {
                precinctData[i] = "";
            }
        }

        return precinctData;
    }
}
