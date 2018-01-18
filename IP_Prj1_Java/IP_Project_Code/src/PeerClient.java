import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import javax.swing.plaf.synth.SynthSpinnerUI;
import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;



public class PeerClient implements Runnable{
	
	private String path;
	private String servername;
	private String peername;
	private int port;
	
	private PeerClient(String filepath,int portnum, String serverhostname, String peerhostname)
	{
		path = filepath;
		port = portnum;
		servername = serverhostname;
		peername = peerhostname;
	
	}
	public static void main (String args[]) throws Exception
	{
		LinkedList<PeerInformation> list = new LinkedList<PeerInformation>();
		PeerInformation x = new PeerInformation();
		try {
			System.out.println("Enter the RFC file path");
			Scanner input= new Scanner(System.in);
			String filepath = input.nextLine();
			System.out.println("Enter the  (Peer) Host port number");
			input= new Scanner(System.in);
			int portnum = Integer.parseInt(input.nextLine());
			System.out.println("Enter the  Peer IP address");
			input= new Scanner(System.in);
			String peerhostname= input.nextLine();
			System.out.println("Enter the  RS Server IP Address");
			input= new Scanner(System.in);
			String serverhostname= input.nextLine();
			PeerServer pserver = new PeerServer(peerhostname,portnum,filepath);
			PeerClient pclient = new PeerClient(filepath, portnum, serverhostname, peerhostname);
			Thread threadserver = new Thread(pserver);
			Thread threadclient = new Thread(pclient);
			threadserver.start();
			threadclient.start();
}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

@SuppressWarnings({ "unchecked", "resource" })
public void run() {
	PeerServer s = new PeerServer(peername,port,path);
	LinkedList<PeerInformation> allpeers = new LinkedList<PeerInformation>();
	LinkedList<PeerInformation> activepeers = new LinkedList<PeerInformation>();
	LinkedList<RFCInformation> rfcquerylist = new LinkedList<RFCInformation>();
	LinkedList<RFCInformation> mergerfclist = new LinkedList<RFCInformation>();
	
	while (true)
	{ 
		System.out.println("Enter your choice number \n 1. Register \n 2. Leave \n 3. Keep Alive \n 4. PQuery  \n 5. RFCQuery \n 6. GET RFC \n 7. Task 1 and Task 2 Output \n 8. Terminate \n");
		Scanner input = new Scanner(System.in);
		int option = input.nextInt();
		MsgCreator sendmessage = new MsgCreator();
		
		
		switch(option)
		{
		case 1: //Register
			try {
				Socket reg = new Socket(servername, 65423);
				ObjectOutputStream ostrm = new ObjectOutputStream(reg.getOutputStream());
				ObjectInputStream istrm = new ObjectInputStream(reg.getInputStream());
				String registermessage = sendmessage.RegReqCreate(peername,port, 0);
				ostrm.writeObject(registermessage);
				System.out.println(registermessage);
			    System.out.println("Registered!!");
			    String rcvmsg = (String) istrm.readObject();
				System.out.println(rcvmsg);
				reg.close();
				break;
			} catch (IOException | ClassNotFoundException e) {
		
				e.printStackTrace();
			}
		case 2: //Leave
			try {
				Socket leave = new Socket(servername, 65423);
				PeerInformation leavingpeer = new PeerInformation();
				ObjectOutputStream ostrm = new ObjectOutputStream(leave.getOutputStream());
				for (PeerInformation l : allpeers)
				{
					if(l.getportnum()== port)
					{
						leavingpeer =l;
					     break;
					}
				}
				int cookie = leavingpeer.getcookie();
				String leavemessage = sendmessage.LeaveReqCreate(peername,port, cookie);
				ostrm.writeObject(leavemessage);
				System.out.println(leavemessage);
			    System.out.println("Peer left the System!!");
				leave.close();
				break;
			} catch (IOException e) {
		
				e.printStackTrace();
			}
		case 3: //KeepAlive
			try {
				Socket keepalive = new Socket(servername,65423);
				PeerInformation alivepeer = new PeerInformation();
				ObjectOutputStream ostrm = new ObjectOutputStream(keepalive.getOutputStream());
				for (PeerInformation l : allpeers)
				{
					if(l.getportnum()== port)
					{
						alivepeer =l;
					     break;
					}
				}
				String alivemessage = sendmessage.KeepAliveReqCreate(peername,port, alivepeer.getcookie());
				ostrm.writeObject(alivemessage);
				System.out.println(alivemessage);
				keepalive.close();
				break;
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		case 4://PQuery
			try {
				Socket query = new Socket(servername,65423);
				PeerInformation pquerypeer = new PeerInformation();
				ObjectOutputStream ostrm = new ObjectOutputStream(query.getOutputStream());
				ObjectInputStream istrm = new ObjectInputStream(query.getInputStream());
				for (PeerInformation l : allpeers)
				{
					if(l.getportnum()== port)
					{
						pquerypeer =l;
					     break;
					}
				}
				String querymessage = sendmessage.PQueryReqCreate(peername,port, pquerypeer.getcookie());
				ostrm.writeObject(querymessage);
				System.out.println(querymessage);
				activepeers = (LinkedList<PeerInformation>) istrm.readObject();
				if(activepeers.size()==1)
				{
					System.out.println("No Other Active Peers Found");
				}
				else
				{
				for (PeerInformation l : activepeers)
				{
					System.out.println("Hostname : "+l.gethostname() + " Port : "+l.getportnum() + " ID : "+ l.getcookie() );
				}
				}
				query.close();
				break;
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		case 5: //RFC Query
			try {
				if(activepeers !=null)
				{
					while(!mergerfclist.isEmpty())
					{
						mergerfclist.removeFirst();
					}
				for (PeerInformation l : activepeers)
				{
				  if (port != l.getportnum())
				  {
					Socket rfcquery = new Socket(l.gethostname(),l.getportnum());
					ObjectOutputStream ostrm = new ObjectOutputStream(rfcquery.getOutputStream());
					ObjectInputStream istrm = new ObjectInputStream(rfcquery.getInputStream());
					String rfcquerymessage = sendmessage.RFCQueryReqCreate(peername,port);
					ostrm.writeObject(rfcquerymessage);
					System.out.println(rfcquerymessage);
					rfcquerylist = (LinkedList<RFCInformation>) istrm.readObject();
					//String message = (String) istrm.readObject();
					//System.out.println(message);
					mergerfclist.addAll(rfcquerylist);
					
					
					rfcquery.close();
					}
				  }
				  for(RFCInformation r : mergerfclist)
					{
						System.out.println("Hostname "+r.gethostname()+" Port: "+ r.getportnum()+" RFCTitle: "+ r.gettitle());
					}
				}
				
				break;
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		case 6: //GETRFC
			try {
			String rfchostname="";
			int rfcportnum = 0;
			System.out.println("Enter the RFC file name(Format rfcnum.pdf/txt) :");
			Scanner inputrfcname = new Scanner (System.in);
			String rfcname =inputrfcname.nextLine();
			long timerstart = System.currentTimeMillis();
			int rfcnum = Integer.parseInt(rfcname.replaceAll("\\D+", ""));
			if (mergerfclist!=null)
			{
			for(RFCInformation r : mergerfclist)
			{
				if (rfcnum == r.getrfcnum())
				{
					rfchostname = r.gethostname();
					rfcportnum = r.getportnum();
					s.rfclist.add(r);
					break;
				}
				
			}
			}
			Socket rfcskt = new Socket(rfchostname, rfcportnum);
			ObjectOutputStream ostrm = new ObjectOutputStream(rfcskt.getOutputStream());
			String message = sendmessage.GetRFCReqCreate(rfchostname, rfcnum);
			System.out.println(message);
			ostrm.writeObject(message);
			String rfcsavepath = path + "/" + rfcname;
			//The following code snippet is referred from www.java2s.com/Code/Java/Network-Protocol/TransferafileviaSocket.htm, http://www.rgagnon.com/javadetails/java-0542.html
			byte[] mybytearray = new byte[10000000];
		    InputStream is = rfcskt.getInputStream();
		    FileOutputStream fos = new FileOutputStream(rfcsavepath);
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
		    int current =0;
		    do {
		         bytesRead =
		            is.read(mybytearray, current, (mybytearray.length-current));
		         if(bytesRead >= 0) current += bytesRead;
		      } while(bytesRead > -1);
		    
		    bos.write(mybytearray, 0, current);
		    bos.flush();
		    System.out.println("File "+rfcname+"downloaded ("+ current +"bytes)");
		    long timerstop = System.currentTimeMillis();
		    long totaltime = (timerstop-timerstart);
		    int rfcnumber = Integer.parseInt(rfcname.replaceAll("\\D+", ""));
		    System.out.println("Time taken to download file is :"+totaltime +"ms");
		    bos.close();
		    fos.close();
		    rfcskt.close();
		    break;
		    }
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		case 7: // download all RFCs
			try {
				long cumulativetime = 0;
				int i =0;
				System.out.println("1. Download all RFC files from other Peers  \n 2. Download specific number of RFC files");
				Scanner choice= new Scanner(System.in);
				int choicenum = choice.nextInt();
				if (choicenum == 1)
				{  i = mergerfclist.size();
				System.out.println("Size of list "+i);
				}
				else if(choicenum ==2){
					System.out.println("Enter number of RFCs to be downloaded");
					Scanner rfcinput= new Scanner(System.in);
					 i = rfcinput.nextInt();	
				}
				else
				{
					System.out.println("Invalid Choice");
					break;
				}
				for(RFCInformation k :mergerfclist) 
				{
				String rfchostname=k.gethostname();
				int rfcportnum = k.getportnum();
				String rfcname =k.gettitle();
				long timerstart = System.currentTimeMillis();
				int rfcnum = Integer.parseInt(rfcname.replaceAll("\\D+", ""));
			
				Socket rfcskt = new Socket(rfchostname, rfcportnum);
				ObjectOutputStream ostrm = new ObjectOutputStream(rfcskt.getOutputStream());
				String message = sendmessage.GetRFCReqCreate(rfchostname, rfcnum);
				System.out.println(message);
				ostrm.writeObject(message);
				String rfcsavepath = path + "/" + rfcname;
				//The following code snippet is referred from www.java2s.com/Code/Java/Network-Protocol/TransferafileviaSocket.htm, http://www.rgagnon.com/javadetails/java-0542.html
				byte[] mybytearray = new byte[10000000];
			    InputStream is = rfcskt.getInputStream();
			    FileOutputStream fos = new FileOutputStream(rfcsavepath);
			    BufferedOutputStream bos = new BufferedOutputStream(fos);
			    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
			    int current =0;
			    do {
			         bytesRead =
			            is.read(mybytearray, current, (mybytearray.length-current));
			         if(bytesRead >= 0) current += bytesRead;
			      } while(bytesRead > -1);
			    
			    bos.write(mybytearray, 0, current);
			    bos.flush();
			    System.out.println("File "+rfcname+"downloaded ("+ current +"bytes)");
			    long timerstop = System.currentTimeMillis();
			    long totaltime = (timerstop-timerstart);
			    cumulativetime += totaltime;
			    System.out.println("Cumulative time for all downloads :"+cumulativetime +"ms");
			    bos.close();
			    fos.close();
			    s.rfclist.add(k);
			    rfcskt.close();
			    i--;
			    if (i ==  0) {
			    	break;
			    }
				}
				
			    break;
			    }
				catch(Exception e)
				{
					e.printStackTrace();
				}
			
		case 8 : //Exit
			System.exit(0);
			break;
		default:
			System.out.println("Invalid Choice.. Enter again");
			
		}
		
	}
	
}

   
}
