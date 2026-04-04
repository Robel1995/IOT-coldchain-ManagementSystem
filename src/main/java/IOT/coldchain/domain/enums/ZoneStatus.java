package IOT.coldchain.domain.enums;

/**
 * Per-zone temperature threat level.
 * Progresses: SAFE → ALERT → WARNING → EMERGENCY → SPOILED.
 * Each level triggers a different notification set.
 */
public enum ZoneStatus {

    /** Temperature is within safe bounds (not approaching limits). */
    SAFE,

    /** Temperature is within 2°C of upper or lower limit. Preventive action recommended. */
    ALERT,

    /** Temperature is exactly AT the boundary (minTemp or maxTemp). One degree from breach. */
    WARNING,

    /** Temperature has crossed the boundary. 30-minute countdown to SPOILED has started. */
    EMERGENCY,

    /** 30 consecutive minutes of breach confirmed. All cargo in this zone is ruined. */
    SPOILED
}