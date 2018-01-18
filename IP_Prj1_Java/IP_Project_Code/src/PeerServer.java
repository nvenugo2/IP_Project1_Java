import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.net.Socket;
import java.net.SocketException;
import java.net.ServerSocket;
import java.io.PrintStream;

public class PeerServer implements Runnable{
	LinkedList<RFCInformation> rfclist= new LinkedList<RFCInformation>();
	private int portnum;
	private String filepath;
	private String hostname;
	private static final ReentrantLock lock = new ReentrantLock();
	

	public PeerServer(String hostname, int portnum, String filepath) {
		this.portnum=portnum;
	    this.filepath=filepath;	
	    this.hostname = hostname;
	}

	public synchronized void run() {
		try {
		PeerServer s = new PeerServer(hostname,portnum,filepath);
		rfclist = s.localrfclist(filepath, hostname);
		ServerSocket pserverskt= new ServerSocket(portnum);
		System.out.println("Peer ServerSocket created........");
		while(true)
		{ 
		Socket pscskt= pserverskt.accept();
		System.out.println("Connected to Peer");
		while(!rfclist.isEmpty())
		{
			rfclist.removeFirst();
		}
			
			rfclist = s.localrfclist(filepath, hostname);
			ObjectOutputStream ostrm = new ObjectOutputStream(pscskt.getOutputStream());
			ObjectInputStream istrm = new ObjectInputStream(pscskt.getInputStream());
			MsgCreator msgcreate = new MsgCreator();
			MsgReader msgread = new MsgReader();
			String request = (String) istrm.readObject();
			msgread.ptopreqread(request);
			String operation = msgread.getoperation();
			String hostname =msgread.gethostname();
			String rfcnumber = msgread.getrfcnum();
			System.out.println("Operation Given is :"+operation+"  Hostname :"+hostname);
			String message;
			OutputStream os=null;
			BufferedInputStream bis=null;
			if((operation.equals("RFC-Index") ) || (operation.equals("RFC")))
			{
			switch(operation)
			{
			case "RFC-Index":
				message = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nOS: Ubuntu 16.04.3_LTS" ;
				System.out.println(message);
				ostrm.writeObject(rfclist);			
				break;
				
			case "RFC":
				try {
				String rfcnum= msgread.getrfcnum();
				//The following code snippet is referred from www.java2s.com/Code/Java/Network-Protocol/TransferafileviaSocket.htm,http://www.rgagnon.com/javadetails/java-0542.html
				String docupath = filepath + "/"+rfcnumber+".pdf";
				System.out.println(docupath);
				File rfcfile= new File(docupath);
				if(rfcfile.exists())
				{
				byte[] mybytearray = new byte[(int) rfcfile.length()];
			    bis = new BufferedInputStream(new FileInputStream(rfcfile));
			    bis.read(mybytearray, 0, mybytearray.length);
			    os = pscskt.getOutputStream();
			    System.out.println("Sending file!!");
			    os.write(mybytearray, 0, mybytearray.length);
			    message = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nOS: Ubuntu 16.04.3_LTS" ;
				System.out.println(message);
			    System.out.println("File Transferring!!!");
			    os.flush();
			    System.out.println("File transfer Done");
				}
				else
				{
					System.out.println("Error , File not transferred!!");
				}
				}finally
				{
					  if(os!=null)  os.close();
					  if(bis!=null)  bis.close();
				}
				break;
			default:
				System.out.println("Invalid Request");
				break;
					
	         }
			}
			else
			{
				System.out.println("400 Bad Request");
			}
			
				pscskt.close();
			
		}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	public LinkedList<RFCInformation> localrfclist(String path, String hostname)
	{ 
		File file = new File(path);
		File[] allfiles = file.listFiles();
		if(allfiles !=null)
		{
		for (File l : allfiles)
		{
			if(l.isFile())
			{
				RFCInformation rfcinfo = new RFCInformation(hostname,"",0);
				String p_RFCTitle= l.getName();
				int rfcnum= Integer.parseInt(p_RFCTitle.replaceAll("\\D+", ""));
				rfcinfo.setrfcnum(rfcnum);
				rfcinfo.settitle(p_RFCTitle);
				rfcinfo.sethostname(hostname);
				rfcinfo.setportnum(portnum);
				rfclist.add(rfcinfo);
			}	
		}
		}
		return rfclist;
		
	}

}
