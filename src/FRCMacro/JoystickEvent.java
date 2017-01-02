package FRCMacro;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static FRCMacro.JoystickEvent.eventType.*;

/**
 * An object representing a change in the state of a joystick, and when it occurred.
 *
 * @author Nicholas DeLello
 * @see simulatedJoystick
 * @see Macro
 */
class JoystickEvent {

    /**
     * The eventType of event, whether a button was pressed, a button was released, an axis was moved, or a pov switch moved.
     */
    public enum eventType {
        PRESS, RELEASE, AXIS, POV
    }

    private final JoystickEvent.eventType eventType;
    private final int stickId;
    private final int id;
    private Double val;
    private final long time;
    private static SimpleDateFormat fmt;

    /**
     * Creates a joystick event. (JoystickEvent.eventType.PRESS/RELEASE/POV)
     *
     * @param type  The eventType of event (JoystickEvent.eventType.PRESS/RELEASE/POV)
     * @param id    The id of the joystick being used.
     * @param btnId The id of the button being pressed/released.
     */
    public JoystickEvent(JoystickEvent.eventType type, int id, int btnId) {
        this.eventType = type;
        this.stickId = id;
        this.id = btnId;
        this.time = System.currentTimeMillis();
    }

    /**
     * Creates a joystick event. (JoystickEvent.eventType.AXIS/POV)
     *
     * @param type   The eventType of event (JoystickEvent.eventType.AXIS)
     * @param id     The id of the joystick being used.
     * @param axisId The id of the axis being changed.
     * @param val    The current value of the axis.
     */
    public JoystickEvent(JoystickEvent.eventType type, int id, int axisId, double val) {
        this.eventType = type;
        this.stickId = id;
        this.id = axisId;
        this.val = val;
        this.time = System.currentTimeMillis();
    }

    /**
     * Creates a joystick event. (Press/Release)
     *
     * @param type  The eventType of event (JoystickEvent.eventType.PRESS/RELEASE)
     * @param time  The time, in milliseconds, the event occurred. (from System.currentTimeMillis())
     * @param id    The id of the joystick being used.
     * @param btnId The id of the button being pressed/released.
     */
    public JoystickEvent(JoystickEvent.eventType type, long time, int id, int btnId) {
        this.eventType = type;
        this.stickId = id;
        this.id = btnId;
        this.time = time;
    }

    /**
     * Creates a joystick event. (Axis)
     *
     * @param type   The eventType of event (JoystickEvent.eventType.AXIS)
     * @param time   The time, in milliseconds, the event occurred. (from System.currentTimeMillis())
     * @param id     The id of the joystick being used.
     * @param axisId The id of the axis being changed.
     * @param val    The current value of the axis.
     */
    public JoystickEvent(JoystickEvent.eventType type, long time, int id, int axisId, double val) {
        this.eventType = type;
        this.stickId = id;
        this.id = axisId;
        this.val = val;
        this.time = time;
    }

    /**
     * @return The time at which this event occurred.
     */
    public long getTime() {
        return time;
    }

    /**
     * @return The current value of the axis, if it is a AXIS event, and null otherwise.
     */
    public Double getVal() {
        return val != null ? val: null;
    }

    /**
     * @return The eventType of event this event is.
     */
    public JoystickEvent.eventType getEventType() {
        return eventType;
    }

    /**
     * @return The id of the button if it is not a POV event, and null otherwise.
     */
    public Integer getID() {
        return id;
    }

    /**
     * @return The value of the POV switch, if it is a POV event, and null otherwise.
     */
    public Integer getPOVValue() {
        return this.eventType == POV ? val.intValue(): null;
    }

    /**
     * @return The ID of the stick this event is coming from.
     */
    public int getStickId() {
        return stickId;
    }

    /**
     * Converts this into a string which can be easily split and turned back into a JoystickEvent.
     *
     * @return This serialized into a string.
     */
    public String toString() {
        String str = time + ":";
        switch (eventType) {
            case PRESS:
                str += "press";
                break;
            case RELEASE:
                str += "release";
                break;
            case AXIS:
                str += "axis";
                break;
            case POV:
                str += "POV";
                break;
        }
        return str + ',' + stickId + ',' + id + ',' +
               (eventType == AXIS ? String.valueOf(val) + ',':
                eventType == POV ? String.valueOf(val.intValue()): "") + '\n';
    }

    /**
     * Converts this into a human-readable string. For logging purposes, mostly.
     *
     * @return This event as a human-readable string.
     */
    public String toReadableString() {
        fmt = fmt == null ? new SimpleDateFormat("MM/dd/yyyy hh:mm:ss"): fmt; //Create the date formatter if it does not exist.
        return String.format(Locale.ENGLISH, "%s: Joystick %d's %s %s\n", fmt.format(time), stickId,
                             eventType == PRESS || eventType == RELEASE ? "Button ": eventType == AXIS ? "Axis": "POV",
                             eventType == PRESS ? "pressed.": eventType == RELEASE ? "released.": ("set to " + val));
    }

    /**
     * Returns if the provided {@link JoystickEvent} is the same as this one.
     *
     * @param that The {@link JoystickEvent} to check for equality.
     * @return If they are equal.
     */
    public boolean equals(JoystickEvent that) { //Fun. If you let IntelliJ simplify it all the way down, it becomes a one-liner!
        return this == that ||
               that != null && this.stickId == that.stickId && this.id == that.id && this.time == that.time &&
               this.eventType == that.eventType && (this.val != null ? this.val.equals(that.val): that.val == null);
    }

    /**
     * Returns the hashcode of the given object, using {@link #stickId}, {@link #id}, and {@link #val}, but not {@link #time}.
     *
     * @return The hashcode of the given object, using {@link #stickId}, {@link #id}, and {@link #val}, but not {@link #time}.
     */
    @Override
    public int hashCode() {
        return ((eventType.hashCode() * 31 + stickId) * 31 + id) * 31 + (val != null ? val.hashCode(): 0);
    }
}
