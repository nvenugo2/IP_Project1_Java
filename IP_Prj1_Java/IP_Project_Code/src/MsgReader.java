import java.io.Serializable;
import java.util.Arrays;

public class MsgReader implements Serializable{

	private String operation;
	private String hostname;
	private int portnum;
	private int cookie;
	private String version;
	private String rfcnum;
	
	public void ptorsreqread(String msg)
	{
		String[] response=msg.split("[ \n]+");
		operation= response[0];
		version = response[1];
		hostname = response[3];
		portnum = Integer.parseInt(response[5]);
		cookie = Integer.parseInt(response[7]);
	}
	public void ptopreqread(String msg)
	{  
		String[] response=msg.split("[ \n]+");
		operation= response[1];
		if(operation.equals("RFC-Index"))
		{
		version = response[2];
		hostname = response[4];
		}
		else if(operation.equals("RFC"))
		{	
		version = response[3];
		hostname = response[5];
		rfcnum = response[2];
		}
	}
	
	public String gethostname()
	{
		return hostname;
	}
	public int getportnum()
	{
		return portnum;
	}
	public String getrfcnum()
	{
		return rfcnum;
	}
	public int getcookie()
	{
		return cookie;
	}
	public String getoperation()
	{
		return operation;
	}
	public String getversion()
	{
		return version;
	}
		
	
	}


