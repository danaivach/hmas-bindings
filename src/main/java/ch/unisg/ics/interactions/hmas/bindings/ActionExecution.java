package ch.unisg.ics.interactions.hmas.bindings;

import java.util.Optional;

public class ActionExecution {

  private final Action action;
  private final Optional<String> inputData;
  private final Optional<String> outputData;

  public ActionExecution(Action action, Optional<String> inputData, Optional<String> outputData) {
    this.action = action;
    this.inputData = inputData;
    this.outputData = outputData;
  }

  public Action getAction() {
    return action;
  }

  public Optional<String> getInputData() {
    return inputData;
  }

  public Optional<String> getOutputData() {
    return outputData;
  }
}
