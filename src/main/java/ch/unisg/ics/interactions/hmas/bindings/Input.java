package ch.unisg.ics.interactions.hmas.bindings;

import ch.unisg.ics.interactions.hmas.interaction.shapes.IOSpecification;

public interface Input {

  IOSpecification getInputSpecification();

  Object getData();
}
