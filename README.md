<h1>📧 Email Sending Application (Java GUI + SMTP)</h1>

<p>
This project is a <strong>Java GUI-based Email Sending Application</strong> that enables users to send emails
through an SMTP server (like Gmail). The application uses <strong>Swing</strong> for the graphical interface
and <strong>JavaMail API</strong> for handling email delivery securely and efficiently.
</p>

<h2>🧩 Features</h2>
<ul>
  <li>Simple and responsive GUI using Swing</li>
  <li>Fast email sending through SMTP</li>
  <li>Supports Gmail and other SMTP providers</li>
  <li>Modular structure divided into 4 logical parts</li>
  <li>Lightweight and easy to execute</li>
</ul>

<h2>🧱 Project Structure</h2>
<p>The project is divided into four parts to support clarity:</p>
<table>
  <tr><th>Part</th><th>Section</th><th>Description</th></tr>
  <tr><td>1️⃣</td><td>Email UI</td><td>Builds the user interface using Java Swing</td></tr>
  <tr><td>2️⃣</td><td>Email Data</td><td>Stores sender, recipient, subject, and message</td></tr>
  <tr><td>3️⃣</td><td>Email Sender</td><td>Handles SMTP configuration and sending logic</td></tr>
  <tr><td>4️⃣</td><td>Main Controller</td><td>Integrates GUI and backend logic</td></tr>
</table>

<h2>⚙ Requirements</h2>
<ul>
  <li>Java JDK 8 or higher</li>
  <li>JavaMail API (javax.mail.jar and activation.jar)</li>
</ul>
<p>📥 Download JavaMail from:
<a href="https://javaee.github.io/javamail/" target="_blank">https://javaee.github.io/javamail/</a></p>

<h2>🚀 How to Run</h2>
<ol>
  <li>Download and add <code>javax.mail.jar</code> and <code>activation.jar</code> to your project folder.</li>
  <li>Compile the file:</li>
  <pre><code>javac -cp .;javax.mail.jar;activation.jar EmailApp.java</code></pre>
  <li>Run the application:</li>
  <pre><code>java -cp .;javax.mail.jar;activation.jar EmailApp</code></pre>
  <li>Enter your Gmail address, recipient address, subject, and message.</li>
  <li>When prompted, enter your Gmail <strong>App Password</strong> (not your regular password).</li>
</ol>

<h2>📬 Sending an Email</h2>
<ul>
  <li>Launch the application and fill in all fields.</li>
  <li>Click <strong>Send Email</strong>.</li>
  <li>If successful, you’ll see “Email Sent Successfully!”.</li>
  <li>If failed, you’ll see “Failed to Send Email.”</li>
</ul>

<h2>🧠 Notes</h2>
<ul>
  <li>Internet connection must be active.</li>
  <li>For Gmail, enable 2-Step Verification and generate an App Password.</li>
  <li>SMTP host/port can be changed to support Outlook, Yahoo, etc.</li>
</ul>

<h2>🏁 Summary</h2>
<p>
This project demonstrates a <strong>modular, object-oriented Java application</strong> that combines
<strong>GUI development</strong> and <strong>email networking</strong> concepts.  
It is lightweight, efficient.
</p>


