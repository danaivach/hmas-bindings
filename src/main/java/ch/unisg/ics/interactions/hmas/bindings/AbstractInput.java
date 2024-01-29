package ch.unisg.ics.interactions.hmas.bindings;

import ch.unisg.ics.interactions.hmas.interaction.signifiers.InputSpecification;

public abstract class AbstractInput implements Input {

  protected final InputSpecification specification;

  protected final Object data;

  protected AbstractInput(InputSpecification specification, Object data) {
    this.specification = specification;
    this.data = data;
  }

  @Override
  public InputSpecification getInputSpecification() {
    return this.specification;
  }

  @Override
  public Object getData() {
    return this.data;
  }
}
