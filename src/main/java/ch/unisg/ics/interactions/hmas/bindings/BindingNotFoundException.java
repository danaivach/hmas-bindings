package ch.unisg.ics.interactions.hmas.bindings;

/**
 * Exception thrown whenever {@link ProtocolBindings#getBinding(Form)} is called
 * but no suitable binding has been registered for the URI scheme provided in the form.
 */
public class BindingNotFoundException extends RuntimeException {

}
