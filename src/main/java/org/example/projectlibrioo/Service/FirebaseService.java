package org.example.projectlibrioo.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseService.class);

    // ── Firebase path constants ────────────────────────────────────────────────
    private static final String PATH_ROBOT           = "robot";
    private static final String KEY_TARGET_SHELF     = "targetShelf";
    private static final String KEY_STATUS           = "status";
    private static final String KEY_CURRENT_STEP     = "currentStep";
    private static final String KEY_CURRENT_COMMAND  = "currentCommand";

    // ── Robot status values (mirrors ESP32 / Firebase convention) ─────────────
    public static final String STATUS_IDLE      = "IDLE";
    public static final String STATUS_MOVING    = "MOVING";
    public static final String STATUS_ARRIVED   = "ARRIVED";
    public static final String STATUS_RETURNING = "RETURNING";
    public static final String STATUS_STOPPED   = "STOPPED";
    public static final String STATUS_ERROR     = "ERROR_LOST_LINE";

    // ── Command values ─────────────────────────────────────────────────────────
    private static final String CMD_NONE = "";   // empty = no pending command
    private static final String CMD_BACK = "BACK";
    private static final String CMD_STOP = "STOP";

    // ── Shared DatabaseReference ───────────────────────────────────────────────
    private final DatabaseReference robotRef;

    public FirebaseService() {
        FirebaseApp app = FirebaseApp.getInstance();
        this.robotRef = FirebaseDatabase.getInstance(app).getReference(PATH_ROBOT);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SEND SHELF NUMBER  →  triggers robot to navigate to that shelf
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Sends a shelf number to the robot and returns a future that completes
     * when Firebase confirms the write (or fails with an exception).
     */
    public CompletableFuture<Void> sendShelfNumber(int shelfNumber) {
        if (shelfNumber <= 0) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Shelf number must be positive, got: " + shelfNumber)
            );
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put(KEY_TARGET_SHELF,    shelfNumber);
        updates.put(KEY_STATUS,          STATUS_MOVING);
        updates.put(KEY_CURRENT_STEP,    0);
        updates.put(KEY_CURRENT_COMMAND, CMD_NONE);  // ← was "none", must be ""

        return toFuture(robotRef.updateChildrenAsync(updates), "sendShelfNumber(" + shelfNumber + ")");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SEND BACK COMMAND  →  robot reverses to start position
    // ══════════════════════════════════════════════════════════════════════════

    public CompletableFuture<Void> sendBackCommand() {
        Map<String, Object> updates = new HashMap<>();
        updates.put(KEY_CURRENT_COMMAND, CMD_BACK);
        updates.put(KEY_STATUS,          STATUS_RETURNING);

        return toFuture(robotRef.updateChildrenAsync(updates), "sendBackCommand");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SEND STOP COMMAND  →  emergency stop
    // ══════════════════════════════════════════════════════════════════════════

    public CompletableFuture<Void> sendStopCommand() {
        Map<String, Object> updates = new HashMap<>();
        updates.put(KEY_CURRENT_COMMAND, CMD_STOP);
        updates.put(KEY_STATUS,          STATUS_STOPPED);

        return toFuture(robotRef.updateChildrenAsync(updates), "sendStopCommand");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RESET  →  clears all robot state back to IDLE
    // ══════════════════════════════════════════════════════════════════════════

    public CompletableFuture<Void> resetRobot() {
        Map<String, Object> updates = new HashMap<>();
        updates.put(KEY_TARGET_SHELF,    0);
        updates.put(KEY_STATUS,          STATUS_IDLE);
        updates.put(KEY_CURRENT_STEP,    0);
        updates.put(KEY_CURRENT_COMMAND, CMD_NONE);

        return toFuture(robotRef.updateChildrenAsync(updates), "resetRobot");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  READ STATUS  →  one-shot read of the current robot state
    // ══════════════════════════════════════════════════════════════════════════

    public CompletableFuture<RobotState> getRobotState() {
        CompletableFuture<RobotState> future = new CompletableFuture<>();

        robotRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    RobotState state = new RobotState(
                            safeInt(snapshot, KEY_TARGET_SHELF),
                            safeInt(snapshot, KEY_CURRENT_STEP),
                            safeString(snapshot, KEY_STATUS),
                            safeString(snapshot, KEY_CURRENT_COMMAND)
                    );
                    future.complete(state);
                } catch (Exception e) {
                    log.error("getRobotState: failed to parse snapshot", e);
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                log.error("getRobotState: Firebase error {}", error.getMessage());
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  REAL-TIME STATUS LISTENER  →  call once; fires on every status change
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Registers a persistent listener on /robot/status.
     * Returns the listener so the caller can remove it later with
     *   robotRef.child(KEY_STATUS).removeEventListener(listener)
     */
    public ValueEventListener listenToStatus(StatusListener callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if (status != null) {
                    log.debug("Robot status changed → {}", status);
                    callback.onStatusChanged(status);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                log.error("listenToStatus cancelled: {}", error.getMessage());
            }
        };

        robotRef.child(KEY_STATUS).addValueEventListener(listener);
        return listener;
    }

    /** Removes a previously registered status listener. */
    public void removeStatusListener(ValueEventListener listener) {
        robotRef.child(KEY_STATUS).removeEventListener(listener);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    /** Wraps an ApiFuture<Void> from Firebase into a CompletableFuture<Void>. */
    private CompletableFuture<Void> toFuture(
            com.google.api.core.ApiFuture<Void> apiFuture, String operationName) {

        CompletableFuture<Void> cf = new CompletableFuture<>();
        com.google.api.core.ApiFutures.addCallback(
                apiFuture,
                new com.google.api.core.ApiFutureCallback<Void>() {
                    @Override public void onSuccess(Void result) {
                        log.debug("Firebase write OK: {}", operationName);
                        cf.complete(null);
                    }
                    @Override public void onFailure(Throwable t) {
                        log.error("Firebase write FAILED: {} — {}", operationName, t.getMessage());
                        cf.completeExceptionally(t);
                    }
                },
                Runnable::run
        );
        return cf;
    }

    private int safeInt(DataSnapshot snap, String key) {
        Object val = snap.child(key).getValue();
        if (val instanceof Number n) return n.intValue();
        return 0;
    }

    private String safeString(DataSnapshot snap, String key) {
        Object val = snap.child(key).getValue();
        return val != null ? val.toString() : "";
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  INNER TYPES
    // ══════════════════════════════════════════════════════════════════════════

    /** Immutable snapshot of the robot node in Firebase. */
    public record RobotState(
            int    targetShelf,
            int    currentStep,
            String status,
            String currentCommand
    ) {
        public boolean isIdle()      { return STATUS_IDLE.equals(status); }
        public boolean isMoving()    { return STATUS_MOVING.equals(status); }
        public boolean isArrived()   { return STATUS_ARRIVED.equals(status); }
        public boolean isReturning() { return STATUS_RETURNING.equals(status); }
        public boolean isStopped()   { return STATUS_STOPPED.equals(status); }
        public boolean hasError()    { return STATUS_ERROR.equals(status); }
    }

    /** Callback interface for real-time status updates. */
    @FunctionalInterface
    public interface StatusListener {
        void onStatusChanged(String newStatus);
    }
}