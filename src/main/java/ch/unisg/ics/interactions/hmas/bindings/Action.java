package ch.unisg.ics.interactions.hmas.bindings;

import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;

import java.io.IOException;
import java.util.Optional;

public interface Action {

  Form getForm();

  Optional<String> getActorId();

  Action setActorId(String actorId);

  Optional<String> getOperationType();

  ActionExecution execute() throws IOException;

  ActionExecution execute(Input input) throws IOException;

}
