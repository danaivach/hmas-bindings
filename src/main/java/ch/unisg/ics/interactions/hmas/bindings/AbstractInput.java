package ch.unisg.ics.interactions.hmas.bindings;

import ch.unisg.ics.interactions.hmas.interaction.shapes.IOSpecification;

public abstract class AbstractInput implements Input {

  protected final IOSpecification specification;

  protected final Object data;

  protected AbstractInput(IOSpecification specification, Object data) {
    this.specification = specification;
    this.data = data;
  }

  @Override
  public IOSpecification getInputSpecification() {
    return this.specification;
  }

  @Override
  public Object getData() {
    return this.data;
  }
}
