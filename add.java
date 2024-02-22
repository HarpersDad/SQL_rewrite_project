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

public class add
{
    static LocalDateTime thisDayAndTime = LocalDateTime.now();
    static DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    static DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    static String dateToday = thisDayAndTime.format(formatDate);
    static String timeToday = thisDayAndTime.format(formatTime);
    static int count = 0;
    static int page = 1;
    static int fontSize = 10;

    public static void addTransaction(Date startDate, Date endDate, String user) throws IOException
    {
        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        try
        {
            // creates a pdf document writer and pdf document
            PdfWriter pWriter = new PdfWriter("C:/Test/add.pdf");
            PdfDocument pdf = new PdfDocument(pWriter);
            pdf.setDefaultPageSize(PageSize.A4.rotate());
            Document doc = new Document(pdf);
            doc.setMargins(0f,0f,0f,0f);

            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new RotateEventHandler(PageSize.A4.rotate()));

            String startDateRange = startDate.toString().substring(5) + "/" + startDate.toString().substring(0,4);
            String endDateRange = endDate.toString().substring(5) + "/" + endDate.toString().substring(0,4);

            startDateRange = startDateRange.replace("-", "/");
            endDateRange = endDateRange.replace("-", "/");

            Text text1 = new Text("REPORT DATE  "+ dateToday +"                                 VOTER PROCESSING SYSTEM                                       PAGE         1\n");
            text1.setFontSize(fontSize); //                                                                                         |

            // Files ID to left side and Report Time to right side
            Text text2 = new Text("USER  "+addPaddingR(user, 56)+"ADD TRANSACTIONS                                             TIME  "+timeToday + "\n");
            text2.setFontSize(fontSize); //                                                     |

            // add date range
            Text text3 = new Text("INCLUDING ........:  DATES  FROM   "+startDateRange+"   TO   "+endDateRange+"\n");
            text3.setFontSize(fontSize);

            // column headings
            Text text4_1 = new Text("TYPE   LAST NAME                                         FIRST NAME          MIDDLE NAME                 ID #                     PROTECT\n");
            text4_1.setFontSize(fontSize);

            Text text4_2 = new Text("ADDRESS                                                      APT NO                                                       ZIP CODE\n");
            text4_2.setFontSize(fontSize);

            Text text4_3 = new Text("SEX    DATE OF BIRTH            PARTY      PREC               REG. DATE           HOME PHONE         WORK PHONE         CELL PHONE\n");
            text4_3.setFontSize(fontSize);

            Paragraph paragraph1 = new Paragraph();
            paragraph1.setTextAlignment(TextAlignment.LEFT);

            Paragraph paragraph2 = new Paragraph();
            paragraph2.setTextAlignment(TextAlignment.LEFT);
            paragraph2.setMargins(0,0,0,42);

            Paragraph paragraph3 = new Paragraph();
            paragraph3.setTextAlignment(TextAlignment.LEFT);

            paragraph1.add(text1).addStyle(normal);
            paragraph1.add(text2).addStyle(normal);
            paragraph1.add(text3).addStyle(normal);
            paragraph1.add(text4_1).addStyle(normal);
            paragraph2.add(text4_2).addStyle(normal);
            paragraph2.add(text4_3).addStyle(normal);
            paragraph3.add("--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

            doc.add(paragraph1);
            doc.add(paragraph2);
            doc.add(paragraph3);

            // for loop iterates over the date range and uses the date as a parameter for the function call along with the doc
            //for(LocalDate thisDate = startDate.toLocalDate(); thisDate.isBefore(endDate.toLocalDate()); thisDate = thisDate.plusDays(1))
            //{
                getDataForReport(doc, startDate.toString(), endDate.toString(), user);
            //}

            doc.close();

            String filePath = "C:/Test/add.pdf";
            File file = new File(filePath);

            //Desktop.getDesktop().print(file);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    // gets voter address
    private static String[] getAddress(String code)
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

            PreparedStatement stmt = conn.prepareStatement("SELECT [STREET_100],[STRNAME100],[STRTYPE100],[ZIP100], [STRDIR100] FROM [].[].[] WHERE [STRID_100] = '" + code + "'");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // fcstrt100
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
        }
        return addressZip;
    }

    // returns voter ssn
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

