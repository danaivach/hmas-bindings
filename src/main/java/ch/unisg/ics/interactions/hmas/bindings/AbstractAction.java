package ch.unisg.ics.interactions.hmas.bindings;

import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractAction implements Action {

  protected final Form form;

  protected final Optional<String> operationType;

  protected Optional<String> actorId;

  public AbstractAction(Form form) {
    this(form, null);
  }

  public AbstractAction(Form form, String operationType) {
    this.form = Objects.requireNonNull(form, "Form must not be null");
    this.operationType = Optional.ofNullable(operationType);
    this.actorId = Optional.empty();
  }

  @Override
  public Form getForm() {
    return this.form;
  }

  @Override
  public Optional<String> getActorId() {
    return this.actorId;
  }

  @Override
  public Optional<String> getOperationType() {
    return this.operationType;
  }

  @Override
  public Action setActorId(String actorId) {
    this.actorId = Optional.of(actorId);
    return this;
  }

}
