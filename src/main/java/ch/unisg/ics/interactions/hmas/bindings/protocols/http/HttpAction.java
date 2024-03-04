package ch.unisg.ics.interactions.hmas.bindings.protocols.http;

import ch.unisg.ics.interactions.hmas.bindings.AbstractAction;
import ch.unisg.ics.interactions.hmas.bindings.ActionExecution;
import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpAction extends AbstractAction {

  private final static Logger LOGGER = Logger.getLogger(HttpAction.class.getCanonicalName());

  private final BasicClassicHttpRequest request;

  public HttpAction(Form form, String operationType) {
    super(form, operationType);
    this.request = createHttpRequest();
  }

  public HttpAction(Form form) {
    super(form);
    this.request = createHttpRequest();
  }

  private BasicClassicHttpRequest createHttpRequest() {
    BasicClassicHttpRequest httpRequest = new BasicClassicHttpRequest(this.getMethodName(), this.form.getTarget());
    httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, this.form.getContentType());
    return httpRequest;
  }

  @Override
  public HttpAction setActorId(String actorId) {
    super.setActorId(actorId);
    this.request.setHeader("X-Agent-WebID", this.actorId.get());

    return this;
  }

  @Override
  public ActionExecution execute(Input input) throws IOException {
    this.request.setEntity(new StringEntity(String.valueOf(input.getData()),
            ContentType.create(form.getContentType())));
    return this.execute();
  }

  @Override
  public ActionExecution execute() throws IOException {
    HttpClient client = HttpClients.createDefault();
    HttpUriRequest uriRequest = (HttpUriRequest) request;
    BasicClassicHttpResponse response = (BasicClassicHttpResponse) client.execute(uriRequest);

    return getHttpActionExection(response);
  }

  private HttpActionExecution getHttpActionExection(BasicClassicHttpResponse response) {
    Optional<String> inputData = Optional.empty();
    Optional<String> outputData = Optional.empty();

    if (this.request.getEntity() != null) {
      try {
        inputData = Optional.of(EntityUtils.toString(this.request.getEntity(), StandardCharsets.UTF_8));
      } catch (IOException | ParseException e) {
        e.printStackTrace();
      }
    }

    if (response.getEntity() != null) {
      try {
        outputData = Optional.of(EntityUtils.toString(this.request.getEntity(), StandardCharsets.UTF_8));
      } catch (IOException | ParseException e) {
        e.printStackTrace();
      }
    }

    return new HttpActionExecution(this, response, inputData, outputData);

  }


  public HttpAction setHeader(String key, String value) {
    if ("X-Agent-WebID".equals(key)) {
      return this.setActorId(value);
    } else {
      this.request.setHeader(key, value);
    }
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[Http Request] Method: ").append(request.getMethod());
    try {
      builder.append(", Target: ").append(request.getUri().toString());
      for (Header header : request.getHeaders()) {
        builder.append(", ").append(header.getName()).append(": ").append(header.getValue());
      }
      if (request.getEntity() != null) {
        StringWriter writer = new StringWriter();
        IOUtils.copy(request.getEntity().getContent(), writer, StandardCharsets.UTF_8.name());
        builder.append(", Payload: ").append(writer);
      }
    } catch (UnsupportedOperationException | IOException | URISyntaxException e) {
      LOGGER.log(Level.WARNING, e.getMessage());
    }
    return builder.toString();
  }

  private String getMethodName() {
    // Try to get method name from form.getTarget()
    String methodName = form.getMethodName(form.getTarget()).orElse(null);

    // If method name is not present, try to get it from form.getMethodName(contentType)
    if (methodName == null && operationType.isPresent()) {
      methodName = form.getMethodName(operationType.get()).orElse(null);
    }

    // If method name is still not present, raise an exception
    if (methodName == null) {
      throw new IllegalArgumentException("MethodName cannot be determined");
    }

    return methodName;
  }

  public BasicClassicHttpRequest getRequest() {
    return this.request;
  }
}


