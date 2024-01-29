package ch.unisg.ics.interactions.hmas.bindings.protocols;

import ch.unisg.ics.interactions.hmas.bindings.BindingNotFoundException;
import ch.unisg.ics.interactions.hmas.bindings.BindingNotRegisteredException;
import ch.unisg.ics.interactions.hmas.interaction.signifiers.Form;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProtocolBindingTest {

  @Test
  void testBindUnknownBindingClass() {

    assertThrows(BindingNotRegisteredException.class, () -> {
      ProtocolBindings.registerProtocolBinding("unknownBindingClass");
    });
  }

  @Test
  void testGetUnknownBindingClass() {

    Form unknownSchemeForm = new Form.Builder("unknown://api.interactions.ics.unisg.ch/cherrybot/operator")
            .build();

    assertThrows(BindingNotFoundException.class, () -> {
      ProtocolBindings.getBinding(unknownSchemeForm);
    });
  }

}
