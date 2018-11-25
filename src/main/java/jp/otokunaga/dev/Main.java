package jp.otokunaga.dev;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watson.developer_cloud.assistant.v2.Assistant;
import com.ibm.watson.developer_cloud.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageContext;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageInput;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v2.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v2.model.SessionResponse;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

public class Main {
	public static ObjectMapper objectMapper = new ObjectMapper();
	private static Properties props;
	private static final String testMessage = "こんにちは";
	public static void main(String[] args) {
		buildProp();
		createSession(testMessage);
	}
	/**
	 * build property file
	 * reference https://stackoverflow.com/questions/20348657/cannot-load-properties-file-from-resources-directory
	 */
	public static void buildProp(){
		String resourceName = "watson.properties"; // could also be a constant
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		props = new Properties();
		try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
		    props.load(resourceStream);
		}catch(IOException e){
			System.out.println("file not found. File name is "+ resourceName);
		}


	}
	public static void createSession(String testMessage){

		MessageContext context = new MessageContext();

		String API_KEY = props.getProperty("API_KEY");
		String ASSISTANT_ID = props.getProperty("ASSISTANT_ID");
		IamOptions iamOptions = new IamOptions.Builder().apiKey(API_KEY).build();
		Assistant service = new Assistant("2018-11-08", iamOptions);
		service.setEndPoint("https://gateway-tok.watsonplatform.net/assistant/api"
				);
		CreateSessionOptions options = new CreateSessionOptions.Builder(ASSISTANT_ID).build();

		SessionResponse response = service.createSession(options).execute();

		String sessionId = response.getSessionId();


		MessageInput input = new MessageInput.Builder()
				  .text(testMessage)
				  .build();
				MessageOptions messageOptions = new MessageOptions.Builder()
				  .assistantId(ASSISTANT_ID)
				  .sessionId(sessionId)
				  .context(context)
				  .input(input)
				  .build();
				MessageResponse messageResponse = service.message(messageOptions).execute();

				System.out.println(messageResponse);
	}

}
