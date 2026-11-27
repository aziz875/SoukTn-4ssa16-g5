package tn.souktn.dispute.entity;

/**
 * Cycle de vie d'un litige.
 * Les transitions autorisées seront gérées plus tard par Spring StateMachine.
 */
public enum DisputeStatus {
    OPEN,
    UNDER_REVIEW,
    RESOLVED,
    REJECTED
}
