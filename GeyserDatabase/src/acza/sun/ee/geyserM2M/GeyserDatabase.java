/* --------------------------------------------------------------------------------------------------------
 * DATE:	09 Jun 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: 
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: 
 * ---------------------------------------------------------------------------------------------------------
 */

package acza.sun.ee.geyserM2M;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.om2m.commons.resource.Application;
import org.eclipse.om2m.commons.resource.ContentInstance;
import org.eclipse.om2m.commons.resource.Notify;
import org.eclipse.om2m.commons.resource.StatusCode;
import org.eclipse.om2m.commons.utils.XmlMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class GeyserDatabase {

	private static final Logger logger = LogManager.getLogger(GeyserDatabase.class);
	private static SCLapi nscl;
	
	private static String APOC_URL;
	private static int APOC_PORT;
	private static String APOC;

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static String DB_URL;//DB_URL = "jdbc:mysql://localhost/GeyserSimM2M"; geyserm2m.cuxbzsmchnt1.us-west-2.rds.amazonaws.com

	//  Database credentials
	private static String USER;
	private static final String PASS = "gnm2mnscl";
	
	private static Connection rdb_conn = null;
	

	public static void main(String args[]){
		// ---------------------- Sanity checking of command line arguments -------------------------------------------
		if( args.length != 5)
		{
			System.out.println( "Usage: <NSCL IP address> <aPoc URL> <aPoc PORT>  <database URL> <database USER>" ) ;
			return;
		}

		final String NSCL_IP_ADD = args[0];//"52.10.236.177";//"localhost";//
		if(!ipAddressValidator(NSCL_IP_ADD)){
			System.out.println( "IPv4 address invalid." ) ;
			return;
		}

		APOC_URL = args[1];
		try{
			APOC_PORT = Integer.parseInt( args[2] ); // Convert the argument to ensure that is it valid
		}catch ( Exception e ){
			System.out.println( "aPoc port invalid." ) ;
			return;
		}
		APOC = APOC_URL + ":" + APOC_PORT;
		
		
		DB_URL = "jdbc:mysql://"+ args[3] +"/GeyserM2M";
		USER = args[4];
		//---------------------------------------------------------------------------------------------------------------

		logger.info("GeyserDatabase Usage: <NSCL IP address> <aPoc URL> <aPoc PORT>  <database URL> <database USER>");
		logger.info("GeyserDatabase started with parameters: " + args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4]);
		nscl = new SCLapi("nscl", NSCL_IP_ADD, "8080", "admin:admin");
		/* ***************************** START APOC SERVER ************************************************/

		Server server = new Server(APOC_PORT);

		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);

		// IMPORTANT:
		// This is a raw Servlet, not a Servlet that has been configured
		// through a web.xml @WebServlet annotation, or anything similar.
		handler.addServletWithMapping(ApocServlet.class, "/*");

		try {
			server.start();
			logger.info("Apoc server started.");
		} catch (Exception e) {
			logger.fatal("Apoc server failed to start.", e);
			return;
		}
		/* ********************************************************************************************/
		
		

		/*	PSEUDO:
		 * Subscribe to "applications"
		 * (Only once):
		 	* Discover current geyser applications at NSCL
		 	* Subscribe to content (both DATA and SETTINGS?)
		 * 
		 */
		
		//Look for all existing GEYSER applications and subscribe to them.
		List<String> appList = nscl.retrieveApplicationList();
		for(String app : appList){
			if(app.startsWith("geyser")){
				nscl.subscribeToContent(getGeyserIdFromString(app), "DATA", "geyser", APOC);
			}
			
		}
		
		nscl.subscribeToApplications("database", APOC);
		

	}
	
	private static boolean ipAddressValidator(final String ip_adr){
		
		if(ip_adr.equalsIgnoreCase("localhost"))
			return true;
		
		 Pattern adr_pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$", Pattern.DOTALL);
		 Matcher matcher = adr_pattern.matcher(ip_adr);
		 return matcher.matches();
	}
	
	
	@SuppressWarnings("serial")
	public static class ApocServlet extends HttpServlet {

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

			String requestURI = request.getRequestURI();
			
			InputStream in = request.getInputStream();
			InputStreamReader inr = new InputStreamReader(in);
			BufferedReader bin = new BufferedReader(inr);

			StringBuilder builder = new StringBuilder();
			String line;
			while((line = bin.readLine()) != null){
				builder.append(line);
			}
			
			
			XmlMapper xm = XmlMapper.getInstance();
			Notify notify = (Notify) xm.xmlToObject(builder.toString());
			System.out.println("Inbound notification: " + notify.getStatusCode() + " -- Request URI: " + requestURI);

			//(1)
			String target_resource = requestURI.substring(requestURI.lastIndexOf("/")+1);
			if(target_resource.equalsIgnoreCase("application")){
				
				Application app = (Application) xm.xmlToObject(new String(notify.getRepresentation().getValue(), StandardCharsets.ISO_8859_1));
				if(app.getAppId().startsWith("geyser")){
					if(notify.getStatusCode().equals(StatusCode.STATUS_CREATED)){
						logger.info("New application registered : " + app.getAppId());
						logger.info("Geyser ID parser: " + getGeyserIdFromString(app.getAppId()));
						nscl.subscribeToContent(getGeyserIdFromString(app.getAppId()), "DATA", "geyser", APOC);
					}
					else if(notify.getStatusCode().equals(StatusCode.STATUS_DELETED)){
						logger.warn("Application deregistered : " + app.getAppId());
					}
					else{
						logger.warn("Unexpexted application notification status code: " + notify.getStatusCode());
					}
					System.out.println();
				}
			}
			else if(target_resource.startsWith("geyser")){
				Long target_geyserclient_id = getGeyserIdFromString(target_resource);
				ContentInstance ci = (ContentInstance) xm.xmlToObject(new String(notify.getRepresentation().getValue(), StandardCharsets.ISO_8859_1));
				String jsonCommand = new String(ci.getContent().getValue(), StandardCharsets.ISO_8859_1);
				System.out.println("Inbound data string for Geyser "+ target_geyserclient_id +": " + jsonCommand);


				//Parse JSON, build SQL string and insert into database
				try{
					String sql = generateSQLDatapointEntry(jsonCommand);

					Statement stmt = null;
					try{
						//Register JDBC driver
						Class.forName(JDBC_DRIVER);

						//Open a connection
						rdb_conn = DriverManager.getConnection(DB_URL,USER,PASS);

						//Execute a query
						stmt = rdb_conn.createStatement();
						stmt.executeUpdate(sql);
						System.out.println("Inserted into RDB.");
						System.out.println();
						
						stmt.close();
						rdb_conn.close();
					}catch(SQLException se){
						//Handle errors for JDBC
						logger.error("SQLException: ", se);
					}catch(Exception e){
						//Handle errors for Class.forName
						logger.error("Unexpected database exception: ", e);
					}finally{
						//finally block used to close resources
						try{
							if(stmt!=null)
								stmt.close();
						}catch(SQLException se2){
							logger.error("SQLException closing statement: ", se2);
						}// nothing we can do
						try{
							if(rdb_conn!=null)
								rdb_conn.close();
						}catch(SQLException se){
							logger.error("SQLException closing database connection: ", se);
						}
					}
					
				}catch(ParseException pe){
					logger.error("Corrupt inbound  JSON: " + jsonCommand + ". Parse exeption at position: " + pe.getPosition(), pe);
				}
			}
			else{
				logger.warn("Unknown target resource apoc recieved: " + target_resource);
			}

		}
	}
	
	private static long getGeyserIdFromString(String appId){
		try{
			return new Long(appId.substring(appId.lastIndexOf("_")+1));
		} catch (Exception e){
			logger.error("Geyser ID failure. Using defualt ID '0000'"); 
			return (long)0000;
		}
	}
	
	private static Object getValueFromJSON(String key, String JSON) throws ParseException{

		JSONParser parser=new JSONParser();

		Object obj = parser.parse(JSON);
		JSONArray array = new JSONArray();
		array.add(obj);	
		JSONObject jobj = (JSONObject)array.get(0);

		return jobj.get(key);
	}
	
	//Important. If the JSON is corrupt no entry should be written to RDB. This function must throw an exception.
	private static String generateSQLDatapointEntry(String jsonDatapoint) throws ParseException{
		
		//Geyser ID
		long geyser_id = (long)getValueFromJSON("ID", jsonDatapoint);

		//Version
		long version = 0001;
		
		//Timestamps 
		java.sql.Timestamp serverTS = new java.sql.Timestamp(System.currentTimeMillis());
		
		java.sql.Timestamp clientTS;
		try{
			long clientUnixTS = (long)getValueFromJSON("Tstamp", jsonDatapoint);
			clientTS = new java.sql.Timestamp(clientUnixTS*1000);
		}catch(Exception e){
			clientTS = serverTS;
		}
		
		//Relay state
		boolean relay_state = false; //(2)
		String rs = (String)getValueFromJSON("Rstate", jsonDatapoint);
		if(rs.equalsIgnoreCase("ON"))
			relay_state = true;
		else
			relay_state = false;
		
		//Valve state
		boolean valve_state = false; 
		String vs = (String)getValueFromJSON("Vstate", jsonDatapoint);
		if(vs.equalsIgnoreCase("OPEN"))
			relay_state = true;
		else
			relay_state = false;
		
		//Drip detect (Geyser state)
		boolean drip_detect = false;
		//String gs = (String)getValueFromJSON("Gstate", jsonDatapoint);
		//if(gs.equalsIgnoreCase("OK"))
		//	relay_state = true;
		//else
		//	relay_state = false;
		
		//Temperatures
		long t1 = (long)getValueFromJSON("T1", jsonDatapoint);
		long t2 = (long)getValueFromJSON("T2", jsonDatapoint); 
		long t3 = (long)getValueFromJSON("T3", jsonDatapoint);
		long t4 = (long)getValueFromJSON("T4", jsonDatapoint);

		//TODO
		long watt_avgpmin = (long)getValueFromJSON("KW", jsonDatapoint);;
		long kwatt_tot = 0;
		long hot_flow_ratepmin = 0;
		long hot_litres_tot = 0;
		long cold_flow_ratepmin = 0;
		long cold_litres_tot = 0;



		
		return "INSERT INTO timestamps(geyser_id, version, server_stamp, client_stamp, relay_state, valve_state, drip_detect, "
				+ "t1, t2, t3, t4, watt_avgpmin, kwatt_tot, hot_flow_ratepmin, hot_litres_tot, cold_flow_ratepmin, cold_litres_tot)"
				+ "VALUES(" + geyser_id
				+ ", " + version
				+ ", " + "'" + serverTS + "'"
				+ ", " +  "'" + clientTS + "'"
				+ ", " + relay_state //(2)
				+ ", " + valve_state
				+ ", " + drip_detect
				+ ", " + t1
				+ ", " + t2
				+ ", " + t3
				+ ", " + t4
				+ ", " + watt_avgpmin
				+ ", " + kwatt_tot
				+ ", " + hot_flow_ratepmin 
				+ ", " + hot_litres_tot 
				+ ", " + cold_flow_ratepmin 
				+ ", " + cold_litres_tot 
				+")";
	}
}

