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
                .from("zhangning_holley@126.com")
                .to("ebay@imnavy.com")//wanghua96_1@aliyun.com
                .subject("Hello! JunHaiHe.")
                .textMessage("发件人张宁.... 这是一封测试邮件.....!");

        SmtpServer smtpServer = MailServer.create()
                .host("smtp.126.com")
                .port(25)
                .auth("zhangning_holley@126.com","520liuqiumei")
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
