package jar;

import java.io.Serializable;

public class FileMessageForServlet implements Serializable {

    protected String filename ;
    protected String user1 ;
    protected String user2 ;
    protected byte[] content ;

    public FileMessageForServlet(String filename ,String user1 , String user2 , byte[] content){
        this.filename = filename;
        this.user1 = user1 ;
        this.user2 = user2 ;
        this.content = content ;
    }

    public String getFileName() {
		return this.filename ;
	}
	
	public byte[] getFileContent() {
		return this.content ;
	}
	
	public String getuser1() {
		return this.user1 ;
	}
	
	public String getuser2() {
		return this.user2 ;
	}
	

    
}