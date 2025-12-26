import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/*
  Full corrected EmailApp.java
  - Form moved to NORTH so fields don't get overlapped
  - Message area put in CENTER
  - Toolbar and status in SOUTH
  - Ensured text fields are editable and focusable
*/

class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private boolean succeeded;
    private String email;
    private char[] password;
    public LoginDialog(Frame parent) {
        super(parent, "Sign in", true);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        JPanel labels = new JPanel(new GridLayout(0, 1, 6, 6));
        labels.add(new JLabel("Email"));
        labels.add(new JLabel("Password"));
        JPanel fields = new JPanel(new GridLayout(0, 1, 6, 6));
        emailField = new JTextField(28);
        passwordField = new JPasswordField(28);
        fields.add(emailField);
        fields.add(passwordField);
        panel.add(labels, BorderLayout.WEST);
        panel.add(fields, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton signIn = new JButton("Sign in");
        JButton cancel = new JButton("Cancel");
        buttons.add(cancel);
        buttons.add(signIn);
        panel.add(buttons, BorderLayout.SOUTH);
        getContentPane().add(panel);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
        signIn.addActionListener(e -> {
            email = emailField.getText().trim();
            password = passwordField.getPassword();
            if (email.isEmpty() || password.length == 0) {
                JOptionPane.showMessageDialog(this, "Enter email and password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            succeeded = true;
            dispose();
        });
        cancel.addActionListener(e -> {
            succeeded = false;
            dispose();
        });
    }
    public boolean isSucceeded() { return succeeded; }
    public String getEmail() { return email; }
    public char[] getPassword() { return password; }
}

class EmailData {
    private final String sender;
    private final String[] to;
    private final String subject;
    private final String message;
    private final String cc;
    private final String bcc;
    public EmailData(String sender, String[] to, String subject, String message, String cc, String bcc) {
        this.sender = sender;
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.cc = cc;
        this.bcc = bcc;
    }
    public String getSender() { return sender; }
    public String[] getTo() { return to; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getCc() { return cc; }
    public String getBcc() { return bcc; }
}

class EmailSender {
    public static void sendEmail(EmailData data, String password) throws Exception {
        Properties props = new Properties();
        String domain = extractDomain(data.getSender());
        configureSMTP(props, domain);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(data.getSender(), password);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(data.getSender()));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(joinRecipients(data.getTo())));
        if (data.getCc() != null && !data.getCc().isBlank())
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(normalizeList(data.getCc())));
        if (data.getBcc() != null && !data.getBcc().isBlank())
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(normalizeList(data.getBcc())));
        String subject = data.getSubject() == null ? "" : data.getSubject();
        String body = data.getMessage() == null ? "" : data.getMessage();
        msg.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "UTF-8");
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(textPart);
        msg.setContent(mp);
        Transport.send(msg);
    }
    private static String extractDomain(String email) {
        if (email == null) return "";
        int at = email.indexOf('@');
        if (at < 0) return "";
        return email.substring(at + 1).toLowerCase();
    }
    private static void configureSMTP(Properties props, String domain) {
        if (domain.contains("gmail")) {
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
        } else if (domain.contains("outlook") || domain.contains("hotmail") || domain.contains("live")) {
            props.put("mail.smtp.host", "smtp-mail.outlook.com");
            props.put("mail.smtp.port", "587");
        } else if (domain.contains("yahoo")) {
            props.put("mail.smtp.host", "smtp.mail.yahoo.com");
            props.put("mail.smtp.port", "587");
        } else {
            props.put("mail.smtp.host", "smtp." + domain);
            props.put("mail.smtp.port", "587");
        }
    }
    private static String joinRecipients(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) continue;
            String s = arr[i].trim();
            if (s.isEmpty()) continue;
            if (sb.length() > 0) sb.append(",");
            sb.append(s);
        }
        return sb.toString();
    }
    private static String normalizeList(String raw) {
        if (raw == null) return "";
        String[] parts = raw.split("[,;\\s]+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String s = p.trim();
            if (s.isEmpty()) continue;
            if (sb.length() > 0) sb.append(",");
            sb.append(s);
        }
        return sb.toString();
    }
}

