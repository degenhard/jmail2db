import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.util.Date;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import com.sun.mail.imap.IMAPInputStream;
import com.sun.mail.imap.IMAPNestedMessage;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QPDecoderStream;

import java.sql.*;

public class jmail2db {


	/**
	 * @param args
	 * @return 
	 * @return 
	 * @throws IOException 
	 * @throws MessagingException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	
	public static String getplainBody(Object multibody, String str_body) throws MessagingException {

		Multipart mp;
		
		if (multibody instanceof String)  
	    { 
				str_body += multibody;  
	    } 
		
	    if (multibody instanceof Multipart)  
	    {  
	        mp = (Multipart)multibody;  
	        for ( int x = 0; x < mp.getCount(); x++ ){
	       	 
	       	 	BodyPart part = mp.getBodyPart(x);
	       	 
	        	try {
					if (part.getContent() instanceof BASE64DecoderStream) {
							 
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	        	  
	        	try {
					if (part.getContent() instanceof String) {
							str_body += part.getContent();
							}	
	        	} catch (UnsupportedEncodingException ex) {
					
	        		//InputStream is = part.getInputStream();
					str_body += "UnsupportedEncoding!";
					//str_body += is.toString();
					//System.out.println("FUNKTIONIERT!");				
	        	
	        	} catch (IOException e) {
					// TODO Auto-generated catch block
					//System.out.println("Kein Single");
					//e.printStackTrace();
				} 
	        	 
	        	 
	        	try {
					if (part.getContent() instanceof QPDecoderStream) {
							 
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	        		
	        	try {
					if (part.getContent() instanceof IMAPInputStream) {
							 
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	            	 
	        	try {
					if (part.getContent() instanceof IMAPNestedMessage) {
							   			 
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	        	
	        	try {
					if (part.getContent() instanceof Multipart) { 
						 str_body = getplainBody(part.getContent(), str_body);
					}
				} catch (IOException e) {
					
				}
	        	 
	         }
	     }
	     return str_body;
	}
	
	public static void getMail( String mail_host, String mail_user, String mail_passwd, String db_name, Boolean readwrite, String type ) throws Exception {
		
		Class.forName("org.sqlite.JDBC");
		String jdbc = "jdbc:sqlite:" + db_name;
		Connection conn = DriverManager.getConnection(jdbc);
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table if not exists emails (mail_body, mail_to, mail_to_personal, mail_from, mail_from_personal, mail_date, mail_subject);");
		
		PreparedStatement prep = conn.prepareStatement("insert into emails values (?, ?, ?, ?, ?, ? ,?);");
		String MAIL_BODY;
		String MAIL_TO;
		String MAIL_TO_PERSONAL;
		String MAIL_FROM;
		String MAIL_FROM_PERSONAL;
		String MAIL_DATE;
		String MAIL_SUBJECT;
		System.setProperty("mail.mime.decodetext.strict", "false");
		System.setProperty("mail.mime.address.strict", "true");
		SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Session session = Session.getDefaultInstance( new Properties() );
		
	    Store store = session.getStore( type );
	    store.connect( mail_host, mail_user, mail_passwd );

	    Folder folder = store.getFolder( "INBOX" );
	    if(readwrite) {
	    	folder.open( Folder.READ_WRITE );
	    } else {
	    	folder.open( Folder.READ_ONLY );
	    }
	    	
	    Message message[] = folder.getMessages();
	    
	    System.out.println("Now save "+message.length+ " messages. ("+new Date()+")");
	    for ( int i = 0; i < message.length; i++ )
	    {
	      Message m = message[i];
	 
	      MAIL_FROM = ((InternetAddress) m.getFrom()[0]).getAddress();
	      MAIL_FROM_PERSONAL = ((InternetAddress) m.getFrom()[0]).getPersonal();
	      MAIL_TO = ((InternetAddress) m.getReplyTo()[0]).getAddress();
	      MAIL_TO_PERSONAL = ((InternetAddress) m.getReplyTo()[0]).getPersonal();
	      MAIL_BODY = getplainBody(m.getContent(), "");
	      MAIL_DATE = datetime.format(m.getSentDate());
	      MAIL_SUBJECT = m.getSubject();
	      
	      System.out.print(i+1);
	      if (i+1 < message.length){
	    	  System.out.print(",");
	      }
	      prep.setString(1, MAIL_BODY);
	      prep.setString(2, MAIL_TO);
	      prep.setString(3, MAIL_TO_PERSONAL);
	      prep.setString(4, MAIL_FROM);
	      prep.setString(5, MAIL_FROM_PERSONAL);
	      prep.setString(6, MAIL_DATE);
	      prep.setString(7, MAIL_SUBJECT);
	      prep.addBatch();
	      m.setFlag(Flags.Flag.DELETED, true);
	       
	    }
	    conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	    conn.close();
	    folder.close(true);
	    store.close();
	    System.out.println("\nDone...("+new Date()+")\n\n");
	  }
	

	public static void main( String args[] ) throws Exception
	  {
		String mailserver = "";
		String username = "";
		String password = "";
		Boolean mode = false;
		String dbfile = "emails.db";
		String type = "";
		
		for (int i = 0; i < args.length; i++){
			if(args[i].contains("--readwrite")) {
				mode = true;
			}
			if(args[i].contains("--mailserver")) {
				mailserver = args[i+1]; 
			}

			if(args[i].contains("--username")) {
				username = args[i+1]; 
			}

			if(args[i].contains("--password")) {
				password = args[i+1]; 
			}

			if(args[i].contains("--dbfile")) {
				dbfile = args[i+1]; 
			}
			
			if(args[i].contains("--type")) {
				String value = args[i+1];
				if(value.contains("pop3") || value.contains("pop3s") || value.contains("imap") || value.contains("imaps")) {
					type = value;
				} 
			}		
			
		}
		
		if(mailserver.isEmpty() || username.isEmpty() || password.isEmpty() || type.isEmpty()) {
			System.out.println("Usage:\n --type pop3, pop3s, imap, imaps\n --username username\n --password password\n --mailserver mailserver\n --readwrite (optional, if not set is readonly on mailserver)\n --dbfile filename (optional, default is 'emails.db')");
			
		} else {
			getMail(mailserver,username,password,dbfile,mode,type);
		}
	  }
	

}
