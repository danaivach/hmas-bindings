package ch.unisg.ics.interactions.hmas.bindings.protocols.http;

import ch.unisg.ics.interactions.hmas.bindings.ActionExecution;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;

import java.util.Optional;

public class HttpActionExecution extends ActionExecution {

  private final ClassicHttpRequest request;
  private final ClassicHttpResponse response;

  public HttpActionExecution(HttpAction httpAction, ClassicHttpResponse response, Optional<String> inputData, Optional<String> outputData) {
    super(httpAction, inputData, outputData);
    this.request = httpAction.getRequest();
    this.response = response;
  }

  public ClassicHttpRequest getRequest() {
    return request;
  }

  public ClassicHttpResponse getResponse() {
    return response;
  }
}
