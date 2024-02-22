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

public class delete
{
    // formats dates
    static LocalDateTime thisDayAndTime = LocalDateTime.now();
    static DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    static DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    static String dateToday = thisDayAndTime.format(formatDate);
    static String timeToday = thisDayAndTime.format(formatTime);

    // counts voters per page
    static int count = 0;

    // counts pages
    static int page = 1;

    public static void deleteTransaction(Date startDate, Date endDate, String user) throws IOException
    {
        int fontSize = 10;

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        try
        {
            // creates a pdf document writer and pdf document
            PdfWriter pWriter = new PdfWriter("C:/Test/delete.pdf");
            PdfDocument pdf = new PdfDocument(pWriter);
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document doc = new Document(pdf);
            doc.setMargins(0f,0f,0f,0f);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new RotateEventHandler(PageSize.A4.rotate()));

            String startDateRange = startDate.toString().substring(5) + "/" + startDate.toString().substring(0,4);
            String endDateRange = endDate.toString().substring(5) + "/" + endDate.toString().substring(0,4);

            startDateRange = startDateRange.replace("-", "/");
            endDateRange = endDateRange.replace("-", "/");

            Text text1 = new Text("REPORT DATE  "+ dateToday +"                                 VOTER PROCESSING SYSTEM                                   PAGE            1\n");
            text1.setFontSize(fontSize);

            // Files ID to left side and Report Time to right side
            Text text2 = new Text("USER  "+addPaddingR(user, 55)+"DELETE TRANSACTIONS                                          TIME  "+timeToday + "\n");
            text2.setFontSize(fontSize);

            // add date range
            Text text3 = new Text("INCLUDING ........:  DATES  FROM   "+startDateRange+"   TO   "+endDateRange + "\n");
            text3.setFontSize(fontSize);

            // column headings
            Text text4_1 = new Text("LAST NAME                                   FIRST NAME         MIDDLE NAME          ID #                        REASON\n");
            text4_1.setFontSize(fontSize);

            Paragraph paragraph1 = new Paragraph();
            paragraph1.setTextAlignment(TextAlignment.LEFT);

            paragraph1.add(text1).addStyle(normal);
            paragraph1.add(text2).addStyle(normal);
            paragraph1.add(text3).addStyle(normal);
            paragraph1.add(text4_1).addStyle(normal);
            paragraph1.add("--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

            doc.add(paragraph1);

            getDataForReport(doc, startDate.toString(), endDate.toString(), user);

            doc.close();

            // print report
            String filePath = "C:/Test/delete.pdf";
            File file = new File(filePath);

            //Desktop.getDesktop().print(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getSSN(String idNum)
    {
        String ssn = "";

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT [SSN303] FROM [].[].[] WHERE [IDNUM303] = '" + idNum + "'");
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                ssn = rs.getString(1);


                return ssn;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return ssn;
    }

    // function that adds data to the report
    private static void getDataForReport(Document doc, String start, String stop, String user) throws IOException
    {
        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(10);

        Style smaller = new Style();
        PdfFont font2 = PdfFontFactory.createFont(FontConstants.COURIER);
        smaller.setFont(font2).setFontSize(8);

        String[] voterData = new String[8];
        String ssn;

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
                    "[].[TRANCD307], " +
                    "[].[REASON307], " +
                    "[].[STATUS300]" +
                    "FROM [] " +
                    "INNER JOIN [] " +
                    "ON .IDNUM300 = .IDNUM307 " +
                    "AND .TRANDTE307 " +
                    "BETWEEN '"+start+"' AND '"+stop+"' " +
                    "ORDER BY [].[SURNAME300] ASC, [].[GIVEN300]")
                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // vpmast300
                voterData[0] = rs.getString(1);  // id
                voterData[1] = rs.getString(2);  // last
                voterData[2] = rs.getString(3);  // first
                voterData[3] = rs.getString(4);  // middle
                voterData[4] = rs.getString(5);  // rank
                voterData[5] = rs.getString(6);  // code
                voterData[6] = rs.getString(7);  // reason
                voterData[7] = rs.getString(8);  // status

                // get ssn
                ssn = getSSN(voterData[0]);

                String formattedSSN = ssn.substring(0,3) + "-" + ssn.substring(3,5) + "-" + ssn.substring(5);

                // check for null rank
                if (voterData[4] == null)
                {
                    voterData[4] = "";
                }

                for (int n = 0; n < 7; n++)
                {
                    // checks for a null value, and replaces it with an empty string
                    if (voterData[n] == null)
                    {
                        voterData[n] = "";
                    }
                    else
                    {
                        voterData[n] = voterData[n].trim();
                    }
                }

                // add data to pdf document
                if (Objects.equals(voterData[7], "D"))
                {
                    //                               LAST NAME + RANK                                             FIRST                                MIDDLE                        ID #                               SSN                                    REASON
                    Text text1 = new Text(addPaddingR(voterData[1] + " " + voterData[4], 44) + addPaddingR(voterData[2], 19) + addPaddingR(voterData[3], 21) + addPaddingR(voterData[0], 11) + addPaddingR(formattedSSN, 17) + voterData[5] + " " + voterData[6] + "\n");
                    text1.setFontSize(10);

                    Paragraph paragraph1 = new Paragraph();

                    paragraph1.add(text1).addStyle(normal);

                    paragraph1.add("\n--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                    doc.add(paragraph1);

                    if (count == 10)
                    {
                        Text blankSpace = new Text("\n");

                        paragraph1.add(blankSpace);
                    }

                    count++;

                    // adds header to the top of each new page
                    if (count == 11) // subtract 2
                    {
                        // sets page number
                        page++;
                        String paddedPage = "";

                        if (Integer.toString(page).length() == 1)
                        {
                            paddedPage = addPaddingR("PAGE", 15) + " " + page;
                        }

                        if (Integer.toString(page).length() == 2)
                        {
                            paddedPage = addPaddingR("PAGE", 14) + " " + page;
                        }

                        if (Integer.toString(page).length() == 3)
                        {
                            paddedPage = addPaddingR("PAGE", 13) + " " + page;
                        }

                        if (Integer.toString(page).length() == 4)
                        {
                            paddedPage = addPaddingR("PAGE", 12) + " " + page;
                        }

                        if (Integer.toString(page).length() == 5)
                        {
                            paddedPage = addPaddingR("PAGE", 11) + " " + page;
                        }

                        if (Integer.toString(page).length() == 6)
                        {
                            paddedPage = addPaddingR("PAGE", 10) + " " + page;
                        }

                        if (Integer.toString(page).length() == 7)
                        {
                            paddedPage = addPaddingR("PAGE", 9) + " " + page;
                        }

                        Text headerText1 = new Text("REPORT DATE  "+ dateToday +"                                 VOTER PROCESSING SYSTEM                                   " + paddedPage + "\n");
                        headerText1.setFontSize(10);

                        // Files ID to left side and Report Time to right side
                        Text headerText2 = new Text("USER  "+addPaddingR(user, 55)+"DELETE TRANSACTIONS                                          TIME  "+timeToday + "\n");
                        headerText2.setFontSize(10);

                        // add date range
                        Text headerText3 = new Text("INCLUDING ........:  DATES  FROM   " + start.substring(4,6) + "/" + start.substring(6) + "/" + start.substring(0,4) +"   TO   " + stop.substring(4,6) + "/" + stop.substring(6) + "/" + stop.substring(0,4) + "\n");
                        headerText3.setFontSize(10);

                        // column headings - this will take 3 rows
                        Text headerText4_1 = new Text("LAST NAME                                   FIRST NAME         MIDDLE NAME          ID #                        REASON\n");

                        Paragraph header1 = new Paragraph();
                        header1.setTextAlignment(TextAlignment.LEFT);

                        header1.add(headerText1).addStyle(normal);
                        header1.add(headerText2).addStyle(normal);
                        header1.add(headerText3).addStyle(normal);
                        header1.add(headerText4_1).addStyle(normal);
                        header1.add("--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                        doc.add(header1);

                        count = 0;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // function that adds whitespace to the string to pad the report
    public static String addPaddingR(String str, int R)
    {
        String s = "";

        StringBuilder strBuilder = new StringBuilder(s);

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
