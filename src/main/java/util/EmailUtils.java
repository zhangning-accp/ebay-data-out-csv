package util;

import jodd.mail.Email;
import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;

/**
 * Created by zn on 2018/8/4.
 */
public class EmailUtils {
    public static void sendEmail(String content){
        Email email = Email.create()
                .from("909604945@qq.com")
                .to("909604945@qq.com")
                .subject("Hello! HuaWang guys!")
                .textMessage("A plain text message...");

        SmtpServer smtpServer = MailServer.create()
                .host("http://mail.com")
                .port(21)
                .buildSmtpMailServer();
        SendMailSession session = smtpServer.createSession();
        session.open();
        session.sendMail(email);

        session.close();

    }

    public static void main(String ... args) {
        sendEmail("");
    }
}
