package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.AbstractInput;
import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.core.vocabularies.CORE;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.AbstractIOSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.OutputSpecification;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
      if (validate(specification, data)) {
        return new JsonInput(specification, data);
      }
    }

    if (semanticTypes.contains(CORE.AGENT.stringValue())) {
      if (validate(specification, data)) {
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

  @Override
  public Map<String, Object> unbind(OutputSpecification specification, Object data) {
    Set<String> semanticTypes = specification.getRequiredSemanticTypes();

    if (semanticTypes.contains(CORE.AGENT.stringValue())) {
      JsonObject jsonObject = JsonParser.parseString((String) data).getAsJsonObject();

      HashMap<String, Object> accountDetails = new HashMap<>();
      String accountId = jsonObject.get("account").getAsString();
      accountDetails.put("http://xmlns.com/foaf/0.1/account", accountId);

      if (validate(specification, accountDetails)) {
        return accountDetails;
      }
    }

    return null;
  }


  boolean validate(AbstractIOSpecification specification, Object data) {
    // TODO SHACL validation
    return true;
  }

  class JsonInput extends AbstractInput {

    protected JsonInput(InputSpecification specification, Object data) {
      super(specification, data);
    }

  }

}


