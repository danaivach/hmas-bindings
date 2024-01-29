package ch.unisg.ics.interactions.hmas.bindings;

import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;

public interface Input {

  InputSpecification getInputSpecification();

  Object getData();
}
