/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Random;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pasan
 */
public class VerifyEmail extends HttpServlet {

    Gson gson;

    public VerifyEmail() {

        gson = new Gson();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

          String mail=request.getParameter("email");
          String pname =request.getParameter("pname");
        try {

          

            Properties p = System.getProperties();

            p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            p.put("mail.smtp.port", "587");
            p.put("mail.smtp.host", "smtp.gmail.com");
            p.put("mail.smtp.auth", "true");
            p.put("mail.smtp.starttls.enable", "true");

            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {

                    PasswordAuthentication p = new PasswordAuthentication("kaveeshanadun7@gmail.com", "brick34op");
                    return p;
                }
            };

            javax.mail.Session s = javax.mail.Session.getInstance(p, authenticator);

            MimeMessage email = new MimeMessage(s);

            String subject = "Selling Product Approve";

        
            String content = pname+" :Item is Approved by Ruwan Super Center Admin";;

            email.setFrom(new InternetAddress("kaveeshanadun7@gmail.com"));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
            email.setSubject(subject);
            email.setContent(content, "text/html");

            Transport.send(email);

              return;

        } catch (Exception e) {
            e.printStackTrace();
        }

       

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

  

}
