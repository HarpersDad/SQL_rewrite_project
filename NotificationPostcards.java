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
import com.itextpdf.layout.property.TextAlignment;

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
    static DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    static String dateToday = thisDayAndTime.format(formatDate);

    // counts voters per page
    static int count = 0;

    // counts pages
    static int page = 1;

    public static void postcardNotification(Date startDate, Date endDate, String user) throws IOException
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

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new RotateEventHandler(PageSize.A4.rotate()));

            getDataForReport(doc, startDate.toString(), endDate.toString(), user);

            doc.close();

            // print report
            String filePath = "C:/Test/postCard.pdf";
            File file = new File(filePath);

            //Desktop.getDesktop().print(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // function that adds data to the report
    private static void getDataForReport(Document doc, String start, String stop, String user) throws IOException
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

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(10);

        Style smaller = new Style();
        PdfFont font2 = PdfFontFactory.createFont(FontConstants.COURIER);
        smaller.setFont(font2).setFontSize(8);

        String[] voterData = new String[9];

        start = start.replace("-", "");
        stop = stop.replace("-", "");

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[].[IDNUM300], " +
                    "[].[SURNAME300], " +
                    "[].[GIVEN300], " +
                    "[].[MIDDLE300], " +
                    "[].[RANK300], " +
                    "[].[STRID_300], " +
                    "[].[STRUNIT300]," +
                    "[].[PRECODE300], " +
                    "[].[TRANCD315]" +
                    "FROM [] " +
                    "INNER JOIN [] " +
                    "ON .IDNUM300 = .IDNUM315 " +
                    "AND .TRANDTE315 " +
                    "BETWEEN '"+start+"' AND '"+stop+"' " +
                    "ORDER BY [].[SURNAME300] ASC, [].[GIVEN300]")
                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                voterData[0] = rs.getString(1); // id
                voterData[1] = rs.getString(2); // surname
                voterData[2] = rs.getString(3); // given
                voterData[3] = rs.getString(4); // middle
                voterData[4] = rs.getString(5); // rank
                voterData[5] = rs.getString(6); // address code
                voterData[6] = rs.getString(7); // apt num
                voterData[7] = rs.getString(8); // precinct code
                voterData[8] = rs.getString(9); // trancd315

                for (int i = 0; i < 9; i++)
                {
                    if (voterData[i] == null)
                    {
                        voterData[i] = "";
                    }
                }

                String[] voterAddress = getVoterAddress(voterData[5]);
                String[] precinctData = getPrecinctData(voterData[7]);

                String addressString = voterAddress[0] + " " + voterAddress[1] + " " + voterAddress[2] + " " + voterAddress[3] + " " + voterData[6];
                String precinctString = precinctData[3] + " " + precinctData[5] + " " + precinctData[4] + "" + precinctData[6];

                // applied to vote
                if (Objects.equals(voterData[7], "A") || Objects.equals(voterData[7], "T")) // if trancd315 == A or T
                {
                    line0 = addPaddingR("Your application to register to", 35) + voterData[0] + "\n";
                    line1 = addPaddingR("vote has been received and", 35) + voterData[1] + voterData[4] + ", " + voterData[2] + " " + voterData[3] + "\n";
                    line2 = addPaddingR("accepted.  The name of your", 35) + addressString + "\n";
                    line3 = addPaddingR("precinct and voting location is.", 35) + " " + voterAddress[4] + "\n";
                    line4 = "\n";
                    line5 = precinctData[0] + "\n";
                    line6 = precinctString + "\n";
                    line7 = addPaddingR(precinctData[1], 35) + " Interested in becoming an Election Officer?\n";
                    line8 = addPaddingR(precinctData[2], 35) + " Volunteer at  or call .\n";
                    line9 = "\n";
                }

                // applied change
                else
                {
                    line0 = addPaddingR("The Voter Registration Office", 35) + voterData[0] + "\n";
                    line1 = addPaddingR("has processed a change to your", 35) + voterData[1] + voterData[4] + ", " + voterData[2] + " " + voterData[3] + "\n";
                    line2 = addPaddingR("current voter registration.", 35) + addressString + "\n";
                    line3 = addPaddingR("Your precinct and voting", 35) + " " + voterAddress[4] + "\n";
                    line4 = addPaddingR("location are shown below.", 35) + "\n";
                    line5 = "\n";
                    line6 = precinctData[0] + "\n";
                    line7 = precinctString + "\n";
                    line8 = addPaddingR(precinctData[1], 35) + " Interested in becoming an Election Officer?\n";
                    line9 = addPaddingR(precinctData[2], 35) + " Volunteer at  or call .\n";
                }

                int fontSize = 8;

                Text text1 = new Text("\n\n");
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
                Text text21 = new Text("\n");
                text21.setFontSize(fontSize);

                Paragraph paragraph1 = new Paragraph();

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

                doc.add(paragraph1);

                if (count == 10) {
                    Text blankSpace = new Text("\n");
                    paragraph1.add(blankSpace);
                }

                count++;

                // adds header to the top of each new page
                if (count == 11)
                {
                    count = 0;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String[] getVoterAddress(String addressCode)
    {

        String[] addressZip = new String[5];
        addressZip[0] = "";
        addressZip[1] = "";
        addressZip[2] = "";
        addressZip[3] = "";
        addressZip[4] = "";

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT [STREET_100],[STRNAME100],[STRDIR100],[STRTYPE100],[ZIP100] FROM [].[].[] WHERE [STRID_100] = '" + addressCode + "'");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // fcstrt100
                addressZip[0] = rs.getString(1); // num
                addressZip[1] = rs.getString(2); // name
                addressZip[2] = rs.getString(3); // direction
                addressZip[3] = rs.getString(4); // type
                addressZip[4] = rs.getString(5); // zip
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 5; i++)
        {
            if (addressZip[i] == null)
            {
                addressZip[i] = "";
            }
        }

        return addressZip;
    }

    public static String[] getPrecinctData(String precinctCode)
    {
        String[] precinctData = new String[7];

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[].[PRENAME500], " +
                    "[].[LOCNAME500], " +
                    "[].[PLACED500], " +
                    "[].[STRNUM501], " +
                    "[].[STRDIR501], " +
                    "[].[STRNAME501], " +
                    "[].[STRTYPE501] " +
                    "FROM [].[].[]" +
                    "INNER JOIN [] " +
                    "ON .LOCNAME500 = .LOCNAME501 " +
                    "WHERE [PRECODE500] = '" + precinctCode + "'");

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // the order that these are put into the array is the same as they are listed above
                precinctData[0] = rs.getString(1); // precinct name
                precinctData[1] = rs.getString(2); // location name
                precinctData[2] = rs.getString(3); // voting place
                precinctData[3] = rs.getString(4); // street number
                precinctData[4] = rs.getString(5); // street direction
                precinctData[5] = rs.getString(6); // street name
                precinctData[6] = rs.getString(7); // street type
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

    // function that adds whitespace to the string to pad the report
    public static String addPaddingR(String str, int R)
    {
        String s = "";

        StringBuilder strBuilder = new StringBuilder(s);

        if (str.length() > R)
        {
            str = str.substring(0,R-3);
        }

        strBuilder.append(" ".repeat(Math.max(0, R - str.length())));
        return str + strBuilder;
    }

    // function that sets the document orientation
    private record RotateEventHandler(PageSize newPageSize) implements IEventHandler
    {
        @Override
        public void handleEvent(Event event)
        {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            docEvent.getPage().setMediaBox(newPageSize);
        }
    }
}
