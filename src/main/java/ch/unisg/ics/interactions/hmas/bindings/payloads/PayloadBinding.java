package ch.unisg.ics.interactions.hmas.bindings.payloads;

import ch.unisg.ics.interactions.hmas.bindings.Input;
import ch.unisg.ics.interactions.hmas.interaction.shapes.IOSpecification;

import java.util.Set;

public interface PayloadBinding {

  Set<String> getSupportedMediaTypes();

  Input bind(IOSpecification specification, Object data);

  Object unbind(IOSpecification specification, Object data);

}
