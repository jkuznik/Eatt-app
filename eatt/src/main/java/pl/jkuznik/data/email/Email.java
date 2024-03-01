package pl.jkuznik.data.email;

import pl.jkuznik.data.myOrder.MyOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.*;
import javax.mail.internet.*;
public class Email {

//    private final LocalDate localDate;
    private final List<MyOrder> orderList;
        // Dane konta email
    private final String login = "eatt.app@tlen.pl";
    private final String password = "pokyta@r510l";

    private final String host = "smtp.poczta.tlen.pl";
    private final int port = 465;

    public Email(/*LocalDate localDate,*/ List<MyOrder> orderList) {

//        this.localDate = localDate;
        this.orderList = orderList;

            }
    public void sendMail() {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login, password);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(login));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("eatt.app@tlen.pl")
            );
            message.setSubject("Zamówienie z dnia " /*+ localDate*/);
            List<String> parsedOrders = orderList.stream()
                    .map(o -> {
                        String order2 = o.getUserName() + " " + o.getMealName() + " " + o.getNotes();
                        return order2;
                    }).collect(Collectors.toList());

            StringBuilder messageTextBuilder = new StringBuilder();
//            messageTextBuilder.append(localDate.toString());
            for (String parsedOrder : parsedOrders) {
                messageTextBuilder.append(parsedOrder).append(" ");
            }

            String messageText = messageTextBuilder.toString();

            message.setText(messageText);

            // Wysłanie wiadomości
            Transport.send(message);

            System.out.println("Wiadomość została wysłana.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}