package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.BindingNotFoundException;
import ch.unisg.ics.interactions.hmas.bindings.BindingNotRegisteredException;
import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.OutputSpecification;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PayloadBindingTest {

  @Test
  void testBindApplicationJsonBinding() {

    InputSpecification gripperJointInputSpec = new InputSpecification.Builder()
            .setRequiredSemanticTypes(Set.of("https://w3id.org/interactions/ontologies/xarm/v1#GripperJoint"))
            .setDataType("http://www.w3.org/2001/XMLSchema#integer")
            .setMinCount(1)
            .setMaxCount(1)
            .setMinInclusive((double) 0)
            .setMaxInclusive((double) 800)
            .build();

    Form setGripperForm = new Form.Builder("https://api.interactions.ics.unisg.ch/cherrybot/gripper")
            .setMethodName("PUT")
            .setContentType("application/xarm+json")
            .build();

    InputSpecification registerInputSpec = new InputSpecification.Builder()
            .setRequiredSemanticTypes(Set.of(CORE.AGENT.stringValue()))
            .setQualifiedValueShape("http://example.org/agent-details")
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

    OutputSpecification registerOutputSpec = new OutputSpecification.Builder()
            .setRequiredSemanticTypes(Set.of(CORE.AGENT.stringValue()))
            .setQualifiedValueShape("http://example.org/registered-agent-details")
            .setQualifiedMinCount(1)
            .setQualifiedMaxCount(1)
            .setOutput(new OutputSpecification.Builder()
                    .setPath("http://xmlns.com/foaf/0.1/account")
                    .setDataType("http://www.w3.org/2001/XMLSchema#string")
                    .setMinCount(1)
                    .setMaxCount(1)
                    .build())
            .build();

    Form registerForm = new Form.Builder("https://api.interactions.ics.unisg.ch/cherrybot/operator")
            .setMethodName("POST")
            .setContentType("application/xarm+json")
            .build();

    /*
    InputSpecification tcpInputSpec = new InputSpecification.Builder()
            .setRequiredSemanticTypes(Set.of("https://w3id.org/interactions/ontologies/xarm/v1#Movement"))
            .setQualifiedMinCount(1)
            .setQualifiedMaxCount(1)
            .setInput(new InputSpecification.Builder()
                    .setPath("https://w3id.org/interactions/ontologies/xarm/v1#hasSpeed")
                    .setDataType("http://www.w3.org/2001/XMLSchema#integer")
                    .setMinCount(1)
                    .setMaxCount(1)
                    .setMinInclusive(10.0)
                    .setMaxInclusive(50.0)
                    .build())
            .setInput(new InputSpecification.Builder()
                    .setQualifiedValueShape("http://example.org/gripperJointShape")
                    .setPath("https://w3id.org/interactions/ontologies/xarm/v1#hasTarget")
                    .setDataType("http://www.w3.org/2001/XMLSchema#string")
                    .setMinCount(1)
                    .setMaxCount(1)
                    .build())
            .build();
     */

    String xarmJsonBindingClass = ApplicationJsonXArmBinding.class.getName();
    PayloadBindings.registerPayloadBinding(xarmJsonBindingClass);

    PayloadBinding setGripperBinding = PayloadBindings.getBinding(setGripperForm);
    assertEquals(1, setGripperBinding.getSupportedMediaTypes().size());
    assertTrue(setGripperBinding.getSupportedMediaTypes().contains("application/xarm+json"));

    Input boundGripperJointInput = setGripperBinding.bind(gripperJointInputSpec, 50);
    assertEquals(gripperJointInputSpec, boundGripperJointInput.getInputSpecification());
    assertEquals(50, boundGripperJointInput.getData());

    PayloadBinding registerBinding = PayloadBindings.getBinding(registerForm);
    assertEquals(1, registerBinding.getSupportedMediaTypes().size());
    assertTrue(registerBinding.getSupportedMediaTypes().contains("application/xarm+json"));

    String expectedInputData = "{\"name\":\"Danai\",\"email\":\"danaivach@example.org\"}";
    HashMap<String, String> agentDetails = new HashMap<>();
    agentDetails.put("http://xmlns.com/foaf/0.1/name", "Danai");
    agentDetails.put("http://xmlns.com/foaf/0.1/mbox", "danaivach@example.org");
    Input boundRegisterInput = registerBinding.bind(registerInputSpec, agentDetails);
    assertEquals(registerInputSpec, boundRegisterInput.getInputSpecification());
    assertEquals(expectedInputData, boundRegisterInput.getData());

    String expectedValue = "1200A";
    String outputData = "{\"account\":\"1200A\"}";
    Map<String, Object> unBoundRegisterOutput = (Map<String, Object>) registerBinding.unbind(registerOutputSpec, outputData);
    String actualValue = (String) unBoundRegisterOutput.get("http://xmlns.com/foaf/0.1/account");
    assertEquals(expectedValue, actualValue);
  }

  @Test
  void testBindUnknownBindingClass() {

    assertThrows(BindingNotRegisteredException.class, () -> {
      PayloadBindings.registerPayloadBinding("unknownBindingClass");
    });

  }

  @Test
  void testGetUnknownBindingClass() {

    Form unknownMediaTypeForm = new Form.Builder("https://api.interactions.ics.unisg.ch/cherrybot/gripper")
            .setContentType("application/unknown+json")
            .build();

    assertThrows(BindingNotFoundException.class, () -> {
      PayloadBindings.getBinding(unknownMediaTypeForm);
    });

  }
}
