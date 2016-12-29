package FRCMacro;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * A helper class containing all logic needed to implement {@link Macro} into an {@link edu.wpi.first.wpilibj.IterativeRobot IterativeRobot} class.
 * @author Nicholas DeLello
 */
public class MacroHelper {
    /**
     * The directory where all macros are saved to. Make sure Java has permission to read/write to it.
     */
    private final String macroDir;
    /**
     * The {@link SendableChooser} being used to select your autonomous mode.
     */
    private final SendableChooser autoChooser;
    /**
     * An array containing all of your real joysticks.
     */
    private final Joystick[] realSticks;
    private Long lastPress;
    private int[] ids;
    private static boolean debug = false;

    /**
     * Creates a MacroHelper.
     *
     * @param macroDir    The directory to store all macros in.
     * @param autoChooser The {@link SendableChooser} being used
     * @param realSticks  The actual joysticks being used.
     */
    public MacroHelper(String macroDir, SendableChooser autoChooser, int[] ids, Joystick... realSticks) {
        this.macroDir = macroDir;
        this.autoChooser = autoChooser;
        this.ids = ids;
        this.realSticks = realSticks;
    }

    public MacroHelper(String macroDir, SendableChooser autoChooser, int[] ids, boolean debug, Joystick... realSticks) {
        this(macroDir, autoChooser, ids, realSticks);
        MacroHelper.debug = debug;
    }

    /**
     * Adds all saved macros into {@link #autoChooser the class's SendableChooser.}
     */
    public void addExistingMacrosToSendableChooser() {
        File[] macroDirectory = new File(macroDir).listFiles();
        if (macroDirectory != null)
            for (File f : macroDirectory)
                autoChooser.addObject("Macro " + f.getName(), "macro" + f.getName());
    }

    /**
     * Runs the selected macro during autonomous, if a macro is selected. Put this in autonomousPeriodic.
     *
     * @param currentMacro The currently selected macro, if it's already playing, or null otherwise.
     * @return True = Run teleop; False = Stop the robot; null = A macro wasn't chosen in the SendableChooser.
     */
    public Boolean autonMacro(Macro currentMacro) throws IOException {
        String chosenAuton = (String) autoChooser.getSelected();
        if (chosenAuton.startsWith("macro")) {
            if (currentMacro == null) {
                System.out.println("currentMacro is null, generating...");
                currentMacro = new Macro(readFile(macroDir + chosenAuton.substring(5)), realSticks, ids);
                currentMacro.startPlaying();
                System.out.println("Macro length: " + currentMacro.length() / 1000D + "seconds");
            } else
                return currentMacro.isPlaying();
        }
        return null;
    }

    /**
     * Reads a file at the given directory as a String array.
     *
     * @param directory The path to the file
     * @return The file as a String array.
     * @throws IOException If the file cannot be read for any reason.
     */
    public static String[] readFile(String directory) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(directory));
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Overwrites a file with the given path with the given string.
     *
     * @param fileDirectory The path to the file
     * @param info          What to write to the file
     * @throws IOException If the file could not be written to.
     */
    public static void overwriteFile(String fileDirectory, String info) throws IOException {
        Files.write(Paths.get(fileDirectory), info.getBytes("utf8"), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /**
     * Saves a macro to a file, named numerically.
     *
     * @param currentMacro The macro to save.
     * @throws IOException if the file could not be written to, or the macro directory is not a folder.
     */
    public void saveMacro(Macro currentMacro) throws IOException {
        File[] filenames = new File(macroDir).listFiles(); //Gets all of the files in the given directory
        int currentFileNumber = 0; //Since it saves them numerically, it keeps track of the number with an int.
        getFileNumber:
        {
            while (true) { //Keep going until a file with the given name (number) doesn't exist.
                if (filenames == null)
                    throw new FileNotFoundException("The directory " + macroDir + " is not a folder!");
                for (File f : filenames)
                    if (f.getName().equals(String.valueOf(currentFileNumber))) {
                        break getFileNumber;
                    }
                currentFileNumber++;
            }
        }
        autoChooser.addObject("Macro " + currentFileNumber, "macro" + currentFileNumber);
        SmartDashboard.putData("Auto", autoChooser); //Update the SendableChooser with the new macro
        overwriteFile(macroDir + currentFileNumber, currentMacro.toString());
    }

    /**
     * Run when the button to start/stop recording the macro is pressed.
     *
     * @param currentMacro The macro to start/stop.
     */
    public void startOrStopMacro(Macro currentMacro) throws IOException {
        if (currentMacro == null) {
            currentMacro = new Macro(realSticks, ids);
            System.out.println("Recording...");
            currentMacro.startRecording();
        } else {
            System.out.println("Stopped recording.");
            currentMacro.stopRecording();
            this.saveMacro(currentMacro);
            //noinspection UnusedAssignment
            currentMacro = null;
        }
    }

    /**
     * Returns the selected autonomous mode
     *
     * @return The selected autonomous mode
     */
    public String getSelectedAuton() {
        return (String) autoChooser.getSelected();
    }

    /**
     * Adds an auton type with the specified name.
     *
     * @param name The name of the auton type.
     */
    public void addAutonMode(String name) {
        autoChooser.addObject(name, name);
    }
}
