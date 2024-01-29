package ch.unisg.ics.interactions.hmas.bindings.protocols.http;

import ch.unisg.ics.interactions.hmas.bindings.Action;
import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.bindings.payloads.ApplicationJsonXArmBinding;
import ch.unisg.ics.interactions.hmas.bindings.payloads.PayloadBinding;
import ch.unisg.ics.interactions.hmas.bindings.payloads.PayloadBindings;
import ch.unisg.ics.interactions.hmas.bindings.protocols.ProtocolBinding;
import ch.unisg.ics.interactions.hmas.bindings.protocols.ProtocolBindings;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HttpBindingTest {

  Form HTTP_FORM = new Form.Builder("https://api.interactions.ics.unisg.ch/cherrybot/endpoint")
          .setMethodName("PUT")
          .setContentType("application/xarm+json")
          .build();

  InputSpecification PRIMITIVE_INPUT_SPEC = new InputSpecification.Builder()
          .setRequiredSemanticTypes(Set.of("https://w3id.org/interactions/ontologies/xarm/v1#GripperJoint"))
          .setDataType("http://www.w3.org/2001/XMLSchema#integer")
          .build();

  InputSpecification COMPLEX_INPUT_SPEC = new InputSpecification.Builder()
          .setRequiredSemanticTypes(Set.of(CORE.AGENT.stringValue()))
          .setQualifiedValueShape("http://example.org/gripperJointShape")
          .setQualifiedMinCount(1)
          .setQualifiedMaxCount(1)
          .setInput(new InputSpecification.Builder()
                  .setPath("http://xmlns.com/foaf/0.1/name")
                  .setDataType("http://www.w3.org/2001/XMLSchema#string")
                  .setMinCount(1)
                  .setMaxCount(1)
                  .build())
          .setInput(new InputSpecification.Builder()
                  .setPath("http://xmlns.com/foaf/0.1/mbox")
                  .setDataType("http://www.w3.org/2001/XMLSchema#string")
                  .setMinCount(1)
                  .setMaxCount(1)
                  .build())
          .build();

  @Test
  void testHTTPProtocolBinding() {

    ProtocolBinding httpBinding = ProtocolBindings.getBinding(HTTP_FORM);

    assertEquals(2, httpBinding.getSupportedSchemes().size());
    assertTrue(httpBinding.getSupportedSchemes().contains("http"));
    assertTrue(httpBinding.getSupportedSchemes().contains("https"));
    assertEquals("HTTP", httpBinding.getProtocolName());

    Action action = httpBinding.bind(HTTP_FORM);
    assertEquals(HTTP_FORM, action.getForm());
    assertFalse(action.getActorId().isPresent());
    assertFalse(action.getOperationType().isPresent());

  }

  @Test
  void testHTTPProtocolBindingWithActorId() throws URISyntaxException, ProtocolException {

    ProtocolBinding httpBinding = ProtocolBindings.getBinding(HTTP_FORM);

    Action action = httpBinding.bind(HTTP_FORM);
    assertEquals(HTTP_FORM, action.getForm());

    assertFalse(action.getActorId().isPresent());
    action.setActorId("http://hyperagent.org/example-agent");
    assertTrue(action.getActorId().isPresent());
    assertEquals("http://hyperagent.org/example-agent", action.getActorId().get());


    BasicClassicHttpRequest request = ((HttpAction) action).getRequest();

    assertEquals("PUT", request.getMethod());
    assertEquals(HTTP_FORM.getTarget(), request.getUri().toString());
    assertEquals(HTTP_FORM.getContentType(), request.getHeader("Content-Type").getValue());
    assertEquals("http://hyperagent.org/example-agent", request.getHeader("X-Agent-WebID").getValue());
    assertNull(request.getEntity());

    String expectedActionStr = "[Http Request] Method: PUT, Target: https://api.interactions.ics.unisg.ch/cherrybot/endpoint, " +
            "Content-Type: application/xarm+json, X-Agent-WebID: http://hyperagent.org/example-agent";

    String actualActionStr = action.toString();
    assertEquals(expectedActionStr, actualActionStr);
  }

  @Test
  void testHTTPProtocolSetHeader() throws ProtocolException {
    HttpBinding httpBinding = new HttpBinding();
    HttpAction action = httpBinding.bind(HTTP_FORM);
    BasicClassicHttpRequest request = action.getRequest();

    assertFalse(action.getActorId().isPresent());
    assertFalse(request.containsHeader("X-Agent-WebID"));

    action.setHeader("X-Agent-WebID", "http://hyperagent.org/example-agent-1");
    assertTrue(action.getActorId().isPresent());
    assertEquals("http://hyperagent.org/example-agent-1", action.getActorId().get());
    assertTrue(request.containsHeader("X-Agent-WebID"));
    assertEquals("http://hyperagent.org/example-agent-1", request.getHeader("X-Agent-WebID").getValue());

    action.setActorId("http://hyperagent.org/example-agent-2")
            .setActorId("http://hyperagent.org/example-agent-3");
    assertEquals("http://hyperagent.org/example-agent-3", action.getActorId().get());
    assertEquals("http://hyperagent.org/example-agent-3", request.getHeader("X-Agent-WebID").getValue());

    action.setHeader("X-Agent-WebID", "http://hyperagent.org/example-agent-4")
            .setHeader("Authorization", "Basic YWxhZGRpbjpvcGVuc2VzYW1l");
    assertEquals("http://hyperagent.org/example-agent-4", action.getActorId().get());
    assertEquals("http://hyperagent.org/example-agent-4", request.getHeader("X-Agent-WebID").getValue());
    assertTrue(request.containsHeader("Authorization"));
    assertEquals("Basic YWxhZGRpbjpvcGVuc2VzYW1l", request.getHeader("Authorization").getValue());
  }

  @Test
  void testHTTPProtocolWithPrimitiveInput() {
    String xarmJsonBindingClass = ApplicationJsonXArmBinding.class.getName();
    PayloadBindings.registerPayloadBinding(xarmJsonBindingClass);

    PayloadBinding payloadBinding = PayloadBindings.getBinding(HTTP_FORM);
    assertEquals(1, payloadBinding.getSupportedMediaTypes().size());
    assertTrue(payloadBinding.getSupportedMediaTypes().contains("application/xarm+json"));

    ProtocolBinding protocolBinding = ProtocolBindings.getBinding(HTTP_FORM);
    assertEquals(2, protocolBinding.getSupportedSchemes().size());
    assertTrue(protocolBinding.getSupportedSchemes().contains("http"));

    Input primitiveInput = payloadBinding.bind(PRIMITIVE_INPUT_SPEC, 50);
    Action httpAction = protocolBinding.bind(HTTP_FORM);

    assertFalse(httpAction.getInput().isPresent());
    httpAction.setInput(primitiveInput);
    assertTrue(httpAction.getInput().isPresent());
    assertEquals(primitiveInput, httpAction.getInput().get());

    String expectedActionStr = "[Http Request] Method: PUT, Target: https://api.interactions.ics.unisg.ch/cherrybot/endpoint, " +
            "Content-Type: application/xarm+json, Payload: 50";

    String actualActionStr = httpAction.toString();
    assertEquals(expectedActionStr, actualActionStr);
  }

  @Test
  void testHTTPProtocolWithComplexInput() {
    String xarmJsonBindingClass = ApplicationJsonXArmBinding.class.getName();
    PayloadBindings.registerPayloadBinding(xarmJsonBindingClass);
    PayloadBinding payloadBinding = PayloadBindings.getBinding(HTTP_FORM);

    HashMap<String, String> inputData = new HashMap<>();
    inputData.put("http://xmlns.com/foaf/0.1/name", "Danai");
    inputData.put("http://xmlns.com/foaf/0.1/mbox", "danaivach@example.org");

    Input input = payloadBinding.bind(COMPLEX_INPUT_SPEC, inputData);
    HttpAction httpAction = new HttpAction(HTTP_FORM);

    assertFalse(httpAction.getInput().isPresent());
    httpAction.setInput(input);
    assertTrue(httpAction.getInput().isPresent());
    assertEquals(input, httpAction.getInput().get());

    String expectedActionStr = "[Http Request] Method: PUT, Target: https://api.interactions.ics.unisg.ch/cherrybot/endpoint, " +
            "Content-Type: application/xarm+json, Payload: {\"name\":\"Danai\",\"email\":\"danaivach@example.org\"}";

    String actualActionStr = httpAction.toString();
    assertEquals(expectedActionStr, actualActionStr);
  }

}
