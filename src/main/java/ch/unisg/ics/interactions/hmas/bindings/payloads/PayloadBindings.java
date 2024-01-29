package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.BindingNotFoundException;
import ch.unisg.ics.interactions.hmas.bindings.BindingNotRegisteredException;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PayloadBindings {

  private final static Logger LOGGER = Logger.getLogger(PayloadBindings.class.getCanonicalName());

  private static final Map<String, PayloadBinding> registeredPayloadBindings = new HashMap<>();

  private PayloadBindings() {
  }

  public static PayloadBinding getBinding(Form form) throws BindingNotFoundException {
    String contentType = form.getContentType();

    if (!registeredPayloadBindings.containsKey(contentType)) {
      throw new BindingNotFoundException();
    }
    return registeredPayloadBindings.get(contentType);
  }

  public static void registerPayloadBinding(String bindingClass) throws BindingNotRegisteredException {

    for (Map.Entry<String, PayloadBinding> entry : registeredPayloadBindings.entrySet()) {
      if (entry.getValue().getClass().getName().equals(bindingClass)) {
        LOGGER.log(Level.INFO, "No change was performed because a payload binding of type {0} is already registered.", bindingClass);
        return;
      }
    }
    Map<String, PayloadBinding> newBindings = new HashMap<>();

    try {
      PayloadBinding binding = (PayloadBinding) Class.forName(bindingClass).newInstance();

      for (String mediaType : binding.getSupportedMediaTypes()) {
        if (registeredPayloadBindings.containsKey(mediaType)) {
          LOGGER.log(Level.INFO, "The payload binding for the media type {0} has been updated.", mediaType);
        }

        newBindings.put(mediaType, binding);
      }
    } catch (Exception e) {
      throw new BindingNotRegisteredException(e);
    }

    registeredPayloadBindings.putAll(newBindings);
  }

}