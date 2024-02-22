package VoterRegistrationSystem;

import java.io.IOException;
import java.sql.Date;

public class voterMain
{
    public static void voterReports() throws IOException
    {
        Date thisStart = new Date(2024 - 1900,1 - 1,01);
        Date thisEnd = new Date(2024 - 1900,1 - 1,01);

        String userName = "";

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
        NotificationPostcards.postcardNotification(thisStart, thisEnd, userName);

        System.exit(0);
    }
}
