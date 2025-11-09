import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


// Part 1: Email UI

class EmailUI extends JFrame {
    JTextField senderField, recipientField, subjectField;
    JTextArea messageArea;
    JButton sendButton;

    public EmailUI() {
        setTitle("Email Sender");
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Sender:"));
        senderField = new JTextField();
        inputPanel.add(senderField);
        inputPanel.add(new JLabel("Recipient:"));
        recipientField = new JTextField();
        inputPanel.add(recipientField);
        inputPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        inputPanel.add(subjectField);

        messageArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        sendButton = new JButton("Send Email");

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(sendButton, BorderLayout.SOUTH);

        setVisible(true);
    }
}


// Part 2: Email Data Model

class EmailData {
    private String sender, recipient, subject, message;

    public EmailData(String sender, String recipient, String subject, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
}


// Part 3: SMTP Email Sender

class EmailSender {
    public static boolean sendEmail(EmailData data, String password) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(data.getSender(), password);
                    }
                });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(data.getSender()));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(data.getRecipient()));
            msg.setSubject(data.getSubject());
            msg.setText(data.getMessage());

            Transport.send(msg);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}


// Part 4: Main Controller

public class EmailApp {
    public static void main(String[] args) {
        EmailUI ui = new EmailUI();

        ui.sendButton.addActionListener(e -> {
            String sender = ui.senderField.getText();
            String recipient = ui.recipientField.getText();
            String subject = ui.subjectField.getText();
            String message = ui.messageArea.getText();

            String password = JOptionPane.showInputDialog(ui, "Enter Email Password:");

            EmailData data = new EmailData(sender, recipient, subject, message);
            boolean sent = EmailSender.sendEmail(data, password);

            JOptionPane.showMessageDialog(ui, sent ? "Email Sent Successfully!" : "Failed to Send Email.");
        });
    }
}
