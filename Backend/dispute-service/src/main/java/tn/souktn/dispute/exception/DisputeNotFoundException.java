package tn.souktn.dispute.exception;

public class DisputeNotFoundException extends RuntimeException {

    public DisputeNotFoundException(Long id) {
        super("Litige introuvable avec l'id " + id);
    }
}
