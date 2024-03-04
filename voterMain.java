package VoterRegistrationSystem;

import java.io.IOException;
import java.sql.Date;

public class voterMain
{
    public static void voterReports() throws IOException
    {
        Date thisStart = new Date(YYYY - 1900,MM - 1, DD);
        Date thisEnd = new Date(YYYY - 1900,MM - 1, DD);

        String userName = "";
        
		// Kills specified application
        Runtime rt = Runtime.getRuntime();
        rt.exec("taskkill /F /IM APPLICATION.EXE");

        // test add transactions
        //add.addTransaction(thisStart, thisEnd, userName);

        // test change transactions
        //change.changeTransaction(thisStart, thisEnd, userName);

        // test delete transactions
        //delete.deleteTransaction(thisStart, thisEnd, userName);

        // test eo change transactions
        //EOchange.eoChangeTransaction(thisStart, thisEnd, userName);

        // test eo delete transactions
        //EOdelete.eoDeleteTransaction(thisStart, thisEnd, userName);

        // test postcard notification
        //NotificationPostcards.postcardNotification(thisStart, thisEnd);

        // test LocationList
        //LocationList.getLocation(userName, "".toUpperCase(), "".toUpperCase(), "".toUpperCase(), false);

        // test PrecinctList
        //PrecinctList.getPrecincts(userName, "".toUpperCase(), "".toUpperCase(), "".toUpperCase(), true);

        // test street descriptions
        StreetDescriptions.streetDescs("", "", userName);

        System.exit(0);
    }
}
