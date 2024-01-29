package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;

import java.util.Set;

public interface PayloadBinding {

  Set<String> getSupportedMediaTypes();

  Input bind(InputSpecification inputSpec, Object data);

  //Object unbind(OutputSpecification inputSpecification, String contentType, Feedback data);

}
