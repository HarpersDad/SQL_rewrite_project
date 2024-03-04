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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.Style;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class change
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

    // starting function
    public static void changeTransaction(Date startDate, Date endDate, String user) throws IOException
    {
        int fontSize = 10;

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        try
        {
            // creates a pdf document writer and pdf document
            PdfWriter pWriter = new PdfWriter("C:/Test/change.pdf");
            PdfDocument pdf = new PdfDocument(pWriter);
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document doc = new Document(pdf);
            doc.setMargins(0f,0f,0f,0f);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new CommonFunctions.RotateEventHandler(PageSize.A4.rotate()));

            String startDateRange = startDate.toString().substring(5) + "/" + startDate.toString().substring(0,4);
            String endDateRange = endDate.toString().substring(5) + "/" + endDate.toString().substring(0,4);

            startDateRange = startDateRange.replace("-", "/");
            endDateRange = endDateRange.replace("-", "/");

            Text text1 = new Text("REPORT DATE  "+ dateToday +"                                 FCCO VOTER PROCESSING SYSTEM                                   PAGE            1\n");
            text1.setFontSize(fontSize);

            // Files ID to left side and Report Time to right side
            Text text2 = new Text("USER  "+CommonFunctions.addPaddingR(user, 55)+"CHANGE TRANSACTIONS                                          TIME  "+timeToday + "\n");
            text2.setFontSize(fontSize);

            // add date range
            Text text3 = new Text("INCLUDING ........:  DATES  FROM   "+startDateRange+"   TO   "+endDateRange + "\n");
            text3.setFontSize(fontSize);

            // column headings - this will take 3 rows
            Text text4_1 = new Text("LAST NAME                                         FIRST NAME          MIDDLE NAME                 ID #                          PROTECT\n");
            text4_1.setFontSize(fontSize);

            Text text4_2 = new Text("ADDRESS                                                                         MAILING ADDRESS\n");
            text4_2.setFontSize(fontSize);

            Text text4_3 = new Text("SEX    DATE OF BIRTH             PARTY      PREC                REG. DATE              HOME PHONE         WORK PHONE         CELL PHONE\n");
            text4_3.setFontSize(fontSize);

            Paragraph paragraph1 = new Paragraph();
            paragraph1.setTextAlignment(TextAlignment.LEFT);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setTextAlignment(TextAlignment.LEFT);

            paragraph1.add(text1).addStyle(normal);
            paragraph1.add(text2).addStyle(normal);
            paragraph1.add(text3).addStyle(normal);
            paragraph1.add(text4_1).addStyle(normal);
            paragraph1.add(text4_2).addStyle(normal);
            paragraph1.add(text4_3).addStyle(normal);
            paragraph1.add("--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

            doc.add(paragraph1);

            getDataForReport(doc, startDate.toString(), endDate.toString(), user);

            doc.close();

            String filePath = "C:/Test/change.pdf";
            File file = new File(filePath);

            Desktop.getDesktop().open(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
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
        String[] voterData = new String[19];
        String address;
        String zip;
        String ssn;

        start = start.replace("-", "");
        stop = stop.replace("-", "");

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[]," +
                    "[].[], " +
                    "[].[], " +
                    "[].[] " +
                    "FROM [] " +
                    "INNER JOIN [] " +
                    "ON [].[] = [].[] " +
                    "AND [].[] " +
                    "BETWEEN '"+start+"' AND '"+stop+"' " +
                    "ORDER BY [].[] ASC, [].[]");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                voterData[0] = rs.getString(3); 
                voterData[1] = rs.getString(4);  
                voterData[2] = rs.getString(5);  
                voterData[3] = rs.getString(8);   
                voterData[4] = rs.getString(7);   
                voterData[5] = rs.getString(2);   
                voterData[6] = rs.getString(11); 
                voterData[7] = rs.getString(10);  
                voterData[8] = rs.getString(1);  
                voterData[9] = rs.getString(6);   
                voterData[10] = rs.getString(12); 
                voterData[11] = rs.getString(13); 
                voterData[12] = rs.getString(14);
                voterData[13] = rs.getString(15); 
                voterData[14] = rs.getString(9); 
                voterData[15] = rs.getString(16);
                voterData[16] = rs.getString(17);
                voterData[17] = rs.getString(18);
                voterData[18] = rs.getString(19); 

                // fcstrt100
                String[] thisAddy = CommonFunctions.getAddress(voterData[5]);
                address = thisAddy[0] + " " + thisAddy[4] + " " + thisAddy[1] + " " + thisAddy[2];
                zip = thisAddy[3];

                String mailingAddress = CommonFunctions.getMailingAddress(voterData[8]);
                String homeAddress;

                // get ssn
                ssn = CommonFunctions.getSSN(voterData[8]);

                // format dates
                voterData[3] = voterData[3].substring(4,6) + "/" + voterData[3].substring(6) + "/" + voterData[3].substring(0,4);
                voterData[10] = voterData[10].substring(4,6) + "/" + voterData[10].substring(6) + "/" + voterData[10].substring(0,4);

                // check for null rank
                if (voterData[17] == null)
                {
                    voterData[17] = "";
                }

                for (int n = 0; n < 15; n++)
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

                if (!Objects.equals(voterData[9], ""))
                {

                    homeAddress = address + " " + voterData[9] + ", city, state " + zip;
                }
                else
                {
                    homeAddress = address + ", city, state " + zip;
                }

                if (Objects.equals(voterData[11], "0"))
                {
                    voterData[11] = "";
                }
                else
                {
                    voterData[11] = voterData[11].substring(0,3) + "-" + voterData[11].substring(3,6) + "-" + voterData[11].substring(6);
                }

                if (Objects.equals(voterData[12], "0"))
                {
                    voterData[12] = "";
                }
                else
                {
                    voterData[12] = voterData[12].substring(0,3) + "-" + voterData[12].substring(3,6) + "-" + voterData[12].substring(6);
                }

                if (Objects.equals(voterData[13], "0"))
                {
                    voterData[13] = "";
                }
                else
                {
                    voterData[13] = voterData[13].substring(0,3) + "-" + voterData[13].substring(3,6) + "-" + voterData[13].substring(6);
                }

                if (Objects.equals(voterData[16], "C") || Objects.equals(voterData[16], "E"))
                {
                    // add data to pdf document
                    //                     LAST NAME + RANK                                                           FIRST                                MIDDLE                  ID #                               SSN            PROTECT
                    Text text1 = new Text(CommonFunctions.addPaddingR(voterData[0] + " " + voterData[17], 50) + CommonFunctions.addPaddingR(voterData[1], 20) + CommonFunctions.addPaddingR(voterData[2], 28) + CommonFunctions.addPaddingR(voterData[8], 14) + CommonFunctions.addPaddingR(ssn, 16) + voterData[14] + "\n");
                    text1.setFontSize(10);

                    //                                      ADDRESS              mailing address
                    Text text2 = new Text(CommonFunctions.addPaddingR(homeAddress, 80) +  mailingAddress + "\n");
                    text2.setFontSize(10);

                    //                                 SEX                       DATE OF BIRTH                                PARTY                         PREC                           REG. DATE                                 HOME PHONE 11                   WORK PHONE  12                 CELL PHONE 13
                    Text text3 = new Text(CommonFunctions.addPaddingR(voterData[4], 8) + CommonFunctions.addPaddingR(voterData[3], 25) + CommonFunctions.addPaddingR(voterData[6], 11) + CommonFunctions.addPaddingR(voterData[7], 20) + CommonFunctions.addPaddingR(voterData[10], 23) + CommonFunctions.addPaddingR(voterData[11], 19) + CommonFunctions.addPaddingR(voterData[12], 19) + voterData[13] + "\n");
                    text3.setFontSize(10);

                    changedData = getRecordChanges(voterData[8], start, stop);

                    // changes made to record
                    Text text4 = new Text("PRECINCT Changed From .: " + CommonFunctions.addPaddingR(changedData[1] + " " + changedData[14], 25) + "SURNAME Changed From .: " + CommonFunctions.addPaddingR(changedData[4], 25) + "RANK Changed From .: " + CommonFunctions.addPaddingR(changedData[3], 10) + "MAILING ADDRESS .: " + CommonFunctions.addPaddingR(changedData[8], 5) + "HOME PHONE ..: " + changedData[11] + "\n");
                    Text text5 = new Text("PARTY Changed From ....: " + CommonFunctions.addPaddingR(changedData[2] + " " + changedData[15], 25)+ "GIVEN Changed From ...: " + CommonFunctions.addPaddingR(changedData[5], 25) + "ADDRESS ...........: " + CommonFunctions.addPaddingR(changedData[7], 10) + "SEX .............: " + CommonFunctions.addPaddingR(changedData[9], 5) + "WORK PHONE ..: " + changedData[12] + "\n");
                    Text text6 = new Text("ID NUMBER Changed From : " + CommonFunctions.addPaddingR(changedData[0], 25) + "MIDDLE Changed From ..: " + CommonFunctions.addPaddingR(changedData[6], 25) + "UNIT ..............: " + CommonFunctions.addPaddingR(changedData[16], 10) + "DATE OF BIRTH ...: " + CommonFunctions.addPaddingR(changedData[10], 5) + "CELL PHONE ..: " + changedData[13] + "\n");

                    Paragraph paragraph1 = new Paragraph();

                    paragraph1.add(text1).addStyle(normal);
                    paragraph1.add(text2).addStyle(normal);
                    paragraph1.add(text3).addStyle(normal);
                    paragraph1.add(text4).addStyle(smaller);
                    paragraph1.add(text5).addStyle(smaller);
                    paragraph1.add(text6).addStyle(smaller);
                    paragraph1.add("\n-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                    doc.add(paragraph1);

                    count++;

                    // adds header to the top of each new page
                    if (count == 5)
                    {
                        page++;
                        String paddedPage = "";

                        if (Integer.toString(page).length() == 1)
                        {
                            paddedPage = CommonFunctions.addPaddingR("PAGE", 15) + " " + page;
                        }

                        if (Integer.toString(page).length() == 2)
                        {
                            paddedPage = CommonFunctions.addPaddingR("PAGE", 14) + " " + page;
                        }

                        if (Integer.toString(page).length() == 3)
                        {
                            paddedPage = CommonFunctions.addPaddingR("PAGE", 13) + " " + page;
                        }

                        if (Integer.toString(page).length() == 4)
                        {
                            paddedPage = CommonFunctions.addPaddingR("PAGE", 12) + " " + page;
                        }

                        if (Integer.toString(page).length() == 5)
                        {
                            paddedPage = CommonFunctions.addPaddingR("PAGE", 11) + " " + page;
                        }

                        if (Integer.toString(page).length() == 6)
                        {
                            paddedPage = CommonFunctions.addPaddingR("PAGE", 10) + " " + page;
                        }

                        if (Integer.toString(page).length() == 7)
                        {
                            paddedPage = CommonFunctions.addPaddingR("PAGE", 9) + " " + page;
                        }

                        Text headerText1 = new Text("REPORT DATE  "+ dateToday +"                                 FCCO VOTER PROCESSING SYSTEM                                   " + paddedPage + "\n");
                        headerText1.setFontSize(10);

                        // Files ID to left side and Report Time to right side
                        Text headerText2 = new Text("USER  "+CommonFunctions.addPaddingR(user, 55)+"CHANGE TRANSACTIONS                                          TIME  "+timeToday + "\n");
                        headerText2.setFontSize(10);

                        // add date range
                        Text headerText3 = new Text("INCLUDING ........:  DATES  FROM   " + start.substring(4,6) + "/" + start.substring(6) + "/" + start.substring(0,4) +"   TO   " + stop.substring(4,6) + "/" + stop.substring(6) + "/" + stop.substring(0,4) + "\n");
                        headerText3.setFontSize(10);

                        // column headings - this will take 3 rows
                        Text headerText4_1 = new Text("LAST NAME                                         FIRST NAME          MIDDLE NAME                 ID #                          PROTECT\n");
                        headerText4_1.setFontSize(10);

                        Text headerText4_2 = new Text("ADDRESS                                                                         MAILING ADDRESS\n");
                        headerText4_2.setFontSize(10);

                        Text headerText4_3 = new Text("SEX    DATE OF BIRTH             PARTY      PREC                REG. DATE              HOME PHONE         WORK PHONE         CELL PHONE\n");
                        headerText4_3.setFontSize(10);

                        Paragraph header1 = new Paragraph();
                        header1.setTextAlignment(TextAlignment.LEFT);

                        header1.add(headerText1).addStyle(normal);
                        header1.add(headerText2).addStyle(normal);
                        header1.add(headerText3).addStyle(normal);
                        header1.add(headerText4_1).addStyle(normal);
                        header1.add(headerText4_2).addStyle(normal);
                        header1.add(headerText4_3).addStyle(normal);
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

    public static String[] getRecordChanges(String voterID, String start, String stop)
    {
        String[] voterInfo = new String[17];

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]," +
                    "[]" +
                    "FROM [].[].[]" +
                    "WHERE [] = '" + voterID + "' " +
                    "AND [] BETWEEN '"+start+"' AND '"+stop+"'")
;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                voterInfo[0] = rs.getString(1); 
                voterInfo[1] = rs.getString(2);
                voterInfo[2] = rs.getString(3); 
                voterInfo[3] = rs.getString(4); 
                voterInfo[4] = rs.getString(5);
                voterInfo[5] = rs.getString(6);
                voterInfo[6] = rs.getString(7); 
                voterInfo[7] = rs.getString(8);
                voterInfo[8] = rs.getString(9);
                voterInfo[9] = rs.getString(10);
                voterInfo[10] = rs.getString(11); 
                voterInfo[11] = rs.getString(12);
                voterInfo[12] = rs.getString(13);
                voterInfo[13] = rs.getString(14);
                voterInfo[14] = "";
                voterInfo[15] = ""; 
                voterInfo[16] = rs.getString(15); 
            }

            if (Objects.equals(voterInfo[0], "0"))
            {
                voterInfo[0] = "";
            }

            for (int w = 1; w < 17; w++)
            {
                if (voterInfo[w] == null)
                {
                    voterInfo[w] = "";
                }

                if (voterInfo[w] == null && w < 7)
                {
                    voterInfo[w] = "";
                }

                else if (w > 6 && !Objects.equals(voterInfo[w], ""))
                {
                    if (w != 15)
                    {
                        voterInfo[w] = "Y";
                    }
                }
            }

            if (voterInfo[1] != null)
            {
                voterInfo[14] = CommonFunctions.getPrecName(voterInfo[1]);
            }

            if (!Objects.equals(voterInfo[2], ""))
            {
                voterInfo[15] = CommonFunctions.getPartyChangeDate(voterID);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return voterInfo;
    }
}