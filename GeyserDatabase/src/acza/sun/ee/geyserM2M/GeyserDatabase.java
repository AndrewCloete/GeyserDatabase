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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


public class GeyserDatabase {

	
	private static SCLapi nscl = new SCLapi();
	private static int APOC_PORT;


	public static void main(String args[]){
		// ---------------------- Sanity checking of command line arguments -------------------------------------------
		if( args.length != 2)
		{
			System.out.println( "Usage: <NSCL IP address> <aPoc server port>  <TODO: Database parameters>" ) ;
			return;
		}

		final String NSCL_IP_ADD = args[0];//"52.10.236.177";//"localhost";//
		if(!ipAddressValidator(NSCL_IP_ADD)){
			System.out.println( "IPv4 address invalid." ) ;
			return;
		}

		;
		try{
			APOC_PORT = Integer.parseInt( args[1] ); // Convert the argument to ensure that is it valid
		}catch ( Exception e ){
			System.out.println( "aPoc port invalid." ) ;
			return;
		}
		//---------------------------------------------------------------------------------------------------------------

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
			System.out.println("Apoc server started.");
		} catch (Exception e1) {
			System.out.println("Apoc server failed.");
			e1.printStackTrace();
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
				nscl.subscribeToContent(getGeyserIdFromString(app), "DATA", "geyser", "localhost:"+ APOC_PORT);
			}
			
		}
		
		nscl.subscribeToApplications("database", "localhost:"+ APOC_PORT);
		

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
			System.out.println("Inbound POST apoc request received");

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
			System.out.println("Inbound notification: " + notify.getStatusCode());
			
			//Debug
			System.out.println("Request URI: " + requestURI);
			//System.out.println(builder);
			//System.out.println(new String(notify.getRepresentation().getValue(), StandardCharsets.ISO_8859_1));
			
						
			/* PSEUDO:
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
			*/
			String target_resource = requestURI.substring(requestURI.lastIndexOf("/")+1);
			if(target_resource.equalsIgnoreCase("application")){
				
				Application app = (Application) xm.xmlToObject(new String(notify.getRepresentation().getValue(), StandardCharsets.ISO_8859_1));
				if(notify.getStatusCode().equals(StatusCode.STATUS_CREATED)){
					System.out.println("New application registered : " + app.getAppId());
					System.out.println("Geyser ID parser: " + getGeyserIdFromString(app.getAppId()));
					nscl.subscribeToContent(getGeyserIdFromString(app.getAppId()), "DATA", "geyser", "localhost:"+ APOC_PORT);
				}
				else if(notify.getStatusCode().equals(StatusCode.STATUS_DELETED)){
					System.out.println("Application deregistered : " + app.getAppId());
				}
				else{
					System.out.println("Unexpexted application notification status code.");
				}
			}
			else if(target_resource.startsWith("geyser")){
				Long target_geyserclient_id = getGeyserIdFromString(target_resource);
				ContentInstance ci = (ContentInstance) xm.xmlToObject(new String(notify.getRepresentation().getValue(), StandardCharsets.ISO_8859_1));
				System.out.println("Inbound content instance: " + ci.getId());
				String jsonCommand = new String(ci.getContent().getValue(), StandardCharsets.ISO_8859_1);
				System.out.println("Inbound command string for Geyser "+ target_geyserclient_id +": " + jsonCommand);

			}
			
		}
	}
	
	private static long getGeyserIdFromString(String appId){
		try{
			return new Long(appId.substring(appId.lastIndexOf("_")+1));
		} catch (Exception e){
			System.out.println("Geyser ID failure."); 
			return (long)0000;
		}
	}
}
