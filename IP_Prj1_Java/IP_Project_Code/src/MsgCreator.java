import java.io.Serializable;

public class MsgCreator implements Serializable {
	private String msg;
	
	public String RegReqCreate(String  hostname, int portnum, int cookie)
	{   
		msg = "REG P2P-DI/1.0 \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nID: " + Integer.toString(cookie)+ "\nOS: Ubuntu 16.04.3_LTS" ;
		return msg;
	}
	public String LeaveReqCreate(String  hostname, int portnum, int cookie)
	{   
		msg = "LEAVE P2P-DI/1.0 \nHost: "+ hostname +"\nPort: " + Integer.toString(portnum) + "\nID: " + Integer.toString(cookie)+ "\nOS: Ubuntu 16.04.3_LTS" ;
		return msg;
	}
	public String PQueryReqCreate(String  hostname, int portnum, int cookie)
	{   
		msg = "PQUERY P2P-DI/1.0 \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nID: " + Integer.toString(cookie) + "\nOS: Ubuntu 16.04.3_LTS" ;
		return msg;
	}
	public String KeepAliveReqCreate(String  hostname, int portnum, int cookie)
	{   
		msg = "ALIVE P2P-DI/1.0 \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nID: " + Integer.toString(cookie) + "\nOS: Ubuntu 16.04.3_LTS" ;
		return msg;
	}
	public String RFCQueryReqCreate(String  hostname, int portnum)
	{
		msg = "GET RFC-Index P2P-DI/1.0 \nHost: "+hostname + "\nOS: Ubuntu 16.04.3_LTS" ;
		return msg;
	}
	public String GetRFCReqCreate(String hostname,int rfcnum)
	{   msg = "GET RFC "+rfcnum+" P2P-DI/1.0 \nHost: "+hostname + "\nOS: Ubuntu 16.04.3_LTS " ;
		return msg;
	}
	public String RegRespCreate(String  hostname, int portnum, int cookie)
	{
		msg = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nREGISTERED in ID " + Integer.toString(cookie);
		return msg;
	}
	public String LeaveRespCreate(String  hostname, int portnum, int cookie)
	{
		msg = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nDE-REGISTERED from ID " + Integer.toString(cookie);
		return msg;
	}
	public String PQueryRespCreate(String  hostname, int portnum, int cookie)
	{
		msg = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nRETURN ACTIVE LIST ";
		return msg;
	}
	public String KeepAliveRespCreate(String  hostname, int portnum, int cookie)
	{
		msg = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort: " + Integer.toString(portnum) + "\nTTL RESET TO 7200 for ID " + Integer.toString(cookie);
		return msg;
	}
	//public String RFCQueryRespCreate(String  hostname, int portnum, int cookie)
	//{
		//msg = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort:" + Integer.toString(portnum) + "\nREGISTERED in ID " + Integer.toString(cookie);
		//return msg;
	//}
	//public String GetRFCRespCreate(String  hostname, int portnum, int cookie)
	//{
		//msg = "P2P-DI/1.0 200 OK \nHost: "+hostname +"\nPort:" + Integer.toString(portnum) + "\nREGISTERED in ID " + Integer.toString(cookie);
		//return msg;
	//}

}

