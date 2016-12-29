package FRCMacro;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * An object representing a change in the state of a joystick, and when it occurred.
 * @author Nicholas DeLello
 * @see simulatedJoystick
 * @see Macro
 */
class JoystickEvent {

    /**
     * The type of event, whether a button was pressed, a button was released, an axis was moved, or a pov switch moved.
     */
    public enum type {
        PRESS, RELEASE, AXIS, POV
    }

    private final JoystickEvent.type eventType;
    private final int stickId;
    private final int id;
    private Double val;
    private final long time;
    private static SimpleDateFormat fmt;

    /**
     * Creates a joystick event. (JoystickEvent.type.PRESS/RELEASE/POV)
     *
     * @param type  The type of event (JoystickEvent.type.PRESS/RELEASE/POV)
     * @param id    The id of the joystick being used.
     * @param btnId The id of the button being pressed/released.
     */
    public JoystickEvent(JoystickEvent.type type, int id, int btnId) {
        this.eventType = type;
        this.stickId = id;
        this.id = btnId;
        this.time = System.currentTimeMillis();
    }

    /**
     * Creates a joystick event. (JoystickEvent.type.AXIS/POV)
     *
     * @param type   The type of event (JoystickEvent.type.AXIS)
     * @param id     The id of the joystick being used.
     * @param axisId The id of the axis being changed.
     * @param val    The current value of the axis.
     */
    public JoystickEvent(JoystickEvent.type type, int id, int axisId, double val) {
        this.eventType = type;
        this.stickId = id;
        this.id = axisId;
        this.val = val;
        this.time = System.currentTimeMillis();
    }

    /**
     * Creates a joystick event. (Press/Release)
     *
     * @param type  The type of event (JoystickEvent.type.PRESS/RELEASE)
     * @param time  The time, in milliseconds, the event occurred. (from System.currentTimeMillis())
     * @param id    The id of the joystick being used.
     * @param btnId The id of the button being pressed/released.
     */
    public JoystickEvent(JoystickEvent.type type, long time, int id, int btnId) {
        this.eventType = type;
        this.stickId = id;
        this.id = btnId;
        this.time = time;
    }

    /**
     * Creates a joystick event. (Axis)
     *
     * @param type   The type of event (JoystickEvent.type.AXIS)
     * @param time   The time, in milliseconds, the event occurred. (from System.currentTimeMillis())
     * @param id     The id of the joystick being used.
     * @param axisId The id of the axis being changed.
     * @param val    The current value of the axis.
     */
    public JoystickEvent(JoystickEvent.type type, long time, int id, int axisId, double val) {
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
        return val != null ? val : null;
    }

    /**
     * @return The type of event this event is.
     */
    public JoystickEvent.type getEventType() {
        return eventType;
    }

    /**
     * @return The id of the button if it is not a POV event, and null otherwise.
     */
    public Integer getID() {
        return this.eventType != type.POV ? id : null;
    }

    /**
     * @return The value of the POV switch, if it is a POV event, and null otherwise.
     */
    public Integer getPOVValue() {
        return this.eventType == type.POV ? id : null;
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
        return str + ',' + stickId + ',' + id + ',' + (eventType == type.AXIS ? (String.valueOf(val) + ',') : "") + '\n';
    }

    /**
     * Converts this into a human-readable string. For logging purposes, mostly.
     *
     * @return This event as a human-readable string.
     */
    public String toReadableString() {
        fmt = fmt == null ? new SimpleDateFormat("MM/dd/yyyy hh:mm:ss") : fmt; //Create the date formatter if it does not exist.
        return String.format(Locale.ENGLISH, "%s: Joystick %d's %s%s%s\n", fmt.format(time), stickId,
                eventType == type.PRESS || eventType == type.RELEASE ? "Button " : eventType == type.AXIS ? "Axis" : "POV",
                eventType == type.POV ? "" : id + " ",
                eventType == type.PRESS ? "pressed." : eventType == type.RELEASE ? "released." : eventType == type.AXIS ? "set to " + val : "set to " + id);
    }

    /**
     * Returns if the provided {@link JoystickEvent} is the same as this one.
     *
     * @param that The {@link JoystickEvent} to check for equality.
     * @return If they are equal.
     */
    public boolean equals(JoystickEvent that) { //Fun. If you let IntelliJ simplify it all the way down, it becomes a one-liner!
        return this == that || that != null && !(this.stickId != that.stickId || this.id != that.id || this.time != that.time || this.eventType != that.eventType) && (this.val != null ? this.val.equals(that.val) : that.val == null);
    }

    /**
     * Returns the hashcode of the given object, using {@link #stickId}, {@link #id}, and {@link #val}.
     * @return The hashcode of the given object, using {@link #stickId}, {@link #id}, and {@link #val}.
     */
    @Override
    public int hashCode() {
        return ((eventType.hashCode() * 31 + stickId) * 31 + id) * 31 + (val != null ? val.hashCode() : 0);
    }
}
