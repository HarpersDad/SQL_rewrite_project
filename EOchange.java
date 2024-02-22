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

public class EOchange
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

    public static void eoChangeTransaction(Date startDate, Date endDate, String user) throws IOException
    {
        int fontSize = 10;

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        try
        {
            // creates a pdf document writer and pdf document
            PdfWriter pWriter = new PdfWriter("C:/Test/eoChange.pdf");
            PdfDocument pdf = new PdfDocument(pWriter);
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document doc = new Document(pdf);
            doc.setMargins(0f,0f,0f,0f);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new RotateEventHandler(PageSize.A4.rotate()));

            String startDateRange = startDate.toString().substring(5) + "/" + startDate.toString().substring(0,4);
            String endDateRange = endDate.toString().substring(5) + "/" + endDate.toString().substring(0,4);

            startDateRange = startDateRange.replace("-", "/");
            endDateRange = endDateRange.replace("-", "/");

            Text text1 = new Text("REPORT DATE  "+ dateToday +"                                 VOTER PROCESSING SYSTEM                                      PAGE         1\n");
            text1.setFontSize(fontSize);

            // Files ID to left side and Report Time to right side
            Text text2 = new Text("USER  "+addPaddingR(user, 53)+"EO CHANGE TRANSACTIONS                                         TIME  "+timeToday + "\n");
            text2.setFontSize(fontSize);

            // add date range
            Text text3 = new Text("INCLUDING ........:  DATES  FROM   "+startDateRange+"   TO   "+endDateRange + "\n");
            text3.setFontSize(fontSize);

            // column headings
            Text text4_1 = new Text("LAST NAME                       FIRST NAME         MIDDLE NAME          ID #                      PRECINCT                PTY  POS  CODE\n");
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
            String filePath = "C:/Test/eoChange.pdf";
            File file = new File(filePath);

            //Desktop.getDesktop().print(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // gets voter address
    private static String getAddress(String code)
    {
        String[] addressZip = new String[5];

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT [STREET_100],[STRNAME100],[STRTYPE100],[ZIP100], [STRDIR100] FROM [].[].[] WHERE [STRID_100] = '" + code + "'");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                addressZip[0] = rs.getString(1); // num
                addressZip[1] = rs.getString(2); // name
                addressZip[2] = rs.getString(3); // type
                addressZip[3] = rs.getString(4); // zip
                addressZip[4] = rs.getString(5); // street direction
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
            else
            {
                addressZip[i] = " " + addressZip[i];
            }
        }
        return addressZip[0] + addressZip[4] + addressZip[1] + addressZip[2];
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

        String[] changedData;
        String[] voterData = new String[16];
        String ssn;

        start = start.replace("-", "");
        stop = stop.replace("-", "");

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[].[IDNUM300]," +
                    "[].[SURNAME300]," +
                    "[].[GIVEN300]," +
                    "[].[MIDDLE300]," +
                    "[].[PRECODE300]," +
                    "[].[PARTY300]," +
                    "[].[RANK300], " +
                    "[].[TRANCD315], " +
                    "[].[TRANDTE300], " +
                    "[].[STRID_300], " +
                    "[].[STRUNIT300], " +
                    "[].[PTYCDTE300], " +
                    "[].[DOB300], " +
                    "[].[HOMEPHN300], " +
                    "[].[WORKPHN300], " +
                    "[].[CELLPHN300] " +
                    "FROM [] " +
                    "INNER JOIN [] " +
                    "ON .IDNUM300 = .IDNUM315 " +
                    "INNER JOIN [] " +
                    "ON .IDNUM315 = .IDNUM320 " +
                    "AND .TRANDTE315 " +
                    "BETWEEN '"+start+"' AND '"+stop+"' " +
                    "ORDER BY [].[SURNAME300] ASC, [].[GIVEN300]");

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                voterData[0] = rs.getString(1);   // id
                voterData[1] = rs.getString(2);   // last
                voterData[2] = rs.getString(3);   // first
                voterData[3] = rs.getString(4);   // middle
                voterData[4] = rs.getString(5);   // precinct
                voterData[5] = rs.getString(6);   // party
                voterData[6] = rs.getString(7);   // rank
                voterData[7] = rs.getString(8);   // trans code
                voterData[8] = rs.getString(9);   // trans date
                voterData[9] = rs.getString(10);  // address code
                voterData[10] = rs.getString(13); // dob
                voterData[11] = rs.getString(11); // unit
                voterData[12] = rs.getString(12); // party change date
                voterData[13] = rs.getString(14); // home
                voterData[14] = rs.getString(15); // work
                voterData[15] = rs.getString(16); // cell

                // get ssn
                ssn = getSSN(voterData[0]);

                String formattedSSN = "";

                if (ssn.length() <= 1)
                {
                    formattedSSN = "0";
                }
                if (ssn.length() == 8)
                {
                    formattedSSN = "+" + ssn.substring(0,2) + "-" + ssn.substring(2,4) + "-" + ssn.substring(4);
                }
                if (ssn.length() == 9)
                {
                    formattedSSN = ssn.substring(0,3) + "-" + ssn.substring(3,5) + "-" + ssn.substring(5);
                }


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
                if (Objects.equals(voterData[7], "T") || Objects.equals(voterData[7], "E"))
                {
                    String[] eoCodes = getEOCodes(voterData[0]);

                    String address = "";

                    changedData = getRecordChanges(voterData[0], start, stop);

                    for (int i = 0; i < 7; i++)
                    {
                        if (!Objects.equals(changedData[i], ""))
                        {
                            String temp = voterData[i];
                            voterData[i] = changedData[i];
                            changedData[i] = temp;
                        }
                    }

                    if (!Objects.equals(changedData[7], ""))
                    {
                        address = getAddress(voterData[9]);
                    }

                    if (voterData[11] == null)
                    {
                        voterData[11] = "";
                    }

                    if (Objects.equals(changedData[8], "Y"))
                    {
                        changedData[8] = getMailingAddress(voterData[0]);
                    }

                    if (!Objects.equals(changedData[10], ""))
                    {
                        changedData[10] = voterData[10].substring(4,6) + "/" + voterData[10].substring(6) + "/" + voterData[10].substring(0,4);
                    }

                    if (Objects.equals(changedData[11], "Y"))
                    {
                        if (voterData[13].length() == 10)
                        {
                            changedData[11] = voterData[13].substring(0, 3) + "-" + voterData[13].substring(3, 6) + "-" + voterData[13].substring(6);
                        }
                        else
                        {
                            changedData[11] = "REMOVED";
                        }
                    }

                    if (Objects.equals(changedData[12], "Y"))
                    {
                        if (voterData[14].length() == 10)
                        {
                            changedData[12] = voterData[14].substring(0, 3) + "-" + voterData[14].substring(3, 6) + "-" + voterData[14].substring(6);
                        }
                        else
                        {
                            changedData[12] = "REMOVED";
                        }
                    }

                    if (Objects.equals(changedData[13], "Y"))
                    {
                        if (voterData[15].length() == 10)
                        {
                            changedData[13] = voterData[15].substring(0, 3) + "-" + voterData[15].substring(3, 6) + "-" + voterData[15].substring(6);
                        }
                        else
                        {
                            changedData[13] = "REMOVED";
                        }
                    }

                    //                               LAST NAME + RANK                                             FIRST                                MIDDLE                        ID #                               SSN                                   PRECINCT                                                                  PTY                                POS                     CODE
                    Text text1 = new Text(addPaddingR(voterData[1] + " " + voterData[6], 32) + addPaddingR(voterData[2], 19) + addPaddingR(voterData[3], 21) + addPaddingR(voterData[0], 11) + addPaddingR(formattedSSN, 15) + addPaddingR(voterData[4] + " " + getPrecName(voterData[4]), 24) + addPaddingR(voterData[5], 5) + addPaddingR(eoCodes[1], 5) + eoCodes[0] + "\n");
                    text1.setFontSize(10);

                    // changes made to record
                    Text text4 = new Text("PRECINCT Changed To .......: " + addPaddingR(changedData[4] + " " + getPrecName(changedData[4]), 26) + "SURNAME Changed To .: " + addPaddingR(changedData[1], 21) +    "RANK Changed To .........: " + addPaddingR(changedData[6], 14) +                             "HOME PHONE Changed To : " + addPaddingR(changedData[11], 4) + "\n");
                    Text text5 = new Text("ADDRESS Changed To ........:" + addPaddingR(address, 27) +                                                 "GIVEN Changed To ...: " + addPaddingR(changedData[2], 21) +     "DATE OF BIRTH Changed To : " + addPaddingR(changedData[10], 14) +                            "WORK PHONE Changed To : " + addPaddingR(changedData[12], 4) + "\n");
                    Text text6 = new Text("UNIT Changed To ...........: " + addPaddingR(voterData[11], 26) +                                          "MIDDLE Changed To ..: " + addPaddingR(changedData[3], 21) +     "ID NUMBER Changed To ....: " + addPaddingR(changedData[0], 14) +                             "CELL PHONE Changed To : " + addPaddingR(changedData[13], 4) + "\n");
                    Text text7 = new Text("MAILING ADDRESS Changed To : " + (addPaddingR(changedData[8], 26)) +                                       "ZIP CODE Changed To : " + (addPaddingR(changedData[9], 21)) +   "PARTY Changed To ........: " + addPaddingR(changedData[5] + " " + changedData[15], 14) + "\n");

                    Paragraph paragraph1 = new Paragraph();

                    paragraph1.add(text1).addStyle(normal);
                    paragraph1.add(text4).addStyle(smaller);
                    paragraph1.add(text5).addStyle(smaller);
                    paragraph1.add(text6).addStyle(smaller);
                    paragraph1.add(text7).addStyle(smaller);

                    paragraph1.add("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                    doc.add(paragraph1);

                    if (count == 5)
                    {
                        Text blankSpace = new Text("\n");

                        paragraph1.add(blankSpace);
                    }

                    count++;

                    // adds header to the top of each new page
                    if (count == 6)
                    {
                        // sets page number
                        page++;
                        String paddedPage = "";

                        if (Integer.toString(page).length() == 1)
                        {
                            paddedPage = addPaddingR("PAGE", 12) + " " + page;
                        }

                        if (Integer.toString(page).length() == 2)
                        {
                            paddedPage = addPaddingR("PAGE", 11) + " " + page;
                        }

                        if (Integer.toString(page).length() == 3)
                        {
                            paddedPage = addPaddingR("PAGE", 10) + " " + page;
                        }

                        if (Integer.toString(page).length() == 4)
                        {
                            paddedPage = addPaddingR("PAGE", 9) + " " + page;
                        }

                        if (Integer.toString(page).length() == 5)
                        {
                            paddedPage = addPaddingR("PAGE", 8) + " " + page;
                        }

                        if (Integer.toString(page).length() == 6)
                        {
                            paddedPage = addPaddingR("PAGE", 7) + " " + page;
                        }

                        if (Integer.toString(page).length() == 7)
                        {
                            paddedPage = addPaddingR("PAGE", 6) + " " + page;
                        }

                        Text headerText1 = new Text("\n\nREPORT DATE  "+ dateToday +"                                 VOTER PROCESSING SYSTEM                                      " + paddedPage + "\n");
                        headerText1.setFontSize(10);

                        // Files ID to left side and Report Time to right side
                        Text headerText2 = new Text("USER  "+addPaddingR(user, 53)+"EO CHANGE TRANSACTIONS                                         TIME  "+timeToday + "\n");
                        headerText2.setFontSize(10);

                        // add date range
                        Text headerText3 = new Text("INCLUDING ........:  DATES  FROM   " + start.substring(4,6) + "/" + start.substring(6) + "/" + start.substring(0,4) +"   TO   " + stop.substring(4,6) + "/" + stop.substring(6) + "/" + stop.substring(0,4) + "\n");
                        headerText3.setFontSize(10);

                        // column headings - this will take 3 rows
                        Text headerText4_1 = new Text("LAST NAME                       FIRST NAME         MIDDLE NAME          ID #                      PRECINCT                PTY  POS  CODE\n");

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

    public static String getMailingAddress(String voterID)
    {
        String[] mailingAddress = new String[5];
        String address = "";

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[STRNUM301], " +
                    "[STRNAME301] " +
                    "FROM [].[].[]" +
                    "WHERE [IDNUM301] = '" + voterID + "'");

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                mailingAddress[0] = rs.getString(1); // street number
                mailingAddress[1] = rs.getString(2); // street name
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if (mailingAddress[0] != null)
        {
            if (mailingAddress[1] != null)
            {
                address = mailingAddress[0] + " " + mailingAddress[1];
            }
        }

        return address;
    }

    public static String[] getEOCodes(String voterID)
    {
        String[] voterCodes = new String[2];

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[EOCODE320], " +
                    "[EOPOS320] " +
                    "FROM [].[].[]" +
                    "WHERE [IDNUM320] = '" + voterID + "'")                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                voterCodes[0] = rs.getString(1); // eo code
                voterCodes[1] = rs.getString(2); // eo pos
            }

            for (int i = 0; i < 2; i++)
            {
                if (voterCodes[i] == null)
                {
                    voterCodes[i] = "";
                }
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return voterCodes;
    }

    public static String[] getRecordChanges(String voterID, String start, String stop)
    {
        String[] voterInfo = new String[19];

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[PIDNUM315]," +
                    "[PPREC_315]," +
                    "[PPARTY315]," +
                    "[PRANK315]," +
                    "[PSURNME315]," +
                    "[PGIVEN315]," +
                    "[PMIDDLE315]," +
                    "[CADDR315]," +
                    "[CMADDR315]," +
                    "[CSEX315]," +
                    "[CDOB315]," +
                    "[CHOMEPH315]," +
                    "[CWORKPH315]," +
                    "[CCELLPH315]," +
                    "[CAPTNUM315]," +
                    "[TRANDTE315]," +
                    "[CPARTY315]" +
                    "FROM [].[].[]" +
                    "WHERE [IDNUM315] = '" + voterID + "' " +
                    "AND [TRANDTE315] BETWEEN '"+start+"' AND '"+stop+"'")
                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // the order that these are put into the array is the same as they are listed above
                voterInfo[0] = rs.getString(1); // pidnum 1
                voterInfo[1] = rs.getString(5); // psurname 5
                voterInfo[2] = rs.getString(6); // pgiven 6
                voterInfo[3] = rs.getString(7); // pmiddle 7
                voterInfo[4] = rs.getString(2); // pprec 2
                voterInfo[5] = rs.getString(3); // pparty 3
                voterInfo[6] = rs.getString(4); // prank 4
                voterInfo[7] = rs.getString(8); // caddr 8
                voterInfo[8] = rs.getString(16); // cmaddr 9
                voterInfo[9] = rs.getString(10); // csex 10
                voterInfo[10] = rs.getString(11); // cdob 11
                voterInfo[11] = rs.getString(12); // chome 12
                voterInfo[12] = rs.getString(13); // cwork 13
                voterInfo[13] = rs.getString(14); // ccell 14
                voterInfo[14] = ""; // prec name
                voterInfo[15] = ""; // party change date
                voterInfo[16] = rs.getString(15); // apt num 15
                voterInfo[17] = rs.getString(16); // tran dte 16
                voterInfo[18] = rs.getString(17); // changed party 16
            }

            if (Objects.equals(voterInfo[0], "0"))
            {
                voterInfo[0] = "";
            }

            if (voterInfo[0] == null)
            {
                voterInfo[0] = "";
            }

            for (int i = 1; i < 19; i++)
            {
                if (voterInfo[i] == null)
                {
                    voterInfo[i] = "";
                }
            }

            for (int w = 1; w < 18; w++)
            {
                if (w > 6 && !Objects.equals(voterInfo[w], ""))
                {
                    voterInfo[w] = "Y";
                }
            }

            if (voterInfo[1] != null)
            {
                voterInfo[14] = getPrecName(voterInfo[4]);
            }

            voterInfo[15] = getPartyChangeDate(voterID);

            if (Objects.equals(voterInfo[5], ""))
            {
                voterInfo[15] = "";
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return voterInfo;
    }

    public static String getPrecName(String precID)
    {
        String preName = "";

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[PRENAME500] " +
                    "FROM [].[].[]" +
                    "WHERE [PRECODE500] = '" + precID + "'")
                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // the order that these are put into the array is the same as they are listed above
                preName = rs.getString(1);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return preName;
    }

    public static String getPartyChangeDate(String voterID)
    {
        String cDate = "";
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[PTYCDTE300] " +
                    "FROM [].[].[]" +
                    "WHERE [IDNUM300] = '" + voterID + "'")
                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // the order that these are put into the array is the same as they are listed above
                cDate = rs.getString(1);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if (cDate.length() == 8)
        {
            return "on " + cDate.substring(4,6) + "/" + cDate.substring(6) + "/" + cDate.substring(0,4);
        }
        else
        {
            return "";
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
