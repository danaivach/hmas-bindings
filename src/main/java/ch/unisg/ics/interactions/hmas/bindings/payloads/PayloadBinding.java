package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.OutputSpecification;

import java.util.Set;

public interface PayloadBinding {

  Set<String> getSupportedMediaTypes();

  Input bind(InputSpecification specification, Object data);

  Object unbind(OutputSpecification specification, Object data);

}
