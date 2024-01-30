package ch.unisg.ics.interactions.hmas.bindings.protocols.http;

import ch.unisg.ics.interactions.hmas.bindings.ActionExecution;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

import java.util.Optional;

public class HttpActionExecution extends ActionExecution {

  private final BasicClassicHttpRequest request;
  private final BasicClassicHttpResponse response;

  public HttpActionExecution(HttpAction httpAction, BasicClassicHttpResponse response, Optional<String> inputData, Optional<String> outputData) {
    super(httpAction, inputData, outputData);
    this.request = httpAction.getRequest();
    this.response = response;
  }

  public BasicClassicHttpRequest getRequest() {
    return request;
  }

  public BasicClassicHttpResponse getResponse() {
    return response;
  }
}