class EmailUI extends JFrame {
    JTextField senderField;
    JTextField toField;
    JTextField ccField;
    JTextField bccField;
    JTextField subjectField;
    JTextArea messageArea;
    JButton sendButton;
    JButton logoutButton;
    JLabel statusLabel;
    public EmailUI() {
        setTitle("Email Sender");
        setSize(820, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // TOP: form panel (NORTH)
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("Compose Email");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topContainer.add(title, BorderLayout.WEST);
        logoutButton = new JButton("Logout");
        topContainer.add(logoutButton, BorderLayout.EAST);

        JPanel form = new JPanel();
        form.setBorder(new EmptyBorder(10, 0, 10, 0));
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;

        form.add(new JLabel("From:"), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        senderField = new JTextField();
        senderField.setEditable(false);
        senderField.setFocusable(true);
        senderField.setPreferredSize(new Dimension(400, 26));
        form.add(senderField, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0; c.fill = GridBagConstraints.NONE;
        form.add(new JLabel("To (comma/semicolon separated):"), c);
        c.gridx = 1;
        c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        toField = new JTextField();
        toField.setPreferredSize(new Dimension(400, 26));
        toField.setEditable(true);
        toField.setFocusable(true);
        form.add(toField, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0; c.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Cc:"), c);
        c.gridx = 1;
        c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        ccField = new JTextField();
        ccField.setPreferredSize(new Dimension(400, 26));
        ccField.setEditable(true);
        ccField.setFocusable(true);
        form.add(ccField, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0; c.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Bcc:"), c);
        c.gridx = 1;
        c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        bccField = new JTextField();
        bccField.setPreferredSize(new Dimension(400, 26));
        bccField.setEditable(true);
        bccField.setFocusable(true);
        form.add(bccField, c);

        c.gridx = 0; c.gridy++;
        c.weightx = 0; c.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Subject:"), c);
        c.gridx = 1;
        c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        subjectField = new JTextField();
        subjectField.setPreferredSize(new Dimension(400, 26));
        subjectField.setEditable(true);
        subjectField.setFocusable(true);
        form.add(subjectField, c);

        topContainer.add(form, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // CENTER: message area
        messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(messageArea);
        add(scroll, BorderLayout.CENTER);

        // SOUTH: toolbar and status
        JToolBar tool = new JToolBar();
        tool.setFloatable(false);
        JButton cut = new JButton(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        JButton copy = new JButton(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        JButton paste = new JButton(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        tool.add(cut);
        tool.add(copy);
        tool.add(paste);
        sendButton = new JButton("Send");
        tool.addSeparator();
        tool.add(sendButton);
        statusLabel = new JLabel(" ");
        tool.add(Box.createHorizontalGlue());
        tool.add(statusLabel);

        JPanel southWrap = new JPanel(new BorderLayout());
        southWrap.setBorder(new EmptyBorder(6, 8, 8, 8));
        southWrap.add(tool, BorderLayout.CENTER);
        add(southWrap, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        // Do not call setVisible here if caller will; but safe to show:
        setVisible(true);
    }
    public void setStatus(String s) { statusLabel.setText(s); }
}

public class EmailApp {
    private static String sessionEmail;
    private static char[] sessionPassword;
    private static EmailUI ui;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ui = new EmailUI();
            performLogin();
            ui.logoutButton.addActionListener(e -> {
                int ans = JOptionPane.showConfirmDialog(ui, "Logout current user?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION) {
                    clearSession();
                    performLogin();
                }
            });
            ui.sendButton.addActionListener(e -> {
                if (sessionEmail == null || sessionPassword == null) {
                    JOptionPane.showMessageDialog(ui, "Please sign in first", "Not signed in", JOptionPane.WARNING_MESSAGE);
                    performLogin();
                    return;
                }
                String[] recipients = parseList(ui.toField.getText());
                if (recipients.length == 0) {
                    JOptionPane.showMessageDialog(ui, "Enter at least one recipient", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String cc = ui.ccField.getText().trim();
                String bcc = ui.bccField.getText().trim();
                String subject = ui.subjectField.getText().trim();
                String message = ui.messageArea.getText();
                EmailData data = new EmailData(sessionEmail, recipients, subject, message, cc, bcc);
                ui.setStatus("Sending...");
                ui.sendButton.setEnabled(false);
                new Thread(() -> {
                    try {
                        EmailSender.sendEmail(data, new String(sessionPassword));
                        SwingUtilities.invokeLater(() -> {
                            ui.setStatus("Sent ✓");
                            ui.sendButton.setEnabled(true);
                            JOptionPane.showMessageDialog(ui, "Email Sent Successfully!");
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            ui.setStatus("Failed");
                            ui.sendButton.setEnabled(true);
                            JOptionPane.showMessageDialog(ui, "Failed to send: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }).start();
            });
        });
    }
    private static void performLogin() {
        Frame f = ui;
        LoginDialog dlg = new LoginDialog(f);
        dlg.setVisible(true);
        if (!dlg.isSucceeded()) {
            System.exit(0);
        }
        sessionEmail = dlg.getEmail();
        sessionPassword = dlg.getPassword();
        ui.senderField.setText(sessionEmail);
        ui.setStatus("Signed in as " + sessionEmail);
    }
    private static void clearSession() {
        sessionEmail = null;
        sessionPassword = null;
        ui.senderField.setText("");
        ui.setStatus("Signed out");
    }
    private static String[] parseList(String raw) {
        if (raw == null) return new String[0];
        String[] parts = raw.split("[,;\\s]+");
        return java.util.Arrays.stream(parts).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }
}
