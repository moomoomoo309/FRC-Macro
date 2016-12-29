package FRCMacro;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static FRCMacro.JoystickEvent.type.*; //This is so you can use PRESS instead of JoystickEvent.type.PRESS.

/**
 * An example IterativeRobot class implementing Macro recording, Macro playback, storing and reading values from a config file,
 * Joystick event binding.
 * <p>
 * Feel free to make your actual Robot class extend this one instead of IterativeRobot.
 *
 * @author Nicholas DeLello
 * @see Macro
 * @see MacroHelper
 */
public class ExampleMacroRobot extends IterativeRobot {
    //Paths used by the program
    private static final String macroDir = "/home/lvuser/macros";
    private static final String configDir = "/home/lvuser/cfg";

    //Defaults for values loaded from the config
    private static double minSpeed = .45; //The slowest the robot can move, (because under a certain threshold it won't move!)
    private static double maxSpeed = 1; //The fastest it can move. (In case you don't want the motors to run at full power)

    //Stuff whose IDs may need to be changed
    private static final int driveStickId = 0;
    private static final int auxStickId = 1;
    private static final int recordButtonID = 5;
    private static final boolean debug = false; //Makes all errors print out their stack traces. Shouldn't be true unless you're debugging.

    //Initializing variables, no need to change these.
    private static Joystick realDriveStick;
    private static Joystick realAuxStick;
    private static simulatedJoystick driveStick;
    private static simulatedJoystick auxStick;
    private static final SendableChooser autoChooser = new SendableChooser();
    private static Macro currentMacro = null; //Used to keep track of the current macro
    private static MacroHelper macroHelper;
    private static final HashMap<JoystickEvent, Runnable> methods = new HashMap<>();

    public void robotInit() { //Joysticks work oddly at competition when you initialize them outside of robotInit...
        realDriveStick = new Joystick(driveStickId);
        realAuxStick = new Joystick(auxStickId);
        driveStick = new simulatedJoystick(realDriveStick, driveStickId);
        auxStick = new simulatedJoystick(realAuxStick, auxStickId);
        macroHelper = new MacroHelper(macroDir, autoChooser, new int[] {driveStickId, auxStickId}, realDriveStick, realAuxStick);
        macroHelper.addExistingMacrosToSendableChooser(); //The method name should explain itself, if not the JavaDoc.
        addJoystickMethods();
        loadVarsFromConfig();
    }

    /**
     * Registers all of the methods to be run on joystick events.
     */
    public void addJoystickMethods() {
        addJoystickMethod(PRESS, recordButtonID, driveStickId, () -> {
            try {
                macroHelper.startOrStopMacro(currentMacro);
            } catch (Exception e) {
                if (debug)
                    e.printStackTrace();
                System.err.println("Could not access the macro, not starting/stopping...");
            }
        });
    }

    /**
     * Loads variables from the config file, if possible.
     *
     * @return If the variables could be successfully loaded.
     */
    private boolean loadVarsFromConfig() { //Example to load variables from config
        String[] configFile;
        try {
            configFile = MacroHelper.readFile(configDir);
        } catch (IOException e) { //You can't access the file for some reason.
            if (debug)
                e.printStackTrace();
            System.err.println("Could not read config at "+configDir+'.');
            return false;
        }
        //The try and catch is pretty tedious, but it's nice to know which ones failed to load, which is why I use them.
        try {
            minSpeed = Double.parseDouble(configFile[0]); //If it's an int, not a double, use Integer.parseInt().
        } catch (Exception e) { //If it's a boolean, use Boolean.parseBoolean, and so on for other primitives.
            if (debug)
                e.printStackTrace();
            System.err.println("Could not load minSpeed.");
        }
        try {
            maxSpeed = Double.parseDouble(configFile[1]);
        } catch (Exception e) {
            if (debug)
                e.printStackTrace();
            System.err.println("Could not load maxSpeed.");
        }
        return true;
    }

