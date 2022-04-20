/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();
  
		// issues the query instruction
		ResultSet rs = stmt.executeQuery (query);
  
		/*
		 ** obtains the metadata object for the returned result set.  The metadata
		 ** contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
  
		// iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
	   if(outputHeader){
		  for(int i = 1; i <= numCol; i++){
		  System.out.print(rsmd.getColumnName(i) + "\t");
		  }
		  System.out.println();
		  outputHeader = false;
	   }
		   for (int i=1; i<=numCol; ++i)
			  System.out.print (rs.getString (i) + "\t");
		   System.out.println ();
		   ++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	 }//end executeQuery
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Ship");
				System.out.println("2. Add Captain");
				System.out.println("3. Add Cruise");
				System.out.println("4. Book Cruise");
				System.out.println("5. List number of available seats for a given Cruise.");
				System.out.println("6. List total number of repairs per Ship in descending order");
				System.out.println("7. Find total number of passengers with a given status");
				System.out.println("8. Delete Record from Table");
                                System.out.println("9. Print Reservations");
                                System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddShip(esql); break;
					case 2: AddCaptain(esql); break;
					case 3: AddCruise(esql); break;
					case 4: BookCruise(esql); break;
					case 5: ListNumberOfAvailableSeats(esql); break;
					case 6: ListsTotalNumberOfRepairsPerShip(esql); break;
					case 7: FindPassengersCountWithStatus(esql); break;
					case 8: Delete(esql); break;
                                        case 9: Print(esql); break;
                                        case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
public static void Delete(DBproject esql){
        //System.out.println("hello world");
                try{
                System.out.println("Please enter the table you want to delete from ");
                String table = validateString(in.readLine(),1,32);
                while(!table.equals("Ship") && !table.equals("Captain")){
                System.out.print("Please enter a valid table: ");
                table = in.readLine();
                System.out.println();

                }
                System.out.println("Enter primary key");
                String id = validateInteger(in.readLine(), 0,Integer.MAX_VALUE);
                String query = "DELETE FROM " + table + " WHERE id = " +id;
                int rowCount = esql.executeQuery(query);
                }

                catch(Exception e) {
                        System.err.println(e.getMessage());
                }
        }
public static void Print(DBproject esql){
        try{
        System.out.println("Please enter your First Name ");
        String first = "\'"+in.readLine() + "\'";//validateString(in.readLine(),1,32);
        System.out.println("Please enter your Last Name ");
        String last = "\'"+in.readLine() + "\'";//validateString(in.readLine(),1,32);

                //System.out.println("Enter primary key");
                //String id = validateInteger(in.readLine(), 0,Integer.MAX_VALUE);
                String query = "SELECT * FROM Reservation, Customer C WHERE ccid = C.id AND C.fname = " + first + " AND C.lname = " + last;

                int rowCount = esql.executeQuery(query);
        }
        catch(Exception e) {
        System.err.println(e.getMessage());
        }
        }
	//error check for 
	//1. year < 2021
	//2. 0 < # seats < 500
	//3. model and make are both strings (with less <= 32 and 64 respectively)
	public static void AddShip(DBproject esql) {//1
		try{
			String query = "INSERT INTO Ship VALUES(";
			System.out.println("Input Ship ID:");
			String input = validateInteger(in.readLine(),0,Integer.MAX_VALUE) + ",";
			while(checkIDExists(input.substring(0,input.length()-1), "Ship", esql)){
				System.out.println("Please enter an non-existing Ship ID or enter q to quit: ");
				input = in.readLine();
				if(input.equals("q"))
					return;
                                input = validateInteger(input,0,Integer.MAX_VALUE) + ",";
			}
			System.out.println("Input Ship make:");
			input+= "\'" + validateString(in.readLine(),1,32) + "\',";
			System.out.println("Input Ship model:");
			input+= "\'" + validateString(in.readLine(),1,64) + "\',";
			System.out.println("Input Ship age:");
			input+= validateInteger(in.readLine(),0,500) + ",";
			System.out.println("Input Number of seats:");
			input+= validateInteger(in.readLine(),0,500) + ")";
			query+= input; 
			System.out.println(query);
			int rowCount = esql.executeQuery(query);
			System.out.println("total row(s):" + rowCount);
		 }catch(Exception e){
			System.err.println(e.getMessage());
		 }
	}

	public static void AddCaptain(DBproject esql) {//2
		try {
			String query = "INSERT INTO Captain VALUES(";
			System.out.println("Input Captain ID:");
			String input = validateInteger(in.readLine(),0,Integer.MAX_VALUE) + ",";
			while(checkIDExists(input.substring(0,input.length()-1), "Captain",esql)){
				System.out.println("Please enter an non-existing Captain ID or enter q to quit: ");
                                input = in.readLine();
				if(input.equals("q"))
                                        return;
                                input = validateInteger(input,0,Integer.MAX_VALUE) + ",";
			}
			System.out.println("Input Captain Full Name:");
			input+= "\'" + validateString(in.readLine(),1,128) + "\',";
			System.out.println("Input Captain nationality:");
			input+= "\'" + validateString(in.readLine(),1,24) + "\')";
			query+= input; 
			System.out.println(query);
			int rowCount = esql.executeQuery(query);
			System.out.println("total row(s):" + rowCount);
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	public static void AddCruise(DBproject esql) {//3
		try {
			String query = "INSERT INTO Cruise VALUES(";
			System.out.println("Input Cruise number:");
			String input = validateInteger(in.readLine(),0,Integer.MAX_VALUE) + ",";
			while(checkIDExists(input.substring(0,input.length()-1), "Cruise",esql)){
				System.out.println("Please enter an non-existing Cruise ID or enter q to quit: ");
                                input = in.readLine();
                                if(input.equals("q"))
                                        return;
                                input = validateInteger(input,0,Integer.MAX_VALUE) + ",";
			}
			System.out.println("Input Cruise ticket cost:");
			input+= validateInteger(in.readLine(),1,Integer.MAX_VALUE) + ",";
			System.out.println("Input Cruise number of tickets sold:");
			input+= validateInteger(in.readLine(),0,Integer.MAX_VALUE) + ",";
			System.out.println("Input Cruise number of stops:");
			input+= validateInteger(in.readLine(),0,Integer.MAX_VALUE) + ",";
			System.out.println("Input Cruise actual_departure_date as DD/MM/YYYY:");
			input+= "TO_DATE(" + "\'" + validateDate(in.readLine()) + "\', \'DD/MM/YYYY\'),";
			System.out.println("Input Cruise actual_arrival_date as DD/MM/YYYY:");
			input+= "TO_DATE(" + "\'" + validateDate(in.readLine()) + "\', \'DD/MM/YYYY\'),";
			System.out.println("Input Cruise arrival_port:");
			input+= "\'" + validateString(in.readLine(),1,5) + "\',";
			System.out.println("Input Cruise departure_port:");
			input+= "\'" + validateString(in.readLine(),1,5) + "\')";
			query+= input; 
			System.out.println(query);
			int rowCount = esql.executeQuery(query);
			System.out.println("total row(s):" + rowCount);
		}
		catch(Exception e){
		    System.err.println(e.getMessage());
		}
	}


	public static int max_rows[] = {9999};
	public static void BookCruise(DBproject esql) {//4
		// Given a customer and a Cruise that he/she wants to book, add a reservation to the DB
        	try {

			System.out.println("Input Customer ID");
			String cust_id = validateInteger(in.readLine(),0,Integer.MAX_VALUE);
			while(!checkIDExists(cust_id, "Customer",esql)){
				System.out.println("Please enter an existing Customer ID or enter q to quit: ");
				cust_id = in.readLine();
				if(cust_id.equals("q"))
					return;
				cust_id = validateInteger(cust_id,0,Integer.MAX_VALUE);
			}

			System.out.println("Input Cruise Number");
			String cruise_id = validateInteger(in.readLine(),0,Integer.MAX_VALUE);
			//check if cruise id exists
			while(!checkIDExists(cruise_id, "Cruise",esql)){
				System.out.println("Please enter an existing Cruise or enter q to quit: ");
				cruise_id = in.readLine();
				if(cruise_id.equals("q"))
					return;
				cruise_id = validateInteger(cruise_id,0,Integer.MAX_VALUE);
			}

			String available_seats = "SELECT S.seats - C.num_sold AS Num_available FROM Ship S, CruiseInfo CI, Cruise C WHERE C.cnum = " + cruise_id + " AND C.cnum = CI.cruise_id AND S.id = CI.ship_id ";
			//Determine status of reservation (W/R/C)
			String query = "SELECT status FROM Reservation WHERE cid = " + cruise_id + " AND ccid = " + cust_id;
			//Update Reserved to Confirmed
			String query2 = "UPDATE Reservation SET status = CASE WHEN status = 'R' THEN 'C' WHEN status = 'W' THEN 'R' ELSE status END WHERE ccid = "+ cust_id+ "AND cid= " + cruise_id;
			
			if(esql.executeQuery(available_seats)<1){
			System.out.println("Sorry, there are no available seats.");
			query2 = "UPDATE Reservation SET status = CASE WHEN status = 'R' THEN 'C' ELSE status END WHERE ccid = " + cust_id + "AND cid= " +cruise_id;
			}

							
			//create if customer's reservation doesnt exist
			if(!checkReservation(cust_id, cruise_id, esql)){
				System.out.println("Thank you for booking");
			
				int insert_row;
				//adds to database if doesnt exist
				if(esql.executeQuery(available_seats)>0){
					max_rows[0] += 1;
					String query4 = "INSERT INTO Reservation VALUES ("+ max_rows[0] + "," +cust_id + "," + cruise_id + ", 'R')";
					insert_row = esql.executeQuery(query4);
					//update #tickets sold in cruises
					String query6 = "Update Cruise SET num_sold = num_sold + 1 WHERE cnum = " +cruise_id;
					int update = esql.executeQuery(query6);
				}
				if(esql.executeQuery(available_seats)<=0){
					max_rows[0] += 1;
					String query4 = "INSERT INTO Reservation VALUES ("+ max_rows[0] + "," +cust_id + "," + cruise_id + ", 'W')";
					insert_row = esql.executeQuery(query4);
				}
				int check = esql.executeQuery(query);

			}
			else{
				
				int rowCount = esql.executeQuery(query) + esql.executeQuery(query2);//+esql.executeQuery(query3);
			}
	
        	}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	//SELECT seats,numsold FROM CruiseInfo |X| Ship |X| Cruise WHERE numsold - seats = num_seats_available AND c_num = x 
	public static void ListNumberOfAvailableSeats(DBproject esql) {//5
		// For Cruise number and date, find the number of availalbe seats (i.e. total Ship capacity minus booked seats )
		try{
			String query = ""; //gives #seats available on cruise

			System.out.println("Input Cruise Number");
			String cruise_num = validateInteger(in.readLine(),0,Integer.MAX_VALUE);
			while(!checkIDExists(cruise_num, "Cruise",esql)){
				System.out.println("Please enter an existing Cruise or enter q to quit: ");
				cruise_num = in.readLine();
				if(cruise_num.equals("q"))
					return;
				cruise_num = validateInteger(cruise_num,0,Integer.MAX_VALUE);
			}
			//System.out.println("Input Cruise Departure Date as YYYY-MM-DD");
			//String date = "\'" + validateDate(in.readLine()) + "\'";
			query += "SELECT S.seats - C.num_sold AS Num_available FROM Ship S, CruiseInfo CI, Cruise C WHERE C.cnum = " + cruise_num + 
					" AND C.cnum = CI.cruise_id AND S.id = CI.ship_id";			
			int rowCount = esql.executeQuery(query);
			System.out.println("total row(s):" + rowCount);
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	//COUNT rid GROUP BY ship_id ORDER BY DESC 
	public static void ListsTotalNumberOfRepairsPerShip(DBproject esql) {//6
		// Count number of repairs per Ships and list them in descending order
		try{
			String query = "SELECT ship_id ,COUNT(rid) AS Total_Repairs FROM Repairs GROUP BY ship_id ORDER BY Total_Repairs DESC";
			int rowCount = esql.executeQuery(query);
        	        System.out.println("total row(s):" + rowCount);
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	//COUNT ccid WHERE cid = cruise_num && status = STATUS
	public static void FindPassengersCountWithStatus(DBproject esql) {//7
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		try{
			String query = "SELECT status, COUNT(ccid) AS Total FROM Reservation WHERE STATUS = " ;
			System.out.println("Which status (W,C,R) do you want to check?: ");
			String input = in.readLine();

			//checking that user provides proper input
			while(!input.equals("W") && !input.equals("C") && !input.equals("R")){
				System.out.print("Please provide a valid option: ");
				input = in.readLine();
				System.out.println();
			}

			query += "\'" + input + "\'";
			query += " GROUP BY status ORDER BY Total DESC";
			int rowCount = esql.executeQuery(query);
        	System.out.println("total row(s):" + rowCount);
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	//additional methods added

	public static String validateString(String value, int min, int max){
		String toReturn = value;
		while(toReturn.length() < min || toReturn.length() > max){
			System.out.println("Please provide input with more than " + min + " characters and less than " + max + " characters: ");
			try{
				toReturn = in.readLine();
			}
			catch(Exception e){
				System.err.println(e.getMessage());
			}
		}
		return toReturn;
	}

	public static String validateInteger(String value, int min, int max){
		String toReturn = value;

		while(!isInteger(toReturn) || Integer.parseInt(toReturn) >= max || Integer.parseInt(toReturn) < min){
			System.out.println("Please provide Integer value > " + min + " and < " + max + ": ");
			try{
				toReturn = in.readLine();
			}
			catch(Exception e){
				System.err.println(e.getMessage());
			}
		}

		return toReturn;
	}

	//validates that all entries input are Integers
	public static boolean isInteger(String value){
		for(int i = 0; i < value.length(); i++){
			if(value.charAt(i) < '0' || value.charAt(i) > '9')
				return false;
		}

		return true;
	}

	//to check if an ID already exists
	public static boolean checkIDExists(String value, String relation, DBproject esql){
			String query;
			int rowCount = 0; 
			if(relation.equals("Cruise"))
				query = "SELECT * FROM Cruise WHERE cnum = " + value;
			else if(relation.equals("Reservation"))
				query = "SELECT * FROM Reservation WHERE cid = " + value;
			else
				query = "SELECT * FROM " + relation + " WHERE id = " + value;
			try{
				rowCount = esql.executeQuery(query);
			}
			catch(Exception e){
				System.err.println(e.getMessage());
			}
			if(rowCount > 0)
					return true;
			return false;
	}
	public static boolean checkReservation(String id, String cnum, DBproject esql) {
		String query;
		int rowCount = 0;
		query = "SELECT * FROM Reservation WHERE ccid = " + id + " AND cid = " + cnum;
		try{
			rowCount = esql.executeQuery(query);
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
		if(rowCount >0){
			return true;
		}
		return false;
	}

	public static String validateDate(String value){
		String toReturn = value;
		while(!isValidDate("dd/MM/yyyy",toReturn)){
			System.out.println("Please Input Cruise date as DD/MM/YYYY within range:");
			try{
				toReturn = in.readLine();
			}
			catch(Exception e){
				System.err.println(e.getMessage());
			}
		}
		return toReturn;
	}

	//found code to validate the date on https://stackoverflow.com/questions/20231539/java-check-the-date-format-of-current-string-is-according-to-required-format-or/20232680
	public static boolean isValidDate(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

}
