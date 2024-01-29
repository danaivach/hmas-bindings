package ch.unisg.ics.interactions.hmas.bindings.protocols.http;

import ch.unisg.ics.interactions.hmas.bindings.protocols.ProtocolBinding;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;

import java.util.HashSet;
import java.util.Set;

public final class HttpBinding implements ProtocolBinding {

  private final static String HTTP_PROTOCOL = "HTTP";

  private final static Set<String> SUPPORTED_SCHEMES = new HashSet<>();

  static {
    SUPPORTED_SCHEMES.add("http");
    SUPPORTED_SCHEMES.add("https");
  }

  public HttpBinding() {
  }

  @Override
  public String getProtocolName() {
    return HTTP_PROTOCOL;
  }

  @Override
  public Set<String> getSupportedSchemes() {
    return SUPPORTED_SCHEMES;
  }

  @Override
  public HttpAction bind(Form form) {
    return bind(form, null);
  }

  @Override
  public HttpAction bind(Form form, String operationType) {
    return new HttpAction(form, operationType);
  }
}
