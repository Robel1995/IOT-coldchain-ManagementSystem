package IOT.coldchain.domain.enums;

/**
 * Notification urgency level emitted by domain events.
 * Maps directly to which stakeholders are notified and on which channels.
 *
 * <pre>
 * NONE      → no action needed
 * ALERT     → Admin + Dispatcher + Driver (dashboard/email) — approaching threshold
 * NOTIFY    → Admin + Dispatcher + Driver + HealthCenter (email) — at boundary
 * EMERGENCY → All parties (SMS + Telegram) — active breach, timer started
 * SPOILED   → All parties (all channels) — 30-min breach confirmed
 * </pre>
 */
public enum NotificationLevel {
    NONE,
    ALERT,
    NOTIFY,
    EMERGENCY,
    SPOILED
}