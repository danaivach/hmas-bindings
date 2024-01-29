package ch.unisg.ics.interactions.hmas.bindings.protocols;

import ch.unisg.ics.interactions.hmas.bindings.BindingNotFoundException;
import ch.unisg.ics.interactions.hmas.bindings.BindingNotRegisteredException;
import ch.unisg.ics.interactions.hmas.bindings.protocols.http.HttpBinding;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Adapted from Victor Charpenay's code at https://github.com/Hypermedea/wot-td-java/blob/master/src/main/java/ch/unisg/ics/interactions/wot/td/bindings/ProtocolBindings.java
 */

public class ProtocolBindings {

  private final static Logger LOGGER = Logger.getLogger(ProtocolBindings.class.getCanonicalName());

  private static final Map<String, ProtocolBinding> registeredProtocolBindings = new HashMap<>();

  static {
    String httpBindingClass = HttpBinding.class.getName();
    ProtocolBindings.registerProtocolBinding(httpBindingClass);
  }

  private ProtocolBindings() {
  }

  public static ProtocolBinding getBinding(Form form) throws BindingNotFoundException {
    String scheme = getScheme(form.getTarget());

    if (!registeredProtocolBindings.containsKey(scheme)) {
      throw new BindingNotFoundException();
    }

    return registeredProtocolBindings.get(scheme);

  }

  public static void registerProtocolBinding(String bindingClass) throws BindingNotRegisteredException {
    for (Map.Entry<String, ProtocolBinding> entry : registeredProtocolBindings.entrySet()) {
      if (entry.getValue().getClass().getName().equals(bindingClass)) {
        LOGGER.log(Level.INFO, "No change was performed because a protocol binding of type {0} is already registered.", bindingClass);
        return;
      }
    }

    Map<String, ProtocolBinding> newBindings = new HashMap<>();

    try {
      ProtocolBinding binding = (ProtocolBinding) Class.forName(bindingClass).newInstance();

      for (String scheme : binding.getSupportedSchemes()) {
        if (registeredProtocolBindings.containsKey(scheme)) {
          LOGGER.log(Level.INFO, "The protocol binding for the media type {0} has been updated.", scheme);
        }

        newBindings.put(scheme, binding);
      }
    } catch (Exception e) {
      throw new BindingNotRegisteredException(e);
    }

    registeredProtocolBindings.putAll(newBindings);
  }

  private static String getScheme(String uriOrTemplate) {

    String scheme = null;

    int endOfSchemeIndex = uriOrTemplate.indexOf(':');

    if (endOfSchemeIndex >= 0) {
      scheme = uriOrTemplate.substring(0, endOfSchemeIndex);
    }

    return scheme;
  }

}
