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

    // A payload binding can be registered (symmetrical to how a protocol binding can be registered)
    // Here, a payload binding is registered for application/xarm+json 
    /* Note: a payload binding could be for a more "generic" media type. If I were to design a payload binding 
       for JSON, I would request associating the SHACL input/output specification with a JSON-ld context so that 
       the RDF input/output could be automatically serialized in application/json. JSON-ld contexts could also be used
       to create and register domain-specific+json payload bindings in a more structured way, i.e. to couple them to  
       JSON-LD contexts instead of ad-hoc rules in a specific programming language. See here example JSON-ld contexts
       that correspond to SHACL input/output specifications: https://github.com/HyperAgents/hmas/blob/244b40424da8fda2385b70b42df8ce02857a51dc/src/jacamo.ttl#L1272-L1371
    
    */ 
    String xarmJsonBindingClass = ApplicationJsonXArmBinding.class.getName();
    PayloadBindings.registerPayloadBinding(xarmJsonBindingClass);

    // One can look up a payload binding based on a Form (exactly as a protocol binding can be looked up based on a Form)
    PayloadBinding setGripperBinding = PayloadBindings.getBinding(setGripperForm);
    assertEquals(1, setGripperBinding.getSupportedMediaTypes().size());
    // A media type for a payload binding is like what a protocol scheme is for a protocol binding
    assertTrue(setGripperBinding.getSupportedMediaTypes().contains("application/xarm+json"));

    // A payload binding associates (input/output) data to a specific serialization format found in an (input/output) specification
    // In hmas-java SHACL descriptions are used as input/output specifications.
    // The motivation is that the input/output is provided w.r.t. what can be expressed as a SHACL shape, including RDF triples. This would also enable 
    // validation of input/output against a SHACL shape. 
    // Here, binding is performed based on a SHACL shape that looks like this:
    /* [ a sh:Shape;
         sh:datatype xs:int ;
       ... ] .
    */
    Input boundGripperJointInput = setGripperBinding.bind(gripperJointInputSpec, 50);
    assertEquals(gripperJointInputSpec, boundGripperJointInput.getInputSpecification());
    assertEquals(50, boundGripperJointInput.getData());

    PayloadBinding registerBinding = PayloadBindings.getBinding(registerForm);
    assertEquals(1, registerBinding.getSupportedMediaTypes().size());
    assertTrue(registerBinding.getSupportedMediaTypes().contains("application/xarm+json"));

    // This example is considering a more complex SHACL shape that looks like this:
    /* [ a sh:Shape;
         sh:class hmas:Agent ;
         sh:property [ a sh:Shape;
            sh:path foaf:name ;
            sh:datatype xs:str ;
            ...
         ];
         sh:property [ a sh:Shape;
            sh:path foaf:mbox ;
            sh:datatype xs:str ;
            ...
         ];
       ...  
       ];\n" +
    */
    // Here, the input/output data is provided in a Java HashMap. "Ideally", the data could be provided in a more RDF-based
    // format, but a) I was afraid to introduce a dependency to an external library like RDF4J, and b) this would require 
    // further extension of an agent programming language (e.g., to support RDF-like types in AgentSpeak). 
    // A more RDF-based type would enable an entity to pass input data and retrieve output data in RDF. Here, the input/output  
    // could look like the following:
    /* [
          <ex:an-agent> a hmas:Agent ;
            foaf:name "Danai";
            foaf:mbox "danai.vach@example.org".
       ]
    */
    //Here, the input data is serialized to application/xarm+json
    String expectedInputData = "{\"name\":\"Danai\",\"email\":\"danaivach@example.org\"}";
    HashMap<String, String> agentDetails = new HashMap<>();
    agentDetails.put("http://xmlns.com/foaf/0.1/name", "Danai");
    agentDetails.put("http://xmlns.com/foaf/0.1/mbox", "danaivach@example.org");
    Input boundRegisterInput = registerBinding.bind(registerInputSpec, agentDetails);
    assertEquals(registerInputSpec, boundRegisterInput.getInputSpecification());
    assertEquals(expectedInputData, boundRegisterInput.getData());

    //Here, the output JSON payload should be mapped to an RDF representation
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
