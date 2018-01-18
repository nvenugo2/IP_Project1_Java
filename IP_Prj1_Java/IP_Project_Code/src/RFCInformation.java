import java.io.Serializable;

public class RFCInformation implements Serializable {
	private String p_hostname;
	private int p_portnum;
    private String p_RFCTitle;
    private int p_rfcnum;
    
    public RFCInformation(String hostname, String Title, int rfcnum)
    {
    	this.p_hostname = hostname;
    	this.p_RFCTitle= Title;
    	this.p_rfcnum =rfcnum;
    }
   public void sethostname(String hostname)
   {
	   this.p_hostname = hostname;
   }
   public String gethostname()
   {
	   return p_hostname;
   }
   public void setportnum(int portnum)
   {
	   this.p_portnum=portnum;
   }
   public int getportnum()
   {
	   return p_portnum;
   }
   public String gettitle()
   {
	   return p_RFCTitle;
   }
   public void settitle(String title)
   {
	  p_RFCTitle = title;
   }
   public int getrfcnum() {
	   return p_rfcnum;
   }
   public void setrfcnum(int rfcnum) {
	   this.p_rfcnum=rfcnum;
   }


	public void run()
	{
		
	}
	
}
