package jar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class AgentModel {

	private int userId;
	protected String address;
	protected HashMap<String, String> listOfPseudo;
	protected HashMap<String, String> typeOfPseudo;
	private String pseudo;
	final Scanner scanner = new Scanner(System.in);
	private Socket clientSocket;
	private boolean indoor ;

	public AgentModel(int userId, String address) {
		this.address = address;
		listOfPseudo = new HashMap<String, String>();
		typeOfPseudo = new HashMap<String, String>();
		pseudo = "";
	}

	public HashMap<String, String> getAllPseudos() {
		return this.listOfPseudo;
	}

	public boolean getMyType(){
		return this.indoor ;
	}
	public void setMytype(boolean indoor){
		this.indoor = indoor ;
	}

	public HashMap<String, String> getAllTypePseudos() {
		return this.typeOfPseudo;
	}

	// Retourne l'ID de l'Utilisateur
	public int getUserId() {
		return this.userId;
	}

	// Retourne le pseudo de l'Utilisateur
	public String getPseudo() {
		return this.pseudo;
	}

	// Modifie le pseudo de l'Utilisateur
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	// Retourne l'adresse IP de l'Utilisateur
	public String getIpAddr() {
		return this.address;
	}

	//get the broadcast p address
	public InetAddress getBroadcast() {
		Enumeration<NetworkInterface> interfaces;
		InetAddress broadcast = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback())
					continue;
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					broadcast = interfaceAddress.getBroadcast();
					if (broadcast != null) {
						return broadcast;
					}
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return broadcast;
	}

	// return true if all other agent agreed the connection
	public boolean sendBroadCastWithName(UDPClient clientUdp) {
		ExecutorService es = Executors.newCachedThreadPool();
		InetAddress broadCastIp = getBroadcast();

		es.execute(new ClientRunnable(broadCastIp, 25555, 25554, clientUdp));

		try {
			es.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return clientUdp.isConnected;

	}

	public boolean sendBroadCast(String pseudoChoisi, boolean indoor) {

		boolean connected = false;
		String type = "" ; 

		this.setPseudo(pseudoChoisi);
		//System.out.println("Pseudo choisit :" + this.getPseudo() + " , envoie aux autres users en cours ...");

		if (indoor){
			UDPClient clientUdp = new UDPClient(this.getPseudo(), this.getIpAddr(), this.getAllPseudos());
			connected = this.sendBroadCastWithName(clientUdp);
			if (connected) {
				this.listOfPseudo = clientUdp.getList();
				type = "indoor" ;
			}

		} else {
			connected = true ;
			type = "outdoor" ;
		}

		if (connected) {
			if (this.listOfPseudo.containsValue(this.getIpAddr())) {
				// This ip is already in the list
				System.out.println("Removing from list");
				listOfPseudo.values().remove(this.getIpAddr());
			}
			this.listOfPseudo.put(this.getPseudo(), this.getIpAddr());

			connected = sendToServlet(this.getPseudo(), type, indoor);
		} 

		return connected;

	}

	public boolean sendToServlet(String pseudoChoisi, String type, boolean indoor) {
		boolean connected = false;
		//String line;
		try {
			URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/subscribe?name=" + pseudoChoisi
					+ "&type=" + type + "&ip=" + this.getIpAddr());
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			//line = in.lines().collect(Collectors.joining());
			//System.out.println(line);
			in.close();
			connected = true;

		} catch (Exception e) {
			System.out.println("webserver innaccessible ou pseudo incorrect");
			return false ;
		}

		if (connected){
			changeStatusServlet("online", pseudoChoisi, type, indoor);
		}

		return connected;
	}

	public void changeStatusServlet(String status, String pseudoChoisi, String myType, boolean isIndoor) {

		try {
			System.out.println(status);
			URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/publish?name=" + pseudoChoisi
					+ "&state=" + status + "&ip=" + this.getIpAddr() + "&type=" + myType);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(url.openStream());

			if (status == "offline"){
				this.listOfPseudo.clear();
			} else {
				NodeList node = doc.getElementsByTagName("user");

				for (int i = 0; i < node.getLength(); i++) {
					Element ele = (Element) node.item(i);

					String name = (ele.getElementsByTagName("name").item(0).getTextContent());
					String type = (ele.getElementsByTagName("type").item(0).getTextContent());
					String ip = (ele.getElementsByTagName("ip").item(0).getTextContent());
					String stateDist = (ele.getElementsByTagName("state").item(0).getTextContent());

					this.typeOfPseudo.put(name, type);

					if (stateDist.trim().equals("online")  ) {
						if (isIndoor && type.trim().equals("outdoor")){
							this.listOfPseudo.put(name, ip);
						} else {
							this.listOfPseudo.put(name, ip);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Webserver not joinable");
		}
	}

	public boolean servletNotify(boolean isIndoor) {
		try {
			URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/notify");

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(url.openStream());

			NodeList node = doc.getElementsByTagName("user");

			for (int i = 0; i < node.getLength(); i++) {
				Element ele = (Element) node.item(i);

				String name = (ele.getElementsByTagName("name").item(0).getTextContent());
				String type = (ele.getElementsByTagName("type").item(0).getTextContent());
				String ip = (ele.getElementsByTagName("ip").item(0).getTextContent());
				String stateDist = (ele.getElementsByTagName("state").item(0).getTextContent());

				this.typeOfPseudo.put(name, type);

				if (stateDist.trim().equals("online")  ) {
					if (isIndoor && type.trim().equals("outdoor")){
						this.listOfPseudo.put(name, ip);
					} else {
						this.listOfPseudo.put(name, ip);
					}
				}
			}
			return true ;
		} catch (Exception e) {
			System.out.println("Webserver not joinable");
			return false ;
		}

	}

	public String getType(String pseudo) {

		return this.typeOfPseudo.get(pseudo);
	}

	public void openConnectionServlet(String pseudo) {
		try {
			URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/openConnection");

			URLConnection con = url.openConnection();

			HttpURLConnection http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);

			Map<String,String> param = new HashMap<>();
			param.put("user1", this.getPseudo());
			param.put("user2", pseudo);
			param.put("ip", this.getIpAddr());

			StringJoiner separateur = new StringJoiner("&");
			
			for(Map.Entry<String,String> entry : param.entrySet()){
				separateur.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			byte[] out = separateur.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			http.setFixedLengthStreamingMode(length);

			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();
			try(OutputStream os = http.getOutputStream()) {
				os.write(out);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> checkForConnection(){
		try {
			URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/openConnection?myName=" + this.getPseudo());

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(url.openStream());

			NodeList node = doc.getElementsByTagName("user");
			String name = "" ;
			String ip = "";
			for (int i = 0; i < node.getLength(); i++) {
				Element ele = (Element) node.item(i);
				
				 name = (ele.getElementsByTagName("name").item(0).getTextContent());
				 ip = (ele.getElementsByTagName("ip").item(0).getTextContent());

			}

			if (name != "" && ip != ""){
			ArrayList<String> nameAndIp = new ArrayList<String>() ;
			nameAndIp.add(name);
			nameAndIp.add(ip);
			
			return nameAndIp ;
			}
			else {
				return null ;
			}

		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null ;
	}

	public void sendMsgToServlet(String user1, String user2, String msgToSend) {
		try {
			URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/message");
	
			URLConnection con = url.openConnection();

			HttpURLConnection http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setDoOutput(true);

			Map<String,String> param = new HashMap<>();
			param.put("user1", user1);
			param.put("user2", user2);
			param.put("msg", msgToSend);

			StringJoiner separateur = new StringJoiner("&");
			
			for(Map.Entry<String,String> entry : param.entrySet()){
				separateur.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			byte[] out = separateur.toString().getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			http.setFixedLengthStreamingMode(length);

			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			http.connect();
			try(OutputStream os = http.getOutputStream()) {
				os.write(out);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendFileToServlet(String user1, String user2, File fileToSend) {
		try {
			String fileName = fileToSend.getName();
		

			byte[] content = Files.readAllBytes(fileToSend.toPath());



			FileMessageForServlet newMessage = new FileMessageForServlet(fileName, user1, user2, content) ;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = null;
			byte[] objetBytes = null ;
			try {
				out = new ObjectOutputStream(bos);   
				out.writeObject(newMessage);
				out.flush();
				objetBytes = bos.toByteArray();

				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/fileMessage"))
					.POST(HttpRequest.BodyPublishers.ofByteArray(objetBytes))
					.build();
				client.send(request, HttpResponse.BodyHandlers.ofString());





			} finally {
				try {
					bos.close();
				} catch (IOException ex) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getFileFromServlet(String user1, String user2){
		try {

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/fileMessage?user1=" + user1 + "&user2=" + user2))
				.build();
			// Receives the response body as an InputStream
			HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());

			if (response.statusCode() == 403){
				return "";
			} else {


				ObjectInputStream inObj = new ObjectInputStream(response.body());
				FileMessageForServlet myFile = (FileMessageForServlet)inObj.readObject();

				String fileName = myFile.getFileName() ;
				
				byte[] content = myFile.getFileContent() ;

				FileOutputStream fOutput = new FileOutputStream(fileName);
				BufferedOutputStream bOutput = new BufferedOutputStream(fOutput);
				bOutput.write(content);
				bOutput.flush();
				System.out.println("File " + fileName);
				bOutput.close();



				return  "Fichier envoyé" ;
			}
			

		}
		catch (Exception e){
			return "" ;

		}


	}


	public String getMsgFromServlet(String user1, String user2){
		String msg = "";
		try {
			URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/chatServletA2-2/message?user1=" + user1 + "&user2=" + user2);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(url.openStream());

			NodeList node = doc.getElementsByTagName("user");
			for (int i = 0; i < node.getLength(); i++) {
				Element ele = (Element) node.item(i);
				
				 msg = (ele.getElementsByTagName("msg").item(0).getTextContent());

			}
			

		}
		catch (Exception e){
			e.printStackTrace();
		}

		return msg ;

	}




    
    
    public void connectToUser(String pseudoToContact, ChatWindow chat) {
		//System.out.println("Entrez un pseudo pour discuter avec lui");
		//String pseudoToContact = this.askForInput();
		
		if (this.listOfPseudo.containsKey(pseudoToContact)) {
			String addressToContact = this.listOfPseudo.get(pseudoToContact);
			System.out.println(addressToContact);
			
			try {
				clientSocket = new Socket(addressToContact,25556);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			TCPClient clientTcp = new TCPClient(this.getIpAddr(), addressToContact, clientSocket, this.getPseudo(), pseudoToContact, chat);
			Thread clTcp = new Thread(clientTcp);
			clTcp.start();
		}
	}
	






    
    
    
    
    
    
	
	

	
	
	

}