/*------------- NOTES -----------------------------------
 * (1)
 * PSEUDO:
 * If notification is of type "application"
 	* If status = "STATUS_CREATED"
 		* subscribe to new content
		* else if status = "STATUS_DELETED"
			* do nothing
		* else
			* email admin.
 	* (Tables in RDB will have been created beforehand by admin)
 * Else, if it is of type "content"
 	* Put content in database.
 	* 
	* 
	* Only 2 URI possibilities
		* database/application
		* database/geyser_1234
	* 
 * 
 * 
 * (2)
 * MySQL DOES have native boolean types it just says that it is tinyints(1). 
 * But if you give it true or false, it will insert 1 or 0
 * 
 * (3)
 * "Ver":version, 
 * "ID":"id",
 * "Tstamp":timestamp (UNIX in seconds)
 * "Rstate":"relayState",
 * "Vstate":"valveState",
 * "Gstate":"geyserState",
 * "T1":temperature1,
 * "T2":temperature2,
 * "T3":temperature3,
 * "T4":temperature4,
 * "KW":power,
 * "KWH":energy,
 * "HLmin":HflowRate,
 * "HLtotal":HtotalLitres,
 * "CLmin":CflowRate,
 * "CLtotal":CtotalLitres,
 *-------------------------------------------------------*/

