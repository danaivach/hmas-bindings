package ch.unisg.ics.interactions.hmas.bindings.protocols;

import ch.unisg.ics.interactions.hmas.bindings.Action;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;

import java.util.Set;

public interface ProtocolBinding {

  String getProtocolName();

  Set<String> getSupportedSchemes();

  Action bind(Form form);

  Action bind(Form form, String operationType);

}