                return ssn.substring(0,3) + "-" + ssn.substring(3,5) + "-" + ssn.substring(5);
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
        String[] voterData = new String[18];
        String address;
        String zip;
        String ssn;

        start = start.replace("-", "");
        stop = stop.replace("-", "");

        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER);
        normal.setFont(font).setFontSize(fontSize);

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[].[IDNUM300]," +
                    "[].[STRID_300]," +
                    "[].[SURNAME300]," +
                    "[].[GIVEN300]," +
                    "[].[MIDDLE300]," +
                    "[].[STRUNIT300]," +
                    "[].[SEX300]," +
                    "[].[DOB300]," +
                    "[].[PROTECT300]," +
                    "[].[PRECODE300]," +
                    "[].[PARTY300]," +
                    "[].[REGDTE300]," +
                    "[].[HOMEPHN300]," +
                    "[].[WORKPHN300]," +
                    "[].[CELLPHN300]," +
                    "[].[TRANDTE300]," +
                    "[].[TRANCD315], " +
                    "[].[RANK300]" +
                    "FROM [] " +
                    "INNER JOIN [] " +
                    "ON .IDNUM300 = .IDNUM315 " +
                    "AND .TRANDTE315 " +
                    "BETWEEN '"+start+"' AND '"+stop+"' " +
                    "ORDER BY [].[SURNAME300] ASC, [].[GIVEN300]");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // vpmast300
                voterData[0] = rs.getString(3);   // last
                voterData[1] = rs.getString(4);   // first
                voterData[2] = rs.getString(5);   // middle
                voterData[3] = rs.getString(8);   // bday
                voterData[4] = rs.getString(7);   // sex
                voterData[5] = rs.getString(2);   // strid
                voterData[6] = rs.getString(11);  // party
                voterData[7] = rs.getString(10);  // precinct
                voterData[8] = rs.getString(1);   // idnum
                voterData[9] = rs.getString(6);   // aptnum
                voterData[10] = rs.getString(12); // regdate
                voterData[11] = rs.getString(13); // home
                voterData[12] = rs.getString(14); // work
                voterData[13] = rs.getString(15); // cell
                voterData[14] = rs.getString(9);  // protect
                voterData[15] = rs.getString(16); // trandte
                voterData[16] = rs.getString(17); // trancd
                voterData[17] = rs.getString(18); // rank

                // fcstrt100
                String[] thisAddy = getAddress(voterData[5]);
                address = thisAddy[0] + " " + thisAddy[4] + " " + thisAddy[1] + " " + thisAddy[2];
                zip = thisAddy[3];

                // get ssn
                ssn = getSSN(voterData[8]);

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
                        // removes excess whitespace
                        voterData[n] = voterData[n].trim();
                    }
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

                if (Objects.equals(voterData[16], "A") || Objects.equals(voterData[16], "T"))
                {
                    // add data to pdf document
                    //                            TYPE                                            LAST NAME + RANK                                 FIRST                                MIDDLE                             ID #                             SSN                   PROTECT 14
                    Text text1 = new Text(addPaddingR(voterData[16], 7) + addPaddingR(voterData[0] + " " + voterData[17], 50) + addPaddingR(voterData[1], 20) + addPaddingR(voterData[2], 27) + addPaddingR(voterData[8], 14) + addPaddingR(ssn, 16) + voterData[14] + "\n");
                    text1.setFontSize(10);

                    //                                      ADDRESS                       APT NO 9                 ZIP CODE
                    Text text2 = new Text(addPaddingR(address, 61) + addPaddingR(voterData[9], 61) + zip + "\n");
                    text2.setFontSize(10);

                    //                                 SEX                       DATE OF BIRTH                                PARTY                         PREC                           REG. DATE                                 HOME PHONE  11                      WORK PHONE    12               CELL PHONE 13
                    Text text3 = new Text(addPaddingR(voterData[4], 7) + addPaddingR(voterData[3], 25) + addPaddingR(voterData[6], 11) + addPaddingR(voterData[7], 19) + addPaddingR(voterData[10], 20) + addPaddingR(voterData[11], 19) + addPaddingR(voterData[12], 19) + voterData[13] + "\n");
                    text3.setFontSize(10);

                    Paragraph paragraph1 = new Paragraph();
                    paragraph1.setTextAlignment(TextAlignment.LEFT);
                    paragraph1.setMargins(0,0,0,0);

                    Paragraph paragraph2 = new Paragraph();
                    paragraph2.setTextAlignment(TextAlignment.LEFT);
                    paragraph2.setMargins(0,0,0,42);

                    Paragraph paragraph3 = new Paragraph();
                    paragraph3.setTextAlignment(TextAlignment.LEFT);
                    paragraph3.setMargins(0,0,0,0);

                    paragraph1.add(text1).addStyle(normal);
                    paragraph2.add(text2).addStyle(normal);
                    paragraph2.add(text3).addStyle(normal);
                    paragraph3.add("--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                    if (count == 8)
                    {
                        Text blankSpace = new Text("\n");

                        paragraph3.add(blankSpace);
                    }

                    doc.add(paragraph1);
                    doc.add(paragraph2);
                    doc.add(paragraph3);
                    count++;

                    // adds header to the top of each new page
                    if (count == 9)
                    {
                        page++;
                        String paddedPage = "";

                        if (Integer.toString(page).length() == 1)
                        {
                            paddedPage = addPaddingR("PAGE", 13)+page;
                        }

                        if (Integer.toString(page).length() == 2)
                        {
                            paddedPage = addPaddingR("PAGE", 12)+page;
                        }

                        if (Integer.toString(page).length() == 3)
                        {
                            paddedPage = addPaddingR("PAGE", 11)+page;
                        }

                        if (Integer.toString(page).length() == 4)
                        {
                            paddedPage = addPaddingR("PAGE", 10)+page;
                        }

                        if (Integer.toString(page).length() == 5)
                        {
                            paddedPage = addPaddingR("PAGE", 9)+page;
                        }

                        if (Integer.toString(page).length() == 6)
                        {
                            paddedPage = addPaddingR("PAGE", 8)+page;
                        }

                        if (Integer.toString(page).length() == 7)
                        {
                            paddedPage = addPaddingR("PAGE", 7)+page;
                        }

                        Text headerText1 = new Text("\nREPORT DATE  "+ dateToday +"                                 VOTER PROCESSING SYSTEM                                       " + paddedPage + "\n");
                        headerText1.setFontSize(10); //                                                                                         |

                        // Files ID to left side and Report Time to right side
                        Text headerText2 = new Text("USER  "+addPaddingR(user, 56)+"ADD TRANSACTIONS                                             TIME  "+timeToday + "\n");
                        headerText2.setFontSize(10); //                                                     |

                        // add date range
                        Text headerText3 = new Text("INCLUDING ........:  DATES  FROM   " + start.substring(4,6) + "/" + start.substring(6) + "/" + start.substring(0,4) + "   TO   "+ stop.substring(4,6) + "/" + stop.substring(6) + "/" + stop.substring(0,4) +"\n");
                        headerText3.setFontSize(10);

                        // column headings - this will take 3 rows
                        Text headerText4_1 = new Text("TYPE   LAST NAME                                         FIRST NAME          MIDDLE NAME                 ID #                     PROTECT\n");
                        headerText4_1.setFontSize(10);

                        Text headerText4_2 = new Text("ADDRESS                                                      APT NO                                                       ZIP CODE\n");
                        headerText4_2.setFontSize(10);

                        Text headerText4_3 = new Text("SEX    DATE OF BIRTH            PARTY      PREC               REG. DATE           HOME PHONE         WORK PHONE         CELL PHONE\n");
                        headerText4_3.setFontSize(10);

                        Paragraph header1 = new Paragraph();
                        header1.setTextAlignment(TextAlignment.LEFT);

                        Paragraph header2 = new Paragraph();
                        header2.setTextAlignment(TextAlignment.LEFT);
                        header2.setMargins(0,0,0,42);

                        Paragraph header3 = new Paragraph();
                        header3.setTextAlignment(TextAlignment.LEFT);

                        header1.add(headerText1).addStyle(normal);
                        header1.add(headerText2).addStyle(normal);
                        header1.add(headerText3).addStyle(normal);
                        header1.add(headerText4_1).addStyle(normal);
                        header2.add(headerText4_2).addStyle(normal);
                        header2.add(headerText4_3).addStyle(normal);
                        header3.add("--------------------------------------------------------------------------------------------------------------------------------------------").addStyle(normal);

                        doc.add(header1);
                        doc.add(header2);
                        doc.add(header3);

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