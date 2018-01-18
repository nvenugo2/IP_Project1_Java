import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import java.net.Socket;
import java.net.SocketException;
import java.net.ServerSocket;
import java.io.PrintStream;

class DecrementTTL implements Runnable
{
PeerInformation peer = new PeerInformation();
public DecrementTTL(PeerInformation peer)
{
	this.peer = peer;
}
public void run() 
{
 try
 {
	 while(peer.getTTL() > 0)
	 {
		 peer.setTTL(peer.getTTL()-1);
		 Thread.sleep(1000);		 
	 }
	 peer.setactivestatus(false);
 }
 catch(Exception e)
 {
	 e.printStackTrace();
 }
		
}
	
}

public class RSServer implements Runnable{
	private static final ReentrantLock lock = new ReentrantLock();
	public static LinkedList<PeerInformation> allpeers = new LinkedList<PeerInformation>();
	private Socket conskt;
	private static int ID=0;
	
	
	public RSServer(Socket css,int ID) 
	{
	this.conskt = css;
	this.ID=ID;
	}
	
	public static boolean peer_add(PeerInformation Peer)
	{
		lock.lock();
		boolean addedpeer =allpeers.add(Peer);
		lock.unlock();
		return addedpeer;
	}
	
   public void remove_peer(PeerInformation Peer)
   {
	if(Peer.getactievstatus()== true)
	{   lock.lock();
		Peer.setactivestatus(false);
		Peer.setcookie(0);
		lock.unlock();
	}
   }
   public static LinkedList<PeerInformation> ActivePeers()
   {
	   LinkedList<PeerInformation> activepeerslist = new LinkedList<PeerInformation>();
	   for(PeerInformation i : allpeers)
	   {
		  if(i.getactievstatus()) 
		  {
			  activepeerslist.add(i);
		  }  
	   }
	   System.out.println("Active peers list is"+ activepeerslist.size());
	return activepeerslist;   
   }
    
   public static void main (String args[]) throws Exception
   {
 
       try {
       ServerSocket rs = new ServerSocket (65423);
       System.out.println("RS skt created");
       while(true)
       {
       Socket cs = rs.accept();
       System.out.println("Connected to Peer");
       Runnable running = new RSServer(cs,ID);
       Thread t = new Thread  (running);
       t.start();
       }
       }
       catch (Exception e)
       {
           System.out.println("Error creating Socket");
       }
   }

	public synchronized void run() {
		int cookie = ID;
		MsgCreator msgcreate = new MsgCreator();
		MsgReader msgread = new MsgReader();
		System.out.println("RSServer connected to"+ conskt.getInetAddress().getHostName() +" with port number" + conskt.getPort());
		try {
			ObjectInputStream ipstrm= new ObjectInputStream(conskt.getInputStream());
			ObjectOutputStream opstrm = new ObjectOutputStream (conskt.getOutputStream());
			String request = (String) ipstrm.readObject();
			msgread.ptorsreqread(request);
			String operation = msgread.getoperation();
			String message;
			boolean regflag = false;
			
			switch (operation)
			{
			case "REG":

				for (PeerInformation l :allpeers)
				{
					if((l.getportnum())==(msgread.getportnum()))
					{
						System.out.println("REGflag set to true");
					 regflag = true;
					   break;
					}
					System.out.println(l.gethostname());
				}
				if(regflag == false)
				{
				PeerInformation newpeer = new PeerInformation();
				String newhostname = msgread.gethostname();
				newpeer.sethostname(newhostname);
				newpeer.setcookie(++cookie);
				ID+=1;
				newpeer.setactivestatus(true);
				int newportnum = msgread.getportnum();
				newpeer.setportnum(newportnum);
				int newactivenum = newpeer.getactivenum() + 1;
				newpeer.setactivenum(newactivenum);
				Date date = new Date();
				newpeer.setregdate(date);
				newpeer.setTTL(7200);
				RSServer.peer_add(newpeer);
				message = msgcreate.RegRespCreate(newhostname, newportnum, cookie); // If Cookie is 0 it is not yet registered
				System.out.println(message);
				opstrm.writeObject(message);
		        Thread timer = new Thread(new DecrementTTL(newpeer));
		        timer.start();
			
				}
				else
				{
				System.out.println("Already Registered");	
				for (PeerInformation l :allpeers)
				{
					if((l.getportnum())==(msgread.getportnum()))
					{
						l.setTTL(7200);
						l.setactivestatus(true);
						int newactivenum = l.getactivenum() + 1;
						 Thread timer = new Thread(new DecrementTTL(l));
					        timer.start();
						System.out.println("Reset the TTL value");
						String msg = "P2P-DI/1.0 304 NOT-MODIFIED \nHost: "+l.gethostname() +"\nPort: " + Integer.toString(l.getportnum()) + "\nALREADY REGISTERED in ID " + Integer.toString(l.getcookie());
						opstrm.writeObject(msg);
						break;
					}
				}
				}
				break;
				
			case "LEAVE":
				PeerInformation leavingpeer = new PeerInformation();
				int port = msgread.getportnum();
				String hostname = msgread.gethostname();
				
				for (PeerInformation l :allpeers)
				{
					if(l.getportnum()== port)
					{
						leavingpeer =l;
					     break;
					}
				}
				leavingpeer.setactivestatus(false);
				int cookiee= leavingpeer.getcookie();
				leavingpeer.setTTL(0);
				message = msgcreate.LeaveRespCreate(hostname, port, cookiee);
				System.out.println(message);
				break;
				
			case "ALIVE":
				PeerInformation alivepeer = new PeerInformation();
				port = msgread.getportnum();
				hostname = msgread.gethostname();
				cookiee= msgread.getcookie();
				
				for (PeerInformation l :allpeers)
				{
					if(l.getportnum()== port)
					{
						alivepeer =l;
						break;
					}
				}
				alivepeer.setTTL(7200);
				message = msgcreate.KeepAliveRespCreate(hostname, port, cookiee);
				System.out.println(message);
				break;
		
			case "PQUERY":
				PeerInformation querypeer = new PeerInformation();
				LinkedList<PeerInformation> peerlist = new LinkedList<PeerInformation>();
				port = msgread.getportnum();
				hostname = msgread.gethostname();
				cookiee= msgread.getcookie();
				for (PeerInformation l :allpeers)
				{
					if(l.getportnum()== port)
					{
						querypeer =l;
						break;
					}
				}
				querypeer.setTTL(7200);
				peerlist = RSServer.ActivePeers();
				if (RSServer.ActivePeers().size()==1)
				{
					for (PeerInformation l :peerlist)
					{
						if(l.getcookie()== cookiee)
						{
							System.out.println("No Active Peers");
							opstrm.writeObject(peerlist);
							break;
						}
					}
				}
				else
				{	
					message = msgcreate.PQueryRespCreate(hostname, port, cookiee);
					opstrm.writeObject(peerlist);
					System.out.println(message);	
				}
				break;	
			default:
				System.out.println("Invalid Request");
				break;
			}	
		}
		catch (SocketException e) {
			e.printStackTrace();
			System.out.println("Socket exception");
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOexception");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("exception");
		}
		finally {
			try {
				conskt.close();
			}
			catch(IOException e){
				e.printStackTrace();
				System.out.println("Unable to close socket");
				
			}
		}	
	}
}

 