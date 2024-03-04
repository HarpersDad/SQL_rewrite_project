package VoterRegistrationSystem;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class CommonFunctions
{
    public static String[] getAddress(String code)
    {
        String[] addressZip = new String[5];
        addressZip[0] = "";
        addressZip[1] = "";
        addressZip[2] = "";
        addressZip[3] = "";
        addressZip[4] = "";

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT [],[],[],[], [] FROM [].[].[] WHERE [] = '" + code + "'");
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                addressZip[0] = rs.getString(1);
                addressZip[1] = rs.getString(2);
                addressZip[2] = rs.getString(3);
                addressZip[3] = rs.getString(4);
                addressZip[4] = rs.getString(5);
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

    public static String getSSN(String idNum)
    {
        String ssn = "";

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT [] FROM [].[].[] WHERE [] = '" + idNum + "'");
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

    public static String getPrecName(String precID)
    {
        String preName = "";

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[] " +
                    "FROM [].[].[]" +
                    "WHERE [] = '" + precID + "'");
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

    public static String getAdditionalPrecincts(String location, String precinct)
    {
        String[] otherPrecincts = new String[6];
        StringBuilder precinctList = new StringBuilder();
        String returnList;
        int count = 0;

        location = location.replace("'", "");

        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            // sql query
            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[]" + // column to query
                    "FROM [] " + // database being queried
                    "WHERE [] = '" + location + "' " +
                    "ORDER BY [] ASC"); // sort by

            // returned data
            ResultSet rs = stmt.executeQuery(); // send query to database

            while (rs.next())
            {
                otherPrecincts[count] = rs.getString(1);   // set queried info to variable

                count++;
            }

            for (int i = 0; i < 6; i++)
            {
                if (otherPrecincts[i] == null)
                {
                    otherPrecincts[i] = "";
                }
            }

            for (int n = 0; n < 6; n++)
            {
                if (!Objects.equals(otherPrecincts[n], ""))
                {
                    precinctList.append(otherPrecincts[n]).append(", ");
                }
            }

			// for outlier precincts
            if (Objects.equals(precinct, ""))
            {
                precinctList.append("");
            }

            if (Objects.equals(precinct, ""))
            {
                precinctList.append("");
            }

            returnList = precinctList.toString().trim();

            if (returnList.contains(","))
            {
                int lastComma = returnList.lastIndexOf(",");

                if (Objects.equals(returnList.substring(lastComma), ","))
                {
                    returnList = returnList.substring(0, lastComma);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        returnList = returnList.replace(precinct + ", ", "");

        returnList = returnList.replace(", " + precinct, "");

        returnList = returnList.replace(precinct, "NO OTHER PRECINCTS");

        return returnList;
    }

    public static String getPartyChangeDate(String voterID)
    {
        String cDate = "";
        try
        {
            Class.forName("");
            String url = "";
            Connection conn = DriverManager.getConnection(url);

            PreparedStatement stmt = conn.prepareStatement("SELECT " +
                    "[] " +
                    "FROM [].[].[]" +
                    "WHERE [] = '" + voterID + "'");

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

        return "on " + cDate.substring(4,6) + "/" + cDate.substring(6) + "/" + cDate.substring(0,4);
    }

    public static String getMailingAddress(String voterID)
    {
        String[] address = new String[5];

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
                    "WHERE [] = '" + voterID + "'");

            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                // the order that these are put into the array is the same as they are listed above
                address[0] = rs.getString(1);
                address[1] = rs.getString(2);
                address[2] = rs.getString(3);
                address[3] = rs.getString(4);
                address[4] = rs.getString(5);
            }

            for (int i = 0; i < 5; i++)
            {
                if (address[i] == null)
                {
                    address[i] = "";
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if (Objects.equals(address[0], "") && Objects.equals(address[1], "") && Objects.equals(address[2], "") && Objects.equals(address[3], "") && Objects.equals(address[4], ""))
        {
            return "";
        }
        else
        {
            return address[0] + " " + address[1] + ", " + address[2] + ", " + address[3] + " " + address[4];
        }


    }

    public static String addPaddingR(String str, int R)
    {
        String s = "";

        StringBuilder strBuilder = new StringBuilder(s);

        strBuilder.append(" ".repeat(Math.max(0, R - str.length())));
        return str + strBuilder;
    }

    public record RotateEventHandler(PageSize newPageSize) implements IEventHandler
    {
        @Override
        public void handleEvent(Event event)
        {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            docEvent.getPage().setMediaBox(newPageSize);
        }
    }
}
