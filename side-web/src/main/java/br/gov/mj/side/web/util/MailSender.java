package br.gov.mj.side.web.util;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.faces.bean.ApplicationScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class MailSender {

    private static Log log = LogFactory.getLog(MailSender.class);

    private static String FROM = "noreply@mj.gov.br";

    @Resource(mappedName = "java:jboss/mail/mj-mail")
    private Session session;

    @Asynchronous
    public void send(String para, String assunto, String mensagem, boolean isHtml) {

        MimeMessage msg = new MimeMessage(session);

        try {
            InternetAddress from = new InternetAddress(FROM);

            msg.setSentDate(new Date());
            msg.setSubject(assunto);
            msg.setFrom(from);
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(para));

            if (isHtml) {
                msg.setContent(mensagem, "text/html; charset=UTF-8");
            } else {
                msg.setText(mensagem);
            }

            Transport.send(msg, msg.getAllRecipients());
            log.info("E-mail enviado com sucesso para " + (para));
        } catch (AddressException e) {
            log.warn("Erro ao enviar e-mail para " + (para) + ": AddressException:" + e.getMessage());
        } catch (MessagingException e) {
            log.warn("Erro ao enviar e-mail para " + (para) + ": MessagingException:" + e.getMessage());
        }
    }

    @Asynchronous
    public void send(List<String> para, String assunto, String mensagem, boolean isHtml) {

        MimeMessage msg = new MimeMessage(session);

        try {

            InternetAddress from = new InternetAddress(FROM);

            msg.setSentDate(new Date());
            msg.setSubject(assunto);
            msg.setFrom(from);

            for (String endereco : para) {
                msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(endereco));
            }

            if (isHtml) {
                msg.setContent(mensagem, "text/html; charset=UTF-8");
            } else {
                msg.setText(mensagem);
            }
            Transport.send(msg, msg.getAllRecipients());

            log.info("E-mail enviado com sucesso para " + msg.getAllRecipients().toString());

        } catch (AddressException e) {
            log.warn("Erro ao enviar e-mail: AddressException:" + e.getMessage());
        } catch (MessagingException e) {
            log.warn("Erro ao enviar e-mail: MessagingException:" + e.getMessage());
        }
    }
}
