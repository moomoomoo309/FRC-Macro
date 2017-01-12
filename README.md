# FRC-Macro
A utility allowing for FRC robots to record and playback joystick inputs.

Look at ExampleMacroRobot.java for an example of how it should be implemented.

A few useful functions in there:
ScheduleEvent(seconds, function) - Run function after seconds seconds.
NOTE: You need scheduledEvents.forEach(Runnable::run); in teleopPeriodic for this to work!

addJoystickMethod(eventType, button/POV ID, JoystickID, method) - Run method when the given event is passed (I.E. when button 5 on joystick 1 is pressed, or button 3 on joystick 0 is released, etc.)
runJoystickEvents() - Run in teleopPeriodic for the method above.
