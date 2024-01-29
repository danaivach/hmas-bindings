package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.AbstractInput;
import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ApplicationJsonXArmBinding implements PayloadBinding {

  private final static String APPLICATION_JSON_XARM_MEDIA_TYPE = "application/xarm+json";

  private final static Set<String> SUPPORTED_MEDIA_TYPES = new HashSet<>();

  static {
    SUPPORTED_MEDIA_TYPES.add("application/xarm+json");
  }

  @Override
  public Set<String> getSupportedMediaTypes() {
    return SUPPORTED_MEDIA_TYPES;
  }

  @Override
  public Input bind(InputSpecification specification, Object data) {
    Set<String> semanticTypes = specification.getRequiredSemanticTypes();

    if (semanticTypes.contains("https://w3id.org/interactions/ontologies/xarm/v1#GripperJoint")) {
      if (validateInput(specification, data)) {
        return new JsonInput(specification, data);
      }
    }

    if (semanticTypes.contains(CORE.AGENT.stringValue())) {
      if (validateInput(specification, data)) {
        HashMap<String, String> agentDetails = (HashMap<String, String>) data;
        HashMap<String, String> boundAgentDetails = new HashMap<>();

        String agentName = agentDetails.get("http://xmlns.com/foaf/0.1/name");
        String agentEmail = agentDetails.get("http://xmlns.com/foaf/0.1/mbox");
        boundAgentDetails.put("name", agentName);
        boundAgentDetails.put("email", agentEmail);

        return new JsonInput(specification, new Gson().toJson(boundAgentDetails));
      }
    }
    return null;
  }

  boolean validateInput(InputSpecification specification, Object data) {
    // TODO input validation
    return true;
  }

  class JsonInput extends AbstractInput {

    protected JsonInput(InputSpecification specification, Object data) {
      super(specification, data);
    }

  }
}


