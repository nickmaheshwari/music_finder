package edu.uchicago.nmaheshwari.vaadin.views.about;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
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
import edu.uchicago.nmaheshwari.vaadin.cache.Cache;
import edu.uchicago.nmaheshwari.vaadin.views.main.MainView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route(value = "about", layout = MainView.class)
@PageTitle("About This App")
@CssImport("./views/about/about-view.css")
public class AboutView extends Div {

    private TextField subject = new TextField("Subject");
    private TextArea body = new TextArea("Message");
    private Button send = new Button("Send");
    private String emailBaseUrl = "https://soy64ubws6.execute-api.us-east-1.amazonaws.com/Prod/mail";

    private Logger log = LoggerFactory.getLogger(getClass());
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public AboutView() {
        addClassName("about");
        // body.addClassName("message");


        Span description = new Span("This app allows you to search for any artist or song! Simply go to the " +
                "'Music List' tab and enter the name of the song or artist you want to hear and hit 'Enter' to get listening!");
        description.addClassName("text");

        add(description, createTitle(), createFormLayout(), createButtonLayout());


        send.addClickListener(e -> {
            if (subject.isEmpty() || body.isEmpty()) {
                openWarning("Fields cannot be blank");
            } else {
                try {
                    sendEmail(subject.getValue(), body.getValue());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                clearFields();

            }
        });

    }

    private void clearFields(){
        subject.setValue("");
        body.setValue("");
    }

    public void sendEmail(String subject, String message) throws IOException {
        Notification success = new Notification(new Html(String.format(
                "<div class='email-sent-success'><h3>Successfully Sent Message</h3><h4>%s</h4><p>%s</p><div/>", subject,
                message)));

        success.setDuration(3000);
        success.setPosition(Notification.Position.BOTTOM_CENTER);

        success.open();

        String emailFrom = Cache.getInstance().getEmail();
        String bodyPkg = String.format("{\r\n    \"emailFrom\": \"%s\",\r\n    \"subject\": \"%s\",\r\n    \"body\": \"%s\"\r\n}", emailFrom, subject, message);
       Mono<String> k = WebClient.create().post()
                .uri(emailBaseUrl)
                .body(Mono.just(bodyPkg), String.class)
                .retrieve()
                .bodyToMono(String.class);

        k.doOnError(throwable -> {
            log.error("Error received: "+ ExceptionUtils.getStackTrace(throwable));
        }).publishOn(Schedulers.fromExecutor(executorService))
                .subscribe();



      /* OkHttpClient clientPost = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        String emailFrom = Cache.getInstance().getEmail();
        String bodyPkg = String.format("{\r\n    \"emailFrom\": \"%s\",\r\n    \"subject\": \"%s\",\r\n    \"body\": \"%s\"\r\n}", emailFrom, subject, message);
        RequestBody body = RequestBody.create(mediaType, bodyPkg);
        Request requestPost = new Request.Builder()
                .url(emailBaseUrl)
                .method("POST", body)
                .addHeader("X-Amz-Content-Sha256", "d01112b6a6e76ccca0ce2bce4e9f873fe7c12e74c443555d27b3e46da84cee24")
                .addHeader("X-Amz-Date", "20210824T180727Z")
                .addHeader("Authorization", "AWS4-HMAC-SHA256 Credential=AKIAU5XWYUZ5X3IWJJKR/20210531/us-east-2/execute-api/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date, Signature=06e65b3deaf74ee655c7cbd1cd8d7e29982a8c91e4237f97e6dee320638b1c09")
                .addHeader("Content-Type", "application/json")
                .build();
        Response responsePost = clientPost.newCall(requestPost).execute();

        if (responsePost.isSuccessful()) {
            OkHttpClient clientGet = new OkHttpClient();
            Request requestGet = new Request.Builder()
                    .url(emailBaseUrl)
                    .method("GET", null)
                    .addHeader("X-Amz-Date", "20210824T180727Z")
                    //.addHeader("Authorization", "AWS4-HMAC-SHA256 Credential=AKIAU5XWYUZ5X3IWJJKR/20210531/us-east-2/execute-api/aws4_request, SignedHeaders=host;x-amz-date, Signature=8405c0b660e3a75267c8ee4706064fea330b4334f7f3cd704a7c3c0c810948ac")
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