package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.BindingNotFoundException;
import ch.unisg.ics.interactions.hmas.bindings.BindingNotRegisteredException;
import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.shapes.IntegerSpecification;
import ch.unisg.ics.interactions.hmas.interaction.shapes.QualifiedValueSpecification;
import ch.unisg.ics.interactions.hmas.interaction.shapes.StringSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PayloadBindingTest {

  @SuppressWarnings("unchecked")
  @Test
  void testBindApplicationJsonBinding() {

    IntegerSpecification gripperJointInputSpec = new IntegerSpecification.Builder()
            .addRequiredSemanticType("https://w3id.org/interactions/ontologies/xarm/v1#GripperJoint")
            .setMinInclusiveValue(0)
            .setMaxInclusiveValue(800)
            .build();

    Form setGripperForm = new Form.Builder("https://api.interactions.ics.unisg.ch/cherrybot/gripper")
            .setMethodName("PUT")
            .setContentType("application/xarm+json")
            .build();

    QualifiedValueSpecification registerInputSpec = new QualifiedValueSpecification.Builder()
            .addRequiredSemanticTypes(Set.of(CORE.AGENT.stringValue()))
            .setIRIAsString("http://example.org/agent-details")
            .setRequired(true)
            .addPropertySpecification("http://xmlns.com/foaf/0.1/name",
                    new StringSpecification.Builder()
                            .setRequired(true)
                            .build())
            .addPropertySpecification("http://xmlns.com/foaf/0.1/mbox",
                    new StringSpecification.Builder()
                            .setRequired(true)
                            .build())
            .build();

    QualifiedValueSpecification registerOutputSpec = new QualifiedValueSpecification.Builder()
            .addRequiredSemanticType(CORE.AGENT.stringValue())
            .setIRIAsString("http://example.org/registered-agent-details")
            .setRequired(true)
            .addPropertySpecification("http://xmlns.com/foaf/0.1/account", new StringSpecification.Builder()
                    .setRequired(true)
                    .build())
            .build();

    Form registerForm = new Form.Builder("https://api.interactions.ics.unisg.ch/cherrybot/operator")
            .setMethodName("POST")
            .setContentType("application/xarm+json")
            .build();

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
