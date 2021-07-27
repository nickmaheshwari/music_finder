package edu.uchicago.nmaheshwari.vaadin.views.about;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import edu.uchicago.nmaheshwari.vaadin.views.main.MainView;
import org.apache.commons.lang3.StringUtils;

@Route(value = "about", layout = MainView.class)
@PageTitle("About Us")
public class AboutView extends Div {

    private TextField subject = new TextField("Subject");
    private TextArea body = new TextArea("Message");
    private Button send = new Button("Send");
    // private String emailBaseUrl = "https://ieb3ax73n6.execute-api.us-east-2.amazonaws.com/Prod/contacts";


    public AboutView() {
        addClassName("about");
        // body.addClassName("message");


        Span description = new Span("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        description.addClassName("text");

        add(description, createTitle(), createFormLayout(), createButtonLayout());


        send.addClickListener(e -> {
            if (subject.isEmpty() || body.isEmpty()) {
                openWarning("Fields cannot be blank");
            } else {
                sendEmail(subject.getValue(), body.getValue());
                clearFields();

            }
        });

    }

    private void clearFields(){
        subject.setValue("");
        body.setValue("");
    }

    public void sendEmail(String subject, String message)  {
        Notification success = new Notification(new Html(String.format(
                "<div class='email-sent-success'><h3>Successfully Sent Message</h3><h4>%s</h4><p>%s</p><div/>", subject,
                message)));

        success.setDuration(3000);
        success.setPosition(Notification.Position.BOTTOM_CENTER);

        success.open();

        /*OkHttpClient clientPost = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String bodyPkg = String.format("{\r\n    \"emailFrom\": \"%s\",\r\n    \"subject\": \"%s\",\r\n    \"body\": \"%s\"\r\n}", "emailFrom", subject, emailBody);
        RequestBody body = RequestBody.create(mediaType, bodyPkg);
        Request requestPost = new Request.Builder()
                .url(emailBaseUrl)
                .method("POST", body)
                .addHeader("X-Amz-Content-Sha256", "beaead3198f7da1e70d03ab969765e0821b24fc913697e929e726aeaebf0eba3")
                .addHeader("X-Amz-Date", "20210531T164632Z")
                .addHeader("Authorization", "AWS4-HMAC-SHA256 Credential=AKIAU5XWYUZ5X3IWJJKR/20210531/us-east-2/execute-api/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=06e65b3deaf74ee655c7cbd1cd8d7e29982a8c91e4237f97e6dee320638b1c09")
                .addHeader("Content-Type", "application/json")
                .build();
        Response responsePost = clientPost.newCall(requestPost).execute();

        if (responsePost.isSuccessful()) {
            OkHttpClient clientGet = new OkHttpClient();
            Request requestGet = new Request.Builder()
                    .url(emailBaseUrl)
                    .method("GET", null)
                    .addHeader("X-Amz-Date", "20210531T162231Z")
                    .addHeader("Authorization", "AWS4-HMAC-SHA256 Credential=AKIAU5XWYUZ5X3IWJJKR/20210531/us-east-2/execute-api/aws4_request, SignedHeaders=host;x-amz-date, Signature=8405c0b660e3a75267c8ee4706064fea330b4334f7f3cd704a7c3c0c810948ac")
                    .build();
            Response responseGet = clientGet.newCall(requestGet).execute();
            if (responseGet.isSuccessful()) {
                openWarning("Email sent!");
            } else {
                openWarning("Failed to send email!");
            }

        }*/
    }

    public void openWarning(String errorMsg) {
        Notification notification = new Notification(
                errorMsg, 3000, Notification.Position.BOTTOM_CENTER);
        notification.open();
    }



    private Component createTitle() {
        return new H3("Contact Us");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep(StringUtils.EMPTY, 1));
        formLayout.add(subject, body);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(send);
        return buttonLayout;
    }

}