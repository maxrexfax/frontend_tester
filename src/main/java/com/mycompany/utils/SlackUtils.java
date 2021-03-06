/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.utils;

  
	
	
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
/**
 *
 * @author user
 */
public class SlackUtils {
    private static String slackWebhookUrl = "https://hooks.slack.com/services/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";
	
    public static void sendMessage(SlackMessage message) {
	
        CloseableHttpClient client = HttpClients.createDefault();
	
        HttpPost httpPost = new HttpPost(slackWebhookUrl);	
  
	
        try {

            ObjectMapper objectMapper = new ObjectMapper();

            String json = objectMapper.writeValueAsString(message);

            StringEntity entity = new StringEntity(json);

            httpPost.setEntity(entity);

            httpPost.setHeader("Accept", "application/json");

            httpPost.setHeader("Content-type", "application/json");



            client.execute(httpPost);

            client.close();

        } catch (IOException e) {

           e.printStackTrace();

        }

    }
}
