import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class PeerInformation implements Serializable{
	private String p_hostname;
	private int p_cookie;
	private boolean p_activestatus;
	private int p_TTL = 7200;
	private int p_portnum;
	private int p_activenum;
	private Date p_regdate;
	private static final ReentrantLock lock = new ReentrantLock();

   public void setTTL(int ttl)
   {
	   this.p_TTL=ttl;
   }
   public int getTTL()
   {
	   return p_TTL;
   }
   public void sethostname(String hostname)
   {
	   this.p_hostname = hostname;
   }
   public String gethostname()
   {
	   return p_hostname;
	   
   }
   public void setcookie(int cookie)
   {
	   this.p_cookie = cookie; 
   }
   public int getcookie()
   {
	   return p_cookie;
   }
   public void setportnum(int portnum)
   {
	   this.p_portnum=portnum;
   }
   public int getportnum()
   {
	   return p_portnum;
   }
   public void setactivestatus(boolean activestatus)
   {
	  this.p_activestatus=activestatus; 
   }
   public boolean getactievstatus()
   {
	   return p_activestatus;
   }
   public void setactivenum(int activenum)
   {
	   this.p_activenum=activenum;
   }
   public int getactivenum(){
	   return p_activenum;
   }
   public void setregdate(Date regdate)
   {
	   this.p_regdate=regdate;
   }

	public void run()
	{
		
	}
	
}