package ch.unisg.ics.interactions.hmas.bindings;

import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;

import java.util.Optional;

public interface Action extends Behavior {

  Form getForm();

  Optional<String> getActorId();

  Action setActorId(String actorId);

  Optional<String> getOperationType();

  Optional<Input> getInput();

  Action setInput(Input input);

}