    /**
     * Writes the current configurable values to the config file. Run it every time a value changes.
     *
     * @return If the values could successfully be written.
     */
    private boolean updateConfig() {
        String vars = String.valueOf(minSpeed) + '\n' + maxSpeed; //Add more variables with '\n' between as necessary.
        try {
            MacroHelper.overwriteFile(configDir, vars);
        }catch(Exception e) {
            if (debug)
                e.printStackTrace();
            System.err.println("Could not update config.");
            return false;
        }
        return true;
    }

    public void autonomousPeriodic() {
        Boolean runMacro=null;
        try {
            runMacro = macroHelper.autonMacro(currentMacro);
        }catch (Exception e) {
            if (debug)
                e.printStackTrace();
            System.err.println("Could not load macro at " + macroDir + macroHelper.getSelectedAuton().substring(5));
        }
        if (runMacro != null)
            if (runMacro)
                teleopPeriodic();
            else
                stopRobot(); //Put your motors in here!
        else
            switch(macroHelper.getSelectedAuton()) {
                case "example":
                    break;
            }
    }

    /**
     * Adds a new method to run on a given JoystickEvent.
     *
     * @param type The type of event (PRESS,RELEASE,AXIS,POV)
     * @param stickId The ID of the Joystick
     * @param id The ID of the button/axis or the value of the POV
     * @param method The method to run
     */
    public void addJoystickMethod(JoystickEvent.type type, int id, int stickId, Runnable method) {
        methods.put(new JoystickEvent(type, stickId, id), method);
    }

    /**
     * Adds a new method to run on a given JoystickEvent.
     *
     * @param j The JoystickEvent for the method to run on
     * @param method The method to run on the given event.
     */
    public void addJoystickMethod(JoystickEvent j, Runnable method) {
        methods.put(j, method);
    }

    /**
     * Runs the event associated with the given JoystickEvent.
     *
     * @param j The JoystickEvent's method to run.
     */
    public void runJoystickMethod(JoystickEvent j) {
        methods.get(j).run();
    }

    public void teleopPeriodic() { //It drives, has joystick events, and has the throttle.
        driveStick.updateWithEvents(realDriveStick, driveStickId).forEach(this::runJoystickMethod);
        auxStick.updateWithEvents(realAuxStick, auxStickId).forEach(this::runJoystickMethod);
    }

    /**
     * Stops all of the motors, the shooter, or anything else that should be shut off at the end of autonomous.
     * <br><br>
     * Yes, a {@link edu.wpi.first.wpilibj.CANTalon CANTalon} or {@link edu.wpi.first.wpilibj.RobotDrive RobotDrive}, for
     * example, implements {@link MotorSafety} or {@link SpeedController}, so it'll work fine.
     *
     * @param motors All motors which need to be stopped. Can be passed as varargs or as an array. (in the form
     * "motor1,motor2,motor3" or "new MotorSafety[] {motor1,motor2,motor3}" will both work)
     */
    public static void stopRobot(Object... motors) {
        Arrays.stream(motors).forEach(motor -> {
            if (motor instanceof SpeedController)
                ((SpeedController) motor).set(0);
            else if (motor instanceof MotorSafety)
                ((MotorSafety) motor).stopMotor();
        });
    }

    /**
     * Stops all of the motors, the shooter, or anything else that should be shut off at the end of autonomous.
     * <br><br>
     * Yes, a {@link edu.wpi.first.wpilibj.CANTalon CANTalon} or {@link edu.wpi.first.wpilibj.RobotDrive RobotDrive}, for
     * example, implements {@link MotorSafety} or {@link SpeedController}, so it'll work fine.
     *
     * @param preFunction Any function you want run before stopping all of the motors. (Like if you need to move back an arm or something).<br>
     * Feel free to pass it a lambda like "()->Robot.doSomething()" or "()->{Robot.doSomething(); Robot.doSomethingElse(); }".
     * @param motors All motors which need to be stopped. Can be passed as varargs or as an array. (in the form
     * "motor1,motor2,motor3" or "new MotorSafety[] {motor1,motor2,motor3}" will both work)
     */
    public static void stopRobot(Runnable preFunction, Object... motors) {
        preFunction.run();
        stopRobot(motors);
    }
}