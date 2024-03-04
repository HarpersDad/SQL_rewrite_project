package VoterRegistrationSystem;

import com.itextpdf.io.font.FontConstants;
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
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;

public class StreetDescriptions
{
    static LocalDateTime thisDayAndTime = LocalDateTime.now();
    static DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    static DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    static String dateToday = thisDayAndTime.format(formatDate);
    static String timeToday = thisDayAndTime.format(formatTime);
    static int count = 0;
    static int page = 1;
    static int fontSize = 10;
    static String sortType;
    static String sortSQL;

    public static void streetDescs(String distCode, String distNum, String user) throws IOException
    {
        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        String code = "";

        if (distCode.length() < 3)
        {
            sortType = "";

            if (!Objects.equals(distCode, ""))
            {
                sortSQL = switch (distCode)
                {
                    case "" -> "";
                    case "" -> "";
                    case "" -> "";
                    case "" -> "";
                    case "" -> "";
                    default -> "";
                };
                code = sortSQL;
            }
        }
        else
        {
            sortType = "";
            sortSQL = "";
            code = distCode;
        }

        try
        {
            // creates a pdf document writer and pdf document
            PdfWriter pWriter = new PdfWriter("C:/Test/streetDesc.pdf");
            PdfDocument pdf = new PdfDocument(pWriter);
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document doc = new Document(pdf);
            doc.setMargins(0f,0f,0f,0f);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new CommonFunctions.RotateEventHandler(PageSize.A4.rotate()));

            Text text1 = new Text("REPORT DATE  "+ dateToday +"                                 FCCO VOTER REGISTRATION SYSTEM                                     PAGE         1\n");
            text1.setFontSize(fontSize);

            // Files ID to left side and Report Time to right side
            Text text2 = new Text("USER  "+CommonFunctions.addPaddingR(user, 47)+ CommonFunctions.addPaddingR("ADD STREET DESCRIPTIONS BY " + sortType,70)+"TIME  "+timeToday + "\n");
            text2.setFontSize(fontSize); 

            Text text3 = new Text(CommonFunctions.addPaddingR("STREET", 41) + CommonFunctions.addPaddingR("DESCRIPTION", 40) + CommonFunctions.addPaddingR("PRECINCT", 31) + CommonFunctions.addPaddingR("CODE", 7) + "CD  SD  MD  SB  LD");
            text3.setFontSize(fontSize);

            Paragraph paragraph1 = new Paragraph();
            paragraph1.setTextAlignment(TextAlignment.LEFT);

            paragraph1.add(text1).addStyle(normal);
            paragraph1.add(text2).addStyle(normal);
            paragraph1.add(text3).addStyle(normal);
            paragraph1.add("\n--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

            doc.add(paragraph1);
            //doc.add(paragraph2);

            getDataForReport(doc, code, distNum, user);
            
            doc.close();

            String filePath = "C:/Test/streetDesc.pdf";
            File file = new File(filePath);

            Desktop.getDesktop().open(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // function that adds data to the report
    private static void getDataForReport(Document doc, String code, String num, String user) throws IOException
    {
        String[] voterData = new String[4];
        String myQuery;

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        if (!Objects.equals(num, ""))
        {
            myQuery ="SELECT DISTINCT [], [], [], [] FROM (" +
                    "SELECT " +
                    "[].[].[].[], " +
                    "[].[].[].[], " +
                    "[].[].[].[], " +
                    "[].[].[].[] " +
                    "FROM [].[].[] " +
                    "INNER JOIN [].[].[] " +
                    "ON [].[].[].[] = [].[].[].[] " +
                    "WHERE [].[].[].["+code+"] BETWEEN " + num + " AND " + num + " " +
                    ") as rows " +
                    "ORDER BY [] ASC";
        }
        else
        {
            myQuery = "SELECT DISTINCT [], [], [], [] FROM (" +
                    "SELECT " +
                    "[].[].[].[], " +
                    "[].[].[].[], " +
                    "[].[].[].[] " +
                    "[].[].[].[] " +
                    "FROM [].[].[] " +
                    "INNER JOIN [].[].[] " +
                    "ON [].[].[].[] = [].[].[].[] " +
                    "WHERE [].[].[].[] = '" + code + "' " +
                    ") as rows " +
                    "ORDER BY [] ASC";
        }

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement(myQuery);

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                voterData[0] = rs.getString(1);
                voterData[1] = rs.getString(2);
                voterData[2] = rs.getString(3);
                voterData[3] = rs.getString(4);

                // returns precinct name
                String precName = CommonFunctions.getPrecName(voterData[2]);

                // returns districts
                String[] districtID = getDistrictCodes(voterData[2]);

                // returns address ranges
                String[] addressRange = getAddressRanges(voterData[2], voterData[0], voterData[1]);

                for (int n = 0; n < 4; n++)
                {
                    // checks for a null value, and replaces it with an empty string
                    if (voterData[n] == null)
                    {
                        voterData[n] = "";
                    }
                    else
                    {
                        // removes excess whitespace
                        voterData[n] = voterData[n].trim();
                    }
                }

                if (!Objects.equals(voterData[1], ""))
                {
                    voterData[1] = " " + voterData[1];
                }

                if (!Objects.equals(voterData[3], ""))
                {
                    voterData[3] = ", " + voterData[3];
                }

                String odds = addressRange[1] + " - " + addressRange[3];
                String evens = addressRange[0] + " - " + addressRange[2];

                if (Objects.equals(addressRange[1], addressRange[3]) && !Objects.equals(addressRange[1], "09999") || Objects.equals(addressRange[3], "00000"))
                {
                    odds = "ONLY " + addressRange[1] + " FOR";
                }

                if (Objects.equals(addressRange[0], addressRange[2]) && !Objects.equals(addressRange[0], "00000") || Objects.equals(addressRange[2], "09999"))
                {
                    evens = "ONLY " + addressRange[0] + " FOR";
                }

                if (Objects.equals(addressRange[1], "09999") && Objects.equals(addressRange[3], "00000"))
                {
                    odds = "NO RANGE FOR";
                }

                if (Objects.equals(addressRange[0], "09999") && Objects.equals(addressRange[2], "00000"))
                {
                    evens = "NO RANGE FOR";
                }

                // add data to pdf document
                //                                    street                                                             description                                                precinct                               code                            cd                                sd                                 md                         ld                                      sb
                Text text1 = new Text(CommonFunctions.addPaddingR(voterData[0] + voterData[1] + voterData[3], 41) + CommonFunctions.addPaddingR(evens, 14) + CommonFunctions.addPaddingR(" EVEN NUMBERS", 26) + CommonFunctions.addPaddingR(precName, 31) + CommonFunctions.addPaddingR(voterData[2], 7) + CommonFunctions.addPaddingR(districtID[0], 4) + CommonFunctions.addPaddingR(districtID[1], 4) + CommonFunctions.addPaddingR(districtID[2], 4) + CommonFunctions.addPaddingR(districtID[3], 4) + CommonFunctions.addPaddingR(districtID[4], 4) + "\n");
                text1.setFontSize(10);

                Text text2 = new Text(CommonFunctions.addPaddingR(voterData[0] + voterData[1] + voterData[3], 41) + CommonFunctions.addPaddingR(odds, 14) + CommonFunctions.addPaddingR(" ODD NUMBERS", 26) + CommonFunctions.addPaddingR(precName, 31) + CommonFunctions.addPaddingR(voterData[2], 7) + CommonFunctions.addPaddingR(districtID[0], 4) + CommonFunctions.addPaddingR(districtID[1], 4) + CommonFunctions.addPaddingR(districtID[2], 4) + CommonFunctions.addPaddingR(districtID[3], 4) + CommonFunctions.addPaddingR(districtID[4], 4) + "\n");
                text2.setFontSize(10);

                Paragraph paragraph1 = new Paragraph();
                paragraph1.setTextAlignment(TextAlignment.LEFT);
                paragraph1.setMargins(0,0,0,0);

                paragraph1.add(text1).addStyle(normal);
                paragraph1.add(text2).addStyle(normal);
                paragraph1.add("--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                if (count == 13)
                {
                    Text blankSpace = new Text("\n");

                    paragraph1.add(blankSpace);
                }

                doc.add(paragraph1);
                count++;

                // adds header to the top of each new page
                if (count == 14)
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

                    Text headerText1 = new Text("REPORT DATE  "+ dateToday +"                                 FCCO VOTER REGISTRATION SYSTEM                                     "+ paddedPage +"\n");
                    headerText1.setFontSize(fontSize); //                                                                                         |

                    // Files ID to left side and Report Time to right side
                    Text headerText2 = new Text("USER  "+CommonFunctions.addPaddingR(user, 47)+ CommonFunctions.addPaddingR("ADD STREET DESCRIPTIONS BY " + sortType,70)+"TIME  "+timeToday + "\n");
                    headerText2.setFontSize(fontSize); //                                                     |

                    Text headerText3 = new Text(CommonFunctions.addPaddingR("STREET", 41) + CommonFunctions.addPaddingR("DESCRIPTION", 40) + CommonFunctions.addPaddingR("PRECINCT", 31) + CommonFunctions.addPaddingR("CODE", 7) + "CD  SD  MD  SB  LD");
                    headerText3.setFontSize(fontSize);

                    Paragraph header1 = new Paragraph();
                    header1.setTextAlignment(TextAlignment.LEFT);

                    header1.add(headerText1).addStyle(normal);
                    header1.add(headerText2).addStyle(normal);
                    header1.add(headerText3).addStyle(normal);
                    header1.add("\n--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                    doc.add(header1);

                    count = 0;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String[] getDistrictCodes(String precCode)
    {
        String[] distCodes = new String[5];

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[], " +
                    "[], " +
                    "[], " +
                    "[], " +
                    "[] " +
                    "FROM [].[].[]" +
                    "WHERE [] = '" + precCode + "'")
                    ;
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // the order that these are put into the array is the same as they are listed above
                distCodes[0] = rs.getString(1);
                distCodes[1] = rs.getString(2);
                distCodes[2] = rs.getString(3);
                distCodes[3] = rs.getString(4);
                distCodes[4] = rs.getString(5);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return distCodes;
    }

    public static String[] getAddressRanges(String preCode, String strName, String strType)
    {
        String[] func1Array1 = new String[0];
        String[] func1Array2 = new String[4];

        func1Array2[0] = "9999";
        func1Array2[1] = "9999";

        func1Array2[2] = "0";
        func1Array2[3] = "0";

        String psString;

        if (strType == null)
        {
            psString = "SELECT [] FROM [] WHERE [] = '" + preCode + "' AND [] = '" + strName + "' ORDER BY [] ASC";
        }
        else
        {
            psString = "SELECT [] FROM [] WHERE [] = '" + preCode + "' AND [] = '" + strName + "' AND [] = '" + strType + "' ORDER BY [] ASC";
        }

        try {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement(psString);

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                func1Array1 = Arrays.copyOf(func1Array1, func1Array1.length + 1);

                func1Array1[func1Array1.length - 1] = rs.getString(1);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if (func1Array1.length > 0)
        {
            if (Integer.parseInt(func1Array1[0]) % 2 == 0)
            {
                func1Array2[0] = func1Array1[0];
            }
            else
            {
                func1Array2[1] = func1Array1[0];
            }

            // lowest
            for (int i = 0; i < func1Array1.length; i++)
            {
                //even
                if (Integer.parseInt(func1Array1[i]) % 2 == 0)
                {
                    if (Integer.parseInt(func1Array1[i]) < Integer.parseInt(func1Array2[0]))
                    {
                        func1Array2[0] = func1Array1[i];
                    }
                }
                // odd
                else
                {
                    if (Integer.parseInt(func1Array1[i]) < Integer.parseInt(func1Array2[1]))
                    {
                        func1Array2[1] = func1Array1[i];
                    }
                }
            }

            // highest
            for (int j = 0; j < func1Array1.length; j++)
            {
                // odd
                if (Integer.parseInt(func1Array1[j]) % 2 == 1)
                {
                    if (Integer.parseInt(func1Array1[j]) > Integer.parseInt(func1Array2[2]))
                    {
                        func1Array2[3] = func1Array1[j];
                    }
                }
                // even
                else
                {
                    if (Integer.parseInt(func1Array1[j]) > Integer.parseInt(func1Array2[3]))
                    {
                        func1Array2[2] = func1Array1[j];
                    }
                }
            }
        }

        while (func1Array2[0].length() < 5)
        {
            func1Array2[0] = "0" + func1Array2[0];
        }

        while (func1Array2[1].length() < 5)
        {
            func1Array2[1] = "0" + func1Array2[1];
        }

        while (func1Array2[2].length() < 5)
        {
            func1Array2[2] = "0" + func1Array2[2];
        }

        while (func1Array2[3].length() < 5)
        {
            func1Array2[3] = "0" + func1Array2[3];
        }

        return func1Array2;
    }
}
