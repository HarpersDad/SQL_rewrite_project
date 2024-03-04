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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PrecinctList
{
    static LocalDateTime thisDayAndTime = LocalDateTime.now();
    static DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    static DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    static String dateToday = thisDayAndTime.format(formatDate);
    static String timeToday = thisDayAndTime.format(formatTime);
    static int count = 0;
    static int page = 1;
    static int fontSize = 9;
    static String distType = "";

    static int lineFormat = 21;
    static int headerFormat = 22;

    static boolean includeAddress = false;
    static String sortingMetric = "";

    public static void getPrecincts(String user, String startingPrecinct, String endingPrecinct, String district, boolean addressFlag) throws IOException
    {
        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        if (Objects.equals(endingPrecinct, ""))
        {
            endingPrecinct = startingPrecinct;
        }

        if (addressFlag)
        {
            lineFormat = 14;
            headerFormat = 15;
            includeAddress = addressFlag;
        }

        if (!Objects.equals(district, ""))
        {
            distType = switch (district)
            {
                case "" -> "";
                case "" -> "";
                case "" -> "";
                case "" -> "";
                case "" -> "";
                default -> "";
            };
        }
        else
        {
            if (startingPrecinct.contains("1") || startingPrecinct.contains("2"))
            {
                distType = "";
                sortingMetric = "";
            }
            else
            {
                distType = "";
            }
        }

        try
        {
            // creates a pdf document writer and pdf document
            PdfWriter pWriter = new PdfWriter("C:/Test/Precinct.pdf");
            PdfDocument pdf = new PdfDocument(pWriter);
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document doc = new Document(pdf);
            doc.setMargins(0f,0f,0f,0f);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new CommonFunctions.RotateEventHandler(PageSize.A4.rotate()));

            Text text1 = new Text("REPORT DATE  "+ dateToday +"                                        FCCO VOTER PROCESSING SYSTEM                                                 PAGE         1\n");
            text1.setFontSize(fontSize); 

            // Files ID to left side and Report Time to right side          LOCATION LIST
            Text text2 = new Text("USER  "+CommonFunctions.addPaddingR(user, 60)+"VOTER PRECINCT LISTING                                                    TIME  "+timeToday + "\n");
            text2.setFontSize(fontSize);

            // add date range
            Text text3 = new Text("INCLUDING ........:  ALL PRECINCTS from " + startingPrecinct+" TO "+endingPrecinct+" by " + distType + "\n");
            text3.setFontSize(fontSize);

            // column headings
            Text text4_1 = new Text(CommonFunctions.addPaddingR("CODE", 10) + CommonFunctions.addPaddingR("PRECINCT NAME", 25) + CommonFunctions.addPaddingR("VOTING LOCATION", 85) + CommonFunctions.addPaddingR("COU",8)  + CommonFunctions.addPaddingR("SEN",8) + CommonFunctions.addPaddingR("MAG",8) + CommonFunctions.addPaddingR("LEG",8) + CommonFunctions.addPaddingR("SB",8));
            text4_1.setFontSize(fontSize);


            Paragraph paragraph1 = new Paragraph();
            paragraph1.setTextAlignment(TextAlignment.LEFT);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setTextAlignment(TextAlignment.LEFT);

            paragraph1.add(text1).addStyle(normal);
            paragraph1.add(text2).addStyle(normal);
            paragraph1.add(text3).addStyle(normal);
            paragraph1.add(text4_1).addStyle(normal);
            paragraph2.add("-----------------------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

            doc.add(paragraph1);
            doc.add(paragraph2);

            getDataForReport(doc, startingPrecinct, endingPrecinct, user, district);

            doc.close();

            String filePath = "C:/Test/Precinct.pdf";
            File file = new File(filePath);

            Desktop.getDesktop().open(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // function that adds data to the report
    private static void getDataForReport(Document doc, String startingID, String endingID, String user, String district) throws IOException
    {
        String[] voterData = new String[13];

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);
        String columnName;

        if (Objects.equals(endingID, ""))
        {
            endingID = startingID;
        }

        if (!Objects.equals(district, ""))
        {
            columnName = switch (district)
            {
                case "" -> "";
                case "" -> "";
                case "" -> "";
                case "" -> "";
                case "" -> "";
                default -> "";
            };
        }
        else
        {
            if (startingID.contains("1") || startingID.contains("2"))
            {
                columnName = "";
            }
            else
            {
                columnName = "";
            }
        }

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
                    "[].[] " +

                    "FROM [] " +
                    "INNER JOIN [] " +
                    "ON [].[] = [].[]" +
                    "WHERE ["+ columnName +"] BETWEEN '" + startingID + "' AND '" + endingID + "' " +
                    "ORDER BY [].["+sortingMetric+"] ASC");

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

                for (int i = 0; i < 13; i++)
                {
                    if (voterData[i] == null)
                    {
                        voterData[i] = "";
                    }
                }

                String votingAddress = voterData[9] + " " + voterData[10] + " " + voterData[11] + " " + voterData[12];

                Text text1 = new Text(CommonFunctions.addPaddingR(voterData[0], 10) + CommonFunctions.addPaddingR(voterData[1], 25) + CommonFunctions.addPaddingR(voterData[2], 85) + CommonFunctions.addPaddingR(voterData[4], 8) + CommonFunctions.addPaddingR(voterData[5], 8) + CommonFunctions.addPaddingR(voterData[6], 8) + CommonFunctions.addPaddingR(voterData[7], 8) + CommonFunctions.addPaddingR(voterData[8], 8));
                text1.setFontSize(fontSize);

                Text text2 = new Text(CommonFunctions.addPaddingR("", 10) + CommonFunctions.addPaddingR("", 25) + CommonFunctions.addPaddingR(votingAddress + " "  + voterData[3], 85) + CommonFunctions.addPaddingR("", 8) + CommonFunctions.addPaddingR("", 8) + CommonFunctions.addPaddingR("", 8) + CommonFunctions.addPaddingR("", 8) + CommonFunctions.addPaddingR("", 8));
                text1.setFontSize(fontSize);

                Paragraph paragraph1 = new Paragraph();
                paragraph1.setTextAlignment(TextAlignment.LEFT);
                paragraph1.setMargins(0,0,0,0);

                Paragraph paragraph2 = new Paragraph();
                paragraph2.setTextAlignment(TextAlignment.LEFT);
                paragraph2.setMargins(0,0,0,189);

                Paragraph paragraph3 = new Paragraph();
                paragraph3.setTextAlignment(TextAlignment.LEFT);
                paragraph3.setMargins(0,0,0,0);

                paragraph1.add(text1).addStyle(normal);
                paragraph3.add("-----------------------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                if (count == lineFormat)
                {
                    Text blankSpace = new Text("\n");

                    paragraph3.add(blankSpace);
                }

                doc.add(paragraph1);

                if (includeAddress)
                {
                    paragraph2.add(text2).addStyle(normal);
                    doc.add(paragraph2);
                }

                doc.add(paragraph3);
                count++;

                // adds header to the top of each new page
                if (count == headerFormat)
                {
                    page++;
                    String paddedPage = "";

                    if (Integer.toString(page).length() == 1)
                    {
                        paddedPage = CommonFunctions.addPaddingR("PAGE", 13)+page;
                    }

                    if (Integer.toString(page).length() == 2)
                    {
                        paddedPage = CommonFunctions.addPaddingR("PAGE", 12)+page;
                    }

                    if (Integer.toString(page).length() == 3)
                    {
                        paddedPage = CommonFunctions.addPaddingR("PAGE", 11)+page;
                    }

                    if (Integer.toString(page).length() == 4)
                    {
                        paddedPage = CommonFunctions.addPaddingR("PAGE", 10)+page;
                    }

                    if (Integer.toString(page).length() == 5)
                    {
                        paddedPage = CommonFunctions.addPaddingR("PAGE", 9)+page;
                    }

                    if (Integer.toString(page).length() == 6)
                    {
                        paddedPage = CommonFunctions.addPaddingR("PAGE", 8)+page;
                    }

                    if (Integer.toString(page).length() == 7)
                    {
                        paddedPage = CommonFunctions.addPaddingR("PAGE", 7)+page;
                    }

                    Text headerText1 = new Text("REPORT DATE  "+ dateToday +"                                        FCCO VOTER PROCESSING SYSTEM                                                 " + paddedPage + "\n");
                    headerText1.setFontSize(fontSize);

                    // Files ID to left side and Report Time to right side
                    Text headerText2 = new Text("USER  "+CommonFunctions.addPaddingR(user, 60)+"VOTER PRECINCT LISTING                                                    TIME  "+timeToday + "\n");
                    headerText2.setFontSize(fontSize);

                    // add date range
                    Text headerText3 = new Text("INCLUDING ........:  ALL PRECINCTS from " + startingID+" TO "+endingID+" by " + distType + "\n");
                    headerText3.setFontSize(fontSize);

                    // column headings
                    Text headerText4_1 = new Text(CommonFunctions.addPaddingR("CODE", 10) + CommonFunctions.addPaddingR("PRECINCT NAME", 25) + CommonFunctions.addPaddingR("VOTING LOCATION", 85) + CommonFunctions.addPaddingR("COU",8)  + CommonFunctions.addPaddingR("SEN",8) + CommonFunctions.addPaddingR("MAG",8) + CommonFunctions.addPaddingR("LEG",8) + CommonFunctions.addPaddingR("SB",8));
                    headerText4_1.setFontSize(fontSize);

                    Paragraph header1 = new Paragraph();
                    header1.setTextAlignment(TextAlignment.LEFT);

                    Paragraph header2 = new Paragraph();
                    header2.setTextAlignment(TextAlignment.LEFT);

                    header1.add(headerText1).addStyle(normal);
                    header1.add(headerText2).addStyle(normal);
                    header1.add(headerText3).addStyle(normal);
                    header1.add(headerText4_1).addStyle(normal);
                    header2.add("-----------------------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                    doc.add(header1);
                    doc.add(header2);

                    count = 0;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
